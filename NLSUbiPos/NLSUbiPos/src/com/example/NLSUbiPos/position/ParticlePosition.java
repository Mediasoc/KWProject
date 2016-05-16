package com.example.NLSUbiPos.position;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.example.NLSUbiPos.building.Building;
import com.example.NLSUbiPos.context.ContextEvent;
import com.example.NLSUbiPos.coordinate.Lonlat;
import com.example.NLSUbiPos.coordinate.Mercator;
import com.example.NLSUbiPos.geometry.Line2d;
import com.example.NLSUbiPos.particle.Particle;
import com.example.NLSUbiPos.stepdetecor.StepEvent;
import com.example.NLSUbiPos.utils.NormalDistribution;
import com.example.NLSUbiPos.wireless.PositionInfo;
import com.example.NLSUbiPos.wireless.PositionProb;
import android.location.Location;

/**
 * added by LiuDonghui on 20160415
 * @author Lames
 *
 */
public class ParticlePosition extends Position{
	
	public static enum Motion{
		walking, turning, stairs, running
	}
	
	public static enum State{
		texting, calling, pocket, handSwing
	}
	
	private int numberOfParticles;
	
	private Set<Particle> particles;
	
	private double heading;
	
	private double stepLength;
	
	private double positionX;
	
	private double positionY;
	
	private double headingSpread = Math.PI * 45 /180;
	
	private double stepLengthSpread = 0.2;
	
	private double positionSpread = 0.7;
	
//	private double initialSpread = 0.7;
	
	private int stepCount = 0;
	
	private Building building;
	
	private static final int DEFAULT_PARTICLE_COUNT = 2000;
	
	private boolean STEP_CALI = true;
	
	private boolean HEADING_CALI = true;
	
	private double headingBias = 0.0;
	
	private boolean GPSAssistance = false;
	
	private boolean WiFiAssistance = false;
	
	private int GPSCredibility = 3;
	
	private float GPSAccuracy = 2;
	
	private double GPSBearing = 0;
	
	public Lonlat CurrentLonlatLocation;
	
	private List<PositionProb> WiFiList;
	
	private Collection<Line2d> workingSet = new HashSet<Line2d>();
	
	public ParticlePosition(double xAverage, double yAverage, int floor){
		numberOfParticles = DEFAULT_PARTICLE_COUNT;
		stepCount = 0;
		setPosition(xAverage, yAverage, floor);
	}
	
	@Override
	public void setPosition(double xAverage, double yAverage, int floor) {
		int number = 1;
		double length;
		
		if(particles != null && particles.size() ==0){
			while (number <= numberOfParticles){
				length = 0.7 + stepLengthSpread * NormalDistribution.randn();
				particles.add(Particle.singlePosition(xAverage, yAverage, floor, headingSpread, 
						length, number));
				number++;
			}
			positionX = xAverage;
			positionY = yAverage;
		}else {
			particles = new HashSet<Particle>(numberOfParticles);
			if(WiFiAssistance && WiFiList != null){
				for(PositionProb positionProb : WiFiList){
					while(number <= positionProb.prob * numberOfParticles){
						length = 0.7 +stepLengthSpread * NormalDistribution.randn();				
						particles.add(Particle.circleNormalDistribution(positionProb.aPositionInfo.x, 
								positionProb.aPositionInfo.y, floor, positionSpread, heading, 
								headingSpread, length, number));
						number++;
					}
					number = 0;
				}
			}else{
				while(number <= numberOfParticles){
					length = 0.7 +stepLengthSpread * NormalDistribution.randn();				
					particles.add(Particle.circleNormalDistribution(xAverage, yAverage, floor, 
							positionSpread, heading, headingSpread, length, number));
					number++;
				}		
			}
			computeCloudAverage();			
		}
		
	}

