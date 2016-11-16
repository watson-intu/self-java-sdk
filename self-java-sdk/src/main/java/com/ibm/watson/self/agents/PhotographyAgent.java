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

public class PhotographyAgent implements IAgent, IBlackBoard {
	
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
			// Throw say object on blackboard
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("m_Name", "Photography");
			IThing sayThing = new IThing();
			sayThing.setType("Goal");
			sayThing.setBody(wrapperObject);
			BlackBoard.getInstance().addThing(sayThing, "");
			
			// Grab last image and display
			
			// Reset everything
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
		isRunning = true;
		Thread personTimer = new Thread(new PersonTimer());
		personTimer.start();
		return logger.exit(true);
	}

	public boolean onStop() {
		BlackBoard.getInstance().unsubscribeFromType("ProxyIntent", this, "");
		BlackBoard.getInstance().unsubscribeFromType("Person", this, "");
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
