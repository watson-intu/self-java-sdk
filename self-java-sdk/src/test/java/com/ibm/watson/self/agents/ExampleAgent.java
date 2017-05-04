
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

package com.ibm.watson.self.agents;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.ibm.watson.self.blackboard.BlackBoard;
import com.ibm.watson.self.blackboard.IBlackBoard;
import com.ibm.watson.self.blackboard.IThing;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;
import com.ibm.watson.self.blackboard.ThingEvent;

public class ExampleAgent implements IAgent, IBlackBoard {

	
	private static Logger logger = LogManager.getLogger(ExampleAgent.class.getName());
	UUID uuid = UUID.randomUUID();
	
	private double emotionalState = 0.5;
	private String instanceId;


	public void onThingEvent(ThingEvent thingEvent) {
		System.out.println("On ThingEvent()");
		logger.info("Received EmotionalState: " + thingEvent.getThing().getData().get("m_EmotionalState").getAsDouble());
		JsonObject wrapperObject = new JsonObject();
		wrapperObject.addProperty("m_Text", "This is a response from the java agent when emotion gets high!");
		IThing thing = new IThing();
		thing.setType("Say");
		thing.setBody(wrapperObject);
		BlackBoard.getInstance().addThing(thing, "");
		if(emotionalState > 0.7) {
			emotionalState = 0.5;
		}
		
	}

	public String getAgentName() {
		return "EmotionAgent";
	}

	public String getAgentId() { 
		return instanceId;
	}

	public boolean onStart() {
		logger.entry();
		instanceId = uuid.toString();
		BlackBoard.getInstance().subscribeToType("EmotionalState", ThingEventType.TE_ADDED, this, "");
		return logger.exit(true);
	}

	public boolean onStop() {
		BlackBoard.getInstance().unsubscribeFromType("EmotionalState", this, "");
		return logger.exit(true);
	}

}
