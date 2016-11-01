package com.ibm.watson.self.topics;

public interface IEvent {

	public void onEvent(String event);
	
	public boolean isActive();
	
	public void shutdown();
}
