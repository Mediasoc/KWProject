package com.example.NLSUbiPos.coordinate;

import com.example.NLSUbiPos.coordinate.Lonlat;

public class Mercator {
	
	public double x=0;
	public double y=0;
	
	public Mercator(double xx,double yy){
	this.x=xx;
	this.y=yy;}
	
	public double getx(){
		return x;
	}
	
	public double gety(){
		return y;
	}

	
	public Lonlat mercatortolonlat( ){
		double x=this.getx()/20037508.34*180;
		double y=this.gety()/20037508.34*180;
		y=180/Math.PI*(2*Math.atan(Math.exp(y*Math.PI/180))-Math.PI/2);
		Lonlat a=new Lonlat(x,y);
		return a;
	}
}
