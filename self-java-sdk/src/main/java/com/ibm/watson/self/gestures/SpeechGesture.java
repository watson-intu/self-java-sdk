package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;
import com.ibm.watson.self.sensors.ISensor;
import com.ibm.watson.self.sensors.SensorManager;

public class SpeechGesture implements IGesture {

	private String instanceId;
	
	public SpeechGesture() {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
	}
	
	public String getGestureId() {
		return "tts";
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
		String text  = params.get("text").getAsString();
		String gender = params.get("gender").getAsString();
		String language = params.get("language").getAsString();
		ISensor sensor = SensorManager.getInstance().findSensor("AudioData");
		if(sensor != null)
			sensor.onPause();
		System.out.println("SAY: " + text);
		// TODO: Send text to android speak method
		// mTTS.speak(text):
		GestureManager.getInstance().onGestureDone(this, false);
		if(sensor != null)
			sensor.onResume();
		return true;
	}

	public boolean abort() {
		return true;
	}

}
