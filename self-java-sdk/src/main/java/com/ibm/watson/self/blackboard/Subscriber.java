package com.ibm.watson.self.blackboard;

import com.ibm.watson.self.blackboard.IThing.ThingEventType;

public class Subscriber {

	public IBlackBoard callback;
	public ThingEventType eventType;
	public String path;
	
	public Subscriber(IBlackBoard callback, ThingEventType eventType, String path) {
		this.callback = callback;
		this.eventType = eventType;
		this.path = path;
	}
	
	public IBlackBoard getCallback() {
		return this.callback;
	}
	
	public void setCallback(IBlackBoard callback) {
		this.callback = callback;
	}
	
	public ThingEventType getEventType() {
		return this.eventType;
	}
	
	public void setEventType(ThingEventType eventType) {
		this.eventType = eventType;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
}
