package com.example.NLSUbiPos.linearalgebra;

/**
 * This class represents a vector that has three elements. 
 */
public class Vector3d {
	
	// the array wrapped in the class
	private double[] data;
	
	/**
	 * The default constructor. It initializes the data to all zeros.
	 */
	public Vector3d() {
		data = new double[3];
	}
	
	/**
	 * Constructor with the given three elements.
	 * @param x the first element
	 * @param y the second element
	 * @param z the third element
	 */
	public Vector3d(double x, double y, double z) {
		data = new double[3];
		data[0] = x;
		data[1] = y;
		data[2] = z;
	}
	
	/**
	 * Constructor with the given array. The length of the array must be three.
	 * @param data the array used to initialize the vector
	 */
	public Vector3d(double[] data) {
		this.data = new double[3];
		if (data.length == 3) {
			for (int i=0; i<3; i++) {
				this.data[i] = data[i];
			}
		}
	}
	
	/**
	 * Copies the content of the given vector to the current vector.
	 * @param vector
	 */
	public void copy(Vector3d vector) {
		for (int i=0; i<3; i++) {
			this.data[i] = vector.data[i];
		}
	}
	
	/**
	 * Modifies the value in the specified position.
	 * @param value the new value used to replace the old value
	 * @param index the specified position
	 * @return true if 0<=index<=2; false else
	 */
	public boolean setValue(double value, int index){
		if(index<0 || index >3) {
			return false;
		} else {
			this.data[index] = value;
			return true;
		}
	}
	
	/**
	 * Modifies all the values in the vector.
	 * @param data the array used to modify the old value
	 * @return true if 0<=data.length<=2; false else
	 */
	public boolean setAllValues(double[] data) {
		if (data.length != 3) {
			return false;
		} else {
			for (int i=0; i<3; i++) {
				this.data[i] = data[i];
			}
			return true;
		}
	}
	
	/**
	 * Gets the value in the specified position.
	 * @param index the array index to retrieve
	 * @return the value in the specified index
	 */
	public double getValue(int index) {
		return this.data[index];
	}
	
	/**
	 * Gets all the values in the vector.
	 * @return the array wrapped in the vector
	 */
	public double[] getAllValues() {
		return this.data;
	}
	
	/**
	 * Gets the amplitude of the vector.
	 * @return the amplitude of the vector
	 */
	public double getSize() {
		return Math.sqrt(data[0]*data[0] + data[1]*data[1] + data[2]*data[2]);
	}
	
	/**
	 * Normalization operation. The amplitude of the returned vector will be 1.
	 * @return the normalized vector
	 */
	public Vector3d normalize() {
		Vector3d normalizedVector = new Vector3d();
		double size = this.getSize();
		normalizedVector.data[0] = this.data[0] / size;
		normalizedVector.data[1] = this.data[1] / size;
		normalizedVector.data[2] = this.data[2] / size;
		
		return normalizedVector;
	}
	
	/**
	 * Scalar multiplication operation.
	 * @param factor the scalar value used to multiply every element of the vector 
	 * @return the vector whose elements are factor times of thats of the original vector.
	 */
	public Vector3d scalarMultiply(double factor) {
		Vector3d scalarVector = new Vector3d();
		for (int i=0; i<3; i++) {
			scalarVector.data[i] = factor * this.data[i];
		}
		return scalarVector;
	}

	/**
	 * The cross multiplication operation. The current vector will be on the left side and
	 * the other vector will be on the right side.
	 * @param rightVector the vector which will be on the right side
	 * @return the cross product of these two vector
	 */
	public Vector3d crossProduct(Vector3d rightVector) {
		Vector3d product = new Vector3d();
		product.data[0] = this.data[1]*rightVector.data[2] - this.data[2]*rightVector.data[1];
		product.data[1] = this.data[2]*rightVector.data[0] - this.data[0]*rightVector.data[2];
		product.data[2] = this.data[0]*rightVector.data[1] - this.data[1]*rightVector.data[0];
		
		return product;
		
	}
}
