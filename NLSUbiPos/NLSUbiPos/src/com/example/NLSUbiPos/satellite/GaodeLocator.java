package com.example.NLSUbiPos.satellite;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.example.NLSUbiPos.coordinate.Lonlat;
import com.example.NLSUbiPos.coordinate.Mercator;
import com.pop.android.common.util.EvilTransform;
import com.pop.android.common.util.PublicLib;
import com.pop.android.location.WzSDKManagerFactory;
import com.pop.android.location.api.WzSdkConfig;
import com.pop.android.location.api.outloc.WzChipDiffConfig;
import com.pop.android.location.api.outloc.WzNtripSetting;
import com.pop.android.location.api.outloc.WzOutLocation;
import com.pop.android.location.api.outloc.WzOutLocationListener;
import com.pop.android.location.api.outloc.WzOutLocationManager;

public class GaodeLocator extends GPSLocator implements AMapLocationListener {
  public AMapLocationClient mLocationClient=null;
  public AMapLocationClientOption option=null;
    
  //location result in Mercator coordinate
  public Mercator CurrentLocationMercator;
  //location result in Lonlat coordinate
  public Lonlat CurrentLocationLonlat;
  
	//SDK配置管理器
	private WzOutLocationManager mWzOutLocationManager;
	//NtripServer mountpoint
    private final String DEFAULT_MOUNTPOINT = "RTCM32_GGB";
    //NtripServer format
    private final String DEFAULT_FORMAT =  "RTCM3";
    //应用申请秘钥
    private static final String APP_SECRET = "a066306b2609e9ab5fa0f45dbb2ec9c4fd94638e6a12985bc2f0a986997ff8a2";
    //已配对蓝牙的MAC地址. 务必先配对
    private final String btAddress = "00:11:67:11:11:4A";
    
    private WzOutLocationListener mWzOutLocationListener;
  
	/**
	 * constructor of GaodeLocator,context is the application context used,for example a=new GaodeLocator(this)
	 * @param context
	 */
	public GaodeLocator(Context context){
		
		this.context = context;
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
		//设置NtripSetting
    	WzNtripSetting ntripSetting = new WzNtripSetting("ntrip.qxwz.com",8001,DEFAULT_MOUNTPOINT,DEFAULT_FORMAT,"P_LDHQXWZ1","5efe0d6");
        //设置WzChipDiffConfig
        WzSdkConfig mWzSdkConfig =new  WzChipDiffConfig (PublicLib.getAppKey(context), APP_SECRET, null,btAddress,ntripSetting);
        //初始化SDK管理器
        mWzOutLocationManager = WzSDKManagerFactory.getWzOutLocationManager(context, mWzSdkConfig);
        //初始化位置监听
        mWzOutLocationListener = new WzOutLocationListener() {
            @Override
            public void onLocationChanged(WzOutLocation wzOutLocation) {
              //接收位置数据
            	Log.d("WzOutLocator", "onLocationChanged()"
                		+ ""+wzOutLocation.getLongitude()+" "+wzOutLocation.getLatitude());
            	Location location = EvilTransform.transform(wzOutLocation);

        		notifyGPSPosition(location);
            }

            @Override
            public void onStatusChanged(int i) {

            }

        };
        //获取location
        mWzOutLocationManager.requestLocationUpdates(0, 0, mWzOutLocationListener, new Handler(), null);
		
		
		
//		timer=new Timer();
//		timerTask=new TimerTask(){
//		public void run(){
		//coordinate convert
//		CurrentLocationMercator=CurrentLocationLonlat.lonlattomercator();
//		notifyGPSPosition(CurrentLocationMercator);
//		}
//		};
		
//		timer.schedule(timerTask, 0,interval);
	}
	
		
	@Override
	public void onLocationChanged(AMapLocation location) {
		
		CurrentLocationLonlat=new Lonlat(location.getLongitude(),location.getLatitude());
	}

}
