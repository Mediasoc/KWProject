package com.example.NLSUbiPos.heading;

import java.util.ArrayList;

import android.hardware.SensorEventListener;

/**
 * This class is the basic class providing the user's heading. Every heading provider must
 * extend this class.
 */
public abstract class Compass implements SensorEventListener{
	
	// the heading change listeners registering in this heading provider
	ArrayList<OnHeadingChangeListener> onHeadingChangeListeners = new ArrayList<OnHeadingChangeListener>();
	
	/**
	 * Registers a heading change listener in this heading provider.
	 * @param listener the heading change listener to be registered
	 */
	public void addOnHeadingChangeListener(OnHeadingChangeListener listener) {
		onHeadingChangeListeners.add(listener);
	}
	 
	/**
	 * Unregisters all the heading change listeners in this class
	 */
	public void removeOnHeadingChangeListeners() {
		onHeadingChangeListeners.clear();
	}
	
	/**
	 * Notifies all the listeners that the heading has changed. 
	 * It invokes all the callback methods in the registered listeners. 
	 * @param heading the changed heading
	 */
	public void notifyHeadingChange(double heading) {
		for (OnHeadingChangeListener listener : onHeadingChangeListeners) {
			listener.onHeadingChange(heading);
		}
	}
}
