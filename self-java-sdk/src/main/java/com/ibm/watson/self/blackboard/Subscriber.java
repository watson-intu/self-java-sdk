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
	
}
