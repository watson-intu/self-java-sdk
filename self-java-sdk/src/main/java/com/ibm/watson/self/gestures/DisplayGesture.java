package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;

public class DisplayGesture implements IGesture {

	public String getGestureId() {
		return "SelfDisplayGesture";
	}

	public String getInstanceId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
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
		System.out.println("Displaying: " + type);
		System.out.println("Data is: " + data);
		GestureManager.getInstance().onGestureDone(this, false);
		return true;
	}

	public boolean abort() {
		return true;
	}

}
