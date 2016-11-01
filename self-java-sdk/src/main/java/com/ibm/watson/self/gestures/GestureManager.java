package com.ibm.watson.self.gestures;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class GestureManager implements IEvent {

	private static GestureManager instance = null;
	private HashMap<String, IGesture> gesturesMap = new HashMap<String, IGesture>();
	private HashMap<String, Boolean> overrideMap = new HashMap<String, Boolean>();
	
	public GestureManager() {
		TopicClient.getInstance().subscribe("gesture-manager", this);
	}
	
	public static GestureManager getInstance() {
		if(instance == null) {
			instance = new GestureManager();
		}
		
		return instance;
	}
	
	public void addGesture(IGesture gesture, boolean override) {
		String gestureId = gesture.getGestureId();
		if(!gesturesMap.containsKey(gestureId)) {
			if(gesture.onStart()) {
				JsonObject wrapperObject = new JsonObject();
				wrapperObject.addProperty("event", "add_gesture_proxy");
				wrapperObject.addProperty("gestureId", gestureId);
				wrapperObject.addProperty("instanceId", gesture.getInstanceId());
				wrapperObject.addProperty("override", override);
				TopicClient.getInstance().publish("gesture-manager", wrapperObject, false);
				gesturesMap.put(gestureId, gesture);
				overrideMap.put(gestureId, override);
				System.out.println("Added gesture: " + gestureId);
			}
		}
	}
	
	public void removeGesture(IGesture gesture) {
		String gestureId = gesture.getGestureId();
		if(gesturesMap.containsKey(gestureId)) {
			if(gesture.onStop()) {
				gesturesMap.remove(gestureId);
				overrideMap.remove(gestureId);
				JsonObject wrapperObject = new JsonObject();
				wrapperObject.addProperty("event", "remove_gesture_proxy");
				wrapperObject.addProperty("gestureId", gestureId);
				wrapperObject.addProperty("instanceId", gesture.getInstanceId());
				TopicClient.getInstance().publish("gesture-manager", wrapperObject, false);
				System.out.println("Removed gesture: " + gestureId);
			}
		}
	}

	public void onEvent(String event) {
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		String gestureId = wrapperObject.get("gestureId").getAsString();
		String instanceId = wrapperObject.get("instanceId").getAsString();
		String eventName = wrapperObject.get("event").getAsString();
		IGesture gesture = gesturesMap.get(gestureId);
		if(gesture == null) {
			System.out.println("Failed to find gesture: " + gestureId);
			return;
		}
		
		if(eventName.equals("execute_gesture")) {
			JsonObject paramsObject = wrapperObject.get("params").getAsJsonObject();
			if(!gesture.execute(paramsObject)) {
				System.out.println("Failed to execute gesture: " + gestureId);
			}
		}
		else if(eventName.equals("abort_gesture")) {
			if(!gesture.abort()) {
				System.out.println("Failed to abort gesture: " + gestureId);
			}
		}
		else {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty("failed_event", eventName);
			failedObject.addProperty("event", "error");
			TopicClient.getInstance().publish("gesture-manager", failedObject, false);
		}
	}
	
	public void onGestureDone(IGesture gesture, boolean error) {
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("event", "execute_done");
		wrapperObject.addProperty("gestureId", gesture.getGestureId());
		wrapperObject.addProperty("instanceId", gesture.getInstanceId());
		wrapperObject.addProperty("error", error);
		TopicClient.getInstance().publish("gesture-manager", wrapperObject, false);
	}
}
