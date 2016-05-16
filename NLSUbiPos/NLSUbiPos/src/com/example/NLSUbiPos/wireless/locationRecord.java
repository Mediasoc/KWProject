package com.example.NLSUbiPos.wireless;

import java.util.ArrayList;
import java.util.List;

public class locationRecord {

	public Long UserId;
	public List<PositionInfo> PositionInfoList;
	public List<Long> timeStampList;
	
	public locationRecord()
	{
		UserId = (long) 0;
		PositionInfoList = new ArrayList<PositionInfo>();
		timeStampList = new ArrayList<Long>();
	}
}
