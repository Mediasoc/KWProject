package com.example.NLSUbiPos.wireless;

import com.example.NLSUbiPos.coordinate.Mercator;

/**
 * Interface definition for a callback to be invoked when the wireless access point position
 * is available. The access point must accord with some conditions.
 */
public interface OnWirelessPositionListener {
	/**
	 * Called when the wireless access point position is available.
	 * @param coordinate the coordinate of the access point
	 */
	public void onWirelessPosition(Mercator mercator);
}
