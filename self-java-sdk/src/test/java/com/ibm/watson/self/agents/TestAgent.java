package com.ibm.watson.self.agents;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.self.constants.SelfConfigurationConstants;
import com.ibm.watson.self.topics.TopicClient;

public class TestAgent {

	private static String host = null;
	private static String port = null;
	
	private static Logger logger = LogManager.getLogger(TestAgent.class.getName());
	
	public static void main(String[] args) {
		
		if(args.length == 2) {
			host = args[0];
			port = args[1];
		}
		new TestAgent();
			
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
	
	public TestAgent() {
		boolean isConnected = connectToIntu();
		if(!isConnected) {
			logger.error("Cannot connect to Intu!! Shutting down...");
			return;
		}
		GameAgent agent = new GameAgent();
		AgentSociety.getInstance().addAgent(agent, false);
		
		int i = 0;
		while(i < 60) {
			try {
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		AgentSociety.getInstance().removeAgent(agent);
	}
}
