package com.example.NLSUbiPos.position;
/**
 * this class manage the detectors in the system
 */
import com.example.NLSUbiPos.context.ContextDetector;
import com.example.NLSUbiPos.floor.FloorDetector;
import com.example.NLSUbiPos.heading.Compass;
import com.example.NLSUbiPos.heading.GyroCompass;
import com.example.NLSUbiPos.stepdetecor.MovingAverageStepDetector;
import com.example.NLSUbiPos.stepdetecor.StepDetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class PositionClient {
	
	Context context;
	
	FloorDetector floordetector;
	
	Compass compass;
	
	ContextDetector contextdetector;
	
	StepDetector stepdetector;
	
	SensorManager sensormanager;
	
	public PositionClient(Context context){
		this.context=context;
		stepdetector=new MovingAverageStepDetector();
		compass=new GyroCompass();
		/**
		 * the extend floordetector and contextdetector are needed 
		 */
	}
	
	/**
	 * Registers sensor service for detectors(waiting for floor and context detectors)
	 */
	public void registerSensorListener() {
		sensormanager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
		Sensor accelerometer = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor gyroscope = sensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		Sensor magnetometer = sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		if(accelerometer==null || gyroscope==null || magnetometer==null) {
			throw new RuntimeException("not supported sensor(accelerometer|gyroscope|magnetometer)");
		}
		
		// registers accelerometer for step detector
		sensormanager.registerListener(stepdetector, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		
		// registers gyroscope, accelerometer and magnetometer for heading provider
		sensormanager.registerListener(compass, gyroscope, SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(compass, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(compass, magnetometer, SensorManager.SENSOR_DELAY_GAME);
		
	}
	
	
	/**
	 * Unregister the sensor service of the detectors(waiting for floor and context detectors)
	 */
	public void unregisterSensorListener() {
		sensormanager.unregisterListener(stepdetector);
		sensormanager.unregisterListener(compass);
	}
	
	public FloorDetector getFloorDetector(){
		return floordetector;
	}
	
	public ContextDetector getContextDetector(){
		return contextdetector;
	}
	
	public StepDetector getStepDetector(){
		return stepdetector;
	}
	
	public Compass getCompass(){
		return compass;
	}

}
