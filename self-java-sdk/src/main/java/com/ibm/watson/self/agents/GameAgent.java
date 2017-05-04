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

import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.self.blackboard.BlackBoard;
import com.ibm.watson.self.blackboard.IBlackBoard;
import com.ibm.watson.self.blackboard.IThing;
import com.ibm.watson.self.blackboard.ThingEvent;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;

/**
 * This agent entertains a person by starting a game on game_intent. When the game is started, it plays sound of an animal 
 * which is to be guessed. The player is given three chances for wrong answers.
 *
 */
public class GameAgent implements IAgent, IBlackBoard {

	private static Logger logger = LogManager.getLogger(GameAgent.class.getName());
	UUID uuid = UUID.randomUUID();
	
	private String instanceId;
	private String animal = null;
	private int failures = 0;
	
	private static final String DIRECTORY = "shared/sounds/";
	private static final String WAV = ".wav";	
	private static final String[] ANIMALS = { "bird", "cat", "cow", "dog", "rooster" };
	private static final String[] CORRECT_ANSWERS = { "That's right! Congratulations!", "Correct! Good job!", "You got it! Way to go!" };
	private static final String[] WRONG_ANSWERS = { "That's wrong! Try again!", "Not quite, try another time!", "Nice shot, but try again!" };
	
	public GameAgent() {
		instanceId = uuid.toString();
	}
	
	public void onThingEvent(ThingEvent thingEvent) {
		String type = thingEvent.getThing().getType();
		if(type.equals("ProxyIntent")) {
			onProxyIntent(thingEvent.getThing());
		}
	}
	
	private void onProxyIntent(IThing thing) {
		logger.entry();
		JsonParser parser = new JsonParser();
		JsonObject intentObject = parser.parse(thing.toString()).getAsJsonObject();
		logger.info(intentObject);
		if(intentObject.get("m_Intent").getAsJsonObject().get("conversation")
				.getAsJsonObject().get("intents").getAsJsonArray()
				.get(0).getAsJsonObject().get("intent").getAsString().equals("game_intent"))
		{
			onGameIntent();
		}
		else if(intentObject.get("m_Intent").getAsJsonObject().get("conversation")
				.getAsJsonObject().get("intents").getAsJsonArray()
				.get(0).getAsJsonObject().get("intent").getAsString().equals("animal_intent")) {
			onAnimalIntent(intentObject);
		}

	}
	
	private void onGameIntent() {
		String file = getRandomAnimal();
		JsonObject wrapperObject = new JsonObject();
		JsonObject paramsObject = new JsonObject();
		paramsObject.addProperty("Type_", "ParamsMap");
		paramsObject.addProperty("sound", file);
		wrapperObject.add("m_Params", paramsObject);
		wrapperObject.addProperty("m_Name", "Game");
		IThing sayThing = new IThing();
		sayThing.setType("Goal");
		sayThing.setBody(wrapperObject);
		BlackBoard.getInstance().addThing(sayThing, "");
	}
	
	private void onAnimalIntent(JsonObject intentObject) {
		JsonArray entities = intentObject.get("m_Intent").getAsJsonObject().get("conversation")
				.getAsJsonObject().get("entities").getAsJsonArray();
		String targetAnimal = null;
		for(JsonElement e : entities) {
			JsonObject element = e.getAsJsonObject();
			if(element.get("entity").getAsString().equals("animals")) {
				targetAnimal = element.get("value").getAsString();
				break;
			}
		}
		
		if(targetAnimal != null && animal != null) {
			if(targetAnimal.equals(animal)) {
				JsonObject wrapperObject = new JsonObject();
				int rand = new Random().nextInt(CORRECT_ANSWERS.length);
				wrapperObject.addProperty("m_Text", CORRECT_ANSWERS[rand]);
				IThing thing = new IThing();
				thing.setType("Say");
				thing.setBody(wrapperObject);
				BlackBoard.getInstance().addThing(thing, "");
				animal = null;
				failures = 0;
			}
			else {
				failures++;
				JsonObject wrapperObject = new JsonObject();
				if(failures == 3)
				{
					wrapperObject.addProperty("m_Text", "You suck at this. the answer was: " + animal);
					animal = null;
					failures = 0;
				}
				else {
					int rand = new Random().nextInt(WRONG_ANSWERS.length);
					wrapperObject.addProperty("m_Text", WRONG_ANSWERS[rand]);
				}
				IThing thing = new IThing();
				thing.setType("Say");
				thing.setBody(wrapperObject);
				BlackBoard.getInstance().addThing(thing, "");
				
			}
			
		}
	}
	
	private String getRandomAnimal() {
		int rand = new Random().nextInt(ANIMALS.length);
		animal = ANIMALS[rand];
		return DIRECTORY + animal + WAV;
	}

	public String getAgentName() {
		return "GameAgent";
	}

	public String getAgentId() {
		return instanceId;
	}

	public boolean onStart() {
		logger.entry();
		BlackBoard.getInstance().subscribeToType("ProxyIntent", ThingEventType.TE_ADDED, this, "");
		return logger.exit(true);
	}

	public boolean onStop() {
		BlackBoard.getInstance().unsubscribeFromType("ProxyIntent", this, "");
		return logger.exit(true);
	}

}
