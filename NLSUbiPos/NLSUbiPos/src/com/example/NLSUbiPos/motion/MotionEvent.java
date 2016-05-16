package com.example.NLSUbiPos.motion;

public class MotionEvent{
	
	//the timestamp when the floor change is detected
	private long timestamp;
	
	//motion of user
	private int motion;
	
	//Constructor
	public MotionEvent(long timestamp,int motion){
		this.timestamp=timestamp;
		this.motion=motion;
	}
	
	//Get the timestamp
	public long gettimestamp(){
		return this.timestamp;
	}
	
	//Get the motion
	public int getmotion(){
		return this.motion;
	}
}