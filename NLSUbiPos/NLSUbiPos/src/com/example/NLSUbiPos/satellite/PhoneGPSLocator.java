package com.example.NLSUbiPos.satellite;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

public class PhoneGPSLocator extends GPSLocator{

	private Location mLocation;
	private LocationManager locationManager;
	
	public PhoneGPSLocator(Context context){
		this.context = context;
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void startLocating(){
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			getLocation();
		}else{
			toggleGPS();
			new Handler().postDelayed(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					getLocation();
				}
				
			}, 2000);
		}
	}
	
	public void stopLocating(){
		locationManager.removeUpdates(locationListener);
	}
	
	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if(location != null){
				notifyGPSPosition(location);
			}
		}

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
		
	};
	
	private void toggleGPS() {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
		}
		}
	
	private void getLocation(){
		mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(mLocation == null){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
		}
	}

}
