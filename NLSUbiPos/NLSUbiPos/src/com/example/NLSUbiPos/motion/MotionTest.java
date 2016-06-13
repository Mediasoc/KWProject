package com.example.NLSUbiPos.motion;


import com.example.NLSUbiPos.position.PositionClient;
import com.example.NLSUbiPos.R;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/*
 * this is an activity that tests the onMotionListener
 */
public class MotionTest extends Activity implements OnMotionListener{
	Button startButton;
	Button stopButton;
	TextView showText;
	SimpleMotionDetector md;
	PositionClient pc;
	int motion;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motiontest);
        pc=new PositionClient(this);
        pc.getMotionDetector().addOnMotionListener(this);
		showText = (TextView) findViewById(R.id.showText);
		startButton = (Button) findViewById(R.id.Start);
		stopButton = (Button) findViewById(R.id.Stop);
		startButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				pc.registerSensorListener();
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			    Thread1 th=new Thread1();
			    th.start();
			}
		});
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pc.unregisterSensorListener();
				pc.getMotionDetector().removeOnMotionListener();
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				
			}
		});
		
        
    }
	
class Thread1 extends Thread{
	public void run(){
		int i=0;
		for(i=0;i<100;i++){
		try{
			sleep(1000);
	
			System.out.println(motion);
			}
		catch(Exception e){
		}
		}
	}
}
	

	@Override
	public void onMotion(int motion) {
		// TODO 自动生成的方法存根
		this.motion=motion;
	}
	
}
