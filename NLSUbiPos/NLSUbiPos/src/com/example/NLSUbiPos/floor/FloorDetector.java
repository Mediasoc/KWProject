package com.example.NLSUbiPos.floor;
/**
 * the abstract class of floor change detector,which can provide FloorEvent. Every floor event provider
 * must extend it
 */
import java.util.ArrayList;

import android.hardware.SensorEventListener;



public abstract class FloorDetector implements SensorEventListener{
	
	public int floor;
	//the floor event listeners registered
	private ArrayList<OnFloorListener> OnFloorListeners= new ArrayList<OnFloorListener>();
	
	
	//register a floor event listener
	public void addOnFloorListener(OnFloorListener listener){
		OnFloorListeners.add(listener);
	}
	
	//unregister all the floor event listeners
	public void removeOnFloorListener(){
		OnFloorListeners.clear();
	}
	
	//Notify all the listeners registered a floor event has occourred
	public void notifyFloorEvent(int floor){
		for(OnFloorListener listener:OnFloorListeners){
			listener.onFloor(floor);
		}
	}
}