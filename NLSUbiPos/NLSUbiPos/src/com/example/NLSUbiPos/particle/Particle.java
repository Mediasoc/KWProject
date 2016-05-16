package com.example.NLSUbiPos.particle;

import java.util.Random;

import com.example.NLSUbiPos.position.ParticlePosition.Motion;
import com.example.NLSUbiPos.position.ParticlePosition.State;
import com.example.NLSUbiPos.utils.NormalDistribution;
import com.example.NLSUbiPos.wireless.PositionInfo;

public class Particle implements Cloneable{
	
	private double xCoordinate;
	
	private double yCoordinate;
	
	private int floor;
	
	private double headingBias;
	
	private double stepLength;
	
	private int ID;
	
	private Motion motion;
	
	private State state;
	
	private PositionInfo wifiPosition;
	
	public Particle(double xCoordinate, double yCoordinate, int floor, double bias, double stepLength, int ID){
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.floor = floor;
		this.headingBias = bias;
		this.stepLength = stepLength;
		this.ID = ID;
		this.wifiPosition = new PositionInfo();
	}
	
	public static Particle circleNormalDistribution(double xAverage, double yAverage, int floor, double radiusSigma, double heading, double biasSTD, double stepLength, int ID){
		double bias = biasSTD * NormalDistribution.randn();
		double angle = heading +bias;
		double radius = radiusSigma * NormalDistribution.randn();
		double xCoordinate = xAverage + radius * Math.sin(angle);
		double yCoordinate = yAverage + radius * Math.cos(angle);
		return new Particle(xCoordinate, yCoordinate, floor, bias, stepLength, ID);
	}
	
	public static Particle singlePosition(double xAverage, double yAverage, int floor, double biasSTD, double stepLength, int ID){
		double bias = biasSTD * NormalDistribution.randn();
		return new Particle(xAverage, yAverage, floor, bias, stepLength, ID);
	}
	
	public void inheritStepCali(Particle particle){
		this.xCoordinate = particle.xCoordinate;
		this.yCoordinate = particle.yCoordinate;
		this.headingBias = particle.headingBias;
		this.stepLength = particle.stepLength;
		this.floor = particle.floor;
	}
	
	public void inheritHeadingCali(Particle particle){
		this.xCoordinate = particle.xCoordinate;
		this.yCoordinate = particle.yCoordinate;
		this.headingBias = particle.headingBias;
		this.floor = particle.floor;
	}
	
	public void inheritPosition(Particle particle){
		this.xCoordinate = particle.xCoordinate;
		this.yCoordinate = particle.yCoordinate;
	}
	
	public void motionConfigure(double stepLength){
		switch(this.motion){
		case walking:
			this.stepLength = stepLength;
			break;
		case turning:
			this.stepLength = 0.8 * stepLength;
			break;
		case stairs:
			this.stepLength = 0.5;
			break;		
		case running:
			this.stepLength = 1.25 * stepLength;
			break;
		}
	}
	
	public void stateConfigure(){
		switch(this.state){
		case texting:
			break;
		case calling:
			this.headingBias += 20 * Math.PI / 180;
			break;
		case pocket:
			this.headingBias += 40 * Math.PI / 180;
			break;
		case handSwing:
			this.headingBias += 30 * Math.PI / 180;
			break;
		}
	}
	
	public double getXCoordinate(){
		return xCoordinate;
	}
	
	public double getYCoordinate(){
		return yCoordinate;
	}
	
	public int getFloor(){
		return floor;
	}
	
	public double getHeadingBias(){
		return headingBias;
	}
	
	public double getStepLength(){
		return stepLength;
	}
	
	public int getID(){
		return ID;
	}
	
	public PositionInfo getWiFiLocation(){
		return wifiPosition;
	}
	
	public void setXCoordinate(double x){
		this.xCoordinate = x;
	}
	
	public void setYCoordinate(double y){
		this.yCoordinate = y;
	}
	
	public void setFloor(int floor){
		this.floor = floor;
	}
	
	public void setHeadingBias(double bias){
		this.headingBias = bias;
	}
	
	public void setStepLength(double length){
		this.stepLength = length;
	}
	
	public void setID(int id){
		this.ID = id;
	}
	
	public void setWiFiLocation(PositionInfo pos){
		this.wifiPosition = pos;
	}

}
