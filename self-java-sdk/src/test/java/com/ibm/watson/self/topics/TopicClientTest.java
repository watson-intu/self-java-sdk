package com.ibm.watson.self.topics;

import java.io.IOException;

import javax.websocket.DeploymentException;

import com.ibm.watson.self.constants.SelfConfigurationConstants;
import com.ibm.watson.self.gestures.DisplayGesture;
import com.ibm.watson.self.gestures.GestureManager;

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
		
		GestureManager.getInstance().addGesture(new DisplayGesture(), true);
		int i = 0;
		while(i < 30) {
			try {
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		GestureManager.getInstance().shutdown();
	}
}
