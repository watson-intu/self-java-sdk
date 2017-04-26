package com.ibm.watson.self.extractors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class FeatureManager implements IEvent {

	public static FeatureManager instance = null;
	
	private HashMap<String, IFeatureExtractor> extractorMap = new HashMap<String, IFeatureExtractor>();
	private HashMap<String, Boolean> overrideMap = new HashMap<String, Boolean>();
	
	private static Logger logger = LogManager.getLogger(FeatureManager.class.getName());
	private boolean started;
	
	public FeatureManager() {
		TopicClient.getInstance().subscribe("feature-manager", this);
		started = true;
	}
	
	public static FeatureManager getInstance() {
		if(instance == null) {
			instance = new FeatureManager();
		}
		
		return instance;
	}
	
	public boolean isRegistered(IFeatureExtractor extractor) {
		logger.entry(extractor);
		return logger.exit(extractorMap.containsKey(extractor.getFeatureExtractorId()));
	}
	
	/**
	 * Will add a feature extractor
	 * @param extractor - extractor to be added
	 * @param override - defines if it should override another extractor
	 */
	public void addFeatureExtractor(IFeatureExtractor extractor, boolean override) {
		logger.entry();
		if(!extractorMap.containsKey(extractor.getFeatureExtractorId())) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(FeatureExtractorConstants.EVENT, 
					FeatureExtractorConstants.ADD_EXTRACTOR_PROXY);
			wrapperObject.addProperty(FeatureExtractorConstants.EXTRACTOR_ID, 
					extractor.getFeatureExtractorId());
			wrapperObject.addProperty(FeatureExtractorConstants.NAME, 
					extractor.getFeatureExtractorName());
			wrapperObject.addProperty(FeatureExtractorConstants.OVERRIDE, override);
			TopicClient.getInstance().publish(FeatureExtractorConstants.FEATURE_MANAGER,
					wrapperObject.toString(), false);
			extractorMap.put(extractor.getFeatureExtractorId(), extractor);
			overrideMap.put(extractor.getFeatureExtractorId(), override);
		}
		logger.exit();
	}
	
	/**
	 * unregisters feature extractor from self instance
	 * @param extractor - extractor to be removed
	 */
	public void removeFeatureExtractor(IFeatureExtractor extractor) {
		logger.entry();
		
		if(extractorMap.containsKey(extractor.getFeatureExtractorId())) {
			overrideMap.remove(extractor.getFeatureExtractorId());
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(FeatureExtractorConstants.EVENT, 
					FeatureExtractorConstants.REMOVE_EXTRACTOR_PROXY);
			wrapperObject.addProperty(FeatureExtractorConstants.EXTRACTOR_ID,
					extractor.getFeatureExtractorId());
			TopicClient.getInstance().publish(FeatureExtractorConstants.FEATURE_MANAGER,
					wrapperObject.toString(), false);
		}
		
		logger.exit();
	}
	
	
	
	public void onEvent(String event) {
		logger.entry(event);
		
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		String extractorId = wrapperObject.get(FeatureExtractorConstants.EXTRACTOR_ID).getAsString();
		if(!extractorMap.containsKey(extractorId)) {
			logger.error("Failed to find feature extractor id!");
			return;
		}
		IFeatureExtractor extractor = extractorMap.get(extractorId);
		boolean failed = false;
		String eventName = wrapperObject.get(FeatureExtractorConstants.EVENT).getAsString();
		if(eventName.equals(FeatureExtractorConstants.START_EXTRACTOR)) {
			if(!extractor.onStart()) {
				logger.error("Failed to start extractor: " + extractorId);
				failed = true;
			}
		}
		else if(eventName.equals(FeatureExtractorConstants.STOP_EXTRACTOR)) {
			if(!extractor.onStop()) {
				logger.error("Failed to stop extractor: " + extractorId);
				failed = true;
			}
		}
		
		if(failed) {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty(FeatureExtractorConstants.FAILED_EVENT, eventName);
			failedObject.addProperty(FeatureExtractorConstants.EVENT, 
					FeatureExtractorConstants.ERROR);
			TopicClient.getInstance().publish(FeatureExtractorConstants.FEATURE_MANAGER,
					failedObject.toString(), false);
		}
		
		logger.exit();
	}

	public boolean isActive() {
		return started;
	}

	public void shutdown() {
		TopicClient.getInstance().unsubscribe("feature-manager", this);
		started = false;
		
	}

	public void onDisconnect() {
		logger.entry();
		
		for(String extractorId : extractorMap.keySet()) {
			IFeatureExtractor extractor = extractorMap.get(extractorId);
			extractor.onStop();
		}
		
		logger.exit();
	}

	public void onReconnect() {
		logger.entry();
		
		for(String extractorId : extractorMap.keySet()) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(FeatureExtractorConstants.EVENT, 
					FeatureExtractorConstants.ADD_EXTRACTOR_PROXY);
			wrapperObject.addProperty(FeatureExtractorConstants.EXTRACTOR_ID, 
					extractorId);
			wrapperObject.addProperty(FeatureExtractorConstants.NAME, 
					extractorMap.get(extractorId).getFeatureExtractorName());
			wrapperObject.addProperty(FeatureExtractorConstants.OVERRIDE, 
					overrideMap.get(extractorId));
			TopicClient.getInstance().publish(FeatureExtractorConstants.FEATURE_MANAGER,
					wrapperObject.toString(), false);
		}
		
		logger.exit();
	}

}
