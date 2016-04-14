package com.example.NLSUbiPos.floor;

import java.util.ArrayList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class FloorRecognition extends FloorDetector {
   //the array that stores pressure data
	private float[] pressureData;
	
   //current pressure
	private float currentpressure;
	
	//the average of pressure 
	//private double  pressuremean;
	
	// the size of pressure data
	private int pressureSize;
	
	// an arraylist that stores pressure data
	private  ArrayList<Float> pressureList;
	
	// the number of  floor
	int floornum;
	
	//the number of initial floor
	int initialfloor;
	
     // Constructor of this class
	public FloorRecognition(){
		 pressureData = new float[100];
		 currentpressure=0;
		// pressuremean=0;
		 pressureSize=0;
		 pressureList=new ArrayList<Float>();
	}
	
	//Processes the pressure event received from the phone sensor.
	public void processPressureEvent(SensorEvent event){
		pressureData[pressureSize] = event.values[0];
		 currentpressure=event.values[0];
		 pressureList.add(event.values[0]);
		 pressureSize++;
		 if(pressureSize>=50 ) {
				getFloor();
			//	long timeStamp = event.timestamp;
				int floor=this.getFloor();
				notifyFloorEvent(floor);
			}
		
	}
	
	
	
	
	//return floor number
	private int getFloor() {
		//pressuremean= getMean(pressureData, pressureSize);
	    pressureSize = 0;
	    int a=(int) ((currentpressure-pressureList.get(3))/0.42);
		floornum=initialfloor-a;
		return floornum;
		
	}


	/*private double getMean(float[] data, int size) {
		if(size == 0)  return 0;
		float sum = 0;
		for(int i=0; i<size; i++) {
			sum += data[i];
		}
		return sum / size;
	}
	*/


	@Override
	public void onSensorChanged(SensorEvent event) {	
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
				processPressureEvent(event);
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自动生成的方法存根
		
	}

}
