package com.example.NLSUbiPos.context;
import com.example.NLSUbiPos.floor.*;
import com.example.NLSUbiPos.motion.SimpleMotionDetector;

import java.util.Calendar;
import com.example.NLSUbiPos.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//this is an activity which tests comprehensive contexts
public class ContextDetectActivity extends Activity implements SensorEventListener{
	private SensorManager sensorManager = null;
	private Button startButton;
	private Button stopButton;
	private Button ExitButton;
	private TextView showText;
	private EditText floorText;
	private PressureFloorDetector pfd;
	private SimpleMotionDetector md;
	private IODetector id;
	private LocationManager locationManager;
	private Thread th;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_context);
		//create new object of PressureFloorDetector, MotionDetector and IODetector
		pfd=new PressureFloorDetector();
		md=new SimpleMotionDetector();
		id=new IODetector(this);
		
		//register the locationManager
		locationManager=id.locationManager;
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		//Definition of sensors
		final Sensor pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		final Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		final Sensor linearacc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		final Sensor sensorlight = id.la.mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		
        //Register the LocationListener with the location manager service
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500, 1,locationListener); 
		
		//The text printed to screen
		showText = (TextView) findViewById(R.id.showText);
		
		//Definition of buttons
		startButton = (Button) findViewById(R.id.Start);
		stopButton = (Button) findViewById(R.id.Stop);
		ExitButton = (Button) findViewById(R.id.Exit);
		floorText=(EditText) findViewById(R.id.edittext);
		
		// The callback when StartButton is clicked
		startButton.setOnClickListener(new OnClickListener(){
			@SuppressLint("ShowToast") @Override
		public void onClick(View v) {
				
		//Registers a SensorEventListener for the given sensor.
			sensorManager.registerListener(ContextDetectActivity.this, pressure, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(ContextDetectActivity.this, gravity, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(ContextDetectActivity.this, linearacc, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(ContextDetectActivity.this, sensorlight, SensorManager.SENSOR_DELAY_GAME);				startButton.setEnabled(false);
			stopButton.setEnabled(true);
			ExitButton.setEnabled(true);
			
			//Input the initial floor number
			if(floorText.getText().toString().length()==0)
			{
			Toast.makeText(ContextDetectActivity.this, "请输入初始楼层", Toast.LENGTH_LONG).show();
			}
			else
			pfd.setinifloor(Integer.parseInt(floorText.getText().toString()));
			
			//A new thread of IOdetect
			th=new Thread(new indoordetect());
	     	th.start();	     	 
			}
		});
		
		
		// The callback when StopButton is clicked
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sensorManager.unregisterListener(ContextDetectActivity.this);
				pfd.pressureList.clear();
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				ExitButton.setEnabled(true);
			}
		});
		
		// The callback when ExitButton is clicked
		ExitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sensorManager.unregisterListener(ContextDetectActivity.this);
				finish();
			}
		});
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(ContextDetectActivity.this);
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
	}
	
    //Used for receiving notifications from the LocationManager when the location has changed.
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
	
	// another thread that start wifi scanning 
	class indoordetect implements Runnable{

		@Override
		public void run() {
			id.start();	
		}
    	
    }
	
	
	//print the context information on the phone screen
	private void updateMessage() {

		Calendar now = Calendar.getInstance();
		String time = "" + now.get(Calendar.YEAR) + ":" + (now.get(Calendar.MONTH)+1) + ":" + now.get(Calendar.DAY_OF_MONTH) + 
				":" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
		int s=md.getmotion(); 
		int floornum=pfd.getFloor();
		int iocontext=id.GetIOcontext();
		String r=null;
		String t=null;
		switch(s){
		case(0):
			r="walk";
		    break;
		case(1):
		    r="still";
		    break;
		case(2):
			r="elevator up";
		    break;
		case(3):
			r="elevator down";
		    break;
		case(4):
			r="upstairs";
		    break;
		case(5):
			r="downstairs";
		    break; 
		}
		
		switch(iocontext){
		case(0):
			t="Outdoor";
			break;
		case(1):
			t="Indoor";
		    break;
		}
		
	    showText.append(time+"\n"+"->"+t+"\n"+"->Floornum="+floornum+"\n"+"->"+r+"\n");	    

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		md.onSensorChanged(event);
		pfd.onSensorChanged(event);
		id.onSensorChanged(event);
		//Call the updateMeaasge about per 2 seconds
		if(pfd.getpresize()>=80&&md.getgraSize()>=100 && md.getLinaccSize()>=100 ) {
			updateMessage();
			pfd.notifyFloorEvent(pfd.getFloor());
		}
		
		
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO
		
	}
}
