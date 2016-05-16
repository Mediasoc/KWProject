package com.example.NLSUbiPos.motion;


import com.example.fusionnavigation.R;

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

public class MotionTestActivity extends Activity implements SensorEventListener{
	SensorManager sensorManager = null;
	Button startButton;
	Button stopButton;
	TextView showText;
	SimpleMotionDetector md;
	Thread th;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.motiontest);
    	md=new SimpleMotionDetector();
//    	sensorManager=md.sensorManager;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		final Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		final Sensor linearacc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		final Sensor pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		if(gravity==null || linearacc==null||pressure==null) {
			Toast.makeText(this, "Gravity|Linearac sensor is not supporter", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		showText = (TextView) findViewById(R.id.showText);
		startButton = (Button) findViewById(R.id.Start);
		stopButton = (Button) findViewById(R.id.Stop);
		startButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			
				sensorManager.registerListener(MotionTestActivity.this, gravity, SensorManager.SENSOR_DELAY_GAME);
				sensorManager.registerListener(MotionTestActivity.this, linearacc, SensorManager.SENSOR_DELAY_GAME);
				sensorManager.registerListener(MotionTestActivity.this, pressure, SensorManager.SENSOR_DELAY_GAME);
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
		});
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sensorManager.unregisterListener(MotionTestActivity.this);
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		});
		
        
    }
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		md.onSensorChanged(event);
		 System.out.println(md.getLinaccSize());
		if(md.getgraSize()>=100 && md.getLinaccSize()>=100) {
			int s=md.getmotion();
			showText.append(s+"\n");
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自动生成的方法存根
		
	}
	
}
