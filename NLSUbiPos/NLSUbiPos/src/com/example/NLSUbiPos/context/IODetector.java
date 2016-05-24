package com.example.NLSUbiPos.context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

//the class to detect the user is indoor or outdoor
public class IODetector extends ContextDetector {

	//the number of GPS satellite
	private int GPSN;
	
	//the mean SNR of the GPS signal
	private double GPSSNR;
	
	//the maximum SNR of GPS signal
	private double maxSNR;
	
	//the number of WiFi aps 
	private int wifiN;
	
	//the standard deviation of wifi rssi
	private double wifistd;
	
	//the average of rssi
	private double wifimean;
	
	//0 represents indoor and 1 represents outdoor
	private int iocontext; 
	
	//register the locationManager
	public LocationManager locationManager;
	
	//the object of LightAdmin and WifiAdmin
    LightAdmin la; 
    WifiAdmin wa;
    
    //Used for receiving notifications from the LocationManager when the location has changed.
//    LocationListener locationListener;
    
	//the Constructor of this class, initialization of LightAdmin, WifiAdmin
	public IODetector(Context context){
		la=new LightAdmin(context);
		wa=new WifiAdmin(context);
		locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addGpsStatusListener(listener);
	}
	
//	This listener is used for receiving notifications when GPS status has changed.
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
            switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus gpsStatus=locationManager.getGpsStatus(null);
                int maxSatellites = gpsStatus.getMaxSatellites();
                Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                double E=0;
                int count = 0;
                while (iters.hasNext() && count <= maxSatellites) {     
                    GpsSatellite s = iters.next();
                    if(s.usedInFix()){
                    E+=s.getSnr();
                    count++;    } 
                    if (s.getSnr() > maxSNR){
		        		   maxSNR = (int) s.getSnr();	
		        	   }   
//                    System.out.println(maxSNR);
                }   
                if (count==0) GPSSNR=0;
                else GPSSNR=E/count;
                GPSN=count;
//                System.out.println("Satelite N:"+count+", SNR:"+GPSSNR);
         
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
            }
		}
	};
	
// Start the WiFi scanning and get the iocontext
	public void start() {
			wa.WifiScanLock();
			while(true){			
				wa.StartScan();
				try {
					Thread.sleep(1000);
					List<ScanResult> wifiList = wa.GetWifiList();
					wifiN=wa.GetWifiNumber();
					wifimean=wa.GetWifiMean();
					wifistd=wa.GetWifiStd();
//					iocontext=GetIOcontext();
					notifyContextEvent(GetIOcontext());
//					 System.out.println(GetIOcontext());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	
	public void stop(){
		la.mSensorManager.unregisterListener(this);
		wa.mWifiManager.setWifiEnabled(false);
//		locationManager.removeUpdates(locationListener);
		
	}
	
	
	// return the context of indoor or outdoor,0 means outdoor and 1 means indoor
	
	 public int GetIOcontext(){
	
		if(la.GetLight()>1000)
		   {
			iocontext=0; 
			}
		else if (GPSSNR<16||maxSNR<28||GPSN<7)
		   {
			iocontext=1;
			}
		else if (wifiN*0.11+wifimean*-0.07524+wifistd*-0.12+GPSN*-0.59+GPSSNR*-0.14>-9.82)
		   {
			iocontext=1;
			}
		else
		{ iocontext=0;}
	    return iocontext;		
	}
	
	//another method of indoor detect using confidence level
//	public int GetIOcontext(){
//		double light=0;
//		double gps=0;
//		double snr=0;
//		double wifi=0;
//		if(la.GetLight()>1000)
//		   {
//			light=1;
//			}
//		if(la.GetLight()<=1000&&la.GetLight()>500)
//		   {
//			light=0.5;
//			}
//		if(la.GetLight()<=500)
//		   {
//			light=0;
//			}
//		if(GPSN<7)
//		{
//			gps=-0.5;
//		}
//		if(GPSN>=8&&GPSN<=10)
//		{
//			gps=0;
//		}
//		if(GPSN>=11)
//		{
//			gps=0.5;
//		}
//		if(GPSSNR<14)
//		{
//			snr=-0.5;
//		}
//		if(GPSSNR>=14&&GPSSNR<18)
//		{
//			snr=-0.3;
//		}
//		if(GPSSNR>=18&&GPSSNR<21)
//		{
//			snr=0;
//		}
//		if(GPSSNR>=21&&GPSSNR<24)
//		{
//			snr=0.3;
//		}
//		if(GPSSNR>=24)
//		{
//			snr=0.5;
//		}
//		
//		if(wifimean<68)
//		{
//			wifi=-1;
//		}
//		
//		if(wifimean>=68&&wifimean<76)
//		{
//			wifi=-0.5;
//		}
//		
//		if(wifimean>=76)
//		{
//			wifi=0;
//		}
//	
//		iocontext=(light+gps+wifi+snr>0)?0:1;
//	    return iocontext;		
//	}
	

	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
				la.onSensorChanged(event);
				//la.SetLight( event.values[0]);
				
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自动生成的方法存根
		
	}

}
