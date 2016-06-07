package com.example.NLSUbiPos.motion;

import java.util.ArrayList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ComplexMotionDetector extends MotionDetector {

    //the array which saves sensor data 
	private float [][] gravityData ;
	private float[][] linearaccData;
	private float[] pressureData;
	private float[][] gyroData; 
	
	//the amplitude of sensor data
	private float[] linamplitude;
	private float[] graamplitude;
	
	//the size of sensor array
	private int gravitySize;
	private int linearaccSize;
	private int pressureSize;
	private int gyroSize;
	
	//the difference of pressure data about per 2 seconds
	private float pressuredif;
	
	//the instances of weka
	private Double[] instances;
	
	/*the motion context of user, 0 means walk, 1 means still, 2 means elevator up,
	3 means elevator down, 4 means upstairs, 5 means downstairs*/
	private int motioncontext;

	//the Constructor of this class
	public ComplexMotionDetector(){
		gravityData = new float[3][200];
		linearaccData = new float[3][200];
		gyroData= new float[3][200];
		pressureData=new float[200];
		linamplitude = new float[200];
		graamplitude = new float[200];
		gravitySize = 0;
		linearaccSize=0;
		gyroSize = 0;
		pressureSize=0;
		instances = new Double[10];

	}
	
	
	 
	 //return the motion context
	 public int getmotion() {
		 instances[0] = getMean(gravityData[0], gravitySize-100,gravitySize);
		 instances[1] = getMean(gravityData[1], gravitySize-100,gravitySize);
		 instances[2] = getMean(gravityData[2], gravitySize-100,gravitySize);	
		 for(int i=0;i<linearaccSize; i++) {
				linamplitude[i] = (float) Math.sqrt(linearaccData[0][i]*linearaccData[0][i] +
						linearaccData[1][i]*linearaccData[1][i] +linearaccData[2][i]*linearaccData[2][i]);
			}
		 instances[3] = getMean(gyroData[0], gyroSize-100,gyroSize);
		 instances[4] = getMean(gyroData[2], gyroSize-100,gyroSize);
		 instances[5] = getVariance(linearaccData[0], linearaccSize-100,linearaccSize);
		 instances[6] = getVariance(linearaccData[1], linearaccSize-100,linearaccSize);
		 instances[7] = getVariance(linearaccData[2], linearaccSize-100,linearaccSize);
		 instances[8] = getMean(linamplitude, linearaccSize-100,linearaccSize);
		 instances[9] = getMean(gyroData[1], gyroSize-100,gyroSize);
		 pressuredif=getPredif();
		 gravitySize = 0;
		 linearaccSize = 0;
		 gyroSize = 0;
		 pressureSize = 0;
		
		 if(pressuredif>0.04)
			{
			     if(pressuredif>=0.1&&instances[8]<0.8)
			     {
			    	 motioncontext=12;	            //elevator down	;		
			     }
			     else if(pressuredif>0.04&&pressuredif<0.1&&instances[8]<0.8)
				 {
			    	 motioncontext=14;	            //escalator down	;			
	     		 }
			     else if(pressuredif>0.04&&pressuredif<0.15&&instances[8]>=0.8)
				 {
			    	 motioncontext=10;	            //stairs down			
	     		 }
			}
			
			else if(pressuredif<-0.04)
			{
			     if(pressuredif<=-0.1&&instances[8]<0.8)
			     {
			    	 motioncontext=11;	            //elevator up	;			
			     }
			     else if(pressuredif<-0.04&&pressuredif>-0.1&&instances[8]<0.8)
				 {
			    	 motioncontext=13;	            //escalator up	;		
	     		 }
			     
			     else if(pressuredif<-0.04&&pressuredif>-0.1&&instances[8]>=0.8)
				 {
			    	 motioncontext=9;	            //stairs up	
	     		 }
			}
			
			else{
			
			    if(instances[8]<=0.6)
				{
			    	
			    	motioncontext=0;                //still
				}
				else if(instances[2]>7&&instances[7]>=5)
				{
					motioncontext=5;	            //run text	
				}
				else if(instances[2]>7&&instances[7]<5)
				{
					motioncontext=1;	            //walk text
				}
				
				else if(instances[8]>11&&instances[3]>=1.5)
				{
					motioncontext=7;	            //run pocket	
				}
				
				else if(instances[0]>5.5&&instances[8]<=3.5)
				{
					motioncontext=2;	            //walk call
				}
				
				else if(instances[0]>5.5&&instances[8]>3.5&&instances[4]>=1.2)
				{
					motioncontext=4;	            //walk handswing
				}
				
				else if(instances[0]>5.5&&instances[8]>3.5&&instances[4]<1.2)
				{
					motioncontext=6;	            //run call
				}
				else if(instances[1]>8&&instances[3]>0.8)
				{
					motioncontext=3;	            //walk pocket
				}
			    
				else if(instances[1]>8&&instances[3]<=0.8)
				{
					motioncontext=8;	            //run handswing	
				}
				else
				{
					 motioncontext=15;	            //unknown	;	
				}
				
				}
		 
		 
		 
			return motioncontext;
		}
	//a method that gets the mean value of sensor data
	 public double getMean(float[] data, int startid, int endid) {
		    	int size=endid-startid+1;
		    	if(startid-endid> 0)  return 0;
				float sum = 0;
				for(int i=startid-1; i<endid; i++) {
					sum += data[i];
				}
				return sum / size;
			}
		 
	 public double getVariance(float[] data, int startid, int endid) {
	    	int size=endid-startid+1;
	    	if(startid-endid> 0)  return 0;
	  		double mean;
	  		double sumVariance=0;
	  		mean=getMean(data,startid,endid);
	  		for(int i=startid-1; i<endid; i++) {
	  			sumVariance += (data[i] - mean) * (data[i] - mean);
	  		}
	  		return sumVariance / size;
	  	}
	    
	 
	 
	 
	 //get the linearaccSize
	 public int getLinaccSize(){
		 return linearaccSize;
	 }
	 
	 //get the gravitySize 
	 public int getgraSize(){
		 return gravitySize;
		
	 }

	 //get difference of pressure data per 60 data
	 public float getPredif(){
		 pressuredif=pressureData[pressureSize-1]-pressureData[1];
			return pressuredif;
		}
	 
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {
		case Sensor.TYPE_GRAVITY:
			gravityData[0][gravitySize] = Math.abs(event.values[0]);
			gravityData[1][gravitySize] = Math.abs(event.values[1]);
			gravityData[2][gravitySize] = Math.abs(event.values[2]);
			gravitySize++;
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			linearaccData[0][linearaccSize] = event.values[0];
			linearaccData[1][linearaccSize] = event.values[1];
			linearaccData[2][linearaccSize] = event.values[2];
			linearaccSize++;
			break;
		case Sensor.TYPE_PRESSURE:
			pressureData[pressureSize] = event.values[0];
			pressureSize++;
			break;
		case Sensor.TYPE_GYROSCOPE:
			gyroData[0][gyroSize] = Math.abs(event.values[0]);
			gyroData[1][gyroSize] = Math.abs(event.values[1]);
			gyroData[2][gyroSize] = Math.abs(event.values[2]);
			gyroSize++;
			break;	
		}
//		notify the motionEvent per 2s
		if(linearaccSize>=100&&gravitySize>=100&&gyroSize>=100&&pressureSize>=60)
		{
			int a=getmotion();
			notifyMotionEvent(a);
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自动生成的方法存根
		
	}
	 
	 
	
	
	
	
	
	
}
