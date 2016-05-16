package com.example.NLSUbiPos.position;
/**
 * this class manage the detectors in the system
 */
import com.example.NLSUbiPos.context.ContextDetector;
import com.example.NLSUbiPos.context.IODetector;
import com.example.NLSUbiPos.floor.FloorDetector;
import com.example.NLSUbiPos.floor.PressureFloorDetector;
import com.example.NLSUbiPos.heading.Compass;
import com.example.NLSUbiPos.heading.GyroCompass;
import com.example.NLSUbiPos.motion.MotionDetector;
import com.example.NLSUbiPos.motion.SimpleMotionDetector;
import com.example.NLSUbiPos.stepdetecor.MovingAverageStepDetector;
import com.example.NLSUbiPos.stepdetecor.StepDetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class PositionClient {
	
	Context context;
	
	FloorDetector floordetector;
	
	Compass compass;
	
//	ContextDetector contextdetector;
	IODetector contextdetector;
	
	StepDetector stepdetector;
	
	SensorManager sensormanager;
	
	MotionDetector motiondetector;
	
	public PositionClient(Context context){
		this.context=context;
		stepdetector=new MovingAverageStepDetector();
		compass=new GyroCompass();
		//added by wl
		floordetector=new PressureFloorDetector();
		contextdetector=new IODetector(context);
		motiondetector=new SimpleMotionDetector();
		
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
		
		//added by wl
		Sensor pressure= sensormanager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		Sensor light= sensormanager.getDefaultSensor(Sensor.TYPE_LIGHT);
		
		Sensor gravity= sensormanager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		Sensor linacc= sensormanager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	
		
		if(accelerometer==null || gyroscope==null || magnetometer==null||pressure==null) {
			throw new RuntimeException("not supported sensor(accelerometer|gyroscope|magnetometer|pressure)");
		}
		
		// registers accelerometer for step detector
		sensormanager.registerListener(stepdetector, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		
		// registers gyroscope, accelerometer and magnetometer for heading provider
		sensormanager.registerListener(compass, gyroscope, SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(compass, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(compass, magnetometer, SensorManager.SENSOR_DELAY_GAME);
		
		/*
		 * added by wl
		 * */
		 
		// registers accelerometer for floordetector
		sensormanager.registerListener(floordetector, pressure, SensorManager.SENSOR_DELAY_GAME);
		
		// registers accelerometer for context detector
		sensormanager.registerListener(contextdetector, light, SensorManager.SENSOR_DELAY_GAME);
		
		sensormanager.registerListener(motiondetector, gravity, SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(motiondetector, linacc, SensorManager.SENSOR_DELAY_GAME);
		sensormanager.registerListener(motiondetector, pressure, SensorManager.SENSOR_DELAY_GAME);
		
		
		
		
		
	}
	
	
	/**
	 * Unregister the sensor service of the detectors(waiting for floor and context detectors)
	 */
	public void unregisterSensorListener() {
		sensormanager.unregisterListener(stepdetector);
		sensormanager.unregisterListener(compass);
		sensormanager.unregisterListener(floordetector);
		sensormanager.unregisterListener(contextdetector);
	}
	
	public FloorDetector getFloorDetector(){
		return floordetector;
	}
	
	public IODetector getContextDetector(){
		return contextdetector;
	}
	
	public StepDetector getStepDetector(){
		return stepdetector;
	}
	
	public Compass getCompass(){
		return compass;
	}
	public MotionDetector getMotionDetector(){
		return motiondetector;
	}
	

}
