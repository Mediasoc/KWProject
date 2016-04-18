package com.example.NLSUbiPos.floor;

import java.util.ArrayList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class PressureFloorDetector extends FloorDetector {
   //an array that stores pressure data
	private float[] pressureData;
	
   //current pressure
	private float currentpressure;
	
	// the size of pressure data
	public int pressureSize;
	
	// an arraylist that stores pressure data
	ArrayList<Float> pressureList;
	
	// the number of  floor
	private int floornum;
	
	//the number of initial floor
	private int initialfloor;
	
     // Constructor of this class
	public PressureFloorDetector(){
		 pressureData = new float[100];
		 currentpressure=0;
		 pressureSize=0;
		 pressureList=new ArrayList<Float>();
	}
	
	//set the initial floor number
	public void setinifloor(int floor){
		
		initialfloor=floor;
	}
	
	//Processes the pressure event received from the phone sensor.
	public void processPressureEvent(SensorEvent event){
		pressureData[pressureSize] = event.values[0];
		 currentpressure=event.values[0];
		 pressureList.add(event.values[0]);
		 pressureSize++;
	}
	
	// return current pressure data
	public float getcurrentpressure()
	{
		return currentpressure;
	}
	
	
	//return floor number
	public int getFloor() {
	    int a=(int) ((currentpressure-pressureList.get(3))/0.42);
		floornum=initialfloor-a;
		return floornum;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {	
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
				processPressureEvent(event);
					//	long timeStamp = event.timestamp;
					//	notifyFloorEvent(floor);
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍?
		
	}

}
