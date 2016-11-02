package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;

public class AnimateGesture implements IGesture {

	private String instanceId;
	private String gestureId;
	
	public AnimateGesture() {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
        gestureId = "show_laugh";
	}
	
	public AnimateGesture(String gestureId) {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
        this.gestureId = gestureId;
	}
	
	public String getGestureId() {
		return gestureId;
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
		System.out.println("LAUGHING NOW!");
		GestureManager.getInstance().onGestureDone(this, false);
		return true;
	}

	public boolean abort() {	
		return true;
	}

}
