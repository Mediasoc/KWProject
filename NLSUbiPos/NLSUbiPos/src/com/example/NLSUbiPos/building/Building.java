package com.example.NLSUbiPos.building;

import java.util.HashMap;

/**
 * The class Building is a description of the indoor maps. A building is consist of several Floors.
 * And a Floor is consist of several areas such as walls or stairs. All representations are 
 * actually lines.
 */
public class Building {
	
	// the name of this building
	private String buildingName = null;
	
	// the index of the current floor
	private int currentFloorIndex = 0;
	
	// the projection of the floor numbers to the floor names
	private HashMap<Integer, String> floorNames;
	
	// the projection of the floor numbers to the Floor object
	private HashMap<Integer, Floor> floors;
	
	// the total floor count
	private int floorCount = 0;
	
	/**
	 * Constructor with the building name.
	 * @param buildingName the given name of the building
	 */
	public Building(String buildingName) {
		this.buildingName = buildingName;
		// initialization
		floorNames = new HashMap<Integer, String>();
		floors = new HashMap<Integer, Floor>();
		currentFloorIndex = Integer.MIN_VALUE;
		floorCount = 0;
	}

	/**
	 * Factory method given the pathname of the building's XML file's parent and the building name.
	 * @param parentPath the pathname of the building's XML file's parent
	 * @param buildingName the name of the building and also the XML file's name without the suffix ".xml" 
	 * @return the Building representation of the real building
	 */
	public static Building factory(String parentPath, String buildingName) {
		Building building = null;		
		// parse the XML file
		building = Building.parseFromXml(parentPath + "/" + buildingName + ".xml");
		return building;
	}
	/**
	 * Building the mapping relationship between grid points and lines used for particle filter
	 * @param building the object from class Building
	 */
	public static void pointLinesMap(Building building){
		if (building != null) {
			// the following calls is beneficial for the collision check in the particle filter
			for (Floor floor : building.floors.values()) { 
				floor.getWallsArea().generatePointLinesMap();
				floor.getStairsArea().generatePointLinesMap();
			}
		}
	}
	
	/**
	 * Parses the building's XML file according to the file path.
	 * @param filename the pathname of the building's XML file
	 * @return the Building representation of the building
	 */
	public static Building parseFromXml(String filename) {
		return new BuildingXmlParser().parse(filename);
	}
	
	/**
	 * Gets the name of the building.
	 * @return the name of the building.
	 */
	public String getBuildingName() {
		return this.buildingName;
	}
	
	/**
	 * Adds a floor to the building.
	 * @param floor the Floor object
	 */
	public void addFloor(Floor floor) {
		// adds floor name
		floorNames.put(floor.getFloorIndex(), floor.getFloorName());
		// adds Floor object
		floors.put(floor.getFloorIndex(),  floor);
		// set the current floor
		currentFloorIndex = floor.getFloorIndex();
		// a new floor is added
		floorCount++;
	}
	
	/**
	 * Gets the Floor object by the floor number index
	 * @param floorIndex the number index of the floor
	 * @return the Floor object
	 */
	public Floor getFloorByIndex(int floorIndex) {
		return floors.get(floorIndex);
	}
	
	/**
	 * Gets the current Floor object.
	 * @return the current Floor object
	 */
	public Floor getCurrentFloor() {
		return floors.get(currentFloorIndex);
	}
	
	/**
	 * Gets the number index of the current floor.
	 * @return the number index of the current floor
	 */
	public int getCurrentFloorIndex() {
		return this.currentFloorIndex;
	}
	
	/**
	 * Switches to the specified floor given the floor number index.
	 * @param floorIndex the number index of the floor to be switched to
	 */
	public void setCurrentFloorIndex(int floorIndex) {
		this.currentFloorIndex = floorIndex;
	}
	
	/**
	 * Gets the total floor count of the building.
	 * @return the total floor count of the building
	 */
	public int getFloorCount() {
		return this.floorCount;
	}
}
