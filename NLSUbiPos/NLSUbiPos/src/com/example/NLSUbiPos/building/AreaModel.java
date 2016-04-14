package com.example.NLSUbiPos.building;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.example.NLSUbiPos.geometry.Line2d;
import com.example.NLSUbiPos.geometry.Point2d;
import com.example.NLSUbiPos.geometry.Rectangle;

/**
 * This class is a representation of a building area such as flat walls area, stairs area and so on.
 * The area representation is a line segments set.
 */
public class AreaModel {

	// the complete set of the area
	private Set<Line2d> completeSet;
	
	// the working set of the area. It is used for particle filter
	private Set<Line2d> workingSet;
	
	// the display set of the area. It is used for map display on the screen
	private Set<Line2d> displaySet;
	
	/*
	 * Each element of the HashMap is a projection of grid point to line set.
	 * The projection works like this: get a square centered on the grid point, 
	 * and the length of side is two times of the grid size. The lines
	 * contained in the square is the projection of the grid point.
	 * The coordinate of the grid point is integer times of the grid size.
	 */
	private HashMap<Point2d, Set<Line2d>> pointLinesMap;
	
	// the length of side of the grid or the square
	private final double gridSize = 1.0f;
	
	/**
	 * Default constructor. Initializes the Sets and HashMap.
	 */
	public AreaModel() {
		completeSet = new HashSet<Line2d>();
		workingSet = new HashSet<Line2d>();
		displaySet = new HashSet<Line2d>();
		pointLinesMap = new HashMap<Point2d, Set<Line2d>>();
	}
	
	/**
	 * Adds a line to the complete set of the building area.
	 * @param line the line to be added
	 */
	public void addLine(Line2d line) {
		completeSet.add(line);
	}
	
	/**
	 * Stretch the lines according to the given scale.
	 * @param scale the scale used for stretching the lines
	 */
	public void scaleLines(double scale) {
		HashSet<Line2d> newCompleteSet = new HashSet<Line2d>();
		for (Line2d line : completeSet) {
			// stretch the line
			double x1 = scale * line.getStartPoint().getX();
			double y1 = scale * line.getStartPoint().getY();
			double x2 = scale * line.getEndPoint().getX();
			double y2 = scale * line.getEndPoint().getY();
			newCompleteSet.add(new Line2d(x1, y1, x2, y2));
		}
		completeSet.clear();
		completeSet.addAll(newCompleteSet);
	}
	
	/**
	 * Gets the line set for display
	 * @return the display set
	 */
	public Collection<Line2d> getDisplaySet() {
		return displaySet;
	}
	
	/**
	 * Update the display set when the screen rectangle box in the map changes.
	 * @param boundingBox The rectangle which is like a window. And within the window, the map can be seen
	 */
	public void updateDisplaySet(Rectangle boundingBox) {
		displaySet.clear();
		Line2d screenLine = null;
		for (Line2d line : completeSet) {
			// change the natural coordinate to screen coordinate mode (y coordinate decreases from top to bottom)
			screenLine = new Line2d(line.getX1(), -line.getY1(), line.getX2(), -line.getY2());
			if (boundingBox.isLineIntersection(screenLine)) {
				displaySet.add(line);
			}
		}
		
	}
	
	/**
	 * Generates the projections from grid point to the line set.
	 * The projection works like this: get a square centered on the grid point, 
	 * and the length of side is two times of the grid size. The lines
	 * contained in the square is the projection of the grid point.
	 * The coordinate of the grid point is integer times of the grid size.
	 */
	public void generatePointLinesMap() {
		// initialize two rectangles
		Rectangle lineBox = new Rectangle();
		Rectangle pointBox = new Rectangle();
		
		// each line generate a box
		for (Line2d line : completeSet) {
			// generate a box according to the line and expand it
			lineBox.set(Math.min(line.getX1(), line.getX2()),
					Math.min(line.getY1(), line.getY2()),
					Math.max(line.getX1(), line.getX2()),
					Math.max(line.getY1(), line.getY2()));
			lineBox.expand(gridSize, gridSize, gridSize, gridSize);
			
			for (Point2d gridPoint : lineBox.getGridPoints(gridSize)) {
				pointBox.set(gridPoint.getX(), gridPoint.getY(), gridPoint.getX(), gridPoint.getY());
				// the side length of the rectangle box is two times of the grid size
				pointBox.expand(2*gridSize, 2*gridSize, 2*gridSize, 2*gridSize);
				if (pointBox.isLineIntersection(line)) {
					if (!pointLinesMap.containsKey(gridPoint)) {
						// adds the grid point
						pointLinesMap.put(gridPoint, new HashSet<Line2d>());
					}
					// adds the line to the set which correspond to the grid point
					pointLinesMap.get(gridPoint).add(line);
				}
			}
		}
	}
	
	/**
	 * Gets the working set according to the given coordinates. The coordinates are rounded
	 * to a grid point, and the line set projection of the grid point is returned.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @return the line set projection of the grid point
	 */
	public Collection<Line2d> getWorkingSet(double x, double y) {
		workingSet = new HashSet<Line2d>();
		if (pointLinesMap != null) {
			
			// rounds the given point to the grid point
			Point2d gridPoint = new Point2d(x,y).roundToGridPoint(gridSize);
			
			if (pointLinesMap.containsKey(gridPoint)) {
				// gets the line set corresponding to the grid point
				workingSet = pointLinesMap.get(gridPoint);
			}
		}
		return workingSet;
	}
	
	/**
	 * Gets the HashMap projections from grid point to the line.
	 * @return the HashMap projections
	 */
	public HashMap<Point2d, Set<Line2d>> getPointLinesMap() {
		return pointLinesMap;
	}
}
