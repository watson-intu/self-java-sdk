package com.ibm.watson.self.blackboard;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class BlackBoard implements IEvent {

	private static BlackBoard instance = null;
	
	private HashMap<String, JsonObject> subscriptionMap = new HashMap<String, JsonObject>();
	private boolean started = false;
	
	public BlackBoard() {
		started = true;
	}
	
	public static BlackBoard getInstance() {
		if(instance == null) {
			instance = new BlackBoard();
		}
		
		return instance;
	}
	
	public void subscribeToType(String type, IThing.ThingEventType thingEvent, String path) {
		if(!subscriptionMap.containsKey(path)) {
			TopicClient.getInstance().subscribe(path + "blackboard", this);
		}
	}
	
	public void addThing(IThing thing, String path) {
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("event", "add_object");
		wrapperObject.addProperty("type", thing.getType());
		wrapperObject.addProperty("thing", thing.serialize());
		if(!thing.getParentId().isEmpty())
			wrapperObject.addProperty("parent", thing.getParentId());
		
		TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
	}

	public void onEvent(String event) {
		System.out.println(event);
		
	}

	public boolean isActive() {
		return started;
	}

	public void shutdown() {
		started = false;
		for(String path : subscriptionMap.keySet()) {
			TopicClient.getInstance().unsubscribe(path + "blackboard", this);
		}
	}
}
