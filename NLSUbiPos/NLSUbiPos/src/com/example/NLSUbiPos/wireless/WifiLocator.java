package com.example.NLSUbiPos.wireless;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * The wireless locator using WiFi signals.
 */
public class WifiLocator extends WirelessLocator {
	
	// manages the WiFi function
	private WifiManager wifiManager;
	
	// the received WiFi list by scanning
	private List<ScanResult> scanResults;

	/**
	 * Constructs a WiFi locator.
	 * @param context the application context
	 * @param the pathname of the file storing the WiFi access points information
	 */
	public WifiLocator(Context context, String pathname) {
		super(context, pathname);
		// gets the WiFi manager
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// opens WiFi if it is not enabled
		if (!wifiManager.isWifiEnabled()) {
			if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
				wifiManager.setWifiEnabled(true);
			}
		}
		
		// registers the WiFi results receiver
		context.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// gets the scan results
				scanResults = wifiManager.getScanResults();
				// find the strongest BLE access point
				int index = -1;
				int maxRssi = -100000000;
				for (int i=0; i<scanResults.size(); i++) {
					if (accessPointAddress.contains(scanResults.get(i).BSSID)) {
						if (scanResults.get(i).level >= maxRssi) {
							maxRssi = scanResults.get(i).level;
							index = accessPointAddress.indexOf(scanResults.get(i).BSSID);
							Log.d("wwww",""+scanResults.get(i).BSSID);
						}
					}
					Log.d("qqqq",""+scanResults.get(i).BSSID);
				}
				for (int i=0; i<accessPointAddress.size(); i++) {
					
					Log.d("wwww",""+accessPointAddress.get(i));
						
				}
				Toast.makeText(context, "size:"+scanResults.size()+" "+"rssi:"+maxRssi, Toast.LENGTH_SHORT).show();;
				// the strongest access point
				if (index>=0 && maxRssi>=-40 && times>=0) {
					// release the message
					notifyWirelessPosition(accessPointCoordinate.get(index));
					// executes the scanning for the specified times
					if (times == 1) {
						times = -1;
						stopLocating();
					} else if (times != 0) {
						times--;
					}
				}
			}
			
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	@Override
	public void startLocating(long interval, int times) {
		this.times = times;
		// gets the timer object
		timer = new Timer();
		// the task to be executed
		timerTask = new TimerTask() {

			@Override
			public void run() {
				// starts the WiFi scanning
				wifiManager.startScan();
			}
		};
		
		// executes the task periodically
		timer.schedule(timerTask, 0, interval);
	}
	
}
