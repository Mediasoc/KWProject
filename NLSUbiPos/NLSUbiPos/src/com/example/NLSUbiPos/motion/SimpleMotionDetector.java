package com.example.NLSUbiPos.motion;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SimpleMotionDetector extends MotionDetector {

    //the array which saves sensor data 
	private float [][] gravityData ;
	private float[][] linearaccData;
	private float[] pressureData;
	
	//the amplitude of sensor data
	private float[] amplitude;
	
	//the size of sensor array
	private int gravitySize;
	private int linearaccSize;
	private int pressureSize;
	
	//the difference of pressure data about per 2 seconds
	private float pressuredif;
	
	//the instances of weka
	private Double[] instances;
	
	/*the motion context of user, 0 means walk, 1 means still, 2 means elevator up,
	3 means elevator down, 4 means upstairs, 5 means downstairs*/
	private int motioncontext;

	//the Constructor of this class
	public SimpleMotionDetector(){
		gravityData = new float[3][300];
		linearaccData = new float[3][300];
		amplitude = new float[300];
		gravitySize = 0;
		linearaccSize=0;
		instances = new Double[1];
		pressureData=new float[100];
		pressureSize=0;
	}
	
	//a method that gets the mean value of sensor data
	 public double getMean(float[] data , int size) {
			if(size == 0)  return 0;
			float sum = 0;
			for(int i=0; i<size; i++) {
				sum += data[i];
			}
			return sum / size;
		}
	 
	 //return the motion context
	 public int getmotion() {

			for(int i=0;i<linearaccSize; i++) {
				amplitude[i] = (float) Math.sqrt(linearaccData[0][i]*linearaccData[0][i] + linearaccData[1][i]*linearaccData[1][i] +
						linearaccData[2][i]*linearaccData[2][i]);
			}
			instances[0] = getMean(amplitude, linearaccSize);
			gravitySize = 0;
		    linearaccSize = 0;
		    pressuredif=getPredif();
		    pressureSize=0; 
		   

	    
		//use the pressure data to assist the context detection	
			if (pressuredif>0.08&&instances[0]<0.8)
			{
				motioncontext=3;
				}     //elevator down
		    else if (pressuredif<-0.08&&instances[0]<0.8)
		     {
		    	motioncontext=2;
		      }    //elevator up
		    
			
		    else if (pressuredif>0.035&&instances[0]>0.8)
				{
		    	motioncontext=5;
		    	}  //downstairs
				
			else if (pressuredif<-0.035&&instances[0]>0.8)
			    {
				motioncontext=4;
				} //upstairs
			else if (instances[0]>0.8)
				{
				motioncontext=0;
				}  //walk
			
			else{		
				motioncontext=1;    //still
			}
		    
			return motioncontext;
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
		   
		
		 pressuredif=pressureData[pressureSize-1]-pressureData[pressureSize-75];
			System.out.println(pressuredif);
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
			
			
		}
//		notify the motionEvent per 2s
		if(linearaccSize>=100&&gravitySize>=100&&pressureSize>=80)
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
