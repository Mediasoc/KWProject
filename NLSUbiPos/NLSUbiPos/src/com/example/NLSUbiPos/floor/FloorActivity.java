package com.example.NLSUbiPos.floor;

import java.util.Calendar;
import com.example.NLSUbiPos.R;
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


//this is an activity which tests FloorRecognition.java
public class FloorActivity extends Activity implements SensorEventListener{
	private SensorManager sensorManager = null;
	private Button startButton;
	private Button stopButton;
	private Button ExitButton;
	private TextView showText;
	private EditText floorText;
	private PressureFloorDetector pfd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		pfd=new PressureFloorDetector();
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_floor);
		
		// sensors
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		final Sensor pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		// budgets
		showText = (TextView) findViewById(R.id.showText);
		startButton = (Button) findViewById(R.id.Start);
		stopButton = (Button) findViewById(R.id.Stop);
		ExitButton = (Button) findViewById(R.id.Exit);
		floorText=(EditText) findViewById(R.id.edittext);
		startButton.setOnClickListener(new OnClickListener(){
			@SuppressLint("ShowToast") @Override
			public void onClick(View v) {
				sensorManager.registerListener(FloorActivity.this, pressure, SensorManager.SENSOR_DELAY_GAME);
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				ExitButton.setEnabled(true);
				
				if(floorText.getText().toString().length()==0)
				{
				Toast.makeText(FloorActivity.this, "璇峰～鍐欐ゼ灞傛暟", Toast.LENGTH_LONG).show();
				
				}
				
				else{
				pfd.setinifloor(Integer.parseInt(floorText.getText().toString()));

			
			}
			}
		}
		);
		
		
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sensorManager.unregisterListener(FloorActivity.this);
				pfd.pressureList.clear();
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				ExitButton.setEnabled(true);
			}
		});
		
		ExitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sensorManager.unregisterListener(FloorActivity.this);
				finish();
				//onDestroy();
			}
		});
		
		
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(FloorActivity.this);
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
	}

	private void updateMessage() {
		
//		pfd.pressureSize=0;
		Calendar now = Calendar.getInstance();
		String time = "" + now.get(Calendar.YEAR) + ":" + (now.get(Calendar.MONTH)+1) + ":" + now.get(Calendar.DAY_OF_MONTH) + 
				":" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
	     
		int floornum=pfd.getFloor();
	    showText.append(time+"->Floornum="+floornum+"\n");
//	    this.onPause();
	    

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		pfd.onSensorChanged(event);
//		System.out.println(pfd.pressureSize);
		if(pfd.getpresize()>=50 ) {
			updateMessage();
			pfd.notifyFloorEvent(pfd.getFloor());
		}
		
		
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍?
		
	}
}
