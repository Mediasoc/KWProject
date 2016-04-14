package com.example.NLSUbiPos.satellite;

import com.example.NLSUbiPos.coordinate.Mercator;

public interface  OnGPSPositionListener {
	
	public void onGPSPosition(Mercator mercator);

}
