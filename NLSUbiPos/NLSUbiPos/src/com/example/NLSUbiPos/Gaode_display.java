package com.example.NLSUbiPos;

import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.example.NLSUbiPos.MainActivity.indoordetect;
import com.example.NLSUbiPos.MainActivity.pointLineMapThread;
import com.example.NLSUbiPos.building.Building;
import com.example.NLSUbiPos.coordinate.Lonlat;
import com.example.NLSUbiPos.coordinate.Mercator;
import com.example.NLSUbiPos.map.BaseMapView;
import com.example.NLSUbiPos.map.VectorMapView;
import com.example.NLSUbiPos.position.ParticlePosition;
import com.example.NLSUbiPos.position.PositionClient;
import com.example.NLSUbiPos.wireless.WifiLocator;
import com.example.NLSUbiPos.R;


import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Gaode_display extends Activity {
	private final static String TAG = "MainActivity";
	private PositionClient positionclient;
	private ParticlePosition position;
	private WifiLocator wifilocator;
	private MapView mapview;
	private AMap amap;
	private Thread th;
	private Timer timer;
	private TimerTask timertask;
	private Lonlat lonlatposition;
	private Mercator mercatorposition;
	private MarkerOptions options;
	
	private double x=13505888.973;
	private double y=3658005.876;
	
	
	private Building building;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "MainActivity onCreate()");
		setContentView(R.layout.activity_gaode_display);
		 mapview=(MapView)findViewById(R.id.map);
		 mapview.onCreate(savedInstanceState);
		 
		 amap=mapview.getMap();
		 amap.showIndoorMap(true);
		 
		positionclient=new PositionClient(this);
		wifilocator=new WifiLocator(this);
		position=new ParticlePosition(111.14,776.27,3);
		
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(31.023,121.44), 19);
		 amap.moveCamera(update);
		}
	
	@Override
	protected void onPause(){
		super.onPause();
		mapview.onPause();
		
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
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mapview.onResume();
		Log.d(TAG, "MainActivity onResume()");
		
		building = Building.factory("indoormaps", "wdzl_all");
		building.setCurrentFloorIndex(3);
		
		position.setBuilding(building);
		
		if(wifilocator != null){
			wifilocator.startLocating(200, 0);
			positionclient.getFloorDetector().addOnFloorListener(wifilocator);
			Log.d(TAG, "wifiLocator start");
		}
		pointLineMapThread t1=new pointLineMapThread();
		t1.start();
		th=new Thread(new indoordetect());
     	th.start();	 
     	start();
     	
     	positionclient.getFloorDetector().addOnFloorListener(position);
		
		positionclient.getCompass().addOnHeadingChangeListener(position);
		
		positionclient.getStepDetector().addOnStepListener(position);
		
		positionclient.getContextDetector().addOnContextListener(position);
		
		positionclient.getMotionDetector().addOnMotionListener(position);
		
		wifilocator.addOnWirelessPositionListener(position);
				
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
		mapview.onDestroy();
	}
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.floor_settings_1) {
			positionclient.getFloorDetector().setinifloor(1);
			return true;
		}
		if (id == R.id.floor_settings_2) {
			positionclient.getFloorDetector().setinifloor(2);
			return true;
		}
		if (id == R.id.floor_settings_3) {
			positionclient.getFloorDetector().setinifloor(3);
			return true;
		}
		if (id == R.id.floor_settings_4) {
			positionclient.getFloorDetector().setinifloor(4);
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
	public void start(){
		timer=new Timer();
		timertask=new TimerTask(){

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				amap.clear();
				
				if(position.getLonlatPosition().getlat()!=0&&position.getLonlatPosition().getlon()!=0){
					
					//mercatorposition=new Mercator(x,y);
					//lonlatposition=mercatorposition.mercatortolonlat();
					options=new MarkerOptions();
					if(position.motionLabel==3||position.motionLabel==2){
						options.position(new LatLng(31.023090,121.444127));}
					else{
					options.position(new LatLng(position.getLonlatPosition().getlat(),position.getLonlatPosition().getlon()));}
					options.title("当前位置");
					amap.addMarker(options);
					CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(position.getLonlatPosition().getlat(),position.getLonlatPosition().getlon()), 19);
					 amap.moveCamera(update);
				}
				
			}
			
		};
		timer.schedule(timertask,0,500);
	}
	
	public void stop() {
		if (timer != null) {
			// cancels the scanning task
			timer.cancel();
			timer = null;
		}
	}
	
	

}
