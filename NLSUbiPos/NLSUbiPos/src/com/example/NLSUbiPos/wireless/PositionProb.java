package com.example.NLSUbiPos.wireless;



/*
 typedef struct _PositionProb
{
	double x;
	double y;
	double z;
	double o;        //****** different represents different points or same point?
	double *prob;
	struct _PositionProb *next; //set as null if used as array
	int size;
} PositionProb;
*/
public class PositionProb {
	public PositionInfo aPositionInfo;
	public double prob;
	
	public PositionProb()
	{
		aPositionInfo = new PositionInfo();
		// set init prob to 1
		prob = 1;
	}
}
