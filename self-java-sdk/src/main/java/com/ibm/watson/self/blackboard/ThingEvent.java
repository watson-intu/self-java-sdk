package com.ibm.watson.self.blackboard;

import com.google.gson.JsonObject;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;

public class ThingEvent {

	private ThingEventType eventType;
	private JsonObject event;
	private IThing thing;
	
	public ThingEventType getEventType() {
		return eventType;
	}
	
	public void setEventType(ThingEventType eventType) {
		this.eventType = eventType;
	}
	
	public JsonObject getEvent() {
		return event;
	}
	
	public void setEvent(JsonObject event) {
		this.event = event;
	}
	
	public IThing getThing() {
		return thing;
	}
	
	public void setThing(IThing thing) {
		this.thing = thing;
	}
	
	public String toString() {
		return eventType.toString();
	}
	
}
