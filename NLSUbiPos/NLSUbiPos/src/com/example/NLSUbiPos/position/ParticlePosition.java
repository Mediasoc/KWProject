package com.example.NLSUbiPos.position;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.example.NLSUbiPos.building.Building;
import com.example.NLSUbiPos.context.ContextEvent;
import com.example.NLSUbiPos.geometry.Line2d;
import com.example.NLSUbiPos.particle.Particle;
import com.example.NLSUbiPos.stepdetecor.StepEvent;
import com.example.NLSUbiPos.utils.NormalDistribution;

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
	
	private double stepLengthSpread = 0.1;
	
	private double positionSpread = 0.7;
	
//	private double initialSpread = 0.7;
	
	private int stepCount = 0;
	
	private Building building;
	
	private static final int DEFAULT_PARTICLE_COUNT = 1000;
	
	private boolean STEP_CALI = true;
	
	private boolean HEADING_CALI = true;
	
	private double headingBias = 0.0;
	
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
				particles.add(Particle.singlePosition(xAverage, yAverage, floor, headingSpread, length, number));
				number++;
			}
			positionX = xAverage;
			positionY = yAverage;
		}else {
			particles = new HashSet<Particle>(numberOfParticles);
			while(number <= numberOfParticles){
				length = 0.7 +stepLengthSpread * NormalDistribution.randn();
				particles.add(Particle.circleNormalDistribution(xAverage, yAverage, floor, positionSpread, heading, headingSpread, length, number));
				number++;
			}
			computeCloudAverage();
		}
		
	}

	@Override
	public void onStep(StepEvent event) {
		stepCount++;
		stepLength = event.getStepLength();
		for(Particle particle : particles){
			particle.motionConfigure(stepLength);
		}
		HashSet<Particle> livedParticles = new HashSet<Particle>(particles.size());
		HashSet<Particle> deadParticles = new HashSet<Particle>(particles.size());
		boolean particleLive;
		for(Particle particle : particles){
			particleLive = updateParticle(particle, heading);
			//TODO add wifiAssisted and gpsAssisted functions
			if(particleLive){
				livedParticles.add(particle);
			}else{
				deadParticles.add(particle);				
			}
		}
		particles = livedParticles;
		if(particles.size() == 0){
			//TODO add wifiAssisted and gpsAssisted functions
			setPosition(positionX, positionY, floor);
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
	}

	@Override
	public void onContext(ContextEvent event) {
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
				length = stepLength + stepLengthSpread * NormalDistribution.randn();
				newParticles.add(Particle.circleNormalDistribution(positionX, positionY, floor, positionSpread, heading, headingSpread, length, particle.getID()));
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


	
}
