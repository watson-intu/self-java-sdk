package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;

/**
 * This gesture wraps the local display so that Intu can display to the user
 */
public class DisplayGesture implements IGesture {

	private String instanceId;
	
	public DisplayGesture() {
		 UUID uuid = UUID.randomUUID();
		 instanceId = uuid.toString();
	}
	
	public String getGestureId() {
		return "display";
	}

	public String getInstanceId() {
		return instanceId;
	}

	public boolean onStart() {
		return true;
	}

	public boolean onStop() {
		return true;
	}

	public boolean execute(JsonObject params) {
		String type = params.get("display").getAsString();
		String data = params.get("data").getAsString();
		System.out.println("Executing Display Gesture!!");
		System.out.println("Displaying: " + type);
		System.out.println("Data is: " + data);
		GestureManager.getInstance().onGestureDone(this, false);
		return true;
	}

	public boolean abort() {
		return true;
	}

}
