package com.ibm.watson.self.sensors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.agents.AgentSociety;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class SensorManager implements IEvent {

	private static SensorManager instance = null;
	private boolean started = false;
	private HashMap<String, ISensor> sensorMap = new HashMap<String, ISensor>();
	private HashMap<String, Boolean> overridesMap = new HashMap<String, Boolean>();
	private HashMap<ILocalSensorSubscriber, String> subscriberMap = new HashMap<ILocalSensorSubscriber, String>();
	
	private static Logger logger = LogManager.getLogger(SensorManager.class.getName());
	
	public SensorManager() {
		TopicClient.getInstance().subscribe(SensorConstants.SENSOR_MANAGER, this);
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
		logger.entry();
		if(!sensorMap.containsKey(sensor.getSensorId())) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(SensorConstants.EVENT, SensorConstants.ADD_SENSOR_PROXY);
			wrapperObject.addProperty(SensorConstants.SENSOR_ID, sensor.getSensorId());
			wrapperObject.addProperty(SensorConstants.NAME, sensor.getSensorName());
			wrapperObject.addProperty(SensorConstants.DATA_TYPE, sensor.getDataType());
			wrapperObject.addProperty(SensorConstants.BINARY_TYPE, sensor.getBinaryType());
			wrapperObject.addProperty(SensorConstants.OVERRIDE, override);
			TopicClient.getInstance().publish(SensorConstants.SENSOR_MANAGER, 
					wrapperObject.toString(), false);
			sensorMap.put(sensor.getSensorId(), sensor);
			overridesMap.put(sensor.getSensorId(), override);
			logger.info("Adding sensor id: " + sensor.getSensorId());
		}
		logger.exit();
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
		logger.entry();
		if(!isRegistered(sensor)) {
			logger.error("SendData() invoked on unregistered sensor: " + sensor.getSensorId());
		}
		else {
			TopicClient.getInstance().publish(SensorConstants.SENSOR_PROXY + sensor.getSensorId(), 
					data, false);
		}
		
		for(ILocalSensorSubscriber key : subscriberMap.keySet())
		{
			if(sensor.getDataType().equals(subscriberMap.get(key))) {
				key.getBinaryData(data);
			}
		}
		logger.exit();
	}
	
	/**
	 * Remove a sensor with the remote self instance
	 * @param sensor - sensor to remove
	 */
	public void removeSensor(ISensor sensor) {
		logger.entry();
		if(sensorMap.containsKey(sensor.getSensorId())) {
			sensorMap.remove(sensor.getSensorId());
			overridesMap.remove(sensor.getSensorId());
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(SensorConstants.EVENT, SensorConstants.REMOVE_SENSOR_PROXY);
			wrapperObject.addProperty(SensorConstants.SENSOR_ID, sensor.getSensorId());
			TopicClient.getInstance().publish(SensorConstants.SENSOR_MANAGER, 
					wrapperObject.toString(), false);
		}
		logger.exit();
	}
	
	/**
	 * Find an ISensor based on dataType it has
	 * @param dataType - type of data sensor produces
	 * @return - the instance of the sensor
	 */
	public ISensor findSensor(String dataType) {
		logger.entry();
		
		for(String sensorId : sensorMap.keySet()) {
			ISensor sensor = sensorMap.get(sensorId);
			if(sensor.getDataType().equals(dataType)) {
				return sensor;
			}
		}
		
		return logger.exit(null);
	}
	
	/**
	 * Register with local sensors to receive data
	 * @param photographyAgent
	 * @param string
	 */
	public void registerWithLocalSensor(ILocalSensorSubscriber subscriber, String dataType) {
		subscriberMap.put(subscriber, dataType);
	}
	
	public void unregisterWithLocalSensor(ILocalSensorSubscriber subscriber) {
		subscriberMap.remove(subscriber);
	}

	public void onEvent(String event) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		String eventName = wrapperObject.get(SensorConstants.EVENT).getAsString();
		String sensorId = wrapperObject.get(SensorConstants.SENSOR_ID).getAsString();
		ISensor sensor = sensorMap.get(sensorId);
		if(sensor == null) {
			logger.error("Failed to find sensor: " + sensorId);
			return;
		}
		boolean error = false;
		if(eventName.equals(SensorConstants.START_SENSOR)) {
			if(!sensorMap.get(sensorId).onStart()) {
				logger.error("Failed to start sensor!");
				error = true;
			}
		}
		else if(eventName.equals(SensorConstants.STOP_SENSOR)) {
			if(!sensorMap.get(sensorId).onStop()) {
				logger.error("Failed to stop sensor!");
				error = true;
			}
		}
		else if(eventName.equals(SensorConstants.PAUSE_SENSOR)) {
			sensorMap.get(sensorId).onPause();
		}
		else if(eventName.equals(SensorConstants.RESUME_SENSOR)) {
			sensorMap.get(sensorId).onResume();
		}
		
		if(error) {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty(SensorConstants.FAILED_EVENT, eventName);
			failedObject.addProperty(SensorConstants.EVENT, SensorConstants.ERROR);
			TopicClient.getInstance().publish(SensorConstants.SENSOR_MANAGER, 
					failedObject.toString(), false);
		}
		logger.exit();
	}

	public boolean isActive() {
		return started;
	}

	public void shutdown() {
		TopicClient.getInstance().unsubscribe(SensorConstants.SENSOR_MANAGER, this);
		started = false;
		
	}
}
