package com.ibm.watson.self.blackboard;

import java.io.IOException;

import javax.websocket.DeploymentException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.self.blackboard.IThing.ThingEventType;
import com.ibm.watson.self.constants.SelfConfigurationConstants;
import com.ibm.watson.self.topics.ConversationTest;
import com.ibm.watson.self.topics.TopicClient;

public class BlackBoardTest implements IBlackBoard {

	private static String host = null;
	private static String port = null;
	
	private static Logger logger = LogManager.getLogger();
	
	public static void main(String[] args) {
		
		if(args.length == 2) {
			host = args[0];
			port = args[1];
		}
		new BlackBoardTest();
			
	}
	
	private boolean connectToIntu() {
		TopicClient client = TopicClient.getInstance();
		client.setHeaders(SelfConfigurationConstants.SELF_ID, 
				SelfConfigurationConstants.TOKEN);
		try {
			if(host != null) {
				client.connect(host, port);
			}
			else {
				client.connect(SelfConfigurationConstants.HOST, 
						SelfConfigurationConstants.PORT);
			}
		} catch (DeploymentException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public BlackBoardTest() {
		boolean isConnected = connectToIntu();
		if(!isConnected) {
			logger.error("Cannot connect to Intu!! Shutting down...");
			return;
		}
		BlackBoard.getInstance().subscribeToType("Text", ThingEventType.TE_ADDED, this, "");
		
		int i = 0;
		while(i < 60) {
			try {
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		BlackBoard.getInstance().unsubscribeFromType("Text", this, "");
		logger.info("Finished running blackboard test!");
		
	}

	public void onThingEvent(ThingEvent thingEvent) {
		System.out.println("Received new ThingEvent: " + thingEvent.getThing().getType());
		System.out.println("Text input was: " + thingEvent.getThing().getBody().get("m_Text").getAsString());
	}
}
