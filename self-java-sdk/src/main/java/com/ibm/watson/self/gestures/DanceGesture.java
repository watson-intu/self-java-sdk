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

public class DanceGesture implements IGesture {

	private String instanceId;
	
	public DanceGesture() {
		UUID uuid = UUID.randomUUID();
        instanceId = uuid.toString();
	}
	
	public String getGestureId() {
		return "dance";
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
		int bpm = params.get("BPM").getAsInt();
		// TODO: Execute dance gesture
		return true;
	}

	public boolean abort() {
		return true;
	}

}
