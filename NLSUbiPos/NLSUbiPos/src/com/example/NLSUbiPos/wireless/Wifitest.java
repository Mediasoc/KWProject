package com.example.NLSUbiPos.wireless;


import com.example.NLSUbiPos.position.PositionClient;
import com.example.NLSUbiPos.position.PositionTest;
import com.example.NLSUbiPos.R;

import android.app.Activity;
import android.os.Bundle;

public class Wifitest extends Activity {
	
	private PositionClient positionclient;
	private WifiLocator wifilocator;
	private PositionTest position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifitest);
		
		positionclient=new PositionClient(this);
		wifilocator=new WifiLocator(this);
		position=new PositionTest();
		
		positionclient.getFloorDetector().addOnFloorListener(wifilocator);
		positionclient.getFloorDetector().setinifloor(3);
		wifilocator.addOnWirelessPositionListener(position);
		positionclient.getFloorDetector().addOnFloorListener(position);
		positionclient.registerSensorListener();
		
		wifilocator.startLocating(1500, 0);
		
	}
	
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
