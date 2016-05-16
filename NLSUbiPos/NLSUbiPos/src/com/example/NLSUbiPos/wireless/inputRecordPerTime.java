package com.example.NLSUbiPos.wireless;

import java.util.ArrayList;
import java.util.List;

public class inputRecordPerTime {
	public long timeStamp;
	public List<Double> RssValueList;
	public inputRecordPerTime()
	{
		timeStamp = 0;
		RssValueList = new ArrayList<Double>();
	}
}
