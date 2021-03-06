package com.example.NLSUbiPos;


import com.example.NLSUbiPos.building.Building;
import com.example.NLSUbiPos.map.BaseMapView;
import com.example.NLSUbiPos.map.VectorMapView;
import com.example.NLSUbiPos.position.ParticlePosition;
import com.example.NLSUbiPos.position.PositionClient;
import com.example.NLSUbiPos.satellite.GaodeLocator;
import com.example.NLSUbiPos.satellite.PhoneGPSLocator;
import com.example.NLSUbiPos.wireless.WifiLocator;
import com.example.NLSUbiPos.R;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";
	private PositionClient positionclient;
	private ParticlePosition position;
	private WifiLocator wifilocator;
//	private GaodeLocator gaodelocator;
	private PhoneGPSLocator gpsLocator;
	private BaseMapView mapView;
	private Thread th;
	
	private Building building;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "MainActivity onCreate()");
//		setContentView(R.layout.activity_main);
		positionclient=new PositionClient(this);
		wifilocator=new WifiLocator(this);
		position=new ParticlePosition(100,750,3);
//		gaodelocator = new GaodeLocator(this);

		gpsLocator = new PhoneGPSLocator(this);
		}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.d(TAG, "MainActivity onPause()");
		positionclient.unregisterSensorListener();
		positionclient.getCompass().removeOnHeadingChangeListeners();
		positionclient.getContextDetector().removeOnContextListener();
		positionclient.getFloorDetector().removeOnFloorListener();
		positionclient.getMotionDetector().removeOnMotionListener();
		positionclient.getStepDetector().removeOnStepListeners();
		
		if (wifilocator != null) {
			wifilocator.removeOnWirelessPositionListeners();
			wifilocator.stopLocating();		
			Log.d(TAG, "wifiLocator stop");
		}
/*		
		if(gaodelocator != null){
			gaodelocator.removeOnGPSPositionListener();
			gaodelocator.stopLocating();
			Log.d(TAG, "GaodeLocator stop");
		}*/
		if(gpsLocator != null){
			gpsLocator.removeOnGPSPositionListener();
			gpsLocator.stopLocating();
			Log.d(TAG, "gpsLocator stop");
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "MainActivity onResume()");
		
		building = Building.factory("indoormaps", "wdzl_all");
		building.setCurrentFloorIndex(3);
		mapView = new VectorMapView(this);
		mapView.setBuilding(building);
		mapView.setPositionObject(position);		
		setContentView(mapView);
		position.setBuilding(building);
		
		if(wifilocator != null){
			wifilocator.startLocating(200, 0);
			wifilocator.addOnWirelessPositionListener(position);
			positionclient.getFloorDetector().addOnFloorListener(wifilocator);
			Log.d(TAG, "wifiLocator start");
		}
		/*
		if(gaodelocator != null){
			gaodelocator.startlocating();
			gaodelocator.addOnGPSPositionListener(position);
		}
		*/
		if(gpsLocator != null){
			gpsLocator.startLocating();
			gpsLocator.addOnGPSPositionListener(position);
			Log.d(TAG, "GPSLocator start");
		}
		pointLineMapThread t1=new pointLineMapThread();
		t1.start();
		th=new Thread(new indoordetect());
     	th.start();	 
     	
     	positionclient.getFloorDetector().addOnFloorListener(position);
		
		positionclient.getCompass().addOnHeadingChangeListener(position);
		
		positionclient.getStepDetector().addOnStepListener(position);
		
		positionclient.getContextDetector().addOnContextListener(position);
		
		positionclient.getMotionDetector().addOnMotionListener(position);
				
		positionclient.getContextDetector().locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500, 1,locationListener);
		positionclient.getFloorDetector().setinifloor(3);
		
		positionclient.registerSensorListener();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "MainActivity onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.set_position:
			mapView.setSettingPosition(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	class indoordetect implements Runnable{

		@Override
		public void run() {
			positionclient.getContextDetector().start();
			Log.d(TAG, "indoordetect start");
		}
    	
    }
	
	class pointLineMapThread extends Thread{
		@Override
		public void run(){
			if(building !=null){				
				Building.pointLinesMap(building);
			}
		}
	}
}
