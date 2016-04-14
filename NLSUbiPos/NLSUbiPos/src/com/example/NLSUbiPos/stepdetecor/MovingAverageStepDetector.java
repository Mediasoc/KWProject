package com.example.NLSUbiPos.stepdetecor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * This class is a step detector using moving windows. <br>
 * A step point is the start or the end of a step. And a step is the block between two
 * step points.
 */
public class MovingAverageStepDetector extends StepDetector {
	
	// the moving window with a short window size
	private MovingWindow shortMovingWindow;
	
	// the moving window with a long window size
	private MovingWindow longMovingWindow;
	
	// the window recording data after a step point
	// a StepWindow between two step points may be a valid step
	private StepWindow stepWindow;
	
	private float shortWindowSize;
	private float longWindowSize;
	private float energyThreshold;
	
	// the timestamp of the last sensor event (accelerometer)
	private long lastSensorTimestamp;
	
	// the amplitude of the last accelerometer sensor event
	private float lastAccAmp;
	
	// the amplitude of the current accelerometer sensor event
	private float currentAccAmp;
	
	// the average value of the short moving window
	private float shortWindowAverage;
	
	// the average value of the long moving window
	private float longWindowAverage;
	
	// the accumulated energy of the StepWindow (after a step point)
	private float stepWindowEnergy;
	
	// if a step point is detected
	private boolean stepDetected;
	
	// the old swap state of the short moving window and the long moving window
	private boolean oldSwapState;
	
	// the new swap state of the short moving window and the long moving window
	private boolean newSwapState;
	
	// a SensorSample recording the time interval and value
	private SensorSample sensorSample;
	
	// the default size of the short window
	private static final float SHORT_WINDOW_SIZE = 0.2f;
	
	// the default size of the long window
	private static final float LONG_WINDOW_SIZE = 1.0f;
	
	// the default energy threshold of judging a step 
	private static final float ENERGY_THRESHOLD = 0.025f;
	
	// the number of the nanosecond in a step
	private static final long NANO = (long) Math.pow(10, 9);
	
	/**
	 * Constructors using the default short window size, long window size and energy threshold.
	 */
	public MovingAverageStepDetector() {
		this(SHORT_WINDOW_SIZE, LONG_WINDOW_SIZE, ENERGY_THRESHOLD);
	}
	
	/**
	 * Constructor using the given values.
	 * @param shortWindowSize the size of the short window.
	 * @param longWindowSize the size of the long window.
	 * @param energyThreshold the energy threshold of judging a step
	 */
	public MovingAverageStepDetector(float shortWindowSize, float longWindowSize, float energyThreshold) {
		shortMovingWindow = new MovingWindow(shortWindowSize);
		longMovingWindow = new MovingWindow(longWindowSize);
		stepWindow = new StepWindow();
		lastSensorTimestamp = 0;
		oldSwapState = true;
		this.energyThreshold = energyThreshold;
	}

	/**
	 * Processes the accelerometer event received from the phone sensor.
	 * @param event the received accelerometer event
	 */
	private void processAccelerometerEvent(SensorEvent event) {
		// deals with the first sensor event
		if (lastSensorTimestamp == 0) {
			// records timestamp and calculates the accelerometer amplitude
			lastSensorTimestamp = event.timestamp;
			lastAccAmp = (float) Math.sqrt(event.values[0]*event.values[0]
					+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
			return;
		} else{
			// calculates the accelerometer amplitude
			currentAccAmp = (float) Math.sqrt(event.values[0]*event.values[0]+
					event.values[1]*event.values[1]+event.values[2]*event.values[2]);
			// constructs a SensorSample using the sensor timestamp interval and 
			// the mean value of accelerometer amplitude
			sensorSample = new SensorSample(((float)(event.timestamp-lastSensorTimestamp))/NANO,
					(lastAccAmp+currentAccAmp)/2);
			// adds the SensorSample to the short and long moving windows
			shortMovingWindow.add(sensorSample);
			longMovingWindow.add(sensorSample);
			
			// gets the average values of the short and long moving windows
			shortWindowAverage = shortMovingWindow.getAverage();
			longWindowAverage = longMovingWindow.getAverage();
			
			// gets the swap state of the short and long moving windows
			newSwapState = shortWindowAverage > longWindowAverage;
			// step point detection state is initialized as false
			stepDetected = false;
			// There are two swap point of the short and long window within a step.
			// One is the point where the average value of the short window begins to be larger than that of the long window.
			// And the other is the point where the average value of the short window begins to be smaller than that of the long window.
			// This two points correspond to the rising and falling phase of the acceleration during a step. (fake steps)
			if (newSwapState != oldSwapState) {
				if (newSwapState) {
					stepDetected = true;
				}
				oldSwapState = newSwapState;
			}
			
			// adds two SensorSamples to the StepWindow
			// The first SensorSample records the original acceleration amplitude.
			// The second SensorSample records the value difference of the averages of short and long window.
			stepWindow.add(new SensorSample[]{sensorSample,
					new SensorSample(((float)(event.timestamp-lastSensorTimestamp))/NANO, shortWindowAverage-longWindowAverage)});
			// gets the accumulated energy after the last step point
			stepWindowEnergy = stepWindow.getEnergy();
			
			// judges if it is a step
			if (stepDetected && stepWindowEnergy>=energyThreshold && 
					stepWindow.getDuration()>0.33 && stepWindow.getDuration()<2.0 && 
					stepWindow.getPeakValue()-longWindowAverage>0.2 && 
					longWindowAverage-stepWindow.getValleyValue()>0.7) {
				// step occur
				long timeStamp = event.timestamp;
				double duration = stepWindow.getDuration();
				double stepLength = StepLengthEstimator.binaryLinearStepLength(1/duration, stepWindow.getVariance());
				
				notifyStepEvent(new StepEvent(timeStamp, duration, stepLength));
			}
			
			// resets the StepWindow if a step point is detected
			if (stepDetected) {
				stepWindow.reset();
			}
			lastSensorTimestamp = event.timestamp;
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				processAccelerometerEvent(event);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	
}
