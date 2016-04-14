package com.example.NLSUbiPos.stepdetecor;

/**
 * This class represent a sensor sample, including the time interval and the value. 
 */
public class SensorSample {
	
	// time interval between the previous SensorEvent and the current SensorEvent 
	float interval;
	
	// sensor value extracted the previous SensorEvent and the current SensorEvent
	float value;
	
	/**
	 * Constructor using the given interval and value.
	 * @param interval the given interval. unit(s)
	 * @param value the given value.
	 */
	public SensorSample(float interval, float value) {
		this.interval = interval;
		this.value = value;
	}
}
