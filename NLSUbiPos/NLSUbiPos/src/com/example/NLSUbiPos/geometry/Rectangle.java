package com.example.NLSUbiPos.geometry;

import java.util.ArrayList;

/**
 * This class represents a rectangle area on the 2-dimensional plane. And the coordinate feature
 * is the same as the display screen which means the x coordinate of right point is no smaller than 
 * that of the left point and the y coordinate of the bottom is no smaller than that of the top point.
 */
public class Rectangle {
	// the x coordinate of the left-top point
	private double left;
	
	// the y coordinate of the left-top point
	private double top;
	
	// the x coordinate of the right-bottom point
	private double right;
	
	// the y coordinate of the right-bottom point
	private double bottom;
	
	// the left line of the rectangle
	private Line2d leftEdge;
	
	// the top line of the rectangle
	private Line2d topEdge;
	
	// the right line of the rectangle
	private Line2d rightEdge;
	
	// the bottom line of the rectangle
	private Line2d bottomEdge;
	
	/**
	 * Default constructor.
	 */
	public Rectangle() {
		left = 0;
		top = 0;
		right = 0;
		bottom = 0;
		setEdges();
	}
	
	/**
	 * Constructor with given coordinates.
	 * @param x1 the given x coordinate of the left-top point
	 * @param y1 the given y coordinate of the left-top point
	 * @param x2 the given x coordinate of the right-bottom point
	 * @param y2 the given y coordinate of the right-bottom point
	 */
	public Rectangle(double x1, double y1, double x2, double y2) {
		this.left = x1;
		this.top = y1;
		this.right = x2;
		this.bottom = y2;
		setEdges();
	}
	
	/**
	 * Gets the x coordinate of the left-top point.
	 * @return the x coordinate of the left-top point
	 */
	public double getLeft() {
		return left;
	}
	
	/**
	 * Gets the y coordinate of the left-top point.
	 * @return the y coordinate of the left-top point
	 */
	public double getTop() {
		return top;
	}
	
	/**
	 * Gets the x coordinate of the right-bottom point.
	 * @return the x coordinate of the right-bottom point
	 */
	public double getRight() {
		return right;
	}
	
	/**
	 * Gets the y coordinate of the right-bottom point.
	 * @return the y coordinate of the right-bottom point
	 */
	public double getBottom() {
		return bottom;
	}
	
	/**
	 * Sets the coordinates of the rectangle.
	 * @param x1 the given x coordinate of the left-top point
	 * @param y1 the given y coordinate of the left-top point
	 * @param x2 the given x coordinate of the right-bottom point
	 * @param y2 the given y coordinate of the right-bottom point
	 */
	public void set(double x1, double y1, double x2, double y2) {
		this.left = x1;
		this.top = y1;
		this.right = x2;
		this.bottom = y2;
		setEdges();
	}
	
	/**
	 * Expands the current rectangle with the given values.
	 * @param leftExpansion the expansion size towards the left
	 * @param topExpansion the expansion size towards the top
	 * @param rightExpansion the expansion size towards the right
	 * @param bottomExpansion the expansion size towards the bottom
	 */
	public void expand(double leftExpansion, double topExpansion, double rightExpansion, double bottomExpansion) {
		this.left -= leftExpansion;
		this.top -= topExpansion;
		this.right += rightExpansion;
		this.bottom += bottomExpansion;
		setEdges();
	}
	
	/**
	 * Resets the four side of the rectangle.
	 */
	private void setEdges() {
		// sets the left line 
		leftEdge = new Line2d(left, top, left, bottom);
		// sets the top line
		topEdge = new Line2d(left, top, right, top);
		// sets the right line
		rightEdge = new Line2d(right, top, right, bottom);
		// sets the bottom line
		bottomEdge = new Line2d(left, bottom, right, bottom);
	}
	
	/**
	 * Judges if the given point is in the current rectangle.
	 * @param point the point to be judged
	 * @return true if the point is in the rectangle, false otherwise
	 */
	public boolean isPointIntersection(Point2d point) {
		double x = point.getX();
		double y = point.getY();
		
		return (left<=x && x<=right && top<=y && y<=bottom);
	}
	
	/**
	 * Judges if the current rectangle has intersection with the given line segment.
	 * @param line the given line to be judged
	 * @return true if the rectangle and the line segment have intersection, false otherwise
	 */
	public boolean isLineIntersection(Line2d line) {
		// the line segment is fully in the rectangle or has intersection with the four sides of the rectangle
		return (isPointIntersection(line.getStartPoint()) || isPointIntersection(line.getEndPoint()))
				|| leftEdge.isLineIntersection(line)
				|| topEdge.isLineIntersection(line)
				|| rightEdge.isLineIntersection(line)
				|| bottomEdge.isLineIntersection(line);
	}
	
	/**
	 * Gets the grid points contained in the current rectangle. The coordinates of the grid points
	 * are the integer times of the grid size.
	 * @param gridSize the given grid size
	 * @return all the grid points contained in the current rectangle
	 */
	public ArrayList<Point2d> getGridPoints(double gridSize) {
		ArrayList<Point2d> gridPoints = new ArrayList<Point2d>();
		
		// coordinates of the grid points must be the integer times of the grid size
		int gridLeft = (int) Math.ceil(left / gridSize);
		int gridTop = (int) Math.ceil(top / gridSize);
		int gridRight = (int) Math.floor(right / gridSize);
		int gridBottom = (int) Math.floor(bottom / gridSize);
		
		// gets all the grid points
		if (gridLeft<=gridRight && gridTop<=gridBottom) {
			for (int i=gridTop; i<=gridBottom; i++) {
				for (int j=gridLeft; j<=gridRight; j++) {
					gridPoints.add(new Point2d(j*gridSize, i*gridSize));
				}
			}
		}
		
		return gridPoints;
	}
}
