package com.ibm.watson.self.agents;

public interface IAgent {

	/**
	 * This should return a text name for this agent
	 * @return
	 */
	public String getAgentName();
	
	/**
	 * This should return a unique id for the agent
	 * @return - unique id
	 */
	public String getAgentId();
	
	/**
	 * This is invoked when this agent should start
	 * @return - if it started
	 */
	public boolean onStart();
	
	/**
	 * This is invoked when the agent should stop
	 * @return - if it stopped
	 */
	public boolean onStop();  
}
