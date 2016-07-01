package com.example.liukuangcong.trafficmonitor;

import com.example.liukuangcong.trafficmonitor.TableData.TableInfo;

import android.content.Context;
import android.database.Cursor;
import android.net.TrafficStats;
import android.util.Log;

class TrafficRecord {
	long tx=0;
	long rx=0;
	double tx_mb;
	double rx_mb;
	String tag=null;
	long previous_tx;
	long previous_rx;
	long firstrx;
	long firsttx;
	long secondrx;
	long secondtx;

	TrafficRecord(Context context) {
		DatabaseOperations db=new DatabaseOperations(context);
		Cursor cr=db.dataBaseExists(db,context);
		previous_tx=TrafficStats.getTotalTxBytes();		
		previous_rx=TrafficStats.getTotalRxBytes();	

		if(cr!=null){
			cr.moveToFirst();
			firstrx=Long.parseLong(cr.getString(0));
			firsttx=Long.parseLong(cr.getString(1));
			cr.moveToNext();
			secondrx=Long.parseLong(cr.getString(0));
			secondtx=Long.parseLong(cr.getString(1));

			if(db.checkWhetherReboot(db)){
				rx=previous_rx+secondrx;
				tx=previous_tx+secondtx;
			}
			else{
				db.putInSecondRow(db,firstrx,firsttx);
				rx=firstrx+previous_rx;
				tx=firsttx+previous_tx;
			}

			db.putInfirstRow(db, rx, tx,false);

		}
		//the first time people install this app
		else{
			rx=previous_rx;
			tx=previous_tx;
			db.putInfirstRow(db, rx, tx,true);		
			db.putInSecondRow(db,0,0);
		}

		tx_mb=changeToMB(tx);
		rx_mb=changeToMB(rx);

		Log.d("All Information",db.getTableAsString(db.getReadableDatabase(), TableInfo.TABLE_NAME));
	}

	TrafficRecord(int uid, String tag) {
		tx=TrafficStats.getUidTxBytes(uid);
		rx=TrafficStats.getUidRxBytes(uid);
		this.tag=tag;
	}

	private double changeToMB(long num){
		return num/(1024.0*1024.0);
	}


}