package com.ibm.watson.self.agents;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.self.blackboard.BlackBoard;
import com.ibm.watson.self.blackboard.IBlackBoard;
import com.ibm.watson.self.blackboard.IThing;
import com.ibm.watson.self.blackboard.ThingEvent;
import com.ibm.watson.self.blackboard.IThing.ThingEventType;

public class MusicAgent implements IAgent, IBlackBoard {

	private static Logger logger = LogManager.getLogger(GameAgent.class.getName());
	UUID uuid = UUID.randomUUID();
	
	String instanceId;
	boolean musicStarted = false;
	int bpm = 0;
	
	
	public MusicAgent() {
		instanceId = uuid.toString();
	}
	
	public void onThingEvent(ThingEvent thingEvent) {
		String type = thingEvent.getThing().getType();
		if(type.equals("MusicBeat")) {
			onMusicBeat(thingEvent.getThing());
		}
		else if(type.equals("MusicTempo")) {
			onMusicTempo(thingEvent.getThing());
		}
		else if(type.equals("MusicStarted")) {
			onMusicStarted(thingEvent.getThing());
		}
		else if(type.equals("MusicStopped")) {
			onMusicStopped();
		}
		else
			logger.error("Could not figure out what thing event was!");
		
	}

	private void onMusicStopped() {
		musicStarted = false;		
	}

	private void onMusicStarted(IThing thing) {
		musicStarted = true;
		
	}

	private void onMusicTempo(IThing thing) {
		bpm = thing.getData().get("BPM").getAsInt();	
	}

	private void onMusicBeat(IThing thing) {
		bpm = thing.getData().get("BPM").getAsInt();
		// TODO: Execute gesture here with bpm data
	}

	public String getAgentName() {
		return "MusicAgent";
	}

	public String getAgentId() {
		return instanceId;
	}

	public boolean onStart() {
		logger.entry();
		BlackBoard.getInstance().subscribeToType("MusicBeat", ThingEventType.TE_ADDED, this, "");
		BlackBoard.getInstance().subscribeToType("MusicTempo", ThingEventType.TE_ADDED, this, "");
		BlackBoard.getInstance().subscribeToType("MusicStarted", ThingEventType.TE_ADDED, this, "");
		BlackBoard.getInstance().subscribeToType("MusicStopped", ThingEventType.TE_ADDED, this, "");
		return logger.exit(true);
	}

	public boolean onStop() {
		BlackBoard.getInstance().unsubscribeFromType("MusicBeat", this, "");
		BlackBoard.getInstance().unsubscribeFromType("MusicTempo", this, "");
		BlackBoard.getInstance().unsubscribeFromType("MusicStarted", this, "");
		BlackBoard.getInstance().unsubscribeFromType("MusicStopped", this, "");
		return logger.exit(true);
	}

}
