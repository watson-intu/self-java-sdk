package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;

public class DanceGesture implements IGesture {

	private String instanceId;
	
	public DanceGesture() {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
	}
	
	public String getGestureId() {
		return "dance";
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
		int bpm = params.get("BPM").getAsInt();
		// TODO: Execute dance gesture
		return true;
	}

	public boolean abort() {
		return true;
	}

}
