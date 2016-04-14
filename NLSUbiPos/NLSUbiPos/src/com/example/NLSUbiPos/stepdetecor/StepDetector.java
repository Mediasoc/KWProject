package com.example.NLSUbiPos.stepdetecor;

import java.util.ArrayList;

import android.hardware.SensorEventListener;

/**
 * This class is the basic class providing the step event information. Every step event provider
 * must extend this class.
 */
public abstract class StepDetector implements SensorEventListener {
	
	// the step event listeners registered in this step event provider
	private ArrayList<OnStepListener> onStepListeners = new ArrayList<OnStepListener>();
	
	/**
	 * Registers a step event listener in this step event provider.
	 * @param listener the step event listener to be registered
	 */
	public void addOnStepListener(OnStepListener listener) {
		onStepListeners.add(listener);
	}
	
	/**
	 * Unregister all the heading change listeners in this class
	 */
	public void removeOnStepListeners() {
		onStepListeners.clear();
	}
	
	/**
	 * Notifies all the listeners that a step event has occurred. 
	 * It invokes all the callback methods in the registered listeners. 
	 * @param event the step event that has occurred
	 */
	public void notifyStepEvent(StepEvent event) {
		for (OnStepListener listener : onStepListeners) {
			listener.onStep(event);
		}
	}
}
