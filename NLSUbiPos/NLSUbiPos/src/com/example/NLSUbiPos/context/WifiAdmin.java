 package com.example.NLSUbiPos.context;
 
import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;


//this is a class which manages the WiFi and get the WiFi information
 public class WifiAdmin {
	 
//WifiManager is the API to use when performing WiFi specific operations 
 WifiManager mWifiManager;
 
 //the list that stores the scan result
 private List<ScanResult> mWifiList;
 
 //the number of WiFi AccessPoints
 private int wifiN;
 
 //the average of the square of RSSI
 private double wifiE2;
 
 //the average of RSSI
 private double wifiE;
 
 //the Standard Deviation of RSSI
 private double wifiStd;
 
 //the constructor of this class
 public WifiAdmin(Context context){
	 mWifiManager= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
 }
 
 //call this method to start WiFi scanning
 public void StartScan(){
	 mWifiManager.startScan();
	
 }
 
 //this is a method to keep the WiFi radio awake.
 public void WifiScanLock(){
	 WifiLock scanOnlyLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "scanOnly");      
	 scanOnlyLock.acquire(); 
 }
 
 //return the scan result of WiFi
 public List<ScanResult>GetWifiList(){
	 mWifiList = mWifiManager.getScanResults();
	 return mWifiList;
 }
 
 //return the number of WiFi AccessPoints
 public int GetWifiNumber(){
	 wifiN=mWifiList.size();
	 return wifiN;
 }
 
 //return the average of RSSI
 public double GetWifiMean(){
	 for (ScanResult sr:mWifiList){
			wifiE2+=sr.level*sr.level;
			wifiE+=sr.level;
		}
		if (wifiN>0){
			wifiE2/=wifiN; wifiE/=wifiN;
			wifiE=wifiE*-1;
		}
		return wifiE;
 }
 
 //return the standard deviation of RSSI
 public double GetWifiStd(){
	 wifiStd =Math.sqrt(wifiE2-wifiE*wifiE);
	 return wifiStd;
 }
 
 }
