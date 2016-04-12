package com.example.NLSUbiPos.stepdetecor;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represent a possible step. It stores sensor samples from the last step point.
 */
public class StepWindow {

	// the accumulated energy after the last step point
	private float energy;
	
	// the accumulated time interval after the last step point. unit(s)
	private float totalInterval;
	
	// the sum of sensor values after the last step point
	private float valueSum;
	
	// the peak of the sensor values after the last step point
	private float peakValue;
	
	// the valley of the sensor values after the last step point
	private float valleyValue;
	
	// a queue which records the sensor samples after the last step point
	private Queue<SensorSample> queue;

	/**
	 * Creates an empty StepWindow
	 */
	public StepWindow() {
		queue = new LinkedList<SensorSample>();
		energy = 0.0f;
		totalInterval = 0.0f;
		valueSum = 0.0f;
		peakValue = -100.0f;
		valleyValue = 100.0f;
	}

	/**
	 * Adds two SensorSamples. The first one storing the accelerometer amplitude 
	 * will be added to a queue, and the second one storing the accelerometer 
	 * amplitude difference information is just used to calculate the accumulated energy 
	 * @param sensorSample containing two SensorSamples. One is used for store and 
	 * 			the other is used for calculation
	 */
	public void add(SensorSample[] sensorSample) {
		// adds the first SensorSample to the queue
		queue.offer(sensorSample[0]);
		// calculates time interval, value sum and energy sum
		totalInterval += sensorSample[0].interval;
		valueSum += sensorSample[0].value * sensorSample[0].interval;
		energy += sensorSample[1].value * sensorSample[1].value
				* sensorSample[1].interval;
		
		// records the maximum and minimum value
		if (sensorSample[0].value > peakValue) {
			peakValue = sensorSample[0].value;
		}
		if (sensorSample[0].value < valleyValue) {
			valleyValue = sensorSample[0].value;
		}
	}

	/**
	 * Resets the StepWindow. It is usually called after a step point is checked.
	 */
	public void reset() {
		queue.clear();
		energy = 0.0f;
		totalInterval = 0.0f;
		valueSum = 0.0f;
		peakValue = -100.0f;
		valleyValue = 100.0f;
	}

	/**
	 * Gets the accumulated energy of step.
	 * @return the accumulated energy
	 */
	public float getEnergy() {
		return energy;
	}

	/**
	 * Gets the maximum amplitude of accelerometer during this step.
	 * @return the peak value
	 */
	public float getPeakValue() {
		return peakValue;
	}
	
	/**
	 * Gets the minimum amplitude of accelerometer during this step.
	 * @return the valley value
	 */
	public float getValleyValue() {
		return valleyValue;
	}
	
	/**
	 * Gets the gap between the peak value and the valley value with a step.
	 * @return the gap between peak and valley
	 */
	public float getFallGap() {
		return peakValue - valleyValue;
	}

	/**
	 * Gets the duration of a step.
	 * @return the duration of a step
	 */
	public float getDuration() {
		return totalInterval;
	}

	/**
	 * 
	 */
	public float getAverage() {
		if (totalInterval > 0) {
			return valueSum / totalInterval;
		} else {
			return 9.8f;
		}
	}
	
	/**
	 * Gets the variance of the sensor data within a step.
	 * @return the variance of step
	 */
	public float getVariance() {
		float valueAverage = valueSum / totalInterval;
		float varianceSum = 0.0f;
		for (SensorSample sensorSample : queue) {
			varianceSum += (sensorSample.value - valueAverage)
					* (sensorSample.value - valueAverage)
					* sensorSample.interval;
		}
		return varianceSum / totalInterval;
	}
}
