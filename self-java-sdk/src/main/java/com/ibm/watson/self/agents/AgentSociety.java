package com.ibm.watson.self.agents;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

public class AgentSociety implements IEvent {

	public static AgentSociety instance = null;
	private boolean started = false;
	
	private HashMap<String, IAgent> agentMap = new HashMap<String, IAgent>();
	private HashMap<String, Boolean> overrideMap = new HashMap<String, Boolean>();
	
	private static Logger logger = LogManager.getLogger(AgentSociety.class.getName());
	
	public AgentSociety() {
		TopicClient.getInstance().subscribe("agent-society", this);
		started = true;
	}
	
	public static AgentSociety getInstance() {
		if(instance == null) {
			instance = new AgentSociety();
		}
		
		return instance;
	}
	
	public boolean isRegistered(IAgent agent) {
		return agentMap.containsKey(agent.getAgentId());
	}
	
	public void addAgent(IAgent agent, boolean override) {
		logger.entry();
		if(!agentMap.containsKey(agent.getAgentId())) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("event", "add_agent_proxy");
			wrapperObject.addProperty("agentId", agent.getAgentId());
			wrapperObject.addProperty("name", agent.getAgentName());
			wrapperObject.addProperty("override", override);
			TopicClient.getInstance().publish("agent-society", wrapperObject.toString(), false);
			agentMap.put(agent.getAgentId(), agent);
			overrideMap.put(agent.getAgentId(), override);
		}
		logger.exit();
	}
	
	public void removeAgent(IAgent agent) {
		logger.entry();
		if(agentMap.containsKey(agent.getAgentId())) {
			overrideMap.remove(agent.getAgentId());
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty("event", "remove_agent_proxy");
			wrapperObject.addProperty("agentId", agent.getAgentId());
			TopicClient.getInstance().publish("agent-society", wrapperObject.toString(), false);
		}
		logger.exit();
	}

	public void onEvent(String event) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		String agentId = wrapperObject.get("agentId").getAsString();
		IAgent agent = agentMap.get(agentId);
		if(agent == null) {
			logger.info("Failed to find agent!");
			return;
		}
		
		boolean failed = false;
		String eventName = wrapperObject.get("event").getAsString();
		if(eventName.equals("start_agent")) {
			if(!agent.onStart()) {
				logger.info("Failed to start agent: " + agent.getAgentName());
				failed = true;
			}
		}
		else if(eventName.equals("stop_agent")) {
			if(!agent.onStop()) {
				logger.info("Failed to stop agent: " + agent.getAgentName());
				failed = true;
			}
			agentMap.remove(agent.getAgentId());
		}
		
		if(failed) {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty("failed_event", eventName);
			failedObject.addProperty("event", "error");
			TopicClient.getInstance().publish("agent-society", wrapperObject.toString(), false);
		}
		logger.exit();
	}

	public boolean isActive() {
		return started;
	}

	public void shutdown() {
		started = false;
		TopicClient.getInstance().unsubscribe("agent-society", this);
	}
}
