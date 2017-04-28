package com.ibm.watson.self.extractors;

public interface IFeatureExtractor {

	/**
	 * This returns the name of the feature extractor
	 * @return
	 */
	public String getFeatureExtractorName();
	
	/**
	 * This returns the unique id of the feature extractor
	 * @return
	 */
	public String getFeatureExtractorId();
	
	/**
	 * Returns binary data type that the extractor 
	 * subscribes to
	 * @return
	 */
	public String getBinaryData();
	
	/**
	 * This gets called when the feature extractor has
	 * registered itself and is told to start up
	 * @return
	 */	
	public boolean onStart();
	
	/**
	 * This gets called when the feature extractor has
	 * unregistered itself and is told to stop
	 * @return
	 */
	public boolean onStop();
	
	/**
	 * Callback for all extractors for when data from 
	 * a sensor is produced
	 * @param data - binary data produced by sensor
	 */
	public void onData(byte[] data);
}
