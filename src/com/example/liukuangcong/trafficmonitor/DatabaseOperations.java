package com.example.liukuangcong.trafficmonitor;

import java.io.File;

import com.example.liukuangcong.trafficmonitor.TableData.TableInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

public class DatabaseOperations extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION=1;
	public int id=0;
	public String CREATE_QUERY="CREATE TABLE "+TableInfo.TABLE_NAME+" ( "+TableInfo.ID+" _ID TEXT PRIMARY KEY, "+TableInfo.PREVIOUS_RX_STRING+" TEXT, "+
			TableInfo.PREVIOUS_TX_STRING+" TEXT, "+TableInfo.BOOT_TIME+" TEXT );";
	public long bootTime;

	public DatabaseOperations(Context context) {
		super(context,TableInfo.DATABASE_NAME,null,DATABASE_VERSION);
		bootTime=System.currentTimeMillis()-SystemClock.elapsedRealtime();
	}

	@Override
	public void onCreate(SQLiteDatabase sdb) {
		sdb.execSQL(CREATE_QUERY);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void putInSecondRow(DatabaseOperations dop,long previous_rx,long previous_tx){
		SQLiteDatabase SQ=dop.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(TableInfo.PREVIOUS_RX_STRING,previous_rx);
		cv.put(TableInfo.PREVIOUS_TX_STRING,previous_tx);
		cv.put(TableInfo.ID,id+1);
		cv.put(TableInfo.BOOT_TIME, bootTime);
		boolean sign =checkRowTwo(SQ);
		if(!sign){		
			SQ.insert(TableInfo.TABLE_NAME, null, cv);
		}else{
			SQ.update(TableInfo.TABLE_NAME, cv,"ID="+id+1,null);
		}
		SQ.close();
	}

	public boolean checkRowTwo(SQLiteDatabase SQ){
		String Query = "Select * from " + TableInfo.TABLE_NAME + " where " + TableInfo.ID + " = " + id+1;
		Cursor cursor = SQ.rawQuery(Query, null);
		if(cursor.getCount() <= 0){
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	public void putInfirstRow(DatabaseOperations dop,long previous_rx,long previous_tx,boolean first_time ){
		SQLiteDatabase SQ=dop.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(TableInfo.PREVIOUS_RX_STRING,previous_rx);
		cv.put(TableInfo.PREVIOUS_TX_STRING,previous_tx);
		cv.put(TableInfo.ID,id);
		if(first_time){		
			SQ.insert(TableInfo.TABLE_NAME, null, cv);
		}
		else{
			SQ.update(TableInfo.TABLE_NAME, cv,"ID="+id,null);
		}
		SQ.close();
	}

	public boolean checkWhetherReboot(DatabaseOperations dop){
		Cursor cr=dop.getRebootTime(dop);
		cr.moveToFirst();
		cr.moveToNext();
		
		if(Math.abs(bootTime-Long.parseLong(cr.getString(0)))>2){ //reboot time not equal
			cr.close();
			return false;
		}else{
			cr.close();
			return true;
		}
	}

	public Cursor getRebootTime(DatabaseOperations dop){
		String[] columns={TableInfo.BOOT_TIME};		
		SQLiteDatabase SQ=dop.getReadableDatabase();
		Cursor CR=SQ.query(TableInfo.TABLE_NAME, columns, null,null,null,null,null);
		return CR;

	}

	public Cursor dataBaseExists(DatabaseOperations dop,Context context){
		String[] columns={TableInfo.PREVIOUS_RX_STRING,TableInfo.PREVIOUS_TX_STRING};		
		File dbFile = context.getDatabasePath(TableInfo.DATABASE_NAME);
		if(!dbFile.exists()){
			return null;
		}
		else{
			SQLiteDatabase SQ=dop.getReadableDatabase();
			Cursor CR=SQ.query(TableInfo.TABLE_NAME, columns, null,null,null,null,null);
			return CR;
		}

	}

	public String getTableAsString(SQLiteDatabase db, String tableName) {
		String tableString = String.format("Table %s:\n", tableName);
		Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
		if (allRows.moveToFirst() ){
			String[] columnNames = allRows.getColumnNames();
			do {
				for (String name: columnNames) {
					tableString += String.format("%s: %s\n", name,
							allRows.getString(allRows.getColumnIndex(name)));
				}
				tableString += "\n";

			} while (allRows.moveToNext());
		}
		allRows.close();
		db.close();
		return tableString;
	}

}
