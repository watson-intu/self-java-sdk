package com.ibm.watson.self.blackboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonObject;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class BlackBoard implements IEvent {

	private static BlackBoard instance = null;
	
	private HashMap<String, HashMap<String, List<Subscriber>>> subscriptionMap = new HashMap<String, HashMap<String, List<Subscriber>>>();
	private HashMap<String, IThing> thingMap = new HashMap<String, IThing>();
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
	
	public void subscribeToType(String type, IThing.ThingEventType thingEvent, IBlackBoard blackboard, String path) {
		if(!subscriptionMap.containsKey(path)) {
			TopicClient.getInstance().subscribe(path + "blackboard", this);
			HashMap<String, List<Subscriber>> tempMap = new HashMap<String, List<Subscriber>>();
			subscriptionMap.put(path, tempMap);
		}
		
		HashMap<String, List<Subscriber>> types = subscriptionMap.get(path);
		if(!types.containsKey(type)) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("event", "subscribe_to_type");
			wrapperObject.addProperty("type", type);
			wrapperObject.addProperty("event_mask", ThingEventType.TE_ADDED.getId());
			TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
			List<Subscriber> tempSubList = new ArrayList<Subscriber>();
			types.put(type, tempSubList);
		}
		types.get(type).add(new Subscriber(blackboard, thingEvent, path));
		subscriptionMap.put(path, types);
	}
	
	public void unsubscribeToType(String type, IThing.ThingEventType thingEvent, IBlackBoard blackboard, String path) {
		if(subscriptionMap.containsKey(path)) {
			HashMap<String, List<Subscriber>> types = subscriptionMap.get(path);
			if(types.containsKey(type)) {
				List<Subscriber> sub = types.get(type);
				for(int i = 0; i < sub.size(); i++) {
					if(sub.get(i).callback == blackboard)
					{
						sub.remove(i);
						types.put(type, sub);
						break;
					}
				}
				
				if(sub.size() == 0) {
					types.remove(type);
				}
			}
			
			if(!types.containsKey(type)) {
				JsonObject wrapperObject = new JsonObject();
				wrapperObject.addProperty("event", "unsubscribe_from_type");
				wrapperObject.addProperty("type", type);
				TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
				subscriptionMap.remove(path);
			}
			else {
				subscriptionMap.put(path, types);
			}
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
	
	public void removeThing(IThing thing, String path) {
		removeThing(thing.getGuid(), path);
	}
	
	public void removeThing(String guid, String path) {
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("event", "remove_object");
		wrapperObject.addProperty("thing_guid", guid);
		
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
