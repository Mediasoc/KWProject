package com.example.NLSUbiPos;


import com.example.NLSUbiPos.position.ParticlePosition;
import com.example.NLSUbiPos.position.PositionClient;
import com.example.NLSUbiPos.wireless.WifiLocator;
import com.example.fusionnavigation.R;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private PositionClient positionclient;
	private ParticlePosition position;
	private WifiLocator wifilocator;
	private Thread th;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		positionclient=new PositionClient(this);
		wifilocator=new WifiLocator(this);
		position=new ParticlePosition(0,0,0);
		
		
		positionclient.getFloorDetector().addOnFloorListener(position);
		positionclient.getFloorDetector().addOnFloorListener(wifilocator);
		
		positionclient.getCompass().addOnHeadingChangeListener(position);
		
		positionclient.getStepDetector().addOnStepListener(position);
		
		positionclient.getContextDetector().addOnContextListener(position);
		
		positionclient.getMotionDetector().addOnMotionListener(position);
		
		wifilocator.addOnWirelessPositionListener(position);
		
		
		
		positionclient.getContextDetector().locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500, 1,locationListener);
		positionclient.getFloorDetector().setinifloor(3);
		
		positionclient.registerSensorListener();
		wifilocator.startLocating(1500, 0);
		th=new Thread(new indoordetect());
     	th.start();	 
	}
	
	
	protected void onPause(){
		super.onPause();
		
		positionclient.unregisterSensorListener();
		positionclient.getCompass().removeOnHeadingChangeListeners();
		positionclient.getContextDetector().removeOnContextListener();
		positionclient.getFloorDetector().removeOnFloorListener();
		positionclient.getMotionDetector().removeOnMotionListener();
		positionclient.getStepDetector().removeOnStepListeners();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
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
		}
    	
    }
}
