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
