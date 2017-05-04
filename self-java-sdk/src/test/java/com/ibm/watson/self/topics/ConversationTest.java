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

package com.ibm.watson.self.topics;

import java.util.Scanner;

import com.google.gson.JsonObject;
import com.ibm.watson.self.blackboard.IThing;
import com.ibm.watson.self.constants.SelfConfigurationConstants;
import com.ibm.watson.self.gestures.AnimateGesture;
import com.ibm.watson.self.gestures.DisplayGesture;
import com.ibm.watson.self.gestures.GestureManager;
import com.ibm.watson.self.gestures.SpeechGesture;

public class ConversationTest {

	private boolean isRunning = false;
	private static String host = null;
	private static String port = null;
	
	public static void main(String[] args) {
		
		if(args.length == 2) {
			host = args[0];
			port = args[1];
		}
		new ConversationTest();
			
	}
	
	private boolean connectToIntu() {
		TopicClient client = TopicClient.getInstance();
		client.setHeaders(SelfConfigurationConstants.SELF_ID, 
				SelfConfigurationConstants.TOKEN);
		if(host != null) {
			client.connect(host, port);
		}
		else {
			client.connect(SelfConfigurationConstants.HOST, 
					SelfConfigurationConstants.PORT);
		}
		return true;
	}
	
	public ConversationTest() {
		connectToIntu();
		DisplayGesture gesture = new DisplayGesture();
		SpeechGesture speech = new SpeechGesture();
		AnimateGesture animate = new AnimateGesture();
		GestureManager.getInstance().addGesture(gesture, true);
		GestureManager.getInstance().addGesture(speech, true);
		GestureManager.getInstance().addGesture(animate, true);
		isRunning = true;
		Thread t = new Thread(new TextInput());
		t.start();
	}
	
	public void sendData(String text) {
		IThing thing = new IThing();
		JsonObject bodyObject = new JsonObject();
		bodyObject.addProperty("m_Text", text);
		bodyObject.addProperty("m_LocalDialog", false);
		bodyObject.addProperty("m_ClassifyIntent", true);
		bodyObject.addProperty("m_Language", "en-US");
		thing.setBody(bodyObject);
//		BlackBoard.getInstance().addThing(thing, path);
		TopicClient.getInstance().publish("conversation", text, false);
	}
	
	class TextInput implements Runnable {

		public void run() {
			Scanner keyboard = new Scanner(System.in);

			while(isRunning) {
				System.out.println("Enter something to say: ");
				String text = keyboard.nextLine();
				sendData(text);
			}
		}
		
	}
}
