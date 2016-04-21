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
	
	//check the GPS is OK or not
	private boolean GPSOK;
	
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
	LocationManager locationManager;
	
	//the object of LightAdmin and WifiAdmin
    LightAdmin la; 
    WifiAdmin wa;
//    GPSAdmin ga;
    
//    private Thread th;

    LocationListener locationListener;
    
//    private Sensor sensorlight; 
    
//    private SensorManager sensorManager;
    
//    private WifiManager wifiManager;
        
    private boolean Stopped=true;
    
	private boolean Started=false;
	
	//the Constructor of this class, initialization of LightAdmin, WifiAdmin
	public IODetector(Context context){
		la=new LightAdmin(context);
		wa=new WifiAdmin(context);
		locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addGpsStatusListener(listener);
	}
	
//	
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
                }   
                if (count==0) GPSSNR=0;
                else GPSSNR=E/count;
                GPSN=count;
                System.out.println("Satelite N:"+count+", SNR:"+GPSSNR);
                GPSOK=true;
                
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
            }
		}
	};
	
	
	public void start() {
		Started=true;
//		sensorManager=la.mSensorManager;
//		sensorlight=la.sensorlight;
//		wifiManager=wa.mWifiManager;
			wa.WifiScanLock();
			while(true){			
				wa.StartScan();
				try {
					Thread.sleep(1000);
					if (!Started) {
						Stopped=true;
						locationManager.removeUpdates(locationListener);
						return;
					}
					List<ScanResult> wifiList = wa.GetWifiList();
					wifiN=wa.GetWifiNumber();
					wifimean=wa.GetWifiMean();
					wifistd=wa.GetWifiStd();
					iocontext=GetIOcontext();
//					System.out.println(wifimean);
				
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	public void stop(){
		if (!Started) return;
		Started=false;
		la.mSensorManager.unregisterListener(this);
	}
	
	
	
	public int GetIOcontext(){
		if(la.GetLight()>1000)
		   {
			iocontext=0; 
			}
		else if (GPSSNR<16||maxSNR<28||GPSN<6)
		   {iocontext=1;}
		else if (wifiN*0.11+wifimean*-0.07524+wifistd*-0.12+GPSN*-0.59+GPSSNR*-0.14>-9.82)
		   {
			iocontext=1;
			}
		else
		{ iocontext=0;}
	    return iocontext;	
	   
		
	}

//	public int GetIOcontext(){
//		if(la.GetLight()>1000)
//		   {
//			iocontext=0; 
//			}
//		else if (GPSSNR<16||maxSNR<28||GPSN<6)
//		   {iocontext=1;}
//		else if (wifiN*0.11+wifimean*-0.07524+wifistd*-0.12+GPSN*-0.59+GPSSNR*-0.14>-9.82)
//		   {
//			iocontext=1;
//			}
//		else
//		{ iocontext=0;}
//		System.out.println(maxSNR);
//	    return iocontext;	
//	   
//		
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
