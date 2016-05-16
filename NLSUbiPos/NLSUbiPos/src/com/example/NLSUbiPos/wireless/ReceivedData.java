package com.example.NLSUbiPos.wireless;

import java.util.ArrayList;
import java.util.List;



//c definition
/*
typedef struct _ReceivedData
{ 
	u64 userID;	
	u64 timestamp; 	
	u64 macAddrArray[Ap_Num];
	double RSSiArray[Ap_Num];
} ReceivedData;
*/

public class ReceivedData {

	  public Long userID;
	  public long timestamp;
	  public List<Long> macAddrList;	
	  public List<Double> RSSiList;
	  
	  public ReceivedData()
	  {
		  userID = (long) 0;
		  timestamp = 0;
		  macAddrList = new ArrayList<Long>();
		  RSSiList = new ArrayList<Double>(); 
	  }
}
