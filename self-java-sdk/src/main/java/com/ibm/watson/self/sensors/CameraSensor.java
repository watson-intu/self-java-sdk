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
