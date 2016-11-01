package com.ibm.watson.self.topics;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TopicClient extends Endpoint {

	/*          Logging                 */

    private static Logger logger = LogManager.getLogger();
    
    /*			Variables				*/
    private Object sessionLock;
    private Session session;
    private URI uri;
    private ClientManager client;
    
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
	public boolean connect(String host, int port) throws DeploymentException, IOException {
		logger.entry(host, port);
		uri = URI.create("ws://" + host + ":" + port + "/stream");
		synchronized (sessionLock) {
			if (handshakeModifier == null) {
			    client.connectToServer(this, uri);
			} 
			else {
				System.out.println("Connecting to: " + uri.toString());
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
        System.out.println("closing websocket");
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
    	System.out.println(message);
    }

	@Override
	public void onOpen(Session arg0, EndpointConfig arg1) {
		System.out.println("opening websocket!");	
	}
	
    /**
     * Send a message.
     * @param message
     */
    public void sendMessage(String message) {
		synchronized (sessionLock) {
		    if (session == null) {
		    	return;
		    }
		    session.getAsyncRemote().sendText(message);
		}
    }
    
    public void publish(String path, String data, boolean persisted) {
    	JsonObject wrapperObject = new JsonObject();
    	JsonArray pathArray = new JsonArray();
    	pathArray.add(new JsonPrimitive(path));
    	wrapperObject.add("targets", pathArray);
    	wrapperObject.addProperty("msg", "publish_at");
    	wrapperObject.addProperty("data", data);
    	wrapperObject.addProperty("binary", false);
    	wrapperObject.addProperty("persisted", persisted);
    	this.sendMessage(wrapperObject.toString());
    }
    
    public void publish(String path, byte[] data, boolean persisted) {
    	JsonObject wrapperObject = new JsonObject();
    	JsonArray pathArray = new JsonArray();
    	pathArray.add(new JsonPrimitive(path));
    	wrapperObject.add("targets", pathArray);
    	wrapperObject.addProperty("data", data.toString());
    	wrapperObject.addProperty("binary", true);
    	wrapperObject.addProperty("persisited", persisted);
    	this.sendMessage(wrapperObject.toString());
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
		if (handshakeModifier != null || isConnected())
		    return;
		handshakeModifier = new HandshakeModifier(selfId, token);
    }
}
