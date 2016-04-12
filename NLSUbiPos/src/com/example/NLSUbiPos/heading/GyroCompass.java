package com.example.NLSUbiPos.heading;


import com.example.NLSUbiPos.linearalgebra.Matrix;
import com.example.NLSUbiPos.linearalgebra.Vector3d;
import com.example.NLSUbiPos.stepdetecor.MovingWindow;
import com.example.NLSUbiPos.stepdetecor.SensorSample;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * This class is a heading estimator using gyroscope, accelerometer and magnetometer.<br>
 * The gyroscope is used to get the rotation matrix from the world coordinate to the device coordinate.
 * Since pure gyroscope will accumulate errors, accelerometer and magnetometer are used to align reference
 * vectors to reduce the errors. 
 */
public class GyroCompass extends Compass {

	// a rotation matrix from the world coordinate to the device coordinate.
	// Multiplying a vector will change the coordinates in the world coordinate 
	// to the coordinates in the device coordinate.
	private Matrix worldBase;
	
	/*
	 * The heading of the user in the world coordinate. It is the angle from the world north
	 * to the moving direction of the user.
	 */
	private double heading;
	
	// the timestamp of the last accelerometer event
	private long lastAccTimestamp;
	
	// the acceleration of the last accelerometer event
	private float[] lastAccValues;
	
	// moving window array recording the x, y, z accelerations
	private MovingWindow[] accMovingWindow;
	
	// the complementary factor when using accelerometer for aligning
	private double accComplementaryFactor = 0.1;
	
	// the timestamp of the last gyroscope event
	private long lastGyroTimestamp;
	
	// the gyro values of the last gyroscope event
	private float[] lastGyroValues;
	
	// the complementary factor when using magnetometer for aligning
	private double magComplementaryFactor = 0.1;
	
	// the total times using fast magnetometer aligning
	// using the full aligning which means not multiplying a factor of 1.0
	private int fastMagAlignAttempts = 10;
	
	// the size of the moving windows recording the accelerations 
	public static final float ACC_WINDOW_SIZE = 0.5f;
	
	// how many nanoseconds in a second
	public static final double NANO = Math.pow(10, 9);
	
	/**
	 * The default constructor.
	 */
	public GyroCompass() {
		// set the rotation matrix to a identity matrix.
		worldBase = Matrix.identity(3, 3);
		accMovingWindow = new MovingWindow[]{new MovingWindow(ACC_WINDOW_SIZE),
				new MovingWindow(ACC_WINDOW_SIZE), new MovingWindow(ACC_WINDOW_SIZE)};
		lastAccValues = new float[3];
		lastGyroValues = new float[3];
	}

	/**
	 * Computes the device heading in the world coordinate.<br>
	 * (0,1,0) is the vector of the device heading in the device coordinate. The inverse of
	 * the rotation matrix is the transposition of the rotation matrix. 
	 */
	public void computeHeading() {
		// gets the heading vector in the world coordinate
		Vector3d headingVector = Matrix.rotate(worldBase.transpose(), new Vector3d(0,1,0));
		// gets the angle from the world north to the direction of the device's y axis
		heading = Math.atan2(headingVector.getValue(0), headingVector.getValue(1));
	}
	
	/**
	 * Processes the accelerometer event. It adjusts the rotation matrix by aligning the accelerations.
	 * @param event an accelerometer event
	 */
	public void processAccelerometerEvent(SensorEvent event) {
		if (lastAccTimestamp != 0) {
			// time interval of two adjacent accelerometer events
			double interval = (double)(event.timestamp - lastAccTimestamp) / NANO;
			// adds the x, y, z to the moving windows
			for (int i=0; i<3; i++) {
				accMovingWindow[i].add(new SensorSample((float) interval,
						(event.values[i]+lastAccValues[i])/2));
			}
			// predicts the gravity vector in the device coordinate using rotation matrix.
			// (0,0,1) is the gravity vector in the world coordinate.
			Vector3d predictedGravity  = Matrix.rotate(worldBase, new Vector3d(0,0,1));
			// predicts the gravity vector in the device coordinate using the measured accelerations
			// the averages of the moving windows is an approximation of the real gravity
			Vector3d realGravity = new Vector3d(accMovingWindow[0].getAverage(),
					accMovingWindow[1].getAverage(), accMovingWindow[2].getAverage());
			// rotates the predicted gravity vector using rotation matrix to the real gravity vector
			// gets the rotation vector
			Vector3d rotationVector = predictedGravity.normalize().crossProduct(realGravity.normalize());
			double rotationVectorAmp = rotationVector.getSize();
			rotationVector = rotationVector.scalarMultiply(Math.asin(rotationVectorAmp)/rotationVectorAmp);
			// add the complementary factor to prevent overfit
			rotationVector.copy(rotationVector.scalarMultiply(accComplementaryFactor));
			// generates the rotation matrix using the rotation vector
			Matrix rotationMatrix = Matrix.getRotationMatrix(rotationVector.getAllValues());
			// adjust the rotation matrix
			worldBase = worldBase.rightMultiply(rotationMatrix);
		}
		// records the current event as the last event
		lastAccTimestamp = event.timestamp;
		System.arraycopy(event.values, 0, lastAccValues, 0, 3);
	}

