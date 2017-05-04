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

package com.ibm.watson.self.gestures;

import java.util.UUID;

import com.google.gson.JsonObject;

/**
 * This gesture wraps the local display so that Intu can display to the user
 */
public class DisplayGesture implements IGesture {

	private String instanceId;
	
	public DisplayGesture() {
		 UUID uuid = UUID.randomUUID();
		 instanceId = uuid.toString();
	}
	
	public String getGestureId() {
		return "display";
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
		String type = params.get("display").getAsString();
		String data = params.get("data").getAsString();
		System.out.println("Executing Display Gesture!!");
		System.out.println("Displaying: " + type);
		System.out.println("Data is: " + data);
		GestureManager.getInstance().onGestureDone(this, false);
		return true;
	}

	public boolean abort() {
		return true;
	}

}
