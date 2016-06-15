package com.example.NLSUbiPos.position;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

/**
 * added by LiuDonghui on 20160415
 * @author Lames
 *
 */
public class ParticlePosition extends Position{
/*	
	public static enum Motion{
		walking, turning, stairs, running
	}
	
	public static enum State{
		texting, calling, pocket, handSwing
	}
	*/
	private int numberOfParticles;
	
	private Set<Particle> particles;
	
	private double headingSpread = Math.PI * 45 /180;
	
	private double stepLengthSpread = 0.2;
	
	private double positionSpread = 0.7;
	
//	private double initialSpread = 0.7;
	
	private int stepCount;
	
//	private Building building;
	
	private static final int DEFAULT_PARTICLE_COUNT = 1000;
	
	private boolean STEP_CALI = true;
	
	private boolean HEADING_CALI = false;
	
	private double headingBias = 0.0;
	
	private boolean GPSAssistance = false;
	
	private boolean WiFiAssistance = true;
	
	private int GPSCredibility = 3;
	
	private float GPSAccuracy = 2;
	
	private double GPSBearing = 0;
	
	public Lonlat CurrentLonlatLocation;
	
	private List<PositionProb> WiFiList;
	
	private int motionLabel = 0;
	
	private int contextLabel = -1;
	
	private Collection<Line2d> workingSet = new HashSet<Line2d>();
	
	private Boolean WiFiable;
	
	private Mercator CurrentPosition;
	
	public ParticlePosition(double xAverage, double yAverage, int floor){
		Log.d("MainActivity", "ParticlePosition start");
		numberOfParticles = DEFAULT_PARTICLE_COUNT;
		stepCount = 0;
		CurrentPosition = new Mercator(0,0);
		if(WiFiAssistance && WiFiList != null){
			wifiInitializePosition(WiFiList, floor);
		}else{
			setPosition(xAverage, yAverage, floor);
			WiFiList = new ArrayList<PositionProb>();
		}
		WiFiable = Boolean.valueOf("false");
//		CurrentWiFiLocation = new PositionInfo();
		
	}
	
	@Override
	public void setPosition(double xAverage, double yAverage, int floor) {
		Log.d("MainActivity", "ParticlePosition setPosition()");
		int number = 1;
		double length;
		
		if(particles != null && particles.size() ==0){
			while (number <= numberOfParticles){
				length = 0.7 + stepLengthSpread * NormalDistribution.randn();
				particles.add(Particle.singlePosition(xAverage, yAverage, floor, 
						headingSpread, length, number));
				number++;
			}
			positionX = xAverage;
			positionY = yAverage;
		}else {
			particles = new HashSet<Particle>(numberOfParticles);
			while(number <= numberOfParticles){
				length = 0.7 +stepLengthSpread * NormalDistribution.randn();
				particles.add(Particle.circleNormalDistribution(xAverage, yAverage, floor,
						positionSpread, heading, headingSpread, length, number));
				number++;
				}		
			}
			computeCloudAverage();			
	}
	
	public void wifiInitializePosition(List<PositionProb> list, int floor){
		int number = 1;
		double length;
		particles = new HashSet<Particle>(numberOfParticles);
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
	}

	@Override
	public void onStep(StepEvent event) {
		//TODO
		Log.d("MainActivity", "ParticlePosition onStep()");
		stepCount++;
		stepLength = event.getStepLength();
		for(Particle particle : particles){
			particle.motionConfigure(motionLabel, stepLength + stepLengthSpread * NormalDistribution.randn());
			if(WiFiAssistance && WiFiable){
				particle.setWiFiLocation(findNearestAP(particle, WiFiList));
			}			
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
			if(WiFiAssistance && WiFiable){
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
			}else if(WiFiAssistance && WiFiable){
				wifiInitializePosition(WiFiList, floor);
//				setPosition((CurrentWiFiLocation.x+positionX)/2, (CurrentWiFiLocation.y+positionY)/2, floor);
			}else{
				setPosition(positionX, positionY, floor);
			}
		}else if(particles.size() < 1 * numberOfParticles){
			resample(deadParticles);
		}
		computeCloudAverage();
		computeMeanBias();
		
		savePositionData(event.getTimestamp(), positionX, positionY, floor, heading, WiFiList, motionLabel, contextLabel);
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
				this.heading = (this.heading + GPSCredibility * GPSBearing)
						/(1 + GPSCredibility);
			}else{
				this.heading = GPSBearing;
			}
		}
	}

	@Override
