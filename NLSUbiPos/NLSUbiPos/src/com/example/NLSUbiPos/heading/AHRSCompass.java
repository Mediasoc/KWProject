package com.example.NLSUbiPos.heading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.example.NLSUbiPos.utils.Quaternion;


import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.util.Log;

public class AHRSCompass extends Compass implements SensorEventListener
{
	private float mHeading; 
	private Path route;
	private float [] Gyrovalues=new float[3];
	private float [] Accvalues=new float[3];
	private float [] Magvalues=new float[3];
	private List<Quaternion> AHRS_quaternion;
	private List<double []> mag_test;
	private double [] mag_test2=new double[3];
	private double sampleFreq;
	private double factor1;
	private double factor2;
	private String AHRSMode;
	private int counter;
	private List<Quaternion> quaternion_9dof_buf;
	private List<Quaternion> quaternion_6dof_buf;
	private double yawbias;
	private int interval;
	private int compare_durance;
	private long init_time;
	private double beta;
	private double beta_init;
	private double dT;
	private MadgwickAHRS AHRS_9dof;
	private MadgwickAHRS AHRS_6dof;
	private double [] threeAngle=new double[3];
	
	private long[] timestamps; 

	
	public AHRSCompass() {
		route= new Path();
		route.moveTo(0, 0);
		sampleFreq=500/10;
		dT=1/sampleFreq;
		beta_init=0.5;
		beta=0.5;
		interval=4;
		compare_durance=2*interval;
		AHRS_9dof=new MadgwickAHRS(dT,beta_init);
		AHRS_6dof=new MadgwickAHRS(dT,beta_init);
		init_time=Math.round(2/dT/interval)*interval*3;   
		counter=0;
		quaternion_9dof_buf=new ArrayList<Quaternion>();
		quaternion_6dof_buf=new ArrayList<Quaternion>();
		yawbias=90; //110.5272
		   
		AHRS_quaternion=new ArrayList<Quaternion>();
		mag_test=new ArrayList<double []>();
		timestamps = new long[3];
//		AHRSMode = "9DOF_only";
		AHRSMode = "fused";
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		synchronized (this) {
			float [] values=event.values;

			if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				//processGyroscopeEvent(event);
				Gyrovalues[0]=values[0];
				Gyrovalues[1]=values[1];
				Gyrovalues[2]=values[2];
				timestamps[0]=event.timestamp;

			} else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				Accvalues[0]=(float) (values[0]/9.8);
				Accvalues[1]=(float) (values[1]/9.8);
				Accvalues[2]=(float) (values[2]/9.8);
				timestamps[1]=event.timestamp;			

			} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				Magvalues[0]=values[0];
				Magvalues[1]=values[1];
				Magvalues[2]=values[2];
				timestamps[2]=event.timestamp;
			}	
			if(timestamps[0]==timestamps[1] && timestamps[1]==timestamps[2]){
				calculateHeading(counter,Gyrovalues, Accvalues, Magvalues, sampleFreq, factor1, factor2, AHRSMode);
				counter+=1;
//				Log.d(TAG, "counter = "+counter+", AHRS_quaternion.size(): "+AHRS_quaternion.size());
				if(AHRS_quaternion.size()>0){
					for(int c=0;c<3;c++)
					{
						threeAngle[c]=AHRS_quaternion.get(AHRS_quaternion.size()-1).quaternConj().quaternion2euler()[c]*180/Math.PI;
					}
					mHeading = (float) ((threeAngle[2]+yawbias)*Math.PI/180);
					if(counter == Integer.MAX_VALUE){
						counter = 1000;
					}
					notifyHeadingChange(getHeading());//update the heading while sensor data changed
				}				
			}/*else{
				long min = 0L;
				long average = (timestamps[0]+timestamps[1]+timestamps[2])/3;
				for(int i=0; i<3; i++){
					if(timestamps[i] < average)
						min = timestamps[i];
				}
				sensorFreq = Math.pow(10, 9)/(event.timestamp - min);
				
				Log.d(TAG, "Freq: "+sampleFreq);
			}	*/
		}
	}
	
	public void calculateHeading(int i,float [] Gyro, float [] acc, float [] mag, double sampleFreq, double factor1, double factor2, String AHRSMode)
	{
		AHRSenhanced( i, Gyro,  acc,  mag,  1/sampleFreq, factor1,  factor2, AHRSMode);
		
	}
	public void AHRSenhanced(int i,float [] Gyro, float [] acc, float [] mag, double dT, double factor1, double factor2, String AHRSMode)
	{

		double [] mag_ENU=new double[3];
		double [] true_value={48.5,33.47,46.383};
		double [] threshold_collection={9.3*factor1,8*factor1,10.7*factor1};
		double yaw_dif_threshold = compare_durance*factor2;
		double yaw_difference;
		double [] mag_GoodOrNot2=new double[compare_durance];
		boolean [] check=new boolean[3];
		
		if(AHRSMode.compareTo("9DOF_only")==0)
		{
			AHRS_9dof.update(Gyro, acc, mag);
			AHRS_quaternion.add(AHRS_9dof.getQuaternion());
			reset(AHRS_quaternion);
		}
		else if(AHRSMode.compareTo("6DOF_only")==0)
		{
			AHRS_6dof.updateIMU(Gyro , acc);
			if((i+1)<init_time)
			{
				AHRS_9dof.update(Gyro, acc, mag);
				AHRS_6dof.setQuaternion(AHRS_9dof.getQuaternion());
			}
			if((i+1)==init_time)
			{
				AHRS_6dof=new MadgwickAHRS(dT,beta_init,AHRS_9dof.getQuaternion().getData());
			}
			AHRS_quaternion.add(AHRS_6dof.getQuaternion());
			reset(AHRS_quaternion);
		}
		else if(AHRSMode.compareTo("fused")==0)
		{
			AHRS_9dof.update(Gyro, acc, mag);
			AHRS_6dof.updateIMU(Gyro , acc);
			
			mag_ENU=quaternRotate(mag,AHRS_6dof.getQuaternion());
			mag_test2[0]=Math.sqrt(mag[0] * mag[0] + mag[1] * mag[1] + mag[2] * mag[2]);
			mag_test2[1]=Math.sqrt(mag_ENU[0] * mag_ENU[0] + mag_ENU[1] * mag_ENU[1]);
			mag_test2[2]=Math.atan2(mag_ENU[2], mag_test2[1])*180/Math.PI;
			//System.out.println(i);
			//System.out.println(init_time);
			mag_test.add(mag_test2);
			reset(mag_test);
			if((i+1)<init_time)
			{
				AHRS_quaternion.add(AHRS_9dof.getQuaternion());
				AHRS_6dof.setQuaternion(AHRS_9dof.getQuaternion());
				reset(AHRS_quaternion);
				return;
			}
			else if((i+1)<=init_time+compare_durance-interval)
			{
				if((i+1)==init_time)
				{
					AHRS_9dof=new MadgwickAHRS(dT,beta,AHRS_9dof.getQuaternion().getData());
					AHRS_6dof=new MadgwickAHRS(dT,beta,AHRS_9dof.getQuaternion().getData());
				}
				AHRS_quaternion.add(AHRS_9dof.getQuaternion());
				reset(AHRS_quaternion);
			}
			else if(((i+1)%interval==0) && ((i+1)>=init_time+compare_durance))
			{
				int length = mag_test.size();
				yaw_difference = cal_yaw_diff(quaternion_6dof_buf, quaternion_9dof_buf);
				for(int k=0;k<compare_durance;k++)
				{
					for(int c=0;c<3;c++)
					{
						if(Math.abs(mag_test.get(length-k-1)[c]-true_value[c])<threshold_collection[c])
						{
							check[c]=true;
						}
						else
						{
							check[c]=false;
						}
					}
					if((check[0]==true && check[1]==true) || (check[0]==true && check[2]==true))
					{
						mag_GoodOrNot2[k]=1;
					}
				}
				double nnz=0;
				for(int k=0;k<compare_durance;k++)
				{
					if(mag_GoodOrNot2[k]!=0)
						nnz+=1;
				}
				if(nnz==compare_durance && yaw_difference<yaw_dif_threshold)
				{
					AHRS_6dof.setQuaternion(AHRS_9dof.getQuaternion());
					for(int c=length-interval;c<length;c++)
					{
						AHRS_quaternion.set(c, quaternion_9dof_buf.get(c));
					}
				}				
				else
				{
//					Log.d(AHRSMode, "buf: "+quaternion_6dof_buf.size()+" AHRS_quat: "+AHRS_quaternion.size());
					AHRS_9dof.setQuaternion(AHRS_6dof.getQuaternion());
					for(int c=length-interval;c<length;c++)
					{
						AHRS_quaternion.set(c, quaternion_6dof_buf.get(c-1));		
					}
				}
			}
			quaternion_9dof_buf.add(AHRS_9dof.getQuaternion());
			quaternion_6dof_buf.add(AHRS_6dof.getQuaternion());	
			reset(quaternion_9dof_buf);
			reset(quaternion_6dof_buf);
		}
	}
	
	public void reset(List quat){
		if(quat.size() > compare_durance+1){
			quat.remove(0);
		}
	}
		
	public double[] getAHRS_euler()
	{
		return threeAngle;
	}


	public float getHeading() {
		return mHeading;
	}


	public double [] quaternRotate(float [] v, Quaternion q)
	{
		double [] v2=new double[4];
		v2[0]=0;
		v2[1]=v[0];
		v2[2]=v[1];
		v2[3]=v[2];
		Quaternion q2=new Quaternion(v2);
		Quaternion q3=q.quaternProd(q2);
		Quaternion vOXYZ=q3.quaternProd(q.quaternConj());
		double [] v3=new double[3];
		v3[0]=vOXYZ.getData()[1];
		v3[1]=vOXYZ.getData()[2];
		v3[2]=vOXYZ.getData()[3];
		return v3;
	}
	public double cal_yaw_diff(List<Quaternion> q1,List<Quaternion> q2)
	{
		double yaw_difference;
		List<double []> q1_euler=new ArrayList<double []>();
		List<double []> q2_euler=new ArrayList<double []>();
		for(int c=0;c<q1.size();c++)
		{
			q1_euler.add(q1.get(c).quaternConj().quaternion2euler());
			q2_euler.add(q2.get(c).quaternConj().quaternion2euler());
		}
		List<double []> q1_yaw=new ArrayList<double []>();
		List<double []> q2_yaw=new ArrayList<double []>();
		double [] q1_yaw_tmp=new double[1];
		double [] q2_yaw_tmp=new double[1];
		for(int d=0;d<q1_euler.size();d++)
		{
			q1_yaw_tmp[0]=Math.atan(Math.tan(q1_euler.get(d)[2]));
			q1_yaw.add(q1_yaw_tmp);
			q2_yaw_tmp[0]=Math.atan(Math.tan(q2_euler.get(d)[2]));
			q2_yaw.add(q2_yaw_tmp);
		}
		double sum1=0;
		double sum2=0;
		double mean1;
		double mean2;
		for(int d=0;d<q1_euler.size();d++)
		{
			sum1+=q1_euler.get(d)[2];
			sum2+=q2_euler.get(d)[2];
		}
		mean1=sum1/q1_euler.size();
		mean2=sum2/q2_euler.size();
		double sum3=0;
		double sum4=0;
		double mean3;
		double mean4;
		for(int d=0;d<q1_yaw.size();d++)
		{
			sum3+=q1_yaw.get(d)[0];
			sum4+=q2_yaw.get(d)[0];
		}
		mean3=sum3/q1_yaw.size();
		mean4=sum4/q2_yaw.size();
		List<double []> d1=new ArrayList<double []>();
		List<double []> d2=new ArrayList<double []>();
		double [] minus1=new double[1];
		double [] minus2=new double[1];
		for(int d=0;d<q1_euler.size();d++)
		{
			minus1[0]=q1_euler.get(d)[2]-mean1-q2_euler.get(d)[2]+mean2;
			d1.add(minus1);
		}
		for(int d=0;d<q1_yaw.size();d++)
		{
			minus2[0]=q1_yaw.get(d)[0]-mean3-q2_yaw.get(d)[0]+mean4;
			d2.add(minus2);
		}
		double normSum1=0;
		double normSum2=0;
		double d1norm;
		double d2norm;
		for(int d=0;d<d1.size();d++)
		{
			normSum1+=d1.get(d)[0]*d1.get(d)[0];
		}
		d1norm=Math.sqrt(normSum1);
		for(int d=0;d<d2.size();d++)
		{
			normSum2+=d2.get(d)[0]*d2.get(d)[0];
		}
		d2norm=Math.sqrt(normSum2);
		yaw_difference=Math.min(d1norm, d2norm)*180/Math.PI;
		return yaw_difference;
	}
	
	public double[] getEuler(){
		if(AHRS_quaternion.size()>0){
			return AHRS_quaternion.get(AHRS_quaternion.size()-1).quaternConj().quaternion2euler();
		}else
			return new double[3];
	}
	
	public void saveSensorData(String filename, float[] data){
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File file = new File(Environment.getExternalStorageDirectory(),filename);
			long time = System.nanoTime();
			OutputStream os;
			try{
				os = new FileOutputStream(file,true);
				os.write(String.valueOf(time).getBytes());
				for(int i=0;i<3;i++){
					os.write(" ".getBytes());
					os.write(String.valueOf(data[i]).getBytes());
				}
				os.write("\r\n".getBytes());
				os.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}else{
			Log.d("File save", "External Storage is not available");
		}
		
	}
}
