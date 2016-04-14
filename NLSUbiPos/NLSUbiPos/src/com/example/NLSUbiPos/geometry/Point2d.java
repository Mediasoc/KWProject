package com.example.NLSUbiPos.geometry;

/**
 * This class is a representation of a point on the 2-dimensional plane.
 */
public class Point2d {
	
	// the x coordinate of the point
	private double xCoordinate;
	
	// the y coordinate of the point
	private double yCoordinate;
	
	/**
	 * Default constructor.
	 */
	public Point2d() {
		xCoordinate = 0;
		yCoordinate = 0;
	}
	
	/**
	 * Constructor with the given coordinates.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Point2d(double x, double y) {
		xCoordinate = x;
		yCoordinate = y;
	}
	
	/**
	 * Constructor with the given point. Copy the coordinates of the given point to those of the current one.
	 * @param point the given point to be copied
	 */
	public Point2d(Point2d point) {
		this.xCoordinate = point.xCoordinate;
		this.yCoordinate = point.yCoordinate;
	}
	
	/**
	 * Gets the x coordinate of the current point.
	 * @return the x coordinate
	 */
	public double getX() {
		return xCoordinate;
	}
	
	/**
	 * Gets the y coordinate of the current point
	 * @return the y coordinate
	 */
	public double getY() {
		return yCoordinate;
	}
	
	/**
	 * Sets the x coordinate of the current point.
	 * @param x the given x coordinate
	 */
	public void setX(double x) {
		this.xCoordinate = x;
	}
	
	/**
	 * Sets the y coordinate of the current point.
	 * @param y the given y coordinate
	 */
	public void setY(double y) {
		this.yCoordinate = y;
	}
	
	/**
	 * Sets the coordinates of the current point.
	 * @param x the given x coordinate
	 * @param y the given y coordinate
	 */
	public void set(double x, double y) {
		this.xCoordinate = x;
		this.yCoordinate = y;
	}
	
	/**
	 * Gets the closets grid point using rounding-off method.
	 * The coordinates of the grid point is integer times of the grid size.
	 * @param gridSize the size or the side length of the grid
	 * @return the grid point after rounding-off
	 */
	public Point2d roundToGridPoint(double gridSize) {
		// rounds x coordinate
		double x = gridSize * (int) Math.round(xCoordinate / gridSize);
		// rounds y coordinate
		double y = gridSize * (int) Math.round(yCoordinate / gridSize);
		
		return new Point2d(x, y);
	}

	@Override
	public boolean equals(Object object) {
		// two points equal means their coordinates are the same
		if (object instanceof Point2d) {
			Point2d comparedPoint = (Point2d) object;
			return (this.xCoordinate == comparedPoint.xCoordinate && this.yCoordinate == comparedPoint.yCoordinate);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		// if two points equal, their hash codes are the same
		return (int) (xCoordinate + 13063 * yCoordinate);
	}
	
}
