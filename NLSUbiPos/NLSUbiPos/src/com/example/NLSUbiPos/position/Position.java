package com.example.NLSUbiPos.position;
import com.example.NLSUbiPos.building.Building;
/**
 * the basic class which provide position
 */
import com.example.NLSUbiPos.context.OnContextListener;
import com.example.NLSUbiPos.coordinate.Mercator;
import com.example.NLSUbiPos.floor.OnFloorListener;
import com.example.NLSUbiPos.heading.OnHeadingChangeListener;
import com.example.NLSUbiPos.satellite.OnGPSPositionListener;
import com.example.NLSUbiPos.stepdetecor.OnStepListener;
import com.example.NLSUbiPos.wireless.OnWirelessPositionListener;

/**
 * edited by LiuDonghui on 20160415
 *
 */

public abstract class Position implements OnStepListener, OnHeadingChangeListener,OnFloorListener,OnContextListener,
OnWirelessPositionListener,OnGPSPositionListener {
	
	protected Building building;
	protected Mercator CurrentWiFiLocation;
	protected Mercator CurrentGPSLocation;
	protected int floor;

	public abstract void setPosition(double positionX, double positionY, int floor);
	
	public void setBuilding(Building building){
		this.building = building;
		if(building != null){
			this.floor = building.getCurrentFloorIndex();
		}
	}
	
	public void onWirelessPosition(Mercator mercator){
		CurrentWiFiLocation.x=mercator.x;
		CurrentWiFiLocation.y=mercator.y;
	}
	
	public void onGPSPosition(Mercator mercator){
		CurrentGPSLocation.x=mercator.x;
		CurrentGPSLocation.y=mercator.y;
	}
	
	public void onFloor(int floornum){
		floor=floornum;
	}
}
