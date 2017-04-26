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
}
