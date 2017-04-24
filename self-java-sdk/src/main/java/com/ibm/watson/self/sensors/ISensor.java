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
