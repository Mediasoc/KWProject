package com.example.NLSUbiPos.building;

/**
 * This class is a representation of a building floor. It mainly includes walls and stairs representation.
 */
public class Floor {
	
	// the name of the floor
	private String floorName;
	
	// the number index of the floor
	private int floorIndex;
	
	// the north bias of the floor. It is how much the floor map is rotated clockwise.
	private float floorBias;
	
	// the scale of the floor map
	private float floorScale;
	
	// the representation of the walls of the floor
	private AreaModel walls;
	
	// the representation of the stairs of the floor
	private AreaModel stairs;
	
	// the file path of the bitmap representing the floor map
	private String bitmapPath = null;
	
	// the pixel of the x coordinate of the origin
	private float originPixelX;
	
	// the pixel of the y coordinate of the origin
	private float originPixelY;
	
	// the north bias of the bitmap
	private float bitmapBias;
	
	// the scale of the bitmap
	private float bitmapScale; 
	
	
	/**
	 * Constructor with given floor attributes.
	 * @param floorName the given name of the floor
	 * @param floorIndex the given number index of the floor
	 * @param floorBias the given north bias of the floor
	 * @param floorScale the given scale of the floor
	 */
	public Floor(String floorName, int floorIndex, float floorBias, float floorScale) {
		this.floorName = floorName;
		this.floorIndex = floorIndex;
		this.floorBias = floorBias;
		this.floorScale = floorScale;
		// initialization
		walls = new AreaModel();
		stairs = new AreaModel();
	}
	
	/**
	 * Gets the name of the floor.
	 * @return the name of the floor
	 */
	public String getFloorName() {
		return this.floorName;
	}
	/**
	 * Gets the number index of the floor.
	 * @return the number index of the floor
	 */
	public int getFloorIndex() {
		return this.floorIndex;
	}
	
	/**
	 * Gets the north bias of the floor.
	 * @return the north bias of the floor
	 */
	public float getFloorBias() {
		return this.floorBias;
	}
	
	/**
	 * Gets the scale of the floor map.
	 * @return the scale of the floor map
	 */
	public float getFloorScale() {
		return this.floorScale;
	}
	
	/**
	 * Gets the walls representation of the floor.
	 * @return the walls representation
	 */
	public AreaModel getWallsArea() {
		return walls;
	}
	
	/**
	 * Gets the stairs representation of the floor.
	 * @return the stairs representation
	 */
	public AreaModel getStairsArea() {
		return stairs;
	}
	
	/**
	 * Sets this floor's bitmap. A bitmap is not necessary for a floor.
	 * @param bitmapPath the file path of the bitmap
	 * @param originPixelX the given pixel of the x coordinate of the origin
	 * @param originPixelY the given pixel of the y coordinate of the origin
	 * @param bitmapBias the given north bias of the bitmap
	 * @param bitmapScale the given scale of the bitmap
	 */
	public void setBitmap(String bitmapPath, float originPixelX, float originPixelY, float bitmapBias, float bitmapScale) {
		this.bitmapPath = bitmapPath;
		this.originPixelX = originPixelX;
		this.originPixelY = originPixelY;
		this.bitmapBias = bitmapBias;
		this.bitmapScale = bitmapScale;
	}
	
	/**
	 * Gets the file path of the bitmap.
	 * @return the file path of the bitmap representing the map
	 */
	public String getBitmapPath() {
		return this.bitmapPath;
	}
	
	/**
	 * Gets the pixel of the x coordinate of the origin.
	 * @return the pixel of the x coordinate of the origin
	 */
	public float getOriginPixelX() {
		return this.originPixelX;
	}
	
	/**
	 * Gets the pixel of the y coordinate of the origin.
	 * @return the pixel of the y coordinate of the origin
	 */
	public float getOriginPixelY() {
		return this.originPixelY;
	}
	
	/**
	 * Gets the north bias of the bitmap.
	 * @return the north bias of the bitmap
	 */
	public float getBitmapBias() {
		return this.bitmapBias;
	}
	
	/**
	 * Gets the scale of the bitmap.
	 * @return the scale of the bitmap
	 */
	public float getBitmapScale() {
		return this.bitmapScale;
	}
}
