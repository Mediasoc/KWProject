package com.example.NLSUbiPos.wireless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.NLSUbiPos.coordinate.Mercator;
import com.example.NLSUbiPos.floor.OnFloorListener;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

/**
 * The wireless locator using WiFi signals.
 */
public class WifiLocator extends WirelessLocator implements OnFloorListener {
	
	// manages the WiFi function
	private WifiManager wifimanager;
	//location
	public  Mercator CurrentLocation=new Mercator(0,0);
	
	private int currentfloor=0;
	
	private int receivedfloor=0;
	
	// the received WiFi list by scanning
	private List<ScanResult> scanresults;
	
	//the WiFi signal database
	public  List<databaseRecord> database=new ArrayList<databaseRecord>();
	
	public static List<databaseRecord> database1=new ArrayList<databaseRecord>();
	public static List<databaseRecord> database2=new ArrayList<databaseRecord>();
	public static List<databaseRecord> database3=new ArrayList<databaseRecord>();
	public static List<databaseRecord> database4=new ArrayList<databaseRecord>();
	
	private static List<Boolean>databasestatus=new ArrayList<Boolean>();
	static{
		databasestatus.add(false);
		databasestatus.add(false);
		databasestatus.add(false);
		databasestatus.add(false);
	}
	
	//record the input wifi signal
	public  List<inputRecord> record=new ArrayList<inputRecord>();
	
	static int CurrentUserIndex=0;
	
	RssMacList rssmaclist=new RssMacList();
	public List<PositionProb> PositionProbList;
	
	public List<PositionProb> PositionProbList_tmp;
	
	public List<PositionProb> PositionProbList_simple;
	
//	public  PositionInfo PositionInfoTmp = new PositionInfo();
	
	

