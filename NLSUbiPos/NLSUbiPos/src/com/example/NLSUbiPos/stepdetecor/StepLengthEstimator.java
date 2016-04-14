package com.example.NLSUbiPos.stepdetecor;

/**
 * This class is used for step length estimation.
 */
public class StepLengthEstimator {
	
	// the default step length of a walking person
	private static final double DEFAULT_STEPLENGTH = 0.7;
	
	/**
	 * The constant step length model. <br>
	 * model: stepLength = constant.
	 * @return step length
	 */
	public static double constantStepLength() {
		double stepLength = DEFAULT_STEPLENGTH;
		return stepLength;
	}
	
	/**
	 * The unitary linear step length model. <br>
	 * model: stepLength = param1 * frequency + param2
	 * @param frequency the frequency of the step. It is the reciprocal of the step duration.
	 * @return step length
	 */
	public static double unitaryLinearStepLength(double frequency) {
		double a = 0.2074;
		double b = 0.2963;
		double stepLength = a * frequency + b;
		
		return stepLength;
	}
	
	/**
	 * The binary linear step length model.
	 * model: stepLength = param1 * frequency + param2 * variance + param3 
	 * @param frequency the frequency of the step. It is the reciprocal of the step duration
	 * @param variance the variance of the accelerometer amplitude during the step
	 * @return step length
	 */
	public static double binaryLinearStepLength(double frequency, double variance) {
		double a = 0.1397;
		double b = 0.008823;
		double c = 0.3735;
		double stepLength = a * frequency + b * variance + c;
		
		return stepLength;
	}
	/**
	 * The experience model about human height. <br>
	 * model: stepLength = factor*(0.7+param1*(height-1.75)+param2*((frequency-1.79)*height/1.75))
	 * @param height the height of the walking person
	 * @param frequency the frequency of the step. It is the reciprocal of the step duration
	 * @param factor a individual factor
	 * @return step length
	 */
	public static double heightExperienceStepLength(double height, double frequency, double factor) {
		double a = 0.371;
		double b = 0.227;
		double stepLength = factor * (0.7+a*(height-1.75)+b*((frequency-1.79)*height/1.75));
		
		return stepLength;
	}
}

