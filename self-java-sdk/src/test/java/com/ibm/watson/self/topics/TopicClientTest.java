package com.ibm.watson.self.topics;

import java.io.IOException;

import javax.websocket.DeploymentException;

import com.ibm.watson.self.constants.SelfConfigurationConstants;
import com.ibm.watson.self.gestures.DisplayGesture;
import com.ibm.watson.self.gestures.GestureManager;
import com.ibm.watson.self.sensors.MicrophoneSensor;
import com.ibm.watson.self.sensors.SensorManager;

public class TopicClientTest {

	
	
	public static void main(String[] args) {
		TopicClient client = TopicClient.getInstance();
		client.setHeaders(SelfConfigurationConstants.SELF_ID, 
				SelfConfigurationConstants.TOKEN);
		try {
			client.connect(SelfConfigurationConstants.HOST, 
					SelfConfigurationConstants.PORT);
		} catch (DeploymentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DisplayGesture gesture = new DisplayGesture();
		GestureManager.getInstance().addGesture(gesture, true);
		MicrophoneSensor sensor = new MicrophoneSensor();
		SensorManager.getInstance().addSensor(sensor, true);
		if(sensor.onStart())
			System.out.println("Starting Microphone!");
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
		GestureManager.getInstance().shutdown();
		
		sensor.onStop();
		SensorManager.getInstance().removeSensor(sensor);
		SensorManager.getInstance().shutdown();
	}
}
