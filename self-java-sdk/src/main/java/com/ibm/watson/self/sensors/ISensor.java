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

package com.ibm.watson.self.sensors;

/**
 * Represents all sensors that can receive some type of data from an
 * external source, e.g. video camera, microphone, or data through a
 * connected socket 
 */
public interface ISensor {

    /**
     * unique ID for this sensor
     * @return - unique ID
     */
    public String getSensorId();
    
    /**
     * text name for this sensor
     * @return - text name
     */
    public String getSensorName();
    
    /**
     * type of data class this sensor sends
     * @return - type of data
     */
    public String getDataType();
    
    /**
     * the type of binary data
     * @return - type of binary data
     */
    public String getBinaryType();
    
    /**
     * This is invoked when this sensor should start calling SendData()
     * @return - if it started or not
     */
    public boolean onStart();
    
    /**
     * This is invoked when the last subscriber unsubscribe from this sensor
     * @return - if it stopped
     */
    public boolean onStop();   
    
    /**
     * pause the sensor
     */
    public void onPause();
    
    /**
     * resume the sensor
     */
    public void onResume();
}
