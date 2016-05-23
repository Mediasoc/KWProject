package com.example.NLSUbiPos.wireless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import com.example.NLSUbiPos.wireless.PositionProb;
import com.example.NLSUbiPos.coordinate.Mercator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * This class represents the unified wireless locator. Bluetooth, WiFi and so on can be used for locating.
 */
@SuppressLint("NewApi")
public abstract class WirelessLocator {

	// the OnWirelessPositionListener list
	private ArrayList<OnWirelessPositionListener> onWirelessPositionListeners;
	
	// the application context which is used for system functions
	protected Context context;
	
	// the Timer object
	protected Timer timer;
	
	// period task to be executed
	protected TimerTask timerTask;
	
	/*
	 * How many times the scanning should be executed.
	 * If times > 0, the wireless locator will run for specified times.
	 * If times = 0, the wireless locator will run until the program ends.
	 * If times < 0, the wireless locator will not run.
	 */
	protected int times;
	
	// stores the addresses of the known access points
	protected ArrayList<String> accessPointAddress;
		
	// stores the coordinates of the known access points
	protected ArrayList<Mercator> accessPointCoordinate;
	
	/**
	 * Constructor with the given context.
	 * @param context the application context
	 */
	protected WirelessLocator(Context context) {
		this.context = context;
		accessPointAddress = new ArrayList<String>();
		accessPointCoordinate = new ArrayList<Mercator>();
		onWirelessPositionListeners = new ArrayList<OnWirelessPositionListener>();
		
	}
	
	/**
	 * Adds a wireless access point position listener.
	 * @param listener the wireless access point position listener to be added.
	 */
	public void addOnWirelessPositionListener(OnWirelessPositionListener listener) {
		// add the listener to the list
		onWirelessPositionListeners.add(listener);
	}
	
	/**
	 * Removes all the wireless access point position listeners.
	 */
	public void removeOnWirelessPositionListeners() {
		// clear the list
		onWirelessPositionListeners.clear();
	}
	
	/**
	 * Notifies all the listeners that the wireless access point position is available.
	 * It invokes all the callback methods in the registered listeners. 
	 * @param coordinate the coordinate of the access point which accords with some conditions
	 */
	public void notifyWirelessPosition(List<PositionProb> list) {
		for (OnWirelessPositionListener listener : onWirelessPositionListeners) {
			listener.onWirelessPosition(list);
		}
	}
	
	/**
	 * Reads the known wireless access points information including ID, MAC, position and so on.
	 * @param pathname the pathname of the file which stores the access points information
	 */
	
	
	
	/**
	 * Starts locating the user using the wireless signals.
	 * @param interval the interval of two adjacent scanning actions
	 * @param times how many times the scanning should be executed
	 */
	public abstract void startLocating(long interval, int times);
	
	/**
	 * Stops the wireless locating function.
	 */
	public void stopLocating() {
		if (timer != null) {
			// cancels the scanning task
			timer.cancel();
			timer = null;
		}
	}
	
	
	
}
