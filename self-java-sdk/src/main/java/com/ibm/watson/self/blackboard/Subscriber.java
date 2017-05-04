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

import com.ibm.watson.self.blackboard.IThing.ThingEventType;

/**
 * A data structure to represent a subscriber's callback, path, and the event it is interested in
 */
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
