package com.example.NLSUbiPos.coordinate;

import com.example.NLSUbiPos.coordinate.Mercator;
// Edited by Liu on 20160513
public class Lonlat {
	private double lon=0;
	private double lat=0;
	
	public Lonlat(double llon,double llat)
	{
		this.lon=llon;
		this.lat=llat;
	}

	public double getlon(){
		return lon;
	}
	
	public double getlat(){
		return lat;
	}
	
	public void setlon(double lon){
		this.lon = lon;
	}
	
	public void setlat(double lat){
		this.lat = lat;
	}
	
	public Mercator lonlattomercator(){
		double x=this.getlon()*20037508.34/180;
		double y=Math.log(Math.tan((90+this.getlat())*Math.PI/360))/(Math.PI/180);
		y=y*20037508.34/180;
		Mercator a=new Mercator(x,y);
		return a;
	}
}