//	public void onContext(ContextEvent event) {
	public void onContext(int context) {
		contextLabel = context;
		switch(context){
		case(0):
//			t="Outdoor";
			GPSAssistance = true;
			WiFiAssistance = false;
			break;
		case(1):
			GPSAssistance = false;
			WiFiAssistance = true;
//			t="Indoor";
		    break;
		}
		
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
		CurrentPosition.setX(positionX + 13519000);
		CurrentPosition.setY(positionY + 3635000);
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
		double dist = (particle.getXCoordinate() - particle.getWiFiLocation().aPositionInfo.x) * 
				(particle.getXCoordinate() - particle.getWiFiLocation().aPositionInfo.x) + 
				(particle.getYCoordinate() - particle.getWiFiLocation().aPositionInfo.y) * 
				(particle.getYCoordinate() - particle.getWiFiLocation().aPositionInfo.y);
		if(dist > (4/particle.getWiFiLocation().prob) * (4/particle.getWiFiLocation().prob)){
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
	public void onWirelessPosition(List<PositionProb> list, Boolean flag) {
		//TODO
		Log.d("MainActivity", "ParticlePosition onWirelessPosition() "+list.size()+" Prob: "+list.get(0).prob+" "+list.get(1).prob+" "+list.get(2).prob);
		WiFiList = new ArrayList<PositionProb>();
		WiFiable = flag;
//		CurrentWiFiLocation =new PositionInfo();
		for(int i=0;i<3;i++){
			WiFiList.add(list.get(i));
//			CurrentWiFiLocation.x += list.get(i).aPositionInfo.x * list.get(i).prob;
//			CurrentWiFiLocation.y += list.get(i).aPositionInfo.y * list.get(i).prob;
		}	
	}

	private PositionProb findNearestAP(Particle particle, List<PositionProb> list){
		double dist = Integer.MAX_VALUE;
		PositionProb minDistAP = null;
		double tempDist = 0;
		for(PositionProb pos : list){
			tempDist = Math.sqrt((particle.getXCoordinate() - pos.aPositionInfo.x) * 
					(particle.getXCoordinate() - pos.aPositionInfo.x) + 
					(particle.getYCoordinate() - pos.aPositionInfo.y) * 
					(particle.getYCoordinate() - pos.aPositionInfo.y));
			if(tempDist <= dist){
				minDistAP = pos;
				dist = tempDist;
			}
		}
		return minDistAP;
	}

	@Override
	public void onMotion(int motion) {
		Log.d("MainActivity", "ParticlePosition onMotion()");
		motionLabel = motion;
		switch(motion){
		case 2://elevator up
			setPosition(99,759,floor);
			break;		
		case 3://elevator down
			setPosition(99,759,floor);
			break;
		case 4://upstairs
			if((92.7-positionX)*(92.7-positionX)+(760-positionY)*(760-positionY)>
			(105-positionX)*(105-positionX)+(750-positionY)*(750-positionY)){
				//eastern stairs
				setPosition(105,750,floor);
			}else{
				//western stairs
				setPosition(92.7,760,floor);
			}
			break;
		case 5://downstairs
			if((92.7-positionX)*(92.7-positionX)+(760-positionY)*(760-positionY)>
			(105-positionX)*(105-positionX)+(750-positionY)*(750-positionY)){
				//eastern stairs
				setPosition(105,750,floor);
			}else{
				//western stairs
				setPosition(92.7,760,floor);
			}
			break;
		}
	}

	public void savePositionData(long time, double x, double y, int floor, double heading, List<PositionProb> WiFi, int motion, int context){
		String filename = "PositionData.txt";
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File file = new File(Environment.getExternalStorageDirectory(),filename);
			
			OutputStream os;
			try{
				os = new FileOutputStream(file,true);
				os.write(String.valueOf(time).getBytes());
				os.write(" ".getBytes());
				os.write(String.valueOf((int)(x*1000)/1000.0).getBytes());
				os.write(" ".getBytes());
				os.write(String.valueOf((int)(y*1000)/1000.0).getBytes());
				os.write(" ".getBytes());
				os.write(String.valueOf(floor).getBytes());
				os.write(" ".getBytes());
				os.write(String.valueOf((int)(heading*1000/Math.PI*180)/1000.0).getBytes());
				os.write(" ".getBytes());
				double wifiX = 0;
				double wifiY = 0;
				for(PositionProb positionProb : WiFiList){
					wifiX += positionProb.aPositionInfo.x * positionProb.prob;
					wifiY += positionProb.aPositionInfo.y * positionProb.prob;
				}
				os.write(String.valueOf((int)(wifiX*1000)/1000.0).getBytes());
				os.write(" ".getBytes());
				os.write(String.valueOf((int)(wifiY*1000)/1000.0).getBytes());
				os.write(" ".getBytes());
				os.write(String.valueOf(motion).getBytes());
				os.write(" ".getBytes());
				os.write(String.valueOf(context).getBytes());
				os.write(" ".getBytes());
				os.write("\r\n".getBytes());
				os.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}else{
			Log.d("File save", "External Storage is not available");
		}
	}
	
	@Override
	public String getPositionInformation() {
		return "coordinate:(" + (int)(positionX*1000)/1000.0 + "," + (int)(positionY*1000)/1000.0 +
				")\nheading:" + (int)(heading * 180 / Math.PI * 100) / 100.0 + "°" + 
				"\nsteplength:" + (int)(stepLength*1000)/1000.0 + 
				" \nstepcount:" + stepCount + "\nParticle number:"+particles.size(); 
	}

	@Override
	public void renderPosition(Canvas canvas, float scale) {
		Paint paint = new Paint();
		// Render the position of the user.
		renderPositionMark(canvas, paint, scale);
		// Render the particle cloud around the user.
		renderParticleCloud(canvas, paint, scale);
	}
	
	/**
	 * Render the particle cloud around the user.
	 * @param canvas the Canvas object used to draw
	 * @param paint the Paint object used to draw
	 * @param scale how many pixels correspond to one meter
	 */
	private void renderParticleCloud(Canvas canvas, Paint paint, float scale) {
		//paint.setStyle(Paint.Style.FILL);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2.0f);
		paint.setColor(Color.BLACK);

		// Draw particles
		for (Particle particle : particles) {
			canvas.drawPoint((float)particle.getXCoordinate()*scale, (float)-particle.getYCoordinate()*scale, paint);
		}
		
		// Draw collision set
		paint.setColor(Color.GREEN);
		for (Line2d line : workingSet) {
			// Change the coordinates to pixels
			float x1 = (float) line.getStartPoint().getX() * scale;
			float y1 = - (float) line.getStartPoint().getY() * scale;
			float x2 = (float) line.getEndPoint().getX() * scale;
			float y2 = - (float) line.getEndPoint().getY() * scale;
			canvas.drawLine(x1, y1, x2, y2, paint);
		}
	}
	
	/**
	 * Render the position of the user.
	 * @param canvas the Canvas object used to draw
	 * @param paint the Paint object used to draw
	 * @param scale how many pixels correspond to one meter
	 */
	private void renderPositionMark(Canvas canvas, Paint paint, float scale) {
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4.0f);
		paint.setColor(Color.RED);
		
		float headingInDegree = (float) (heading * 180 / Math.PI);
		// Change the coordinates to pixels.
		float markX = (float) (positionX * scale);
		float markY = (float) (-positionY * scale);
		canvas.save();
		canvas.rotate(headingInDegree, markX, markY);
		// Draw mark circle
		canvas.drawCircle(markX, markY, 10.0f, paint);
		// Draw heading
		canvas.drawLine(markX, markY, markX, markY-40.0f, paint);
		canvas.restore();
	}
	
	public Lonlat getLonlatPosition(){
		return CurrentPosition.mercatortolonlat();
	}


}
