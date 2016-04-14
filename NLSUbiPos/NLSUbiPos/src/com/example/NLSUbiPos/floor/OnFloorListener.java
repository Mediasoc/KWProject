package com.example.NLSUbiPos.floor;
/**
 * Interface definition for a callback to be incoked when the floor change happens
 * 
 *@author WZ
 */

public interface OnFloorListener{
	/**
	 * Called when a floor change happens
	 * @param event
	 */
	public void onFloor(int floor);
}