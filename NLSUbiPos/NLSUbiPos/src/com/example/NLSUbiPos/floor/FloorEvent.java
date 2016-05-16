package com.example.NLSUbiPos.floor;
/**
 * This class includes the information of floor change,when a floor change is detected 
 * it will be sent to the listeners
 */

public class FloorEvent{
	
	//the timestamp when the floor change is detected
	private long timestamp;
	
	//current floor of user
	private int floor;
	
	//Constructor
	public FloorEvent(long timestamp,int floor){
		this.timestamp=timestamp;
		this.floor=floor;
	}
	
	//Get the timestamp
	public long gettimestamp(){
		return this.timestamp;
	}
	
	//Get the floor
	public int getfloor(){
		return this.floor;
	}
}