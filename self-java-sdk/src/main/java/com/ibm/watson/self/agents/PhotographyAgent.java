package com.ibm.watson.self.agents;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.blackboard.BlackBoard;
import com.ibm.watson.self.blackboard.IBlackBoard;
import com.ibm.watson.self.blackboard.IThing;
import com.ibm.watson.self.blackboard.ThingEvent;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;
import com.ibm.watson.self.sensors.ILocalSensorSubscriber;
import com.ibm.watson.self.sensors.SensorManager;

/**
 * This agent takes a snapshot of a person on photography_intent. In case it cannot see the person,
 * it would give a notification saying it cannot see. 
 */
public class PhotographyAgent implements IAgent, IBlackBoard, ILocalSensorSubscriber {
	
	private static Logger logger = LogManager.getLogger(PhotographyAgent.class.getName());
	UUID uuid = UUID.randomUUID();
	
	private String instanceId;
	private boolean personFound = false;
	private IThing person = null;
	private boolean isRunning = false;
	private int personTimer = 0;
	private int personWaitTime = 30;

	public PhotographyAgent() {
		instanceId = uuid.toString();
	}
	
	public void onThingEvent(ThingEvent thingEvent) {
		String type = thingEvent.getThing().getType();
		if(type.equals("Person")) {
			onPerson(thingEvent.getThing());
		}
		else if(type.equals("ProxyIntent")) {
			onProxyIntent(thingEvent.getThing());
		}
		else if(type.equals("Object")) {
			onObject(thingEvent.getThing());
		}
		else {
			logger.error("Could not identify the type of the IThing");
		}
	}

	private void onProxyIntent(IThing thing) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject intentObject = parser.parse(thing.toString()).getAsJsonObject();
		if(!intentObject.get("m_Intent").getAsJsonObject().get("conversation")
				.getAsJsonObject().get("intents").getAsJsonArray()
				.get(0).getAsJsonObject().get("intent").getAsString().equals("photography_intent"))
		{
			logger.error("Not a photography intent!");
			return;
		}
		logger.info("Received onPhotgraphyIntent() with: " + thing.toString());
		if(personFound) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("m_Name", "Photography");
			IThing sayThing = new IThing();
			sayThing.setType("Goal");
			sayThing.setBody(wrapperObject);
			BlackBoard.getInstance().addThing(sayThing, "");
			personFound = false;
			personTimer = 0;			
		}
		else {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("m_Text", "I can't see anyone! Get in front of the camera and ask again");
			IThing sayThing = new IThing();
			sayThing.setType("Say");
			sayThing.setBody(wrapperObject);
			BlackBoard.getInstance().addThing(sayThing, "");
		}
		
	}

	private void onPerson(IThing thing) {
		logger.info("Received onPerson() with: " + thing.toString());	
		personFound = true;
		person = thing;
		personTimer = 0;
	}
	
	private void onObject(IThing thing) {
		logger.info("Received onObject() with: " + thing.toString());
		SensorManager.getInstance().registerWithLocalSensor(this, "VideoData");
	}
	
	public void getData(String data) {
		logger.info("Received non-binary data!");
	}

	public void getBinaryData(byte[] data) {
		// TODO: Send Binary Data from here
		logger.info("Received binary data!");
		SensorManager.getInstance().unregisterWithLocalSensor(this);
		
	}

	public String getAgentName() {
		return "PhotographyAgent";
	}

	public String getAgentId() {
		return instanceId;
	}

	public boolean onStart() {
		logger.entry();
		BlackBoard.getInstance().subscribeToType("ProxyIntent", ThingEventType.TE_ADDED, this, "");
		BlackBoard.getInstance().subscribeToType("Person", ThingEventType.TE_ADDED, this, "");
		BlackBoard.getInstance().subscribeToType("Object", ThingEventType.TE_ADDED, this, "");
		isRunning = true;
		Thread personTimer = new Thread(new PersonTimer());
		personTimer.start();
		return logger.exit(true);
	}

	public boolean onStop() {
		BlackBoard.getInstance().unsubscribeFromType("ProxyIntent", this, "");
		BlackBoard.getInstance().unsubscribeFromType("Person", this, "");
		BlackBoard.getInstance().unsubscribeFromType("Object", this, "");
		isRunning = false;
		return logger.exit(true);
	}
	
	class PersonTimer implements Runnable {
		public void run() {		
			while(isRunning) {
				if(personTimer > personWaitTime) {
					personFound = false;
					personTimer = 0;
				}
				personTimer++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
