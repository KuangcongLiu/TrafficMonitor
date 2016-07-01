package com.example.liukuangcong.trafficmonitor;

import android.provider.BaseColumns;

public class TableData {
	
	public TableData(){
		
	}
	
	public static abstract class TableInfo implements BaseColumns{
		public static final String ID="ID";
		public static final String BOOT_TIME="Boot_time";
		public static final String PREVIOUS_RX_STRING="Previous_rx";
		public static final String PREVIOUS_TX_STRING="Previous_tx";
		public static final String DATABASE_NAME="Data_usage";
		public static final String TABLE_NAME="Date_usage_table";
		
	}

}