	@Override
	public void onStep(StepEvent event) {
		stepCount++;
		stepLength = event.getStepLength();
		for(Particle particle : particles){
//			particle.motionConfigure(stepLength);
			particle.setStepLength(stepLength + stepLengthSpread * NormalDistribution.randn());
			particle.setWiFiLocation(findNearestAP(particle, WiFiList));
		}
		HashSet<Particle> livedParticles = new HashSet<Particle>(particles.size());
		HashSet<Particle> deadParticles = new HashSet<Particle>(particles.size());
		boolean particleLive;
		for(Particle particle : particles){
			if(GPSAssistance){
				if(GPSCredibility > 8){
					particle.setXCoordinate(CurrentGPSLocation.getX());
					particle.setYCoordinate(CurrentGPSLocation.getY());
					particleLive = true;
				}else{
					particleLive = updateParticle(particle, heading) && GPSAssisted(particle,
							CurrentGPSLocation, GPSAccuracy * 2);
				}
			}
			if(WiFiAssistance){
				particleLive = updateParticle(particle, heading) && WiFiAssisted(particle);
			}else{
				particleLive = updateParticle(particle, heading);
			}
			if(particleLive){
				livedParticles.add(particle);
			}else{
				deadParticles.add(particle);				
			}
		}
		particles = livedParticles;
		if(particles.size() == 0){
			if(GPSAssistance){
				if(GPSCredibility < 8){
					setPosition((positionX + GPSCredibility * CurrentGPSLocation.getX())/
							(1 + GPSCredibility),(positionY + GPSCredibility * 
									CurrentGPSLocation.getY()) / (1 + GPSCredibility),floor);
				}else{
					setPosition(CurrentGPSLocation.getX(), CurrentGPSLocation.getY(),floor);
				}
			}else{
				setPosition(positionX, positionY, floor);
			}
		}else if(particles.size() < 1 * numberOfParticles){
			resample(deadParticles);
		}
		computeCloudAverage();
		computeMeanBias();
	}

	@Override
	public void onHeadingChange(double heading) {
		if(HEADING_CALI){
			this.heading = heading + headingBias;
		}else{
			this.heading = heading;
		}		
		if(GPSAssistance){
			if(GPSCredibility < 8){
				this.heading = (this.heading + GPSCredibility * GPSBearing)/(1 + GPSCredibility);
			}else{
				this.heading = GPSBearing;
			}
		}
	}

	@Override
//	public void onContext(ContextEvent event) {
	public void onContext(int context) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean updateParticle(Particle particle, double heading){
		double x = particle.getXCoordinate();
		double y = particle.getYCoordinate();
		double randomStepLength = particle.getStepLength();
		double newX = x + randomStepLength * Math.sin(heading);
		double newY = y + randomStepLength * Math.cos(heading);
		Line2d trajectory = new Line2d(x, y, newX, newY);
		workingSet.clear();
		workingSet.addAll(building.getCurrentFloor().getWallsArea().getWorkingSet(x, y));
		if(building != null){
			for (Line2d line : building.getCurrentFloor().getWallsArea().getWorkingSet(x, y)){
				if(trajectory.isLineIntersection(line)){
					return false;
				}
			}
		}
		particle.setXCoordinate(newX);
		particle.setYCoordinate(newY);
		return true;
	}
	
	public void setBuilding(Building building){
		this.building = building;
	}
	
