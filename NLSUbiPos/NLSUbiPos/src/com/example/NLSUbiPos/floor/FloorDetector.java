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
	
	public int initialfloor;
	
	private ArrayList<OnFloorListener> OnFloorListeners= new ArrayList<OnFloorListener>();
	
    public void setinifloor(int floor){
	}
	
	//register a floor event listener
	public void addOnFloorListener(OnFloorListener listener){
		OnFloorListeners.add(listener);
	}
	
	//unregister all the floor event listeners
	public void removeOnFloorListener(){
		OnFloorListeners.clear();
	}
	
	//Notify all the listeners registered a floor event has occurred
	public void notifyFloorEvent(int floor){
		for(OnFloorListener listener:OnFloorListeners){
			listener.onFloor(floor);
		}
	}
}