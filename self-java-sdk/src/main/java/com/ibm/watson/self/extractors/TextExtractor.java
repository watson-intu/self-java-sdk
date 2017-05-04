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

import java.util.UUID;

import com.ibm.watson.self.sensors.SensorManager;

/**
 * Example of how to implement an extractor.
 * This one will subscribe to AudioData that is
 * produced from the microphone and logic is
 * stubbed out to send audio data to a STT service
 *
 */
public class TextExtractor implements IFeatureExtractor {

	private String featureName;
	private String instanceId;
	private String binaryData;
	private UUID uuid = UUID.randomUUID();
	
	public TextExtractor() {
		this.featureName = "TextExtractor";
		this.instanceId = uuid.toString();
		this.binaryData = "AudioData";
	}
	
	public String getFeatureExtractorName() {
		return this.featureName;
	}

	public String getFeatureExtractorId() {
		return this.instanceId;
	}

	public String getBinaryData() {
		return this.binaryData;
	}

	public boolean onStart() {
		SensorManager.getInstance().addSubscriber(this);
		return true;
	}

	public boolean onStop() {
		SensorManager.getInstance().removeSubscriber(this);
		return true;
	}

	public void onData(byte[] data) {
		System.out.println("Received binary data: " + data.length);
	}

}
