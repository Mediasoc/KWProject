package com.example.NLSUbiPos.context;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

public class IndoorDetector {
	private LocationManager locationManager;
	private LocationListener locationListener;
	private int GPSN;
	private double GPSSNR;
	private boolean GPSOK;
	private double maxSNR;
	private LightAdmin la;
	private WifiAdmin wa;
	
	public IndoorDetector(Context context){
		la=new LightAdmin(context);
		wa=new WifiAdmin(context);
		
		
	}
	
}
