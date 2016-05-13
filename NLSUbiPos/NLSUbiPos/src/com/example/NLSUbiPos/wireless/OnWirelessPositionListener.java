package com.example.NLSUbiPos.wireless;

import java.util.List;

import com.example.NLSUbiPos.coordinate.Mercator;
import com.example.NLSUbiPos.wireless.PositionProb;

/**
 * Interface definition for a callback to be invoked when the wireless access point position
 * is available. The access point must accord with some conditions.
 */
public interface OnWirelessPositionListener {
	/**
	 * Called when the wireless access point position is available.
	 * @param coordinate the coordinate of the access point
	 */
	public void onWirelessPosition(List<PositionProb> list);
}
