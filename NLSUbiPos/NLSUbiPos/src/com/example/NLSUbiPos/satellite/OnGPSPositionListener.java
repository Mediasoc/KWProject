package com.example.NLSUbiPos.satellite;

import android.location.Location;

// Edited by Liu on 20160513

public interface  OnGPSPositionListener {
	
	public void onGPSPosition(Location location);
	//changed by Liu
//	public void onGPSPosition(Mercator mercator);

}
