package com.example.NLSUbiPos.stepdetecor;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is a moving window with the given size. It maintains a Queue which records 
 * the samples.
 */
public class MovingWindow {
	
	// the size of the window
	private float windowSize;
	
	// the Queue records the samples
	private Queue<SensorSample> queue;
	
	// the sum of time interval of all SensorSamples
	private float totalInterval;
	
	// the sum of the sensor values
	private float valueSum;
	
	// the average of the sensor values
	private float valueAverage;
	
	/**
	 * Constructor with a given window size.
	 * @param windowSize the given window size.
	 */
	public MovingWindow(float windowSize) {
		this.windowSize = windowSize;
		queue = new LinkedList<SensorSample>();
		totalInterval = 0;
		valueSum = 0;
		valueAverage = 0;
	}
	
	/**
	 * Adds the new sample to the rail of Queue and removes the overflowed head.
	 * @param sensorSample the new sample to be added
	 */
	public void add(SensorSample sensorSample) {
		// removes the overflowed head
		while (totalInterval > windowSize) {
			SensorSample oldSensorSample = queue.poll();
			totalInterval -= oldSensorSample.interval;
			valueSum -= oldSensorSample.value * oldSensorSample.interval;		
		}
		
		// adds the new sample
		queue.offer(sensorSample);
		totalInterval += sensorSample.interval;
		valueSum += sensorSample.value * sensorSample.interval;
		valueAverage = valueSum / totalInterval;
	}
	
	/**
	 * Gets the average of the sensor values.
	 * @return the average value
	 */
	public float getAverage() {
		return valueAverage;
	}
}
