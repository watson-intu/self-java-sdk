package com.ibm.watson.self.blackboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class BlackBoard implements IEvent {

	private static BlackBoard instance = null;
	
	private HashMap<String, HashMap<String, List<Subscriber>>> subscriptionMap = new HashMap<String, HashMap<String, List<Subscriber>>>();
	private HashMap<String, IThing> thingMap = new HashMap<String, IThing>();
	private boolean started = false;
	
	private static Logger logger = LogManager.getLogger(BlackBoard.class.getName());
	
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
		logger.entry();
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
			wrapperObject.addProperty("event_mask", thingEvent.getId());
			TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
			List<Subscriber> tempSubList = new ArrayList<Subscriber>();
			types.put(type, tempSubList);
		}
		types.get(type).add(new Subscriber(blackboard, thingEvent, path));
		subscriptionMap.put(path, types);
		logger.exit();
	}
	
	public void unsubscribeFromType(String type, IBlackBoard blackboard, String path) {
		logger.entry();
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
		logger.exit();
	}
	
	public void addThing(IThing thing, String path) {
		logger.entry();
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("event", "add_object");
		wrapperObject.addProperty("type", thing.getType());
		wrapperObject.addProperty("thing", thing.serialize());
		if(!thing.getParentId().isEmpty())
			wrapperObject.addProperty("parent", thing.getParentId());
		
		TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
		logger.exit();
	}
	
	public void removeThing(IThing thing, String path) {
		removeThing(thing.getGuid(), path);
	}
	
	public void removeThing(String guid, String path) {
		logger.entry();
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("event", "remove_object");
		wrapperObject.addProperty("thing_guid", guid);		
		TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
		logger.exit();
	}
	
	public void setState(IThing thing, String state, String path) {
		String guid = thing.getGuid();
		setState(guid, state, path);
	}
	
	public void setState(String guid, String state, String path) {
		logger.entry();
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("event", "set_object_state");
		wrapperObject.addProperty("thing_guid", guid);
		wrapperObject.addProperty("state", state);
		TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
		logger.exit();
	}
	
	public void setImportance(IThing thing, double importance, String path) {
		setImportance(thing.getGuid(), thing.getImportance(), path);
	}
	
	public void setImportance(String guid, double importance, String path) {
		logger.entry();
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("event", "set_object_importance");
		wrapperObject.addProperty("thing_guid", guid);
		wrapperObject.addProperty("importance", importance);
		TopicClient.getInstance().publish(path + "blackboard", wrapperObject.toString(), false);
		logger.exit();
	}

	public void onEvent(String event) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		boolean failed = false;
		String eventName = wrapperObject.get("event").getAsString();
		String type = wrapperObject.get("type").getAsString();
		
		ThingEvent thingEvent = new ThingEvent();
		thingEvent.setEventType(ThingEventType.TE_NONE);
		thingEvent.setEvent(wrapperObject);
		if(eventName.equals("add_object")) {
			thingEvent.setEventType(ThingEventType.TE_ADDED);
			IThing someThing = new IThing();
			thingEvent.setThing(someThing);
			try {
				someThing.deserialize(wrapperObject.get("thing").getAsJsonObject());
				if(wrapperObject.has("parent")) {
					someThing.setParentId(wrapperObject.get("parent").getAsString());
				}
				thingEvent.setThing(someThing);
				thingMap.put(thingEvent.getThing().getGuid(), thingEvent.getThing());				
			}
			catch (Exception e) {
				logger.error("Failed to deserialize Blackboard object!!");
				failed = true;
			}
		}
		else if(eventName.equals("remove_object")) {
			thingEvent.setEventType(ThingEventType.TE_REMOVED);
			String guid = wrapperObject.get("thing_guid").getAsString();
			if(thingMap.containsKey(guid)) {
				thingMap.remove(guid);
			}
		}
		else if(eventName.equals("set_object_state")) {
			String guid = wrapperObject.get("thing_guid").getAsString();
			if(thingMap.containsKey(guid)) {
				String state = wrapperObject.get("state").getAsString();
				IThing someThing = thingMap.get(guid);
				someThing.setState(state);
				thingMap.put(guid, someThing);
			}
		}
		else if(eventName.equals("set_object_importance")) {
			String guid = wrapperObject.get("thing_guid").getAsString();
			if(thingMap.containsKey(guid)) {
				long importance = wrapperObject.get("importance").getAsLong();
				IThing someThing = thingMap.get(guid);
				someThing.setImportance(importance);
				thingMap.put(guid, someThing);
			}
		}
		
		if(failed) {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty("failed_event", eventName);
			failedObject.addProperty("event", "error");
			// TODO: Get origin and publish failed event back to origin path
//			TopicClient.getInstance().publish(path, data, persisted);
		}
		else if(thingEvent.getEventType() != ThingEventType.TE_NONE) {
			for (String key : subscriptionMap.keySet()) {
				List<Subscriber> sub = null;
				if(subscriptionMap.get(key).containsKey(type)) {
					sub = subscriptionMap.get(key).get(type);
					for(int i = 0; i < sub.size(); i++) {
						Subscriber subscriber = sub.get(i);
						if(subscriber.callback == null)
							continue;
						subscriber.callback.onThingEvent(thingEvent);
					}
				}
			}
		}
		logger.exit();
	}

	public boolean isActive() {
		return started;
	}

	public void shutdown() {
		logger.entry();
		started = false;
		for(String path : subscriptionMap.keySet()) {
			TopicClient.getInstance().unsubscribe(path + "blackboard", this);
		}
		logger.exit();
	}
}
