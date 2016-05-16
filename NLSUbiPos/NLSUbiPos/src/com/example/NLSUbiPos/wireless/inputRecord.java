package com.example.NLSUbiPos.wireless;

import java.util.ArrayList;
import java.util.List;




//c definition
/*
typedef struct _inputRecord
{
	/*record the former input as matrix as follow
			|	time1 time2 ... time10
	--------+--------------------------
	mac1	|
	mac2	|
	...		|
	mac30	|


	double ** data;
	u64 *maclist;
	u64 UserId;
	u32 *timeStamp;
	int currentTimeSize;
} inputRecord;
*/

//****for easy convertion use      {data[time][macAddr]}
/*
	record the former input as matrix as follow
			|	mac1 mac2 ... mac30
	--------+--------------------------
	time1	|
	time2	|
	...		|
	time10	|

	/
*/

public class inputRecord {

	public Long UserId;
	public List<Long> macList;
	public List<inputRecordPerTime> inputRecordPerTimeList;
	
	public inputRecord()
	{
		UserId = (long) 0;
		macList = new ArrayList<Long>();
		inputRecordPerTimeList = new ArrayList<inputRecordPerTime>();
	}
}
