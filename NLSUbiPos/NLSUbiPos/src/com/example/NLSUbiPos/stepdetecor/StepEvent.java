package com.example.NLSUbiPos.stepdetecor;

/**
 * This class includes some information about a step event. When a step is detected, it will
 * be sent to the listeners.
 */
public class StepEvent {
	
	// the timestamp when the step is detected
	private long timestamp;
	
	// the duration of the detected step
	private double duration;
	
	// the step length of the detected step
	private double stepLength;
	
	/**
	 * Constructor with the given values.
	 * @param timestamp the given timestamp
	 * @param duration the step duration
	 * @param stepLength the step length
	 */
	public StepEvent(long timestamp, double duration, double stepLength) {
		this.timestamp = timestamp;
		this.duration = duration;
		this.stepLength = stepLength;
	}
	
	/**
	 * Gets the timestamp of the step.
	 * @return the timestamp when the step is detected
	 */
	public long getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Gets the duration of the step.
	 * @return the step duration
	 */
	public double getDuration() {
		return this.duration;
	}
	
	/**
	 * Gets the step length of the step
	 * @return the step length
	 */
	public double getStepLength() {
		return stepLength;
	}
}
