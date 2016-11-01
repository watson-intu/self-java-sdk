package com.ibm.watson.self.sensors;

public class SensorManager {

	private static SensorManager instance = null;
	
	public SensorManager() {
		
	}
	
	public static SensorManager getInstance() {
		if(instance == null) {
			instance = new SensorManager();
		}
		
		return instance;
	}
}
