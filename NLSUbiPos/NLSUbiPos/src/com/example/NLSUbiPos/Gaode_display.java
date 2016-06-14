package com.example.NLSUbiPos;

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
		position=new ParticlePosition(0,0,0);
		
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(31.194,121.32), 19);
		 amap.moveCamera(update);
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
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "MainActivity onResume()");
		
		building = Building.factory("indoormaps", "wdzl3L");
		building.setCurrentFloorIndex(3);
		
		position.setBuilding(building);
		
		if(wifilocator != null){
			wifilocator.startLocating(200, 0);
			positionclient.getFloorDetector().addOnFloorListener(wifilocator);
			Log.d(TAG, "wifiLocator start");
		}
		
		th=new Thread(new indoordetect());
     	th.start();	 
     	
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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
	
	

}