	/**
	 * Constructs a WiFi locator.
	 * 
	 */
	public WifiLocator(Context context) {
		super(context);
		wifimanager=(WifiManager)context.getSystemService(context.WIFI_SERVICE);
		WiFiable=false;
		
		if (!wifimanager.isWifiEnabled()) {
			if (wifimanager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
				wifimanager.setWifiEnabled(true);
			}
		}
		//initialize the database
		database1=readDataBase("/sdcard/Fingerprint/Data/database_F1.txt");databasestatus.set(0, true);
		database2=readDataBase("/sdcard/Fingerprint/Data/database_F2.txt");databasestatus.set(1, true);
		database3=readDataBase("/sdcard/Fingerprint/Data/database_F3.txt");databasestatus.set(2, true);
		database4=readDataBase("/sdcard/Fingerprint/Data/database_F4.txt");databasestatus.set(3, true);
		//register wifi receiver
		context.registerReceiver(new BroadcastReceiver(){
			
			public void onReceive(Context context,Intent intent){
				System.out.println(888);
				if(receivedfloor!=0){
					if(databasestatus.get(receivedfloor-1)==true){
					scanresults=wifimanager.getScanResults();
					
					if( scanresults!=null){
					
					ReceivedData receiveddata=new ReceivedData();
					receiveddata.timestamp=System.currentTimeMillis();
					String Macaddr=wifimanager.getConnectionInfo().getMacAddress();
					receiveddata.userID=Long.parseLong(Macaddr.replace(":",""), 16);
					
					for(ScanResult scanresult:scanresults){
						receiveddata.macAddrList.add(Long.parseLong(scanresult.BSSID.replace(":", ""), 16));
						receiveddata.RSSiList.add((double)scanresult.level);
						
					}
					
					//positioning algorithm
					if(receiveddata!=null){
						
				    	  CurrentUserIndex=InternalRecordUpdate(receiveddata);
				    	  rssmaclist=AverageRssByTime(CurrentUserIndex,100);
				    	  rssmaclist=SortByMac(rssmaclist);
				    	  rssmaclist=removeExtraMac(receiveddata,rssmaclist);
				    	  
				    	  
				    	  PositionProbList=getPositionProbList(rssmaclist);
				    	  
				    	  sortPositionProbList();
				    	 PositionProbList_tmp=new ArrayList<PositionProb>();
				    	 PositionProbList_tmp=PositionCounting(3);
				    	 Log.d("WiFI", "PositionProbList_tmp: "+PositionProbList_tmp.get(0).prob);
//				    	  PositionInfoTmp = WKNN(3);
				    	  if(PositionProbList_tmp!=null){
				    	  // CurrentLocation.=PositionInfoTmp.x;
				    	  // CurrentLocation.y=PositionInfoTmp.y;
				    	   notifyWirelessPosition(PositionProbList_tmp,WiFiable);
				    	  //System.out.println(CurrentLocation.x);
				    	  }
				    	   
				    	  
				      }
					}
					
					}
					 
				}
			}
			
			
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		     
	}
	
	public void startLocating(long interval, int times) {
		this.times = times;
		// gets the timer object
		timer = new Timer();
		// the task to be executed
		timerTask = new TimerTask() {

			@Override
			public void run() {
				// starts the WiFi scanning
				wifimanager.startScan();
				
				 
				
			}
		};
		
		// executes the task periodically
		timer.schedule(timerTask, 0, interval);
	}
	
	public void stopLocating() {
		if (timer != null) {
			// cancels the scanning task
			timer.cancel();
			timer = null;
		}
	}

	private List<databaseRecord> readDataBase(String pathname){
		List<databaseRecord> databasetmp=new ArrayList<databaseRecord>();
		File file=new File(pathname);
		if (!file.isAbsolute()) {
			file = new File(Environment.getExternalStorageDirectory(), pathname);
		}
		
		 if (!file.exists())
		    {
		    	Log.e("error", "dbFileNotFound");
		    	return null ;
		    }
		 
		 BufferedReader bufferedreader=null;
		 
		 try{
			 bufferedreader=new BufferedReader(new FileReader(file));
		 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
		    	Log.e("error", "dbFileNotFound");
		    	return null ;
			}
		 
		 String parameterString;
			databaseRecord DatabaseRecordTmp = null;
			PositionInfo PositionInfoTmp = null;
			List<Double> DoubleListTmp = null;
			boolean isPositionFound = false;
			try {

				while ((parameterString = bufferedreader.readLine()) != null) {
					String[] StringArray = parameterString.split(",");
					
					DatabaseRecordTmp = new databaseRecord();
					PositionInfoTmp = new PositionInfo();
					
					PositionInfoTmp.x = Double.parseDouble(StringArray[1]);
					PositionInfoTmp.y = Double.parseDouble(StringArray[2]);
					PositionInfoTmp.z = Double.parseDouble(StringArray[3]);
					PositionInfoTmp.o = Double.parseDouble(StringArray[4]);
					DatabaseRecordTmp.aPositionInfo = PositionInfoTmp;

					isPositionFound = false;
					int PositionIndex;
					for( PositionIndex = 0 ; PositionIndex< databasetmp.size();PositionIndex++){
					
						if(databasetmp.get(PositionIndex).aPositionInfo.o == PositionInfoTmp.o && databasetmp.get(PositionIndex).aPositionInfo.x == PositionInfoTmp.x && databasetmp.get(PositionIndex).aPositionInfo.y == PositionInfoTmp.y && databasetmp.get(PositionIndex).aPositionInfo.z == PositionInfoTmp.z )
						{	
							//already have this point
							isPositionFound = true;
							break;
						}
					}
					
					if(!isPositionFound)
					{
						databasetmp.add(DatabaseRecordTmp);
					}
					
					databasetmp.get(PositionIndex).macAddrList.add(Long.parseLong(StringArray[5]));
					
					
					DoubleListTmp = new ArrayList<Double>();
					for(int i = 6;i<StringArray.length;i++)
					{
						if(i%2 != 0){
						
						DoubleListTmp.add(Double.parseDouble(StringArray[i]));
						}
					}
					
				
					
					databasetmp.get(PositionIndex).RssiListList.add(DoubleListTmp);
					
				}
				
				bufferedreader.close();
				//return databasetmp;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return databasetmp;	
	}
	
	
	// return current user index
		int InternalRecordUpdate(ReceivedData rvPtr) {

			int UserIndex = 0;
			boolean check = false;
			
			//check the user exist or not
			for (UserIndex = 0; UserIndex < record.size(); UserIndex++) {
				if (rvPtr.userID.equals(record.get(UserIndex).UserId)) {
					check = true;
					break;
				}
			}
			
			if (!check) {
				
				//have not this user
				
				inputRecord inputRecordTmp = new inputRecord();
				inputRecordTmp.UserId = rvPtr.userID;
				
				if(rvPtr.macAddrList.size() != 0){
					inputRecordTmp.macList.addAll(rvPtr.macAddrList);
				}
				
				inputRecordPerTime inputRecordPerTimeTmp = new inputRecordPerTime();
				inputRecordPerTimeTmp.timeStamp = rvPtr.timestamp;
				
				
				if(rvPtr.RSSiList.size() != 0){
					inputRecordPerTimeTmp.RssValueList.addAll(rvPtr.RSSiList);
				}
				
				inputRecordTmp.inputRecordPerTimeList.add(inputRecordPerTimeTmp);
				
				record.add(inputRecordTmp);
				
			} else {
				
				//already have this user
				
				if(record.get(UserIndex).inputRecordPerTimeList.size() >= 10)
				{
					//remove first record at index = 0
					record.get(UserIndex).inputRecordPerTimeList.remove(0);
					
				}else
				{
					// just add
				}
				
				inputRecordPerTime inputRecordPerTimeTmp = new inputRecordPerTime();
				inputRecordPerTimeTmp.timeStamp = rvPtr.timestamp;
				
				List<Double> RSSiListTmp = new ArrayList<Double>();
				for(int i = 0;i < record.get(UserIndex).macList.size();i++)
				{
					//init result with -120
					RSSiListTmp.add((double) -120);
				}
		
				if(RSSiListTmp.size() != 0){
				inputRecordPerTimeTmp.RssValueList.addAll(RSSiListTmp);
				}
				
				int rvMacAddrIndex;
				int recordMacAddrIndex;
				for(rvMacAddrIndex = 0;rvMacAddrIndex < rvPtr.macAddrList.size();rvMacAddrIndex++)
				{
					recordMacAddrIndex = record.get(UserIndex).macList.indexOf(rvPtr.macAddrList.get(rvMacAddrIndex));
					if(recordMacAddrIndex == -1)
					{
						// add new macAddr
						record.get(UserIndex).macList.add(rvPtr.macAddrList.get(rvMacAddrIndex));
						// add new to inputRecordPerTime
						inputRecordPerTimeTmp.RssValueList.add((double)-120);
						// add result to former data with -120
						for(int i = 0;i < record.get(UserIndex).inputRecordPerTimeList.size();i++)
						{
							record.get(UserIndex).inputRecordPerTimeList.get(i).RssValueList.add((double) -120);
						}
					}
					//try again
					recordMacAddrIndex = record.get(UserIndex).macList.indexOf(rvPtr.macAddrList.get(rvMacAddrIndex));
					inputRecordPerTimeTmp.RssValueList.set(recordMacAddrIndex, rvPtr.RSSiList.get(rvMacAddrIndex));
				}			
				record.get(UserIndex).inputRecordPerTimeList.add(inputRecordPerTimeTmp);
			}
			// end function
			return UserIndex;
		}
		
		
		// sort RssMacList by descend
		private RssMacList SortByMac(RssMacList aRssMacList) {

			for (int SortIndex = 0; SortIndex < aRssMacList.macList.size(); SortIndex++) {

				for (int i = SortIndex + 1; i < aRssMacList.macList.size(); i++) {
					if (aRssMacList.RssValueList.get(SortIndex) < aRssMacList.RssValueList
							.get(i)) {
						Long macTmp = aRssMacList.macList.get(SortIndex);
						Double rssiTmp = aRssMacList.RssValueList.get(SortIndex);

						aRssMacList.macList.set(SortIndex,
								aRssMacList.macList.get(i));
						aRssMacList.RssValueList.set(SortIndex,
								aRssMacList.RssValueList.get(i));

						aRssMacList.macList.set(i, macTmp);
						aRssMacList.RssValueList.set(i, rssiTmp);
					}
				}
			}
			return aRssMacList;
		}
		
		private RssMacList removeExtraMac(ReceivedData rvPtr, RssMacList aRssMacList) {
			
			RssMacList RssMacListTmp = new RssMacList();

			for(int i = 0;i < aRssMacList.macList.size();i++)
			{
				if(rvPtr.macAddrList.contains(aRssMacList.macList.get(i)))
				{
					RssMacListTmp.macList.add(aRssMacList.macList.get(i));
					RssMacListTmp.RssValueList.add(aRssMacList.RssValueList.get(i));
				}
			}
			
			return RssMacListTmp;
		}
		
		// return a RssList
		RssMacList AverageRssByTime(int UserIndex, int averageRssWeight)
		{
			int AverageRssWeight = averageRssWeight;
			
			RssMacList outputPtr = new RssMacList();

			if(record.get(UserIndex).macList.size() != 0){
			outputPtr.macList.addAll(record.get(UserIndex).macList);
			}
			
			List<Double> RssListTmp = new ArrayList<Double>();

			//init counter = 0 : save the count of each macAddr
			List<Integer> CounterList = new ArrayList<Integer>();
			for(int i = 0; i < record.get(UserIndex).macList.size();i++)
			{
				CounterList.add(0);
			}
			
			int TimeIndex = 0;
			
			for (TimeIndex = 0; TimeIndex < record.get(UserIndex).inputRecordPerTimeList
					.size(); TimeIndex++) {

				if(TimeIndex == 0)
				{
					if(record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList.size() != 0){
					RssListTmp.addAll(record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList);
					}
					
					for(int i = 0; i < record.get(UserIndex).macList.size();i++)
					{

						if(!record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList.get(i).equals((double)-120))
						{
							CounterList.set(i, 1);
						}
						else
						{
							RssListTmp.set(i, (double) 0);
							//ignore -120
						}
						//RssListTmp.add(record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList.get(i));
					}
					
				}else
				{
					if(TimeIndex != record.get(UserIndex).inputRecordPerTimeList.size() - 1){
						for(int macIndex = 0; macIndex < RssListTmp.size(); macIndex++ )
						{
							if(!record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList.get(macIndex).equals((double)-120))
							{
								RssListTmp.set(macIndex, RssListTmp.get(macIndex) + record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList.get(macIndex));
								CounterList.set(macIndex, CounterList.get(macIndex) + 1);
							}else
							{
								// = -120 ignore
							}
						}
					}else
					{
						// add weight to last rv
						for(int macIndex = 0; macIndex < RssListTmp.size(); macIndex++ )
						{
							if(!record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList.get(macIndex).equals((double)-120))
							{
								RssListTmp.set(macIndex, RssListTmp.get(macIndex) + averageRssWeight*record.get(UserIndex).inputRecordPerTimeList.get(TimeIndex).RssValueList.get(macIndex));
								CounterList.set(macIndex, CounterList.get(macIndex) + averageRssWeight);
							}else
							{
								// = -120 ignore
							}
						}
					}
				}
			}
			
			for(int macIndex = 0; macIndex < RssListTmp.size(); macIndex++ )
			{
				if(CounterList.get(macIndex) != 0)
				{	
					RssListTmp.set(macIndex, RssListTmp.get(macIndex)/CounterList.get(macIndex));
				}
				else
				{
					// should not have this situation
					RssListTmp.set(macIndex, (double) -120);
				}
			}
			
			if(RssListTmp.size() != 0){
			outputPtr.RssValueList.addAll(RssListTmp);
			}
			
			return outputPtr;
		}
		
		List<PositionProb> getPositionProbList(RssMacList aRssMacList)
		{
			List<PositionProb> PositionProbList = new ArrayList<PositionProb>();
			PositionProb PositionProbTmp;
			
			int PositionIndex;
			// for debug algorithem
			List<Double> probList;
			for(PositionIndex = 0; PositionIndex < database.size();PositionIndex++)
			{
				PositionProbTmp = new PositionProb();
				PositionProbTmp.aPositionInfo = database.get(PositionIndex).aPositionInfo;
				
				//probList = new ArrayList<Double>();
				
				int RvMacAddrIndex;
				for(RvMacAddrIndex = 0;RvMacAddrIndex < aRssMacList.macList.size();RvMacAddrIndex++)
				{
					int DbMacAddrIndex = database.get(PositionIndex).macAddrList.indexOf(aRssMacList.macList.get(RvMacAddrIndex));
					
					if(DbMacAddrIndex != -1)
					{

						double originValue = aRssMacList.RssValueList.get(RvMacAddrIndex);
						
						if(originValue > -30)
						{
							originValue = -30;
						}else if(originValue <= -90)
						{
							originValue = -89;
						}
						
						int lowerValue =  (int) originValue - 1;
						int higherValue = (int) originValue;
						int indexOfLowerValue = lowerValue +90;
						int indexOfhigherValue = higherValue+90;
						
						double probTmp;
						
						probTmp =
								(higherValue - originValue)*database.get(PositionIndex).RssiListList.get(DbMacAddrIndex).get(indexOfLowerValue)
							+	(originValue - lowerValue)*database.get(PositionIndex).RssiListList.get(DbMacAddrIndex).get(indexOfhigherValue);

						//probList.add(probTmp);
						
						PositionProbTmp.prob =  probTmp*PositionProbTmp.prob;
						
					}else
					{
						//do not contains mac, so prob set to 0;
						//PositionProbTmp.prob = 0;
						//break;
					}
				}
				if(PositionProbTmp.prob!=1){
					
				PositionProbList.add(PositionProbTmp);
				}else
				{
					PositionProbTmp.prob=0;
					PositionProbList.add(PositionProbTmp);
				}
			}
			return PositionProbList;
		}
		
		void sortPositionProbList() {
			
			PositionProb PositionProbTmp = null;
			
			for (int SortIndex = 0; SortIndex < PositionProbList.size(); SortIndex++) {

				for (int i = SortIndex + 1; i < PositionProbList.size(); i++) {
					if (PositionProbList.get(SortIndex).prob < PositionProbList.get(i).prob) {
						
						PositionProbTmp = PositionProbList.get(SortIndex);
						
						PositionProbList.set(SortIndex, PositionProbList.get(i));
						PositionProbList.set(i, PositionProbTmp);
					}
				}
			}
			
}
		
		PositionInfo WKNN(int KnnNumber)
		{
			int knnNumber = KnnNumber;
			
			if(PositionProbList.size() == 0)
			{
				// error return PositionProbList==null
				return null;
				
			}
			
			int PositionProbListIndex ;
			PositionInfo PositionInfoTmp = new PositionInfo();
			double probCount = 0; 
			for(PositionProbListIndex = 0 ; PositionProbListIndex < PositionProbList.size() && PositionProbListIndex < knnNumber;PositionProbListIndex++)
			{
				probCount += PositionProbList.get(PositionProbListIndex).prob;
				PositionInfoTmp.x += (PositionProbList.get(PositionProbListIndex).aPositionInfo.x)* PositionProbList.get(PositionProbListIndex).prob;
				PositionInfoTmp.y += (PositionProbList.get(PositionProbListIndex).aPositionInfo.y)* PositionProbList.get(PositionProbListIndex).prob;
				PositionInfoTmp.z += (PositionProbList.get(PositionProbListIndex).aPositionInfo.z)* PositionProbList.get(PositionProbListIndex).prob;
				PositionInfoTmp.o += (PositionProbList.get(PositionProbListIndex).aPositionInfo.o)* PositionProbList.get(PositionProbListIndex).prob;
			}
			
			if(probCount != 0)
			{
				PositionInfoTmp.x = PositionInfoTmp.x/probCount;
				PositionInfoTmp.y = PositionInfoTmp.y/probCount;
				PositionInfoTmp.z = PositionInfoTmp.z/probCount;
				PositionInfoTmp.o = PositionInfoTmp.o/probCount;
			}else
			{
				// error return probCount==0
				return null;
			}
			
			return PositionInfoTmp;
		}
		
		List<PositionProb> PositionCounting(int Number){
			int num=Number;
			PositionInfo PositionInfoTmp = new PositionInfo();
			PositionProb PositionProbTmp=new PositionProb();
			WiFiable=false;
			
			if(PositionProbList.size() == 0)
			{
				// error return PositionProbList==null
				return null;
				
			}
			//List<PositionProb> PositionProbList=new ArrayList<PositionProb>();
			PositionProbList_simple=new ArrayList<PositionProb>();
			
			
			double probCount = 0;
			int PositionProbListIndex ;
			for(PositionProbListIndex = 0 ; PositionProbListIndex < PositionProbList.size()&&PositionProbListIndex < num;PositionProbListIndex++)
			{
				probCount += PositionProbList.get(PositionProbListIndex).prob;
				
			}
			
			if(probCount!=0){
				//normalization
			/*	for(int i=0;i<PositionProbList.size();i++){
					PositionProbList.get(i).prob=PositionProbList.get(i).prob/probCount;
				}
				*/
				for (int j=0;j<num;j++){
					PositionInfoTmp = new PositionInfo();
					PositionProbTmp = new PositionProb();
					PositionInfoTmp.x = (PositionProbList.get(j).aPositionInfo.x);
					PositionInfoTmp.y = (PositionProbList.get(j).aPositionInfo.y);
					PositionInfoTmp.z = (PositionProbList.get(j).aPositionInfo.z);
					PositionInfoTmp.o = (PositionProbList.get(j).aPositionInfo.o);
					
					PositionProbTmp.aPositionInfo=PositionInfoTmp;
					PositionProbTmp.prob=PositionProbList.get(j).prob/probCount;
					Log.d("WiFi", j+"th PositionProbTmp.prob: "+PositionProbTmp.prob);
					PositionProbList_simple.add(PositionProbTmp);
					
				}
			}else{
				return null;
			}
			
			if(currentfloor==3 && PositionProbList_simple.get(0).prob>0.5){
				WiFiable=true;
			}
			
			if(currentfloor==1||currentfloor==2||currentfloor==4){
				WiFiable=true;
			}
			Log.d("WiFi", "PositionProbList_simple: "+PositionProbList_simple.get(0).prob);
			return PositionProbList_simple;
		}

		@Override
		public void onFloor(int floor) {
			receivedfloor=floor;
			
			if(receivedfloor!=0&&currentfloor!=receivedfloor){
				switch(receivedfloor){
				case 1:
					database=database1;
					break;
				case 2:
					database=database2;
					break;
				case 3:
					database=database3;
					break;
				case 4:
					database=database4;
					break;
				}
			}
			currentfloor=receivedfloor;
		}
}
