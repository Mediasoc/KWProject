package com.example.NLSUbiPos.utils;

public class Quaternion 
{
	double[] data;
	public Quaternion() 
	{
		this.data =new double[4];
	}
	public Quaternion(double [] quaternion) 
	{
		this.data =quaternion;
	}
	public Quaternion quaternProd(Quaternion quaternion)
	{
		double [] ab=new double[4];
		ab[0]=this.data[0]*quaternion.getData()[0]-this.data[1]*quaternion.getData()[1]-this.data[2]*quaternion.getData()[2]-this.data[3]*quaternion.getData()[3];
		ab[1]=this.data[0]*quaternion.getData()[1]+this.data[1]*quaternion.getData()[0]+this.data[2]*quaternion.getData()[3]-this.data[3]*quaternion.getData()[2];
		ab[2]=this.data[0]*quaternion.getData()[2]-this.data[1]*quaternion.getData()[3]+this.data[2]*quaternion.getData()[0]+this.data[3]*quaternion.getData()[1];
		ab[3]=this.data[0]*quaternion.getData()[3]+this.data[1]*quaternion.getData()[2]-this.data[2]*quaternion.getData()[1]+this.data[3]*quaternion.getData()[0];
		Quaternion ab2=new Quaternion(ab);
		return ab2;
	}
	public Quaternion quaternConj()
	{
		double [] qConj=new double[4];
		qConj[0]=this.data[0];
		qConj[1]=-1*this.data[1];
		qConj[2]=-1*this.data[2];
		qConj[3]=-1*this.data[3];
		Quaternion qConj2=new Quaternion(qConj);
		return qConj2;
	}
	public double[] quaternion2euler()
	{
		double phi=Math.atan2(2*this.data[3]*this.data[2]-2*this.data[0]*this.data[1], 2*this.data[0]*this.data[0]+2*this.data[3]*this.data[3]-1);
		double theta=-Math.asin(2*this.data[1]*this.data[3]+2*this.data[0]*this.data[2]);
		double psi=Math.atan2(2*this.data[1]*this.data[2]-2*this.data[0]*this.data[3], 2*this.data[0]*this.data[0]+2*this.data[1]*this.data[1]-1);
		double [] euler={phi,theta,psi};
		//System.out.println(psi);
		return euler;
	}
	public double [] getData()
	{
		return this.data;
	}
	public void setData(double [] a)
	{
		for(int i=0;i<4;i++)
		{
			this.data[i]=a[i];
		}
	}
	public Quaternion normalize()
	{
		double norm=Math.sqrt(this.data[0] * this.data[0] + this.data[1] * this.data[1] + this.data[2] * this.data[2] + this.data[3] * this.data[3]);
		double [] data2=new double[4];
		for(int i=0;i<4;i++)
		{
			data2[i]=this.data[i]/norm;
		}
		Quaternion quaternion=new Quaternion(data2);
		return quaternion;
	}
	public void show()
	{
		System.out.println("data[0]----->"+data[0]);
		System.out.println("data[1]----->"+data[1]);
		System.out.println("data[2]----->"+data[2]);
		System.out.println("data[3]----->"+data[3]);
		System.out.println("----->");
	}
}
