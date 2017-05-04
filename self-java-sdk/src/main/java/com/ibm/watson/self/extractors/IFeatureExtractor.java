/**
* Copyright 2016 IBM Corp. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

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
