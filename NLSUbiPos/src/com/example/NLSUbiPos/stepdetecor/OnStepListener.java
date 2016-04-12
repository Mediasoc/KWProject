package com.example.NLSUbiPos.stepdetecor;

/**
 * Interface definition for a callback to be invoked when a step event occurs.
 */
public interface OnStepListener {
	
	/**
	 * Called when a step event occurs.
	 * @param event the step event including step information
	 */
	public void onStep(StepEvent event);
}
