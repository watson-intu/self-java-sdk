package com.ibm.watson.self.blackboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;
import com.ibm.watson.self.constants.SelfConfigurationConstants;
import com.ibm.watson.self.topics.TopicClient;

public class BlackBoardTest implements IBlackBoard {

	private static String host = null;
	private static String port = null;
	
	private static Logger logger = LogManager.getLogger(BlackBoardTest.class.getName());
	
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
		if(host != null) {
			client.connect(host, port);
		}
		else {
			client.connect(SelfConfigurationConstants.HOST, 
					SelfConfigurationConstants.PORT);
		}
		
		return true;
	}
	
	public BlackBoardTest() {
		TopicClient client = TopicClient.getInstance();
		client.setHeaders(SelfConfigurationConstants.SELF_ID, 
				SelfConfigurationConstants.TOKEN);
		client.connect(SelfConfigurationConstants.HOST, 
				SelfConfigurationConstants.PORT);
		while(!client.isConnected()) {
			try {
				System.out.println("Client not connected yet");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		BlackBoard.getInstance().subscribeToType("EmotionalState", ThingEventType.TE_ADDED, this, "");
		JsonObject wrapperObject = new JsonObject();
        wrapperObject.addProperty("m_Text", "greeting_interaction");
        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("m_PersonGender", "female");
        dataObject.addProperty("m_PersonAge", "25-35");
        dataObject.addProperty("m_PersonName", "Erika");
        dataObject.addProperty("m_SensorName", "Camera1");
        dataObject.addProperty("m_SensorId", "asdf");
        wrapperObject.add("m_Data", dataObject);
        IThing thing = new IThing();
        thing.setType("Text");
        thing.setData(wrapperObject);
        BlackBoard.getInstance().addThing(thing, "");
		
		int i = 0;
		while(i < 60) {
			try {
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		BlackBoard.getInstance().unsubscribeFromType("EmotionalState", this, "");
		logger.info("Finished running blackboard test!");
		
	}

	public void onThingEvent(ThingEvent thingEvent) {
		System.out.println("Received new ThingEvent: " + thingEvent.getThing().getType());
		System.out.println("EmotionalState input was: " + thingEvent.getThing().getData().get("m_EmotionalState").getAsDouble());
	}
}
