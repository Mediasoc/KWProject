package com.example.NLSUbiPos.geometry;


/**
 * This class is a representation of a line segment on the 2-dimensional plane.
 */
public class Line2d {
	
	// the start point of the line segment
	private Point2d startPoint;
	
	// the end point of the line segment
	private Point2d endPoint;
	
	/**
	 * Default Constructor.
	 */
	public Line2d() {
		this(0, 0, 0, 0);
	}
	
	/**
	 * Constructors with given coordinates.
	 * @param x1 the x coordinate of the start point
	 * @param y1 the y coordinate of the start point
	 * @param x2 the x coordinate of the end point
	 * @param y2 the y coordinate of the end point
	 */
	public Line2d(double x1, double y1, double x2, double y2) {
		startPoint = new Point2d(x1, y1);
		endPoint = new Point2d(x2, y2);
	}
	
	/**
	 * Gets the x coordinate of the start point.
	 * @return the x coordinate of the start point
	 */
	public double getX1() {
		return startPoint.getX();
	}
	
	/**
	 * Gets the y coordinate of the start point.
	 * @return the y coordinate of the start point
	 */
	public double getY1() {
		return startPoint.getY();
	}
	
	/**
	 * Gets the x coordinate of the end point.
	 * @return the x coordinate of the end point
	 */
	public double getX2() {
		return endPoint.getX();
	}
	
	/**
	 * Gets the y coordinate of the end point.
	 * @return the y coordinate of the end point.
	 */
	public double getY2() {
		return endPoint.getY();
	}
	
	/**
	 * Gets the start point of the current line segment.
	 * @return the start point
	 */
	public Point2d getStartPoint() {
		return startPoint;
	}
	
	/**
	 * Gets the end point of the current line segment.
	 * @return the end point
	 */
	public Point2d getEndPoint() {
		return endPoint;
	}
	
	/**
	 * Judges if the given point is in the current line segment. And the point is sure 
	 * to be in the line corresponding to the current line segment.
	 * @param point
	 * @return
	 */
	private boolean isPointIntersection(Point2d point) {
		// minimum and maximum x coordinate
		double minX = Math.min(startPoint.getX(), endPoint.getX());
		double maxX = Math.max(startPoint.getX(), endPoint.getX());
		
		// minimum and maximum y coordinate
		double minY = Math.min(startPoint.getY(), endPoint.getY());
		double maxY = Math.max(startPoint.getY(), endPoint.getY());
		
		return	(minX==maxX || minX<=point.getX() && point.getX()<=maxX) && (minY==maxY || minY<=point.getY() && point.getY()<=maxY);
	}
	
	// reference to http://en.wikipedia.org/wiki/Line-line_intersection
	/**
	 * Judges if two line have intersection, strictly two line segments.
	 * @param secondLine the current line is the first line, and this is the second line
	 * @return true if two lines have intersection, false otherwise.
	 */
	public boolean isLineIntersection(Line2d secondLine) {
		// the first line
		double x1 = this.startPoint.getX();
		double y1 = this.startPoint.getY();
		double x2 = this.endPoint.getX();
		double y2 = this.endPoint.getY();
		
		// the second line
		double x3 = secondLine.startPoint.getX();
		double y3 = secondLine.startPoint.getY();
		double x4 = secondLine.endPoint.getX();
		double y4 = secondLine.endPoint.getY();
		
		double denominator = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
		double numeratorX = (x1*y2-y1*x2)*(x3-x4) - (x1-x2)*(x3*y4-y3*x4);
		double numeratorY = (x1*y2-y1*x2)*(y3-y4) - (y1-y2)*(x3*y4-y3*x4);
		
		if (denominator != 0) {
			// two lines are crossed
			Point2d intersectionPoint = new Point2d(numeratorX / denominator, numeratorY / denominator);
			return (this.isPointIntersection(intersectionPoint) && secondLine.isPointIntersection(intersectionPoint));
		} else if (numeratorX != 0) {
			// the two lines are paralleled
			return false;
		} else {
			// the two lines are coincided
			return (this.isPointIntersection(secondLine.getStartPoint())
					|| this.isPointIntersection(secondLine.getEndPoint())
					|| secondLine.isPointIntersection(this.getStartPoint())
					|| secondLine.isPointIntersection(this.getEndPoint()));
		}
	}
	
	@Override
	public String toString() {
		return "("+getX1()+","+getY1()+","+getX2()+","+getY2()+")";
	}
}
