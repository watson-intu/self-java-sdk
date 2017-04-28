package com.ibm.watson.self.classifiers;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class ClassifierManager implements IEvent{

	public static ClassifierManager instance = null;
	private boolean active = false;
	
	private HashMap<String, IClassifier> classifierMap = new HashMap<String, IClassifier>();
	private HashMap<String, Boolean> overrideMap = new HashMap<String, Boolean>();
	
	private static Logger logger = LogManager.getLogger(ClassifierManager.class.getName());
	
	public ClassifierManager() {
		TopicClient.getInstance().subscribe("classifier-manager", this);
		active = true;
	}
	
	public static ClassifierManager getInstance() {
		if(instance == null) {
			instance = new ClassifierManager();
		}
		
		return instance;
	}
	
	public boolean isRegistered(IClassifier classifier) {
		return classifierMap.containsKey(classifier.getClassifierId());
	}

	/**
	 * Add a classifier to this manager. The manager taken ownership of the classifier
	 * @param classifier: The classifier object
	 * @param override: if true, then any classifier with same id will be replaced and if false,
	 * it will execute alongside existing classifiers with same id
	 */
	public void addClassifier(IClassifier classifier, boolean override) {
		logger.entry();
		if(!classifierMap.containsKey(classifier.getClassifierId())) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(ClassifierConstants.EVENT, ClassifierConstants.ADD_CLASSIFIER_PROXY);
			wrapperObject.addProperty(ClassifierConstants.CLASSIFIER_ID, classifier.getClassifierId());
			wrapperObject.addProperty(ClassifierConstants.NAME, classifier.getClassifierName());
			wrapperObject.addProperty(ClassifierConstants.OVERRIDE, override);
			TopicClient.getInstance().publish(ClassifierConstants.CLASSIFIER_MANAGER, wrapperObject.toString(), false);
			classifierMap.put(classifier.getClassifierId(), classifier);
			overrideMap.put(classifier.getClassifierId(), override);
		}
		logger.exit();
	}
	
	/**
	 * Remove a classifier from this manager
	 * @param classifier: classifier object
	 */
	public void removeClassifier(IClassifier classifier) {
		logger.entry();
		if(classifierMap.containsKey(classifier.getClassifierId())) {
			overrideMap.remove(classifier.getClassifierId());
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(ClassifierConstants.EVENT, ClassifierConstants.REMOVE_CLASSIFIER_PROXY);
			wrapperObject.addProperty(ClassifierConstants.CLASSIFIER_ID, classifier.getClassifierId());
			TopicClient.getInstance().publish(ClassifierConstants.CLASSIFIER_MANAGER, wrapperObject.toString(), false);
		}
		logger.exit();
	}
	
    /**
     * Callback that can add or remove proxy classifiers to and from the manager
     * @param event: String representation of the event which can be parsed to a json object
     */
	public void onEvent(String event) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		String classifierId = wrapperObject.get(ClassifierConstants.CLASSIFIER_ID).getAsString();
		IClassifier classifier = classifierMap.get(classifierId);
		if(classifier == null) {
			logger.info("Failed to find the classifier!");
			return;
		}
		
		boolean failed = false;
		String eventName = wrapperObject.get(ClassifierConstants.EVENT).getAsString();
		if(eventName.equals(ClassifierConstants.START_CLASSIFIER)) {
			if(!classifier.onStart()) {
				logger.info("Failed to start classifier: " + classifier.getClassifierName());
				failed = true;
			}
		}
		else if(eventName.equals(ClassifierConstants.STOP_CLASSIFIER)) {
			if(!classifier.onStop()) {
				logger.info("Failed to stop classifier: " + classifier.getClassifierName());
				failed = true;
			}
			classifierMap.remove(classifier.getClassifierId());
		}
		
		if(failed) {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty(ClassifierConstants.FAILED_EVENT, eventName);
			failedObject.addProperty(ClassifierConstants.EVENT, ClassifierConstants.ERROR);
			TopicClient.getInstance().publish(ClassifierConstants.CLASSIFIER_MANAGER, wrapperObject.toString(), false);
		}
		logger.exit();
	}

	public boolean isActive() {
		return active;
	}

	public void shutdown() {
		active = false;
		TopicClient.getInstance().unsubscribe(ClassifierConstants.CLASSIFIER_MANAGER, this);
	}

	public void onDisconnect() {
		for (String classifierId : classifierMap.keySet()) {
			IClassifier classifier = classifierMap.get(classifierId);
			classifier.onStop();
		}
	}

	public void onReconnect() {
		for (String classifierId : classifierMap.keySet()) {
			JsonObject wrapperObject = new JsonObject();
			IClassifier classifier = classifierMap.get(classifierId);
			wrapperObject.addProperty(ClassifierConstants.EVENT, ClassifierConstants.ADD_CLASSIFIER_PROXY);
			wrapperObject.addProperty(ClassifierConstants.CLASSIFIER_ID, classifier.getClassifierId());
			wrapperObject.addProperty(ClassifierConstants.NAME, classifier.getClassifierName());
			wrapperObject.addProperty(ClassifierConstants.OVERRIDE, overrideMap.get(classifierId));
			TopicClient.getInstance().publish(ClassifierConstants.CLASSIFIER_MANAGER, wrapperObject.toString(), false);
		}
	}
}
