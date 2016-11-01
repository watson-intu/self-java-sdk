package com.ibm.watson.self.gestures;

import com.google.gson.JsonObject;

public interface IGesture {

	 /**
	  * The ID of this gesture.
	  * @return gesture id
	  */
    public String getGestureId();
    
    /**
     * get instance id
     * @return
     */
    public String getInstanceId();
    
    /**
     *  Initialize this gesture object
     * @return - false if can't be initialized
     */
    public boolean onStart();
    
    /**
     * Shutdown this gesture object.
     * @return - false if it can't be shutdown
     */
    public boolean onStop();
    
    /**
     * Execute this gesture
     * @param params - params of gesture
     * @return - if it completed
     */
    public boolean execute(JsonObject params);
    
    /**
     * Abort this gesture
     * @return - if it aborted or not
     */
    public boolean abort();
}
