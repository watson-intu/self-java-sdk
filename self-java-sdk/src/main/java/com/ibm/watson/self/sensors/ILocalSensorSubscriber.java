package com.ibm.watson.self.sensors;

public interface ILocalSensorSubscriber {

	public void getData(String data);
	
	public void getBinaryData(byte[] data);
}
