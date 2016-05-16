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
	private int pressureSize;
	
	// an arraylist that stores pressure data
	public ArrayList<Float> pressureList;
	
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
	@Override
	public void setinifloor(int floor){
		
		initialfloor=floor;
	}
	
	//Processes the pressure event received from the phone sensor.
	public void processPressureEvent(SensorEvent event){
		pressureData[pressureSize] = event.values[0];
		 currentpressure=event.values[0];
		 if(pressureList.size()<10){
		 pressureList.add(event.values[0]);
		 }
		 pressureSize++;
		
		floornum=getFloor();
		//传感器每采集一次数据notify一次
		notifyFloorEvent(floornum);
//		System.out.println(floornum);
//		 }
	}
	
	// return current pressure data
	public float getcurrentpressure()
	{
		return currentpressure;
	}
	
	
	//get the pressure size
	public int getpresize(){
		return pressureSize;
	}
	
	//return floor number
	public int getFloor() {
		pressureSize=0;
		int a=(int) ((currentpressure-pressureList.get(0))/0.4);
		floornum=initialfloor-a;
//		floornum=3;
		return floornum;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {	
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
				processPressureEvent(event);
			
//				System.out.println(event.values[0]);
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO
		
	}

}
