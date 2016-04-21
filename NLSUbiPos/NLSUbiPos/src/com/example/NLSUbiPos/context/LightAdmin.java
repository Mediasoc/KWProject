package com.example.NLSUbiPos.context;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

    //this is a class that uses the light sensor and returns the value of light
    public class LightAdmin implements SensorEventListener {

    //the value of light
	private float light;
	
    //the light sensormanager
	SensorManager mSensorManager;
	Sensor sensorlight; 
	
    // the constructor of this class	
	public LightAdmin(Context context){
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}
	
	// return the value of light
	public float GetLight(){
		return light;
	}
 	
	public void SetLight(float li){
		light=li; 
	}
	
	
	//the light value is contained at sensor event
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {
	case Sensor.TYPE_LIGHT:
		 light= event.values[0];
		break;
	}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自动生成的方法存根
		
	}

	
}
