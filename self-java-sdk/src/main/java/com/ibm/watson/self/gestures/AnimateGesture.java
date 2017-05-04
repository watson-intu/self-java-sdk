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
 * This gesture wraps the local animation
 */
public class AnimateGesture implements IGesture {

	private String instanceId;
	private String gestureId;
	
	public AnimateGesture() {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
        gestureId = "show_laugh";
	}
	
	public AnimateGesture(String gestureId) {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
        this.gestureId = gestureId;
	}
	
	public String getGestureId() {
		return gestureId;
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
		System.out.println("LAUGHING NOW!");
		GestureManager.getInstance().onGestureDone(this, false);
		return true;
	}

	public boolean abort() {	
		return true;
	}

}
