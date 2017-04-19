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
	
	public void subscribeToType(String type, IThing.ThingEventType thingEvent, 
			IBlackBoard blackboard, String path) {
		logger.entry();
		if(!subscriptionMap.containsKey(path)) {
			TopicClient.getInstance().subscribe(path + BlackBoardConstants.BLACKBOARD, this);
			HashMap<String, List<Subscriber>> tempMap = new HashMap<String, List<Subscriber>>();
			subscriptionMap.put(path, tempMap);
		}
		
		HashMap<String, List<Subscriber>> types = subscriptionMap.get(path);
		if(!types.containsKey(type)) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(BlackBoardConstants.EVENT, BlackBoardConstants.SUBSCRIBE_TO_TYPE);
			wrapperObject.addProperty(BlackBoardConstants.TYPE, type);
			wrapperObject.addProperty(BlackBoardConstants.EVENT_MASK, thingEvent.TE_ALL.getId());
			TopicClient.getInstance().publish(path + BlackBoardConstants.BLACKBOARD, 
					wrapperObject.toString(), false);
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
				wrapperObject.addProperty(BlackBoardConstants.EVENT, 
						BlackBoardConstants.UNSUBSCRIBE_FROM_TYPE);
				wrapperObject.addProperty(BlackBoardConstants.TYPE, type);
				TopicClient.getInstance().publish(path + BlackBoardConstants.BLACKBOARD, 
						wrapperObject.toString(), false);
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
		wrapperObject.addProperty(BlackBoardConstants.EVENT, BlackBoardConstants.ADD_OBJECT);
		wrapperObject.addProperty(BlackBoardConstants.TYPE, thing.getType());
		wrapperObject.add(BlackBoardConstants.THING, thing.serialize());
		if(thing.getParentId() != null && !thing.getParentId().isEmpty())
			wrapperObject.addProperty(BlackBoardConstants.PARENT, thing.getParentId());
		
		TopicClient.getInstance().publish(path + BlackBoardConstants.BLACKBOARD, 
				wrapperObject.toString(), false);
		logger.exit();
	}
	
	public void removeThing(IThing thing, String path) {
		removeThing(thing.getGuid(), path);
	}
	
	public void removeThing(String guid, String path) {
		logger.entry();
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty(BlackBoardConstants.EVENT, BlackBoardConstants.REMOVE_OBJECT);
		wrapperObject.addProperty(BlackBoardConstants.THING_GUID, guid);		
		TopicClient.getInstance().publish(path + BlackBoardConstants.BLACKBOARD, 
				wrapperObject.toString(), false);
		logger.exit();
	}
	
	public void setState(IThing thing, String state, String path) {
		String guid = thing.getGuid();
		setState(guid, state, path);
	}
	
	public void setState(String guid, String state, String path) {
		logger.entry();
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty(BlackBoardConstants.EVENT, BlackBoardConstants.SET_OBJECT_STATE);
		wrapperObject.addProperty(BlackBoardConstants.THING_GUID, guid);
		wrapperObject.addProperty(BlackBoardConstants.STATE, state);
		TopicClient.getInstance().publish(path + BlackBoardConstants.BLACKBOARD, 
				wrapperObject.toString(), false);
		logger.exit();
	}
	
	public void setImportance(IThing thing, double importance, String path) {
		setImportance(thing.getGuid(), thing.getImportance(), path);
	}
	
	public void setImportance(String guid, double importance, String path) {
		logger.entry();
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty(BlackBoardConstants.EVENT, BlackBoardConstants.SET_OBJECT_IMPORTANCE);
		wrapperObject.addProperty(BlackBoardConstants.THING_GUID, guid);
		wrapperObject.addProperty(BlackBoardConstants.IMPORTANCE, importance);
		TopicClient.getInstance().publish(path + BlackBoardConstants.BLACKBOARD, 
				wrapperObject.toString(), false);
		logger.exit();
	}

	public void onEvent(String event) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		boolean failed = false;
		String eventName = wrapperObject.get(BlackBoardConstants.EVENT).getAsString();
		String type = wrapperObject.get(BlackBoardConstants.TYPE).getAsString();
		
		ThingEvent thingEvent = new ThingEvent();
		thingEvent.setEventType(ThingEventType.TE_NONE);
		thingEvent.setEvent(wrapperObject);
		IThing someThing = new IThing();
		if(eventName.equals(BlackBoardConstants.ADD_OBJECT)) {
			thingEvent.setEventType(ThingEventType.TE_ADDED);			
			thingEvent.setThing(someThing);
			try {
				someThing.deserialize(wrapperObject.get(BlackBoardConstants.THING).getAsJsonObject());
				if(wrapperObject.has(BlackBoardConstants.PARENT)) {
					someThing.setParentId(wrapperObject.get(BlackBoardConstants.PARENT).getAsString());
				}
				thingEvent.setThing(someThing);
				thingMap.put(thingEvent.getThing().getGuid(), thingEvent.getThing());
			}
			catch (Exception e) {
				logger.error("Failed to deserialize Blackboard object!!");
				failed = true;
			}
		}
		else if(eventName.equals(BlackBoardConstants.REMOVE_OBJECT)) {
			thingEvent.setEventType(ThingEventType.TE_REMOVED);
			String guid = wrapperObject.get(BlackBoardConstants.THING_GUID).getAsString();
			if(thingMap.containsKey(guid)) {
				thingMap.remove(guid);
			}
		}
		else if(eventName.equals(BlackBoardConstants.SET_OBJECT_STATE)) {
			String guid = wrapperObject.get(BlackBoardConstants.THING_GUID).getAsString();
			if(thingMap.containsKey(guid)) {
				String state = wrapperObject.get(BlackBoardConstants.STATE).getAsString();
				IThing updateThing = thingMap.get(guid);
				updateThing.setState(state);
				thingMap.put(guid, updateThing);
			}
			else {
				try {
					someThing.deserialize(wrapperObject.get(BlackBoardConstants.THING).getAsJsonObject());
					if(wrapperObject.has(BlackBoardConstants.PARENT)) {
						someThing.setParentId(wrapperObject.get(BlackBoardConstants.PARENT).getAsString());
					}
					thingEvent.setThing(someThing);
					thingMap.put(thingEvent.getThing().getGuid(), thingEvent.getThing());
				}
				catch (Exception e) {
					logger.error("Failed to deserialized Blackboard object when state has changed!!");
				}
			}
		}
		else if(eventName.equals(BlackBoardConstants.SET_OBJECT_IMPORTANCE)) {
			String guid = wrapperObject.get(BlackBoardConstants.THING_GUID).getAsString();
			if(thingMap.containsKey(guid)) {
				long importance = wrapperObject.get(BlackBoardConstants.IMPORTANCE).getAsLong();
				IThing importantThing = thingMap.get(guid);
				importantThing.setImportance(importance);
				thingMap.put(guid, importantThing);
			}
		}
		
		if(failed) {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty(BlackBoardConstants.FAILED_EVENT, eventName);
			failedObject.addProperty(BlackBoardConstants.EVENT, BlackBoardConstants.ERROR);
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
						if(subscriber.eventType == thingEvent.getEventType())
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
			TopicClient.getInstance().unsubscribe(path + BlackBoardConstants.BLACKBOARD, this);
		}
		logger.exit();
	}

	public void onDisconnect() {
		logger.info("BlackBoard has been disconnected!");
	}

	public void onReconnect() {
		
		for (String path : subscriptionMap.keySet()) {
			TopicClient.getInstance().subscribe(path + BlackBoardConstants.BLACKBOARD, this);
			for(String type : subscriptionMap.get(path).keySet()) {
				JsonObject wrapperObject = new JsonObject();
				wrapperObject.addProperty(BlackBoardConstants.EVENT, BlackBoardConstants.SUBSCRIBE_TO_TYPE);
				wrapperObject.addProperty(BlackBoardConstants.TYPE, type);
				Subscriber subscriber = subscriptionMap.get(path).get(type).get(0);
				wrapperObject.addProperty(BlackBoardConstants.EVENT_MASK, ThingEventType.TE_ALL.getId());
				TopicClient.getInstance().publish(path + BlackBoardConstants.BLACKBOARD, 
						wrapperObject.toString(), false);
			}
		}
	}
}
