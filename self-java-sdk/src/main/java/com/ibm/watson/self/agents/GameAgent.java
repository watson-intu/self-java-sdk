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

public class GameAgent implements IAgent, IBlackBoard {

	private static Logger logger = LogManager.getLogger(GameAgent.class.getName());
	UUID uuid = UUID.randomUUID();
	
	private String instanceId;
	
	public GameAgent() {
		instanceId = uuid.toString();
	}
	
	public void onThingEvent(ThingEvent thingEvent) {
		String type = thingEvent.getThing().getType();
		System.out.println("Received event: " + type);
		if(type.equals("ProxyIntent")) {
			onProxyIntent(thingEvent.getThing());
		}
	}
	
	private void onProxyIntent(IThing thing) {
		logger.entry();
		System.out.println(thing.toString());
		JsonParser parser = new JsonParser();
		JsonObject intentObject = parser.parse(thing.toString()).getAsJsonObject();
		System.out.println("onProxyIntent() " + intentObject);
		if(!intentObject.get("m_Intent").getAsJsonObject().get("conversation")
				.getAsJsonObject().get("intents").getAsJsonArray()
				.get(0).getAsJsonObject().get("intent").getAsString().equals("game_intent"))
		{
			logger.error("Not a game intent!");
			return;
		}
		System.out.println("Received game intent");
		JsonObject wrapperObject = new JsonObject();
		JsonObject paramsObject = new JsonObject();
		paramsObject.addProperty("Type_", "ParamsMap");
		paramsObject.addProperty("sound", "shared/sounds/dog.wav");
		wrapperObject.add("m_Params", paramsObject);
		wrapperObject.addProperty("m_Name", "Game");
		IThing sayThing = new IThing();
		sayThing.setType("Goal");
		sayThing.setBody(wrapperObject);
		System.out.println("Sending the payload: " + sayThing.serialize());
		BlackBoard.getInstance().addThing(sayThing, "");
	}

	public String getAgentName() {
		return "GameAgent";
	}

	public String getAgentId() {
		return instanceId;
	}

	public boolean onStart() {
		logger.entry();
		System.out.println("Started GameAgent...");
		BlackBoard.getInstance().subscribeToType("ProxyIntent", ThingEventType.TE_ADDED, this, "");
		return logger.exit(true);
	}

	public boolean onStop() {
		BlackBoard.getInstance().unsubscribeFromType("ProxyIntent", this, "");
		return logger.exit(true);
	}

}
