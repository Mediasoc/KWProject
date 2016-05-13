package com.example.NLSUbiPos.satellite;
/**
 * this class is the basic class of outdoor location based onsatellites
 * @author WZ
 */

// Edited by Liu on 20160513
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;

import com.example.NLSUbiPos.coordinate.Mercator;

public abstract class GPSLocator {
	//the application context
	protected Context context;
	
	//timer object
	protected Timer timer;
	
	//period task to be executed
	protected TimerTask timerTask;
	
	//the OnGPSPositionListener list
	private ArrayList<OnGPSPositionListener> onGPSPositionListeners=new ArrayList<OnGPSPositionListener>();
	
	//add an OnGPSPositionListener 
	public void addOnGPSPositionListener(OnGPSPositionListener listener){
		onGPSPositionListeners.add(listener);
	}
	
	//remove all the OnGPSPositionListeners
	public void removeOnGPSPositionListener(){
		onGPSPositionListeners.clear();
	}
	
	//notify all the listeners registed that a GPSLocation is generated
	public void notifyGPSPosition(Location location){
		for(OnGPSPositionListener listener:onGPSPositionListeners){
			listener.onGPSPosition(location);
		}
	}

}
