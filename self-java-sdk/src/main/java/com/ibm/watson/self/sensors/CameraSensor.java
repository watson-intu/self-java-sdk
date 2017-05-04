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

package com.ibm.watson.self.sensors;

import java.util.UUID;

/**
 * Represents the class that collects video data from a given embodiment
 */
public class CameraSensor implements ISensor {

	private boolean isPaused = false;
	private String sensorId;

	
	public CameraSensor() {
		UUID uuid = UUID.randomUUID();
		sensorId = uuid.toString();
	}
	
	public String getSensorId() {		
        return sensorId;
	}

	public String getSensorName() {
		return "Camera";
	}

	public String getDataType() {
		return "VideoData";
	}

	public String getBinaryType() {
		return "image/jpeg";
	}

	/**
	 * Stub representing the start of the sensor
	 */
	public boolean onStart() {
		return true;
	}

	/**
	 * Stub representing the stopping of the sensor
	 */
	public boolean onStop() {
		return true;
	}

	/**
	 * Stub representing the pausing of the sensor
	 */
	public void onPause() {
		isPaused = true;
	}

	/**
	 * Stub representing the resuming of the sensor
	 */
	public void onResume() {
		isPaused = false;
	}
	
	public void sendData(byte[] buffer) {
		SensorManager.getInstance().sendData(this, buffer);
	}

}
