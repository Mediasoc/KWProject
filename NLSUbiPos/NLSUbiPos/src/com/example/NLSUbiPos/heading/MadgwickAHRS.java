package com.example.NLSUbiPos.heading;

import com.example.NLSUbiPos.utils.Matrix;
import com.example.NLSUbiPos.utils.Quaternion;

//import com.example.utils.*;

public class MadgwickAHRS 
{
	private double samplePeriod;
	private Quaternion quaternion;
	private double beta;
	
	public Quaternion getQuaternion()
	{
		return this.quaternion;
	}
	
	public void setQuaternion(Quaternion quaternion)
	{
		this.quaternion=quaternion;
	}
	
	public MadgwickAHRS()
	{
		double [] a={1,0,0,0};
		this.samplePeriod=1/256;
		this.quaternion=new Quaternion();
		this.quaternion.setData(a);
		this.beta=1;
	}
	public MadgwickAHRS(double SamplePeriod, double Beta,double [] quaternion)
	{
		this.samplePeriod=SamplePeriod;
		this.quaternion=new Quaternion(quaternion);
		this.beta=Beta;
	}
	public MadgwickAHRS(double SamplePeriod, double Beta)
	{
		double [] a={1,0,0,0};
		this.samplePeriod=SamplePeriod;
		this.quaternion=new Quaternion();
		this.quaternion.setData(a);
		this.beta=Beta;
	}
	
	public void update(float[] Gyroscope,float[] Accelerometer,float[] Magnetometer)
	{
		Quaternion q=this.quaternion;
		double [] accNormalized=new double[3];
		double [] magNormalized=new double[3];
		double accNorm=Math.sqrt(Accelerometer[0] * Accelerometer[0] + Accelerometer[1] * Accelerometer[1] + Accelerometer[2] * Accelerometer[2]);
		double magNorm=Math.sqrt(Magnetometer[0] * Magnetometer[0] + Magnetometer[1] * Magnetometer[1] + Magnetometer[2] * Magnetometer[2]);
		if(accNorm!=0 && magNorm!=0)
		{
			for (int i = 0; i < 3; i++)
			{
				accNormalized[i] = Accelerometer[i] / accNorm;
				magNormalized[i] = Magnetometer[i] / magNorm;
			}
			double [] a1={0,magNormalized[0],magNormalized[1],magNormalized[2]};
			Quaternion atemp=new Quaternion(a1);
			Quaternion h=q.quaternProd(atemp.quaternProd(q.quaternConj()));
			double h12Norm=Math.sqrt(h.getData()[1] * h.getData()[1] + h.getData()[2] * h.getData()[2]);
			double [] a2={0,h12Norm,0,h.getData()[3]};
			Quaternion b=new Quaternion(a2);
			Matrix F=new Matrix(6,1, new double[] {
					2*(q.getData()[1]*q.getData()[3] - q.getData()[0]*q.getData()[2]) - accNormalized[0],
					2*(q.getData()[0]*q.getData()[1] + q.getData()[2]*q.getData()[3]) - accNormalized[1],
					2*(0.5 - q.getData()[1]*q.getData()[1] - q.getData()[2]*q.getData()[2]) - accNormalized[2],
					2*b.getData()[1]*(0.5 - q.getData()[2]*q.getData()[2] - q.getData()[3]*q.getData()[3]) + 2*b.getData()[3]*(q.getData()[1]*q.getData()[3] - q.getData()[0]*q.getData()[2]) - magNormalized[0],
					2*b.getData()[1]*(q.getData()[1]*q.getData()[2] - q.getData()[0]*q.getData()[3]) + 2*b.getData()[3]*(q.getData()[1]*q.getData()[0] + q.getData()[3]*q.getData()[2]) - magNormalized[1],
					2*b.getData()[1]*(q.getData()[2]*q.getData()[0] + q.getData()[1]*q.getData()[3]) + 2*b.getData()[3]*(0.5 - q.getData()[1]*q.getData()[1] - q.getData()[2]*q.getData()[2]) - magNormalized[2]});
			Matrix J= new Matrix(6,4, new double[] {
					 -2*q.getData()[2], 2*q.getData()[3], -2*q.getData()[0], 2*q.getData()[1],
					 2*q.getData()[1], 2*q.getData()[0], 2*q.getData()[3], 2*q.getData()[2],
					 0, -4*q.getData()[1], -4*q.getData()[2], 0,
					 -2*b.getData()[3]*q.getData()[2], 2*b.getData()[3]*q.getData()[3], -4*b.getData()[1]*q.getData()[2]-2*b.getData()[3]*q.getData()[0], -4*b.getData()[1]*q.getData()[3]+2*b.getData()[3]*q.getData()[1],
					 -2*b.getData()[1]*q.getData()[3]+2*b.getData()[3]*q.getData()[1], 2*b.getData()[1]*q.getData()[2]+2*b.getData()[3]*q.getData()[0], 2*b.getData()[1]*q.getData()[1]+2*b.getData()[3]*q.getData()[3], -2*b.getData()[1]*q.getData()[0]+2*b.getData()[3]*q.getData()[2],
					 2*b.getData()[1]*q.getData()[2], 2*b.getData()[1]*q.getData()[3]-4*b.getData()[3]*q.getData()[1], 2*b.getData()[1]*q.getData()[0]-4*b.getData()[3]*q.getData()[2], 2*b.getData()[1]*q.getData()[1]});
			Matrix step=J.transpose().times(F);
			double stepNorm=Math.sqrt(step.getData()[0] * step.getData()[0] + step.getData()[1] * step.getData()[1] + step.getData()[2] * step.getData()[2] + step.getData()[3] * step.getData()[3]);
			double [] stepNormalizedArray=new double[4];
			for (int i = 0; i < 4; i++)
			{
				stepNormalizedArray[i] = step.getData()[i] / stepNorm;
			}
			Matrix stepNormalized=new Matrix(4,1,stepNormalizedArray);
			double [] a3={0,Gyroscope[0],Gyroscope[1],Gyroscope[2]};
			Quaternion btemp=new Quaternion(a3);
			Quaternion ctemp=q.quaternProd(btemp);
			double [] temp=new double[4];
			for(int i=0;i<4;i++)
			{
				// temp[i]=0.5*ctemp.getData()[i];
				 temp[i]=ctemp.getData()[i];
				 //modified by wl 7/16/2015   �����0.5
			}
			double [] qDot=new double[4];
			double [] q2=new double[4];
			for(int i=0;i<4;i++)
			{
				qDot[i]=0.5*temp[i]- this.beta*stepNormalizedArray[i];
				q2[i]=q.getData()[i]+qDot[i]*this.samplePeriod;
			}
			Quaternion q3=new Quaternion(q2);
			Quaternion q4=q3.normalize();
			//double qNorm=Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
			//double [] qNormalized=new double[4];
			//for (int i = 0; i < 4; i++)
			//{
				//qNormalized[i] = q[i] / qNorm;
			this.quaternion=q4;
			//}
		}
		
		
	}
	
