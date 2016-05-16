package com.example.NLSUbiPos.wireless;

import java.util.ArrayList;
import java.util.List;



/*
 typedef struct _RssList
{
	MacRssPair *pair;
} RssList;
 */

public class RssMacList {
	
	public List<Long> macList;
	public List<Double> RssValueList;

	public RssMacList()
	{
		macList = new ArrayList<Long>();
		RssValueList = new ArrayList<Double>();
	}
}
