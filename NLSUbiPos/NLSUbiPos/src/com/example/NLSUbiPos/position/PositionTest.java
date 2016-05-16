package com.example.NLSUbiPos.position;

import com.example.NLSUbiPos.context.OnContextListener;
import com.example.NLSUbiPos.coordinate.Mercator;
import com.example.NLSUbiPos.floor.OnFloorListener;
import com.example.NLSUbiPos.heading.OnHeadingChangeListener;
import com.example.NLSUbiPos.motion.OnMotionListener;
import com.example.NLSUbiPos.satellite.OnGPSPositionListener;
import com.example.NLSUbiPos.stepdetecor.OnStepListener;
import com.example.NLSUbiPos.stepdetecor.StepEvent;
import com.example.NLSUbiPos.wireless.OnWirelessPositionListener;

public class PositionTest implements OnStepListener, OnHeadingChangeListener,OnFloorListener,OnContextListener,
OnWirelessPositionListener,OnGPSPositionListener,OnMotionListener {

	public int motion;
	public int floor;
	public int iocontext;
	
	@Override
	public void onGPSPosition(Mercator mercator) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void onWirelessPosition(Mercator mercator) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void onContext(int context) {
		// TODO 自动生成的方法存根
		this.iocontext=context;
//		System.out.println(context);
	}

	@Override
	public void onFloor(int floor) {
		// TODO 自动生成的方法存根
		this.floor=floor;
	}

	@Override
	public void onHeadingChange(double heading) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void onStep(StepEvent event) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void onMotion(int motion) {
		// TODO 自动生成的方法存根
		this.motion=motion;
	}

}