	public void updateIMU(float[] Gyroscope,float[] Accelerometer)
	{
		Quaternion q=this.quaternion;
		double [] accNormalized=new double[3];
		double accNorm=Math.sqrt(Accelerometer[0] * Accelerometer[0] + Accelerometer[1] * Accelerometer[1] + Accelerometer[2] * Accelerometer[2]);
		if(accNorm!=0)
		{
			for (int i = 0; i < 3; i++)
			{
				accNormalized[i] = Accelerometer[i] / accNorm;
			}
			Matrix F=new Matrix(3,1, new double[] {
					2*(q.getData()[1]*q.getData()[3] - q.getData()[0]*q.getData()[2]) - accNormalized[0],
					2*(q.getData()[0]*q.getData()[1] + q.getData()[2]*q.getData()[3]) - accNormalized[1],
					2*(0.5 - q.getData()[1]*q.getData()[1] - q.getData()[2]*q.getData()[2]) - accNormalized[2]});
			Matrix J= new Matrix(3,4, new double[] {
					 -2*q.getData()[2], 2*q.getData()[3], -2*q.getData()[0], 2*q.getData()[1],
					 2*q.getData()[1], 2*q.getData()[0], 2*q.getData()[3], 2*q.getData()[2],
					 0, -4*q.getData()[1], -4*q.getData()[2], 0});
			Matrix step=J.transpose().times(F);
			double stepNorm=Math.sqrt(step.getData()[0] * step.getData()[0] + step.getData()[1] * step.getData()[1] + step.getData()[2] * step.getData()[2] + step.getData()[3] * step.getData()[3]);
			double [] stepNormalizedArray=new double[4];
			for (int i = 0; i < 4; i++)
			{
				stepNormalizedArray[i] = step.getData()[i] / stepNorm;
			}
			Matrix stepNormalized=new Matrix(4,1,stepNormalizedArray);
			double [] a3={0,Gyroscope[0],Gyroscope[1],Gyroscope[2]};
			Quaternion btemp=new Quaternion(a3);
			Quaternion ctemp=q.quaternProd(btemp);
			double [] temp=new double[4];
			for(int i=0;i<4;i++)
			{
				temp[i]=0.5*ctemp.getData()[i];
			}
			double [] qDot=new double[4];
			double [] q2=new double[4];
			for(int i=0;i<4;i++)
			{
				//qDot[i]=0.5*temp[i]- this.beta*stepNormalizedArray[i];
				qDot[i]=temp[i]- this.beta*stepNormalizedArray[i];
				//modified by WL  7/11/2015
				q2[i]=q.getData()[i]+qDot[i]*this.samplePeriod;
			}
			Quaternion q3=new Quaternion(q2);
			Quaternion q4=q3.normalize();
			//double qNorm=Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
			//double [] qNormalized=new double[4];
			//for (int i = 0; i < 4; i++)
			//{
				//qNormalized[i] = q[i] / qNorm;
			this.quaternion=q4;
			//}
		}
		
	}
}