	/**
	 * Processes the gyroscope event. It rotates the rotation matrix according to the gyroscope values.
	 * @param event an gyroscope event
	 */
	public void processGyroscopeEvent(SensorEvent event) {
		
		if (lastGyroTimestamp != 0) {
			// time interval of two adjacent gyroscope events
			double interval = (double)(event.timestamp - lastGyroTimestamp) / NANO;
			// the rotation vector
			double[] rotationVector = new double[3];
			// rotation angle around x, y, z axes
			// minus means that device coordinate keeps fixed and the vector rotates
			rotationVector[0] = -(event.values[0] + lastGyroValues[0]) / 2 * interval;
			rotationVector[1] = -(event.values[1] + lastGyroValues[1]) / 2 * interval;
			rotationVector[2] = -(event.values[2] + lastGyroValues[2]) / 2 * interval;
			// gets rotation matrix
			Matrix rotationMatrix = Matrix.getRotationMatrix(rotationVector);
			// rotation again
			worldBase = worldBase.rightMultiply(rotationMatrix);
		}
		// records the current event as the last event
		lastGyroTimestamp = event.timestamp;
		System.arraycopy(event.values, 0, lastGyroValues, 0, 3);
	}

	/**
	 * Processes the magnetometer event. It adjusts the rotation matrix by align the magnetometer values.
	 * @param event a magnetometer event.
	 */
	public void processMagnetometerEvent(SensorEvent event) {
		// gets the magnetic vector in the world coordinate using measured magnetic values and
		// the rotation matrix
		Vector3d realMagWorld = Matrix.rotate(worldBase.transpose(),
				new Vector3d(event.values[0], event.values[1], event.values[2]));
		// gets the predicted magnetic values using rotation matrix
		// (0,1,0) is the true north vector in the world coordinate
		Vector3d predictedMag = Matrix.rotate(worldBase, new Vector3d(0,1,0));
		// gets the magnetic values in the device coordinate using rotation matrix and
		// the x,y axis components of the real magnetic values in the world coordinate
		Vector3d realMagFromWorldXY = Matrix.rotate(worldBase,
				new Vector3d(realMagWorld.getValue(0), realMagWorld.getValue(1), 0));
		// rotates the the predicted magnetic vector to the real magnetic vector.
		// In theory, the horizontal component of the magnetic values in the world coordinate 
		// only points to the north. So, only x,y axis components of the real magnetic values
		// in the world coordinate are used for aligning.
		Vector3d rotationVector = predictedMag.normalize().crossProduct(realMagFromWorldXY.normalize());
		double rotationVectorAmp = rotationVector.getSize();
		rotationVector = rotationVector.scalarMultiply(Math.asin(rotationVectorAmp)/rotationVectorAmp);
		// adds the complementary factor
		if (fastMagAlignAttempts <=0 ) {
			rotationVector.copy(rotationVector.scalarMultiply(magComplementaryFactor));
		} else {
			// fast complementary mode uses the 1.0 factor
			rotationVector.copy(rotationVector.scalarMultiply(1.0));
			fastMagAlignAttempts--;
		}
		// generates the rotation matrix
		Matrix rotationMatrix = Matrix.getRotationMatrix(rotationVector.getAllValues());
		// adjusts the rotation matrix
		worldBase = worldBase.rightMultiply(rotationMatrix);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			// accelerometer, gyroscope, magnetometer events
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				processAccelerometerEvent(event);
				break;
			case Sensor.TYPE_GYROSCOPE:
				processGyroscopeEvent(event);
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				processMagnetometerEvent(event);
				break;
			}
			computeHeading();
			if (heading == Double.NaN) {
				worldBase.setIdentity();
				fastMagAlignAttempts = 10;
			} else {
				// to notify heading changed
				notifyHeadingChange(heading);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	
}
