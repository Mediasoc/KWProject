package com.example.NLSUbiPos.context;

import java.util.Iterator;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

//the class to detect the user is indoor or outdoor
public class IODetector extends ContextDetector {

	//the number of GPS satellite
	private int GPSN;
	
	//the mean SNR of the GPS signal
	private double GPSSNR;
	
	//check the GPS is OK or not
	private boolean GPSOK;
	
	//the maximum SNR of GPS signal
	private double maxSNR;
	
	//the number of WiFi aps 
	private int WifiN;
	
	//the standard deviation of wifi rssi
	private double std;
	
	//the average of rssi
	private double E;
	
	//the strength of light
	private float light;
	
	//0 means indoor and 1 means outdoor
	private int iocontext; 
	
	//register the locationManager
	private LocationManager locationManager;
	
	//register the wifiManager
    private WifiManager wifiManager;
	
	//the Constructor of this class
	public IODetector(){
		GPSN=0;
		GPSSNR=0;
		GPSOK=false;
		maxSNR=0;
		WifiN=0;
		std=0;
		E=0;
		light=0;
		iocontext=0;
	}
	
	// when GPS status changes, get the GPS information
	public void onGpsStatusChanged(int event) {
		 switch (event) {
	 case GpsStatus.GPS_EVENT_FIRST_FIX:
         break;
     case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
         GpsStatus gpsStatus=locationManager.getGpsStatus(null);
         int maxSatellites = gpsStatus.getMaxSatellites();
         Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();     
         int count = 0;
          E=0;
         while (iters.hasNext() && count <= maxSatellites) {     
             GpsSatellite s = iters.next();
             if(s.usedInFix()){
             E+=s.getSnr();
             count++;    } 
             if (s.getSnr() > maxSNR){
	        		   maxSNR = (int) s.getSnr();
	        	   }  
         }   
         if (count==0) GPSSNR=0;
         else GPSSNR=E/count;
         GPSN=count;
         GPSOK=true;
         break;
     case GpsStatus.GPS_EVENT_STARTED:
         break;
     case GpsStatus.GPS_EVENT_STOPPED:
         break;
     }
	
};

// get WiFi information
public void onWifiChanged(){
	while(true){
		wifiManager.startScan();
		
			String r;
			List<ScanResult> wifiList = wifiManager.getScanResults();
			 WifiN=wifiList.size();
			double E2=0,E=0;
			for (ScanResult sr:wifiList){
				E2+=sr.level*sr.level;
				E+=sr.level;
			}
			if (WifiN>0){
				E2/=WifiN; E/=WifiN;
				E=E*-1;
			}
			 std=Math.sqrt(E2-E*E);
			 
	}
}

// get indoor and outdoor from wifi/gps/light
	public int getIOcontext(){
		if(light>1500)
			iocontext=1;
		else if (GPSSNR<16||maxSNR<28||GPSN<6)
			iocontext=0;
		else if (WifiN*0.11+E*-0.07524+std*-0.12+GPSN*-0.59+GPSSNR*-0.14>-9.82)

			iocontext=0;	
		else
			iocontext=0;
		
		return iocontext;
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
				light=event.values[0];
				getIOcontext();
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自动生成的方法存根
		
	}

}
