package com.example.NLSUbiPos.floor;

import java.util.Calendar;

import com.example.NLSUbiPos.position.ParticlePosition;
import com.example.NLSUbiPos.position.Position;
import com.example.NLSUbiPos.position.PositionClient;
import com.example.fusionnavigation.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//this is an activity which tests onFloorListener
public class FloorTest extends Activity implements OnFloorListener{
	private SensorManager sensorManager = null;
	private Button startButton;
	private Button stopButton;
	private Button ExitButton;
	private TextView showText;
	private EditText floorText;
	private PositionClient pc;
	private int floornum;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		pc=new PositionClient(this);
		pc.getFloorDetector().addOnFloorListener(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_floor);
		showText = (TextView) findViewById(R.id.showText);
		startButton = (Button) findViewById(R.id.Start);
		stopButton = (Button) findViewById(R.id.Stop);
		ExitButton = (Button) findViewById(R.id.Exit);
		floorText=(EditText) findViewById(R.id.edittext);
		startButton.setOnClickListener(new OnClickListener(){
			@SuppressLint("ShowToast") @Override
			public void onClick(View v) {

			pc.getFloorDetector().setinifloor(Integer.parseInt(floorText.getText().toString()));
	        pc.registerSensorListener();
	 
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				ExitButton.setEnabled(true);
//				showText.append("You are at "+floornum+" floor");  
			
			}
		});
		
		
		
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pc.unregisterSensorListener();
				pc.getFloorDetector().removeOnFloorListener();
			
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				ExitButton.setEnabled(true);
			
			}
		});
		
		ExitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
		
			}
		});
		
		
		
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		sensorManager.unregisterListener(FloorActivity.this);
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		
	}

	@Override
	public void onFloor(int floor) {
	
		this.floornum=floor;
	}





}
