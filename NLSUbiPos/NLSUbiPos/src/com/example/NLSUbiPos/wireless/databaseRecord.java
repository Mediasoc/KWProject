package com.example.NLSUbiPos.wireless;

import java.util.ArrayList;
import java.util.List;

import com.example.NLSUbiPos.wireless.PositionInfo;;

public class databaseRecord{

	public PositionInfo aPositionInfo;
	public List<Long> macAddrList;
	public List<List<Double>> RssiListList;
	
	public databaseRecord()
	{
		aPositionInfo = new PositionInfo();
		macAddrList = new ArrayList<Long>();
		RssiListList = new ArrayList<List<Double>>();
	}
}