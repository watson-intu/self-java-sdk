package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;

public class SpeechGesture implements IGesture {

	public String getGestureId() {
		return "tts";
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
		String text  = params.get("text").getAsString();
		String gender = params.get("gender").getAsString();
		String language = params.get("language").getAsString();
		// TODO: Send text to android speak method
		// mTTS.speak(text):
		return true;
	}

	public boolean abort() {
		return true;
	}

}
