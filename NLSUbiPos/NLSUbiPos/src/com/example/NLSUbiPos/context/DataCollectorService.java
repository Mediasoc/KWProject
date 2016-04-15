package com.example.NLSUbiPos.context;

import java.util.ArrayList;

import weka.core.Instance;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DataCollectorService extends Service implements
		SensorEventListener {

	// local binder object used for getting service
	private LocalBinder binder = new LocalBinder();

	// a SensorManager object is used for managing the sensors
	private SensorManager m_sensorManager;

	// the next index of which unit will store the sensors value
	private int linearAccNext = 0;
	private int gravNext = 0;
	private int pressureNext=0;

	// max length of arrays storing the sensor value
	// game mode, 50 points per second, so nearly 6s
	private final int MAXLENGTH = 500;

	// unit used for storing the sensor value,including 3-axis accelerometer
	// sensor,
	// 3-axis gyroscope sensor, and 3-axis magnetometer sensor
	// private float[][] accelerometer = new float[3][MAXLENGTH];
	private float[][] linearAccelerometer = new float[MAXLENGTH][3];
	private float[][] gravity = new float[MAXLENGTH][3];
	private float[] pressure=new float[100000];
//	ArrayList<Float> pressureList = new ArrayList<Float>();
	byte sensorCode = 0;

	// unit storing the feature of a single instance
	double[] instance;
	float pressuredif;
	double pressureVar;

	// called when the service is binded
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d("service", "onbind");
		return binder;
	}

	// called when the service is created
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("service", "oncreate");
		// set sample speed of sensors
		int sampleSpeed = SensorManager.SENSOR_DELAY_GAME;
		// get system service about sensors
		m_sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		Sensor linearAccSensor = m_sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		Sensor gravitySensor = m_sensorManager
				.getDefaultSensor(Sensor.TYPE_GRAVITY);
		//加入气压计，辅助上下楼识别
		Sensor pressureSensor=m_sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		
		if (linearAccSensor == null || gravitySensor == null) {
			Toast.makeText(this, "no linear accelerometer or gravity sensor",
					Toast.LENGTH_LONG).show();
		} 
		
		else if (pressureSensor == null ) {
			Toast.makeText(this, "no Pressure sensor",
					Toast.LENGTH_LONG).show();
		} 
		
		else {
			m_sensorManager.registerListener(this, linearAccSensor, sampleSpeed);
			m_sensorManager.registerListener(this, gravitySensor, sampleSpeed);
			m_sensorManager.registerListener(this, pressureSensor,sampleSpeed);
			instance = new double[6];
//			instance = new double[2];
		}

	}

	// called when the service is destroyed
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// m_sensorManager.unregisterListener(this);
		if (m_sensorManager != null) {
			m_sensorManager.unregisterListener(this);
			m_sensorManager = null;
		}
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	// called when the service is unbinded
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		if (m_sensorManager != null) {
			m_sensorManager.unregisterListener(this);
			m_sensorManager = null;
		}
		Log.d("service", "onunbind");
		return super.onUnbind(intent);
	}

	byte getSensorCode() {
		return sensorCode;
	}

	// the nested class which is used to get DataCollectorService object
	class LocalBinder extends Binder {
		// get DataCollectorService object
		DataCollectorService getService() {
			return DataCollectorService.this;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	// called when sensor values is changed
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// long time = 0L;
		// float[] value = new float[3];

		// write the sensor data into the corresponding place
		switch (event.sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:
			// time = event.timestamp;
			if (linearAccNext == MAXLENGTH)
				linearAccNext = 0;
			for (int i = 0; i < 3; i++) {
				linearAccelerometer[linearAccNext][i] = event.values[i];
			} 
			linearAccNext++;

			break;
		case Sensor.TYPE_GRAVITY:
			// time = event.timestamp;
			if (gravNext == MAXLENGTH)
				gravNext = 0;
			for (int i = 0; i < 3; i++) {
				gravity[gravNext][i] = event.values[i];
			}
			gravNext++;
			break;
			
		case Sensor.TYPE_PRESSURE:
			
//			pressureList.add(event.values[0]);
			if(pressureNext==100000)
				pressureNext=0;
			else
				pressure[pressureNext] = event.values[0];
			    pressureNext++;
//			    System.out.println(pressureNext);
			break;
			
		}
	}

	// calculate the feature of the instance
	/*
	 * linearAccelerometer 0->meanAccM 1->varAccM 2->meanVAcc 3->varVAcc
	 * 4->meanHAccM 5->varHAccM
	 */
	public double[] getMotionData() {

		// calculate the module of accelerometer
		float[] accModule = new float[linearAccNext];
		float[] accModuleH = new float[linearAccNext];
		float[] accProjectionV = new float[linearAccNext];
		for (int i = 0; i < linearAccNext; i++) {
			//calculate accModule
			accModule[i] = (float) Math.sqrt(linearAccelerometer[i][0]
					* linearAccelerometer[i][0] + linearAccelerometer[i][1]
					* linearAccelerometer[i][1] + linearAccelerometer[i][2]
					* linearAccelerometer[i][2]);
			//calculate accProjectionV
			if(i<gravNext){
				accProjectionV[i] = getProjection(linearAccelerometer[i],gravity[i]);
			}
			else{ //projection on the last gravity vector
				accProjectionV[i] = getProjection(linearAccelerometer[i],gravity[gravNext-1]);
			}
			//calculate accModuleH
			accModuleH[i] = (float) Math.sqrt(accModule[i]*accModule[i]-accProjectionV[i]*accProjectionV[i]);
		}
		

		instance[0] = getMean(accModule, 0, linearAccNext);
		instance[1] = getVariance(accModule, 0, linearAccNext);
//		instance[1] = getMean(accProjectionV, 0, linearAccNext);
		instance[2] = getMean(accProjectionV, 0, linearAccNext);
		instance[3] = getVariance(accProjectionV, 0, linearAccNext);
		instance[4] = getMean(accModuleH, 0, linearAccNext);
		instance[5] = getVariance(accModuleH, 0, linearAccNext);
		// set the indexs of sensors as 0
		linearAccNext = 0;
		gravNext = 0;

		return instance;
	}

	public float getPredif(){
//		float[] pressureModule = new float[pressureNext];
		if (pressureNext<80)
			 pressuredif=0;
		else
			pressuredif=pressure[pressureNext-1]-pressure[pressureNext-61];
//		System.out.println(pressure[pressureNext-1]);
		return pressuredif;
	
	}
	
	
	// calculate the mean of an array
	private float getMean(float[] data, int startId, int n) {
		// int n = data.length;
		float sum = 0;

		if (n == 0) { // no data
			return -1;
		}
		// calculate sum and return mean
		else {
			for (int i = startId; i < startId + n; i++)
				sum += data[i];
			return sum / n;
		}
	}

	// calculate the variance of an array
	private float getVariance(float[] data, int startId, int n) {
		// int n = data.length;
		float mean;
		float sumVariance = 0;
		if (n == 0) { // no data
			return -1;
		}
		// calculate mean and square sum of difference,return variance
		else {
			// get mean of data
			mean = getMean(data, startId, n);
			// calculate variance
			for (int i = startId; i < startId + n; i++)
				sumVariance += (data[i] - mean) * (data[i] - mean);
			return sumVariance / n;
		}
	}

	//calculate the projection of source on dst
	private float getProjection(float[] source, float[] dst){
		//get length of source and dst
		int lengthS = source.length;
		int lengthD = dst.length;
		
		if(lengthS==lengthD){
			float innerProduct = 0;
			float moduleDst = 0;
			for(int i=0;i<lengthS;i++){
				innerProduct += source[i]*dst[i];
				moduleDst += dst[i]*dst[i];
			}
			return (float) (innerProduct/Math.sqrt(moduleDst));
		}
		return 0;
	}
}
