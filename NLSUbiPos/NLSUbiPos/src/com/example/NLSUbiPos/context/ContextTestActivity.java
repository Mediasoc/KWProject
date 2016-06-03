package com.example.NLSUbiPos.context;
import com.example.NLSUbiPos.floor.*;
import com.example.NLSUbiPos.motion.SimpleMotionDetector;
import com.example.NLSUbiPos.position.PositionClient;
import com.example.NLSUbiPos.position.PositionTest;

import java.util.Calendar;
import com.example.fusionnavigation.R;
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


//this is an activity which tests comprehensive contexts using listeners
public class ContextTestActivity extends Activity  {
	private Button startButton;
	private Button stopButton;
	private Button ExitButton;
	private TextView showText;
	private EditText floorText;
	private Thread th;
	private PositionClient pc;
	private PositionTest pt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_context);
		//create new object of PressureFloorDetector, MotionDetector and IODetector
		pc=new PositionClient(this);
		pt=new PositionTest();
		pc.getFloorDetector().addOnFloorListener(pt);
		pc.getMotionDetector().addOnMotionListener(pt);
		pc.getContextDetector().addOnContextListener(pt);
	
		
        //Register the LocationListener with the location manager service
		pc.getContextDetector().locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500, 1,locationListener); 
		
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
			pc.registerSensorListener();
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			ExitButton.setEnabled(true);
			
			//Input the initial floor number
			if(floorText.getText().toString().length()==0)
			{
			Toast.makeText(ContextTestActivity.this, "请输入初始楼层", Toast.LENGTH_LONG).show();
			}
			else
			pc.getFloorDetector().setinifloor(Integer.parseInt(floorText.getText().toString()));
			
			//A new thread of IOdetect
			th=new Thread(new indoordetect());
	     	th.start();	     
	     	
	     	//使用线程控制屏幕显示时间
	     	Thread1 th1=new Thread1();
	     	th1.start();
			}
		});
		
		
		// The callback when StopButton is clicked
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				sensorManager.unregisterListener(ContextTestActivity.this);
				pc.unregisterSensorListener();
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
	class Thread1 extends Thread{
		public void run(){
			int i=0;
			for(i=0;i<1000;i++){
			try{
				sleep(2000);
				updateMessage();
				}
			catch(Exception e){
			}
			}
		}
	}
	
	
	
	//print the context information on the phone screen
	private void updateMessage() {

		Calendar now = Calendar.getInstance();
		String time = "" + now.get(Calendar.YEAR) + ":" + (now.get(Calendar.MONTH)+1) + ":" + now.get(Calendar.DAY_OF_MONTH) + 
				":" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
		int s=pt.motion;
		int floornum=pt.floor;
		int iocontext=pt.iocontext;

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
		
		System.out.println(t);
	    showText.append(time+"\n"+"->"+t+"\n"+"->Floornum="+floornum+"\n"+"->"+r+"\n");	    

	}


}