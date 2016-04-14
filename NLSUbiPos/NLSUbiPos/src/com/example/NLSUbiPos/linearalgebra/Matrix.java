package com.example.NLSUbiPos.linearalgebra;

/**
 * This class defines some operations about matrix. The most important feature we use
 * is the rotation matrix. 
 */
public class Matrix {

	// the data in the matrix. It is a two dimensional array.
	private double[][] data;
	
	// the rows of the matrix
	private int rows;
	
	// the columns of the matrix
	private int columns;
	
	/**
	 * Constructor with the given row and column numbers. It generates a full zero matrix.
	 * @param rows the given row number.
	 * @param columns the given column number.
	 */ 
	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		this.data = new double[rows][columns];		
	}
	
	/**
	 * Constructor with given the 2-d array.
	 * @param data the given 2-d array
	 */
	public Matrix(double[][] data) {
		this.rows = data.length;
		this.columns = data[0].length;
		for (int i=0;i<rows;i++) {
			for (int j=0;j<columns;j++) {
				this.data[i][j] = data[i][j];
			}
		}
	}
	
	/**
	 * Generates an identity matrix with the given row and column numbers.
	 * @param rows the given row number
	 * @param columns the given column number
	 * @return an identity matrix 
	 */
	public static Matrix identity(int rows, int columns) {
		Matrix identityMatrix = new Matrix(rows, columns);
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				if(j == i) {
					identityMatrix.data[i][j] = 1;
				} else {
					identityMatrix.data[i][j] = 0;
				}
			}
		}
		
		return identityMatrix;
	}
	
	/**
	 * Set the matrix to an identity matrix.
	 */
	public void setIdentity() {
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				if(j == i) {
					data[i][j] = 1;
				} else {
					data[i][j] = 0;
				}
			}
		}
	}
	
	/**
	 * Copies the content of the given matrix to the current matrix.
	 * @param matrix the given matrix to be copied
	 */
	public void copy(Matrix matrix) {
		if (this.rows!=matrix.rows || this.columns!=matrix.columns) {
			return;
		} else {
			for (int i=0; i<rows; i++) {
				for (int j=0; j<columns; j++) {
					this.data[i][j] = matrix.data[i][j];
				}
			}
		}
	}
	
	/**
	 * Gets the value in the specified location.
	 * @param row the row number in the matrix
	 * @param column the column number in the matrix
	 * @return the value in the specified location
	 */
	public double getValue(int row, int column) {
		return data[row][column];
	}
	
	/**
	 * Gets all the values of the current matrix.
	 * @return the content of the current matrix
	 */
	public double[][] getAllValues() {
		return data;
	}
	
	/**
	 * Gets the transposed matrix of the current matrix.
	 * @return the transposed matrix
	 */
	public Matrix transpose() {
		Matrix transposedMatrix = new Matrix(columns, rows);
		
		for (int i=0; i<columns; i++) {
			for (int j=0; j<rows; j++) {
				transposedMatrix.data[i][j] = data[j][i];
			}
		}
		
		return transposedMatrix;
	}
	
	/**
	 * Scalar multiplication operation with the given factor.
	 * @param factor the scalar factor to multiply every element of the matrix
	 * @return the matrix after scalar multiplication operation.
	 */
	public Matrix scalarMultiply(double factor) {
		Matrix scalarMatrix = new Matrix(rows, columns);
		
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				scalarMatrix.data[i][j] = factor * data[i][j];
			}
		}
		
		return scalarMatrix;
	}
	
	/**
	 * Matrix multiplication operation.
	 * The current matrix will be on the left side and the given matrix will be on the right side. 
	 * @param rightMatrix the given matrix which will be on the right side.
	 * @return the matrix after matrix multiplication
	 */
	public Matrix leftMultiply(Matrix rightMatrix) {
		if (this.columns != rightMatrix.rows) {
			return null;
		} else {
			Matrix resultMatrix = new Matrix(this.rows, rightMatrix.columns);
			
			for (int i=0; i<this.rows; i++) {
				for (int j=0; j<rightMatrix.columns; j++) {
					// gets the element in the (i,j) position.
					// It is the product sum of the left matrix's i-th row and 
					// the right matrix's j-th column.
					resultMatrix.data[i][j] = 0;
					for (int k=0; k<this.columns; k++) {
						resultMatrix.data[i][j] += this.data[i][k]*rightMatrix.data[k][j];
					}
					
				}
			}
			return resultMatrix;
		}
	}
	
	/**
	 * Matrix multiplication operation.
	 * The current matrix will be on the right side and the given matrix will be on the left side.
	 * @param leftMatrix the given matrix which will be on the left side
	 * @return the matrix after matrix multiplication
	 */
	public Matrix rightMultiply(Matrix leftMatrix) {
		if (leftMatrix.columns != this.rows) {
			return null;
		} else {
			Matrix resultMatrix = new Matrix(leftMatrix.rows, this.columns);
			// gets the element in the (i,j) position.
			// It is the product sum of the left matrix's i-th row and 
			// the right matrix's j-th column.
			for (int i=0; i<leftMatrix.rows; i++) {
				for (int j=0; j<this.columns; j++) {
					
					resultMatrix.data[i][j] = 0;
					for (int k=0; k<leftMatrix.columns; k++) {
						resultMatrix.data[i][j] += leftMatrix.data[i][k]*this.data[k][j];
					}
				}
			}
			
			return resultMatrix;
		}
	}
	
	/**
	 * Generates a rotation matrix according to the given vector.
	 * @param angles the 3-d rotation vector which is consist of the rotated angles around x, y, z axes 
	 * @return the rotation matrix
	 */
	public static Matrix getRotationMatrix(double[] angles) {
		if(angles.length != 3) {
			return null;
		} else {
			Matrix rotationMatrix = new Matrix(3, 3);
			// angle[0,1,2] -> rotated angle around [x,y,z] axis
			// or (angle[0], angle[1], angle[2]) is the axis line, and the amplitude is the rotated angle around this axis
			double angleSize = Math.sqrt(angles[0]*angles[0]+angles[1]*angles[1]+angles[2]*angles[2]);
			double cosAngle = Math.cos(angleSize);
			double sinAngle = Math.sin(angleSize);
			
			double[] normVector = {angles[0]/angleSize, angles[1]/angleSize, angles[2]/angleSize};
			
			rotationMatrix.data[0][0] = cosAngle + normVector[0]*normVector[0]*(1-cosAngle);
			rotationMatrix.data[0][1] = normVector[0]*normVector[1]*(1-cosAngle) - normVector[2]*sinAngle;
			rotationMatrix.data[0][2] = normVector[0]*normVector[2]*(1-cosAngle) + normVector[1]*sinAngle;
			
			rotationMatrix.data[1][0] = normVector[0]*normVector[1]*(1-cosAngle) + normVector[2]*sinAngle;
			rotationMatrix.data[1][1] = cosAngle + normVector[1]*normVector[1]*(1-cosAngle);
			rotationMatrix.data[1][2] = normVector[1]*normVector[2]*(1-cosAngle) - normVector[0]*sinAngle;
			
			rotationMatrix.data[2][0] = normVector[0]*normVector[2]*(1-cosAngle) - normVector[1]*sinAngle;
			rotationMatrix.data[2][1] = normVector[1]*normVector[2]*(1-cosAngle) + normVector[0]*sinAngle;
			rotationMatrix.data[2][2] = cosAngle + normVector[2]*normVector[2]*(1-cosAngle);
			
			return rotationMatrix;
		}
	}
	
	/**
	 * Rotates the given vector according to the given rotation matrix.
	 * @param rotationMatrix the given rotation matrix which includes the rotation information
	 * @param vector the given vector which is to be rotated
	 * @return the vector after rotation operation
	 */
	public static Vector3d rotate(Matrix rotationMatrix, Vector3d vector) {
		// rotation matrix is 3*3 dimensional
		// vector is 3*1 dimensional
		if(rotationMatrix.rows!=3 || rotationMatrix.columns!=3) {
			return null;
		} else {
			Vector3d rotatedVector = new Vector3d();
			double[] vectorData = vector.getAllValues();
			double sum = 0;
			for (int i=0; i<3; i++) {
				sum = 0;
				for (int j=0; j<3; j++) {
					sum += rotationMatrix.data[i][j]*vectorData[j];
				}
				rotatedVector.setValue(sum, i);
			}
			return rotatedVector;
		}
	}
}
