package com.ibm.watson.self.sensors;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class SensorManager implements IEvent {

	private static SensorManager instance = null;
	private boolean started = false;
	private HashMap<String, ISensor> sensorMap = new HashMap<String, ISensor>();
	private HashMap<String, Boolean> overridesMap = new HashMap<String, Boolean>();
	
	public SensorManager() {
		TopicClient.getInstance().subscribe("sensor-manager", this);
		started = true;
	}
	
	public static SensorManager getInstance() {
		if(instance == null) {
			instance = new SensorManager();
		}
		
		return instance;
	}
	
	/**
	 * Add a sensor with the remote self instance
	 * @param sensor - sensor to add
	 * @param override - if it should stop remote self sensor or not
	 */
	public void addSensor(ISensor sensor, boolean override) {
		if(!sensorMap.containsKey(sensor.getSensorId())) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("event", "add_sensor_proxy");
			wrapperObject.addProperty("sensorId", sensor.getSensorId());
			wrapperObject.addProperty("name", sensor.getSensorName());
			wrapperObject.addProperty("data_type", sensor.getDataType());
			wrapperObject.addProperty("binary_type", sensor.getBinaryType());
			wrapperObject.addProperty("override", override);
			TopicClient.getInstance().publish("sensor-manager", wrapperObject.toString(), false);
			sensorMap.put(sensor.getSensorId(), sensor);
			overridesMap.put(sensor.getSensorId(), override);
			System.out.println("Adding sensor id: " + sensor.getSensorId());
		}
	}
	
	/**
	 * check if sensor has been added 
	 * @param sensor - sensor 
	 * @return - if it has been added or not
	 */
	public boolean isRegistered(ISensor sensor) {
		return sensorMap.containsKey(sensor.getSensorId());
	}
	
	/**
	 * publish data to topic client
	 * @param sensor - sensor to proxy
	 * @param data - raw data to be sent
	 */
	public void sendData(ISensor sensor, byte[] data) {
		if(!isRegistered(sensor)) {
			System.out.println("SendData() invoked on unregistered sensor: " + sensor.getSensorId());
		}
		else {
			TopicClient.getInstance().publish("sensor-proxy-" + sensor.getSensorId(), data, false);
		}
	}
	
	/**
	 * Remove a sensor with the remote self instance
	 * @param sensor - sensor to remove
	 */
	public void removeSensor(ISensor sensor) {
		if(sensorMap.containsKey(sensor.getSensorId())) {
			sensorMap.remove(sensor.getSensorId());
			overridesMap.remove(sensor.getSensorId());
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("event", "remove_sensor_proxy");
			wrapperObject.addProperty("sensorId", sensor.getSensorId());
			TopicClient.getInstance().publish("sensor-manager", wrapperObject.toString(), false);
		}
	}

	public void onEvent(String event) {
		System.out.println("Received Event on SensorManager: " + event);
		
	}

	public boolean isActive() {
		return started;
	}

	public void shutdown() {
		TopicClient.getInstance().unsubscribe("sensor-manager", this);
		started = false;
		
	}
}
