package com.ibm.watson.self.topics;

import java.io.IOException;

import javax.websocket.DeploymentException;

import com.ibm.watson.self.constants.SelfConfigurationConstants;

public class TopicClientTest {

	public static void main(String[] args) {
		TopicClient client = new TopicClient();
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
		
	}
}
