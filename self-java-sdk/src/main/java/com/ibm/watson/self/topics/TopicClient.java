package com.ibm.watson.self.topics;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class TopicClient extends Endpoint implements MessageHandler.Whole<String>, SendHandler {

	/*          Logging                 */

    private static Logger logger = LogManager.getLogger(TopicClient.class.getName());
    
    /*			Variables				*/
    private Object sessionLock;
    private Session session;
    private URI uri;
    private ClientManager client;
    private HashMap<String, IEvent> subscriptionMap = new HashMap<String, IEvent>();
    private static TopicClient instance = null;
    private String selfId;
    private String token;
    
    /**
     * Modifier for HTTP handshake request.
     */
    public static class HandshakeModifier extends ClientEndpointConfig.Configurator {
	
		private String selfId;
		private String token;
	
		/**
		 * Constructor with authorization value Base64 encoded user:password.
		 * @param authorization
		 */
	
		public HandshakeModifier(String selfId, String token) {
		    this.selfId = selfId;
		    this.token = token;
		}
	
		@Override
		public void beforeRequest(Map<String, List<String>> headers) {
		    headers.put("selfId", Arrays.asList(selfId));
		    headers.put("token", Arrays.asList(token));
		    super.beforeRequest(headers);
		}
    }
    
    // handshake modifier to add headers to main self instance
    private HandshakeModifier handshakeModifier;
    
    public TopicClient() {
    	this.handshakeModifier = null;
    	this.session = null;
    	this.uri = null;
    	this.client = ClientManager.createClient();
    	this.sessionLock = new Object();
    }
    
    public static TopicClient getInstance() {
    	if(instance == null) {
    		instance = new TopicClient();
    	}    	
    	return instance;
    }
    
	/**
	 * Connect to WebSocket server
	 * @param host - ip address
	 * @param port - port number
	 * @param selfId - unique self id
	 * @param token - bearer token for authentication
	 * @return if connection has been established or not
	 * @throws IOException 
	 * @throws DeploymentException 
	 */
	public boolean connect(String host, String port) throws DeploymentException, IOException {
		logger.entry();
		uri = URI.create("ws://" + host + ":" + port + "/stream");
		logger.info("Connecting to: " + uri.toString());
		synchronized (sessionLock) {
			if (handshakeModifier == null) {
			    client.connectToServer(this, uri);
			} 
			else {				
			    ClientEndpointConfig modifier = ClientEndpointConfig.Builder.create()
				    .configurator(handshakeModifier).build();
			    client.connectToServer(this, modifier, uri);
			}
		}
		return logger.exit(true);
	}

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
    	logger.entry();
    	this.session = null;
        logger.info("closing websocket");
        logger.exit();
    }

	@Override
	public void onOpen(Session session, EndpointConfig arg1) {
		logger.entry();
		TopicClient.this.session = session;
		session.addMessageHandler(this);
		logger.info("opening websocket!");	
		logger.exit();
	}
	
    /**
     * Send a message.
     * @param message
     */
    public void sendMessage(JsonObject message) {
    	logger.entry();
		synchronized (sessionLock) {
		    if (session == null) {
		    	return;
		    }
		    message.addProperty("origin", this.selfId + "/.");
		    session.getAsyncRemote().sendText(message.toString());
		}
		logger.exit();
    }
    
    private void sendMessage(JsonObject wrapperObject, byte[] data) {
    	logger.entry();
		synchronized (sessionLock) {
			if (session == null) {
				return;
			}
			wrapperObject.addProperty("data", data.length);
			wrapperObject.addProperty("origin", this.selfId + "/.");
			try {
				byte[] header = wrapperObject.toString().getBytes("UTF-8");
				byte[] frame = new byte[header.length + data.length + 1];
				System.arraycopy(header, 0, frame, 0, header.length);
				System.arraycopy(data, 0, frame, header.length + 1, data.length);
				session.getAsyncRemote().sendBinary(ByteBuffer.wrap(frame));
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to send binary data over socket!");
			}
			
		}
		logger.exit();
	}
    
    
	public void onResult(SendResult result) {	
		logger.entry();
		if (!result.isOK()) {
			logger.error("Received error on Result");
		}
		logger.exit();
	}

	public void onMessage(String message) {
		logger.entry();
		synchronized (sessionLock) {
			if(session == null) {
				return;
			}
			JsonParser parser = new JsonParser();
			JsonObject wrapperObject = parser.parse(message).getAsJsonObject();
			if(!wrapperObject.get("binary").getAsBoolean()) {
				if(subscriptionMap.containsKey(wrapperObject.get("topic").getAsString())) {
					subscriptionMap.get(wrapperObject.get("topic").getAsString())
						.onEvent(wrapperObject.get("data").getAsString());
				}
			}
		}
		logger.exit();
	}
    
    public void publish(String path, String data, boolean persisted) {
    	logger.entry();
    	JsonObject wrapperObject = new JsonObject();
    	JsonArray pathArray = new JsonArray();
    	pathArray.add(new JsonPrimitive(path));
    	wrapperObject.add("targets", pathArray);
    	wrapperObject.addProperty("msg", "publish_at");
    	wrapperObject.addProperty("data", data);
    	wrapperObject.addProperty("binary", false);
    	wrapperObject.addProperty("persisted", persisted);
    	this.sendMessage(wrapperObject);
    	logger.exit();
    }
    
    public void publish(String path, byte[] data, boolean persisted) {
    	logger.entry();
    	JsonObject wrapperObject = new JsonObject();
    	JsonArray pathArray = new JsonArray();
    	pathArray.add(new JsonPrimitive(path));
    	wrapperObject.add("targets", pathArray);
    	wrapperObject.addProperty("msg", "publish_at");
    	wrapperObject.addProperty("binary", true);
    	wrapperObject.addProperty("persisted", persisted);
    	this.sendMessage(wrapperObject, data);
    	logger.exit();
    }

	public void subscribe(String path, IEvent event) {
		logger.entry();
    	if(!subscriptionMap.containsKey(path)) {
    		subscriptionMap.put(path, event);
    	}
    	JsonObject wrapperObject = new JsonObject();
    	JsonArray wrapperArray = new JsonArray();
    	wrapperArray.add(new JsonPrimitive(path));
    	wrapperObject.add("targets", wrapperArray);
    	wrapperObject.addProperty("msg", "subscribe");
    	this.sendMessage(wrapperObject);
    	logger.exit();
    }
    
    public boolean unsubscribe(String path, IEvent event) {
    	logger.entry();
    	if(subscriptionMap.containsKey(path)) {
    		subscriptionMap.remove(path);
    		JsonObject wrapperObject = new JsonObject();
    		JsonArray wrapperArray = new JsonArray();
    		wrapperArray.add(new JsonPrimitive(path));
    		wrapperObject.add("targets", wrapperArray);
    		wrapperObject.addProperty("msg", "unsubscribe");
    		this.sendMessage(wrapperObject);
    		return logger.exit(true);
    	}
    	
    	return logger.exit(false);
    }
	
    /**
     * Check if socket is open.
     * @return
     */
    public boolean isConnected() {
		synchronized (sessionLock) {
		    return (session != null && session.isOpen());
		}
    }
    
    /**
     * Add authorization header to HTTP handshake request. Socket must not be open.
     * @param authorization
     */
    public void setHeaders(String selfId, String token) {
    	logger.entry();
		if (handshakeModifier != null || isConnected())
		    return;
		handshakeModifier = new HandshakeModifier(selfId, token);
		this.selfId = selfId;
		this.token = token;
		logger.exit();
    }
}
