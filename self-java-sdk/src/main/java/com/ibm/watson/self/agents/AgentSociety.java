package com.ibm.watson.self.agents;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.sensors.SensorConstants;
import com.ibm.watson.self.topics.IEvent;
import com.ibm.watson.self.topics.TopicClient;

/**
 * Represents a collection of agents running at a given time
 */
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
	
	/**
	 * Add an agent to this society. The society takes ownership of the agent
	 * @param agent
	 * @param override
	 */
	public void addAgent(IAgent agent, boolean override) {
		logger.entry();
		if(!agentMap.containsKey(agent.getAgentId())) {
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(AgentConstants.EVENT, AgentConstants.ADD_AGENT_PROXY);
			wrapperObject.addProperty(AgentConstants.AGENT_ID, agent.getAgentId());
			wrapperObject.addProperty(AgentConstants.NAME, agent.getAgentName());
			wrapperObject.addProperty(AgentConstants.OVERRIDE, override);
			TopicClient.getInstance().publish(AgentConstants.AGENT_SOCIETY, wrapperObject.toString(), false);
			agentMap.put(agent.getAgentId(), agent);
			overrideMap.put(agent.getAgentId(), override);
		}
		logger.exit();
	}
	
	/**
	 * Remove an agent from this society
	 * @param agent
	 */
	public void removeAgent(IAgent agent) {
		logger.entry();
		if(agentMap.containsKey(agent.getAgentId())) {
			overrideMap.remove(agent.getAgentId());
			JsonObject wrapperObject = new JsonObject();
			wrapperObject.addProperty(AgentConstants.EVENT, AgentConstants.REMOVE_AGENT_PROXY);
			wrapperObject.addProperty(AgentConstants.AGENT_ID, agent.getAgentId());
			TopicClient.getInstance().publish(AgentConstants.AGENT_SOCIETY, wrapperObject.toString(), false);
		}
		logger.exit();
	}

	/**
	 * Callback that can add or remove proxy agents to and from the society
	 */
	public void onEvent(String event) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject wrapperObject = parser.parse(event).getAsJsonObject();
		String agentId = wrapperObject.get(AgentConstants.AGENT_ID).getAsString();
		IAgent agent = agentMap.get(agentId);
		if(agent == null) {
			logger.info("Failed to find agent!");
			return;
		}
		
		boolean failed = false;
		String eventName = wrapperObject.get(AgentConstants.EVENT).getAsString();
		if(eventName.equals(AgentConstants.START_AGENT)) {
			if(!agent.onStart()) {
				logger.info("Failed to start agent: " + agent.getAgentName());
				failed = true;
			}
		}
		else if(eventName.equals(AgentConstants.STOP_AGENT)) {
			if(!agent.onStop()) {
				logger.info("Failed to stop agent: " + agent.getAgentName());
				failed = true;
			}
			agentMap.remove(agent.getAgentId());
		}
		
		if(failed) {
			JsonObject failedObject = new JsonObject();
			failedObject.addProperty(AgentConstants.FAILED_EVENT, eventName);
			failedObject.addProperty(AgentConstants.EVENT, AgentConstants.ERROR);
			TopicClient.getInstance().publish(AgentConstants.AGENT_SOCIETY, wrapperObject.toString(), false);
		}
		logger.exit();
	}

	public boolean isActive() {
		return started;
	}

	/**
	 * Unsubscribe from the agent-society topic
	 */
	public void shutdown() {
		started = false;
		TopicClient.getInstance().unsubscribe(AgentConstants.AGENT_SOCIETY, this);
	}

	public void onDisconnect() {
		for (String agentId : agentMap.keySet()) {
			IAgent agent = agentMap.get(agentId);
			agent.onStop();
		}
	}

	public void onReconnect() {
		for (String agentId : agentMap.keySet()) {
			JsonObject wrapperObject = new JsonObject();
			IAgent agent = agentMap.get(agentId);
			wrapperObject.addProperty(AgentConstants.EVENT, AgentConstants.ADD_AGENT_PROXY);
			wrapperObject.addProperty(AgentConstants.AGENT_ID, agent.getAgentId());
			wrapperObject.addProperty(AgentConstants.NAME, agent.getAgentName());
			wrapperObject.addProperty(AgentConstants.OVERRIDE, overrideMap.get(agentId));
			TopicClient.getInstance().publish(AgentConstants.AGENT_SOCIETY, wrapperObject.toString(), false);
			TopicClient.getInstance().publish(SensorConstants.SENSOR_MANAGER, 
					wrapperObject.toString(), false);
		}
	}
}
