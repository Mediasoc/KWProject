package com.example.NLSUbiPos.context;
import com.example.NLSUbiPos.floor.*;
import com.example.NLSUbiPos.motion.SimpleMotionDetector;
import com.example.NLSUbiPos.position.PositionClient;
import com.example.NLSUbiPos.position.PositionTest;

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
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


//this is an activity which tests comprehensive contexts using listeners
public class IndoorCheck extends Activity  {
	private Button startButton;
	private Button stopButton;
	private Button ExitButton;
	private TextView showText;
	private ImageView img;
	private Thread th;
	private PositionClient pc;
	private PositionTest pt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indoorcheck);
		//create new object of PressureFloorDetector, MotionDetector and IODetector
		pc=new PositionClient(this);
		pt=new PositionTest();
		pc.getFloorDetector().addOnFloorListener(pt);
		pc.getMotionDetector().addOnMotionListener(pt);
		pc.getContextDetector().addOnContextListener(pt);
		
        //Register the LocationListener with the location manager service
		pc.getContextDetector().locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500, 1,locationListener); 
		
		//The text printed to screen
		showText = (TextView) findViewById(R.id.textview);
		img = (ImageView) findViewById(R.id.iocontext);
		
		//Definition of buttons
		startButton = (Button) findViewById(R.id.Startbutton);
		stopButton = (Button) findViewById(R.id.Stopbutton);
		ExitButton = (Button) findViewById(R.id.Exitbutton);

		
		// The callback when StartButton is clicked
		startButton.setOnClickListener(new OnClickListener(){
			@SuppressLint("ShowToast") @Override
		
			public void onClick(View v) {	
			pc.registerSensorListener();
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			ExitButton.setEnabled(true);
			
		
			
			//A new thread of IOdetect
			th=new Thread(new indoordetect());
	     	th.start();	     
	     	
	     	//使用线程控制屏幕显示时间
	   handler.postDelayed(runnable, 4000);
	  
			}
		});
		
		
		// The callback when StopButton is clicked
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				sensorManager.unregisterListener(ContextTestActivity.this);
				pc.unregisterSensorListener();
	            handler.removeCallbacks(runnable);
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				ExitButton.setEnabled(true);
			}
		});
		
		// The callback when ExitButton is clicked
		ExitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pc.unregisterSensorListener();
				finish();
			}
		});
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		pc.unregisterSensorListener();
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
			pc.getContextDetector().start();	
		}
    	
    }
	
	
	//2s显示一次
	Handler handler=new Handler();  
	Runnable runnable=new Runnable() {  
	    @Override  
	    public void run() {  
	        // TODO Auto-generated method stub  
	    	updateMessage();
	        handler.postDelayed(this, 2000);
	        
	    }  
	};  

	
	
	
	//print the context information on the phone screen
	private void updateMessage() {

		Calendar now = Calendar.getInstance();
		String time = ""+ now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
//		int s=pt.motion;
//		int floornum=pt.floor;
		int iocontext=pt.iocontext;
		String t=null;			
		switch(iocontext){
		case(0):
			t="您在室外";
	     	img.setImageResource(R.drawable.out);
			break;
		case(1):
			t="您在室内";
		    img.setImageResource(R.drawable.in);
		    break;
		case(2):
			t="您在交界区";
		    img.setImageResource(R.drawable.buffered);
		    break;
		}
		

	    showText.append(time+"->"+t+"\n");	    

	}


}
