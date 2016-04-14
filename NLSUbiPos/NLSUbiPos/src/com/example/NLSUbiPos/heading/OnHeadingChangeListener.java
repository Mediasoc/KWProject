package com.example.NLSUbiPos.heading;

/**
 * Interface definition for a callback to be invoked when the heading is changed.
 */
public interface OnHeadingChangeListener {
	
	/**
	 * Called when the heading is changed.
	 * @param heading the changed heading
	 */
	public void onHeadingChange(double heading);
}
