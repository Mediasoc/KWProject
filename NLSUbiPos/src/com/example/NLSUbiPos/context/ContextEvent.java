package com.example.NLSUbiPos.context;
/**
 * this class includes the indormation of a context event,when a context event is detected it will be sent to listeners
 * @author WZ
 *
 */

enum Type{
	outdoor,hall,elevator,stairs,corridor
}
public class ContextEvent {

	//the type of location context
	private Type contextType;
	
	/**
	 * the reliabilities of PDR,WiFi,Magnetic,BLE,GPS
	 */
	private double PDR_reliability;
	
	private double WiFi_reliability;
	
	private double Magnetic_reliability;
	
	private double BLE_reliability;
	
	private double GPS_reliability;
	
	//constructor
	public ContextEvent(Type contextType,double PDR_reliability,double WiFi_reliability,double Magnetic_reliability,double BLE_reliability,double GPS_reliability){
		this.contextType=contextType;
		this.PDR_reliability=PDR_reliability;
		this.WiFi_reliability=WiFi_reliability;
		this.Magnetic_reliability=Magnetic_reliability;
		this.BLE_reliability=BLE_reliability;
		this.GPS_reliability=GPS_reliability;
	}
	
	
	public Type getcontextType(){
		return this.contextType;
	}
	
	public double getPDR_reliability(){
		return this.PDR_reliability;
	}
	
	public double getWiFi_reliability(){
		return this.WiFi_reliability;
	}
	
	public double getMagnetic_reliability(){
		return this.Magnetic_reliability;
	}
	
	public double getBLE_reliability(){
		return this.BLE_reliability;
	}
	public double getGPS_reliability(){
		return this.GPS_reliability;
	}
}
