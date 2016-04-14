package com.example.NLSUbiPos.coordinate;

import com.example.NLSUbiPos.coordinate.Mercator;

public class Lonlat {
	public double lon=0;
	public double lat=0;
	
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
	
	public Mercator lonlattomercator(){
		double x=this.getlon()*20037508.34/180;
		double y=Math.log(Math.tan((90+this.getlat())*Math.PI/360))/(Math.PI/180);
		y=y*20037508.34/180;
		Mercator a=new Mercator(x,y);
		return a;
	}
}
