package com.example.NLSUbiPos.context;

/**
 * this class is the basic class providing the context event information.Every context event provider must extend
 * this class
 */
import java.util.ArrayList;

import android.hardware.SensorEventListener;

public abstract class ContextDetector implements SensorEventListener{
	
	//the context event listeners registered
	private ArrayList<OnContextListener> OnContextListeners=new ArrayList<OnContextListener>();
	
	//register a context event listener
	public void addOnContextListener(OnContextListener listener){
		OnContextListeners.add(listener);
	}
	
	//unregister all the context event listeners
	public void removeOnContextListener(){
		OnContextListeners.clear();
	}
	
	//notify all the listeners registered that a context event has occourred
	public void notifyContextEvent(ContextEvent event){
		for(OnContextListener listener:OnContextListeners){
			listener.onContext(event);
		}
	}
	
	

}
