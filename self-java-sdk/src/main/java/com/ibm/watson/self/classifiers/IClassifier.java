package com.ibm.watson.self.classifiers;

public interface IClassifier {
	/**
	 * This should return a text name for this classifier
	 * @return
	 */
	public String getClassifierName();
	
	/**
	 * This should return a unique id for the classifier
	 * @return - unique id
	 */
	public String getClassifierId();
	
	/**
	 * This is invoked when this classifier should start
	 * @return - if it started
	 */
	public boolean onStart();
	
	/**
	 * This is invoked when the classifier should stop
	 * @return - if it stopped
	 */
	public boolean onStop();  

}
