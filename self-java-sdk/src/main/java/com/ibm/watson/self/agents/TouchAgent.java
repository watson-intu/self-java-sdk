package com.ibm.watson.self.agents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.self.blackboard.IBlackBoard;
import com.ibm.watson.self.blackboard.ThingEvent;

public class TouchAgent implements IAgent, IBlackBoard {

	private static Logger logger = LogManager.getLogger(TouchAgent.class.getName());

	public String getAgentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAgentId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean onStart() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onStop() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onThingEvent(ThingEvent thingEvent) {
		// TODO Auto-generated method stub
		
	}

}
