/**
* Copyright 2016 IBM Corp. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package com.ibm.watson.self.blackboard;

import com.google.gson.JsonObject;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;

/**
 * Represents an add, remove, or state change (and others) event that can happen to a thing
 */
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
