package com.example.liukuangcong.trafficmonitor;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.TrafficStats;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class TrafficService extends Service{

	public TrafficService() {
		super();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d("service started","here!");
		Toast.makeText(TrafficService.this, "Service Started", Toast.LENGTH_SHORT).show();




		AlarmManager alarmManager;
		BroadcastReceiver broadcastReceiver;
		PendingIntent pendingIntent;

		broadcastReceiver = new BroadcastReceiver() {


			@Override
			public void onReceive(Context arg0, Intent arg1) {
				WakeLocker.acquire(arg0);
				sendData(arg0);
				WakeLocker.release();

			}
		};

		Calendar cal = Calendar.getInstance();

		//starts at midnight
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		//repeats every 10 sec
		int interval = 1000 * 10 ;

		registerReceiver(broadcastReceiver, new IntentFilter(" com.example.liukuangcong.trafficmonitor"));
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(" com.example.liukuangcong.trafficmonitor"), 0);
		alarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() , interval, pendingIntent);

		//cal.getTimeInMillis()

		return super.onStartCommand(intent, flags, startId);
	}

	long tx;
	long rx;

	public void sendData(Context context) {
		DatabaseOperations db=new DatabaseOperations(context);

		tx=TrafficStats.getTotalTxBytes();		
		rx=TrafficStats.getTotalRxBytes();	

		db.putInFollowingRow(db, rx, tx);

	}

	public static class WakeLocker {
		private static PowerManager.WakeLock wakeLock;

		public static void acquire(Context ctx) {
			if (wakeLock != null) wakeLock.release();

			PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);

			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLocker_TAG");
			wakeLock.acquire();
		}

		public static void release() {
			if (wakeLock != null) wakeLock.release(); wakeLock = null;
		}
	}


	@Override
	public void onDestroy() {
		Toast.makeText(TrafficService.this, "Service Stopped", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub

		return null;
	}

}