	private void resample(HashSet<Particle> deadParticles){
		HashSet<Particle> newParticles = new HashSet<Particle>(numberOfParticles);
		double length;
		Particle livedParticle;
		Iterator<Particle> iter = particles.iterator();
		for(Particle particle : deadParticles){
			if(STEP_CALI){
				if(iter.hasNext()){
					livedParticle = iter.next();
					particle.inheritStepCali(livedParticle);
					newParticles.add(particle);					
				}else{
					iter = particles.iterator();
					livedParticle = iter.next();
					particle.inheritStepCali(livedParticle);
					newParticles.add(particle);
				}
			}else if(HEADING_CALI){
				if(iter.hasNext()){
					livedParticle = iter.next();
					particle.inheritHeadingCali(livedParticle);
					newParticles.add(particle);
				}else{
					iter = particles.iterator();
					livedParticle = iter.next();
					particle.inheritHeadingCali(livedParticle);
					newParticles.add(particle);
				}
			}else{
//				length = stepLength + stepLengthSpread * NormalDistribution.randn();
//				newParticles.add(Particle.circleNormalDistribution(positionX, positionY, floor, positionSpread, heading, headingSpread, length, particle.getID()));
				if(iter.hasNext()){
					livedParticle = iter.next();
					particle.inheritPosition(livedParticle);
					newParticles.add(particle);
				}else{
					iter = particles.iterator();
					livedParticle = iter.next();
					particle.inheritPosition(livedParticle);
					newParticles.add(particle);
				}
			}
		}
		particles.addAll(newParticles);
	}
	
	private void computeCloudAverage(){
		double sumX = 0;
		double sumY = 0;
		for(Particle particle : particles){
			sumX += particle.getXCoordinate();
			sumY += particle.getYCoordinate();
		}
		positionX = sumX / particles.size();
		positionY = sumY / particles.size();
	}
	
	private void computeMeanBias(){
		for(Particle particle : particles){
			headingBias += particle.getHeadingBias();
		}
		headingBias /= particles.size();
	}

	@Override
	public void onGPSPosition(Location location) {
    	CurrentLonlatLocation = new Lonlat(location.getLongitude(),location.getLatitude());
    	//coordinate convert
		CurrentGPSLocation = CurrentLonlatLocation.lonlattomercator();
		if(location.hasAccuracy())
			GPSAccuracy = location.getAccuracy();
		else
			GPSAccuracy = Integer.MAX_VALUE;
		GPSCredibility = (int)(5/GPSAccuracy);
		if(location.hasBearing())
			GPSBearing = location.getBearing() * Math.PI / 180;
		else
			GPSBearing = heading;
	}
	
	private boolean GPSAssisted(Particle particle, Mercator GPS, float accuracy){
		double dist = Math.sqrt((particle.getXCoordinate() - GPS.getX()) * 
				(particle.getXCoordinate() - GPS.getX()) + 
				(particle.getYCoordinate() - GPS.getY()) * 
				(particle.getYCoordinate() - GPS.getY()));
		if(dist > accuracy){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean WiFiAssisted(Particle particle){
		double dist = Math.sqrt((particle.getXCoordinate() - particle.getWiFiLocation().x) * 
				(particle.getXCoordinate() - particle.getWiFiLocation().x) + 
				(particle.getYCoordinate() - particle.getWiFiLocation().y) * 
				(particle.getYCoordinate() - particle.getWiFiLocation().y));
		if(dist > 2){
			return false;
		}else{
			return true;
		}
	}
	
	public void setGPSAssistance(boolean flag){
		this.GPSAssistance = flag;
	}
	
	public void setGPSCredibility(int credit){
		this.GPSCredibility = credit;
	}

	@Override
	public void onWirelessPosition(List<PositionProb> list) {
		WiFiList =list;		
	}

	private PositionInfo findNearestAP(Particle particle, List<PositionProb> list){
		double dist = Integer.MAX_VALUE;
		PositionInfo minDistAP = null;
		double tempDist = 0;
		for(PositionProb pos : list){
			tempDist = Math.sqrt((particle.getXCoordinate() - pos.aPositionInfo.x) * 
					(particle.getXCoordinate() - pos.aPositionInfo.x) + 
					(particle.getYCoordinate() - pos.aPositionInfo.y) * 
					(particle.getYCoordinate() - pos.aPositionInfo.y));
			if(tempDist <= dist){
				minDistAP = pos.aPositionInfo;
				dist = tempDist;
			}
		}
		return minDistAP;
	}

	
}
