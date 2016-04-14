package com.example.NLSUbiPos.satellite;
/**
 * this class is an extend class of GPSLocator which use Gaode Location API to do outdoor location
 * @author WZ
 */
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.example.NLSUbiPos.coordinate.Lonlat;
import com.example.NLSUbiPos.coordinate.Mercator;

public class GaodeLocator extends GPSLocator implements AMapLocationListener {
  public AMapLocationClient mLocationClient=null;
  public AMapLocationClientOption option=null;
  
  
  //location result in Mercator coordinate
  public Mercator CurrentLocationMercator;
  //location result in Lonlat coordinate
  public Lonlat CurrentLocationLonlat;
	/**
	 * constructor of GaodeLocator,context is the application context used,for example a=new GaodeLocator(this)
	 * @param context
	 */
	public GaodeLocator(Context context){
		
		
		mLocationClient=new AMapLocationClient(context);
		mLocationClient.setLocationListener(this);
		option.setLocationMode(AMapLocationMode.Hight_Accuracy);
		option.setNeedAddress(true);
		option.setOnceLocation(false);
		option.setWifiActiveScan(true);
		option.setMockEnable(false);
		option.setInterval(1000);
		mLocationClient.setLocationOption(option);
		mLocationClient.startLocation();
	}
	
	//interval control the frequency the location result is sent
	public void startlocating(long interval){
		timer=new Timer();
		timerTask=new TimerTask(){
		public void run(){
		//coordinate convert
		CurrentLocationMercator=CurrentLocationLonlat.lonlattomercator();
		notifyGPSPosition(CurrentLocationMercator);
		}
		};
		
		timer.schedule(timerTask, 0,interval);
	}
	
		
	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO 自动生成的方法存根
		CurrentLocationLonlat=new Lonlat(location.getLongitude(),location.getLatitude());
	}

}
