package com.example.NLSUbiPos.coordinate;

import com.example.NLSUbiPos.coordinate.Lonlat;
// Edited by Liu on 20160513
public class Mercator {
	
	private double x=0;
	private double y=0;
	
	public Mercator(double xx,double yy){
	this.x=xx;
	this.y=yy;}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}

	
	public Lonlat mercatortolonlat( ){
		double x=this.getX()/20037508.34*180;
		double y=this.getY()/20037508.34*180;
		y=180/Math.PI*(2*Math.atan(Math.exp(y*Math.PI/180))-Math.PI/2);
		x=x-0.00003;
		y=y-0.000108;
		Lonlat a=new Lonlat(x,y);
		return a;
	}
}
