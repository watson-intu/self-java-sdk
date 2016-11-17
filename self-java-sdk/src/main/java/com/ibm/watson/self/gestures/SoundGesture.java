package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;

public class SoundGesture implements IGesture {

	private String instanceId;
	
	public SoundGesture() {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
	}
	
	public String getGestureId() {
		return "sound";
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
		String sound  = params.get("sound").getAsString();
		String path = "./etc/" + sound;
		
		// TODO: Play the wav file - path
		
		GestureManager.getInstance().onGestureDone(this, false);
		return true;
	}

	public boolean abort() {
		return true;
	}

}
