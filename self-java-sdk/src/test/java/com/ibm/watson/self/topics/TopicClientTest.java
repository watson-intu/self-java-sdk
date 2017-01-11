package com.ibm.watson.self.topics;

import com.ibm.watson.self.constants.SelfConfigurationConstants;
import com.ibm.watson.self.gestures.AnimateGesture;
import com.ibm.watson.self.gestures.DisplayGesture;
import com.ibm.watson.self.gestures.GestureManager;
import com.ibm.watson.self.gestures.SpeechGesture;
import com.ibm.watson.self.sensors.MicrophoneSensor;
import com.ibm.watson.self.sensors.SensorManager;

public class TopicClientTest {

	
	
	public static void main(String[] args) {
		TopicClient client = TopicClient.getInstance();
		client.setHeaders(SelfConfigurationConstants.SELF_ID, 
				SelfConfigurationConstants.TOKEN);
		client.connect(SelfConfigurationConstants.HOST, 
				SelfConfigurationConstants.PORT);
		while(!client.isConnected()) {
			try {
				System.out.println("Client not connected yet");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		DisplayGesture gesture = new DisplayGesture();
		SpeechGesture speech = new SpeechGesture();
		AnimateGesture animate = new AnimateGesture();
		GestureManager.getInstance().addGesture(gesture, true);
		GestureManager.getInstance().addGesture(speech, true);
		GestureManager.getInstance().addGesture(animate, true);
		MicrophoneSensor sensor = new MicrophoneSensor();
		SensorManager.getInstance().addSensor(sensor, true);
		int i = 0;
		while(i < 60) {
			try {
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Clean up
		GestureManager.getInstance().removeGesture(gesture);
		GestureManager.getInstance().removeGesture(speech);
		GestureManager.getInstance().removeGesture(animate);
		GestureManager.getInstance().shutdown();
		
		sensor.onStop();
		SensorManager.getInstance().removeSensor(sensor);
		SensorManager.getInstance().shutdown();
	}
}
