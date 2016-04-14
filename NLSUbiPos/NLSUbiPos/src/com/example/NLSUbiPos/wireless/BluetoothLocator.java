package com.example.NLSUbiPos.wireless;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

/**
 * The wireless locator using Bluetooth low energy signals.
 */
@SuppressLint("NewApi")
public class BluetoothLocator extends WirelessLocator {
	
	// the list storing the bluetooth low energy devices information
	private ArrayList<BluetoothDevice> bleDevice;
	
	// the list storing the bluetooth low energy devices rssi
	private ArrayList<Integer> bleRssi;
	
	// the bluetooth adapter of the phone device
	private BluetoothAdapter bluetoothAdapter;
	
	/**
	 * Constructs a bluetooth low energy locator.
	 * @param context the given application context.
	 * @param pathname the pathname of the file storing the ble access points information
	 */
	public BluetoothLocator(Context context, String pathname) {
		super(context, pathname);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bleDevice = new ArrayList<BluetoothDevice>();
		bleRssi = new ArrayList<Integer>();
		
	}
	
	@Override
	public void startLocating(long interval, int times) {
		// limited times
		this.times = times;
		
		// the os handler
		final Handler handler = new Handler();
		
		//how long the scanning action lasts 
		final long scanDuration = interval / 2;
		
		// the timer object
		timer = new Timer();
		
		// the task to be executed periodically
		timerTask = new TimerTask() {

			@Override
			public void run() {
				
				// stop the scanning after the specified time
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// stop the scanning
						bluetoothAdapter.stopLeScan(leScanCallback);
						// find the strongest BLE access point
						int index = -1;
						int maxRssi = -100000000;
						for (int i=0; i<bleDevice.size(); i++) {
							if (accessPointAddress.contains(bleDevice.get(i).getAddress())) {
								if (bleRssi.get(i) >= maxRssi) {
									maxRssi = bleRssi.get(i);
									index = accessPointAddress.indexOf(bleDevice.get(i).getAddress());
								}
							}
						}
						
						// the strongest access point
						if (index>=0 && maxRssi>=-65 && BluetoothLocator.this.times>=0) {
							// release the message
							notifyWirelessPosition(accessPointCoordinate.get(index));
		
							// executes the scanning for the specified times
							if (BluetoothLocator.this.times == 1) {
								BluetoothLocator.this.times = -1;
								stopLocating();
							} else if (BluetoothLocator.this.times != 0) {
								BluetoothLocator.this.times--;
							}
						}
						
						// clear the lists that have been processed 
						bleDevice.clear();
						bleRssi.clear();
					}	
				}, scanDuration);
				
				// start the scanning
				bluetoothAdapter.startLeScan(leScanCallback);
			}
			
		};
		
		// executes the task periodically
		timer.scheduleAtFixedRate(timerTask, 0, interval);
		
	}
	
	/**
	 * Callback used to deliver LE scan results.
	 */
	private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
		
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// adds the device and the rssi to the lists
			if (bleDevice.contains(device)) {
				int index = bleDevice.indexOf(device);
				bleRssi.set(index, rssi);
			} else {
				bleDevice.add(device);
				bleRssi.add(rssi);
			}	
		}
	};

}