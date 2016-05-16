package com.example.NLSUbiPos.context;


import java.util.Timer;
import java.util.TimerTask;

import com.example.NLSUbiPos.position.PositionClient;
import com.example.fusionnavigation.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

// This is an example of IODetector.java
public class IOtest extends Activity implements SensorEventListener{
	private IODetector id;
    Button start;
    TextView testtext;
    Thread th;
    Thread th1;
    int context;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    int lightsize=0;
    private PositionClient pc;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iotest);
//		pc=new PositionClient(this);
	    id=new IODetector(this);
//	    sensorManager=id.la.mSensorManager;
	    locationManager=id.locationManager;
	    final Sensor sensorlight = id.la.mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		start=(Button)findViewById(R.id.starttest);
		testtext=(TextView) findViewById(R.id.result_test);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500, 1,locationListener);  
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		     sensorManager.registerListener(IOtest.this, sensorlight, SensorManager.SENSOR_DELAY_GAME);
				th=new Thread(new indoordetect());
	     		th.start();	     	    
	     		handler.postDelayed(runnable, 2000);
				start.setEnabled(false);			 
			}
		});
		
		
	}
	
	
	private LocationListener locationListener=new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}
	};
	
	// update message per 2 seconds
	Handler handler=new Handler();  
	Runnable runnable=new Runnable() {  
	    @Override  
	    public void run() {  
	        // TODO Auto-generated method stub  
	    	testtext.append(id.GetIOcontext()+"\n");
	        handler.postDelayed(this, 2000);  
	    }  
	};  
		
	
	
    class indoordetect implements Runnable{

		@Override
		public void run() {
			id.start();	
//			testtext.append(context+"\n");
		}
    	
    }
    
 
    
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {
		case Sensor.TYPE_LIGHT:
			lightsize++;
			id.onSensorChanged(event);
			break;	
	}
		if(lightsize>=20 ) {
			updateMessage();
		}
	}

	
	private void updateMessage() {
		// TODO 自动生成的方法存根
		lightsize=0;
//		testtext.append(id.GetIOcontext()+"\n");
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自动生成的方法存根
		
	}


	
}
