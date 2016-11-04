package com.ibm.watson.self.sensors;

import java.util.UUID;

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

	public boolean onStart() {
		return true;
	}

	public boolean onStop() {
		return true;
	}

	public void onPause() {
		isPaused = true;
	}

	public void onResume() {
		isPaused = false;
	}
	
	public void sendData(byte[] buffer) {
		SensorManager.getInstance().sendData(this, buffer);
	}

}
