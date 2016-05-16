package com.example.NLSUbiPos.motion;

import java.util.ArrayList;

import android.hardware.SensorEventListener;

import com.example.NLSUbiPos.floor.OnFloorListener;

public abstract class MotionDetector implements SensorEventListener{
	
	public int motion;
	//the floor event listeners registered
	

	
	private ArrayList<OnMotionListener> OnMotionListeners= new ArrayList<OnMotionListener>();
	
  
	
	//register a floor event listener
	public void addOnMotionListener(OnMotionListener listener){
		OnMotionListeners.add(listener);
	}
	
	//unregister all the floor event listeners
	public void removeOnMotionListener(){
		OnMotionListeners.clear();
	}
	
	//Notify all the listeners registered a floor event has occurred
	public void notifyMotionEvent(int motion){
		for(OnMotionListener listener:OnMotionListeners){
			listener.onMotion(motion);
		}
	}
}
