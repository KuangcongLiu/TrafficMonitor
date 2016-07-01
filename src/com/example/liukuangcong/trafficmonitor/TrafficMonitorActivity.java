package com.example.liukuangcong.trafficmonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.example.liukuangcong.trafficmonitor.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TrafficMonitorActivity extends Activity {
	TextView latest_rx=null;
	TextView latest_tx=null;
	TextView previous_rx=null;
	TextView previous_tx=null;
	TextView delta_rx=null;
	TextView delta_tx=null;
	TrafficSnapshot latest=null;
	TrafficSnapshot previous=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		latest_rx=(TextView)findViewById(R.id.latest_rx);
		latest_tx=(TextView)findViewById(R.id.latest_tx);
		previous_rx=(TextView)findViewById(R.id.previous_rx);
		previous_tx=(TextView)findViewById(R.id.previous_tx);
		delta_rx=(TextView)findViewById(R.id.delta_rx);
		delta_tx=(TextView)findViewById(R.id.delta_tx);

		takeSnapshot(null);
	}

	public void takeSnapshot(View v) {
		previous=latest;
		latest=new TrafficSnapshot(this);

		latest_rx.setText(String.valueOf(latest.device.rx));
		//format("%.2f", latest.device.rx_mb)+" MB");
		latest_tx.setText(String.valueOf(latest.device.tx));
		//(String.format("%.2f", latest.device.tx_mb)+" MB");


		if (previous!=null) {
			previous_rx.setText(String.valueOf(previous.device.rx));
			//(String.format("%.2f", previous.device.rx_mb)+" MB");
			previous_tx.setText(String.valueOf(previous.device.tx));
			//(String.format("%.2f", previous.device.tx_mb)+" MB");

			delta_rx.setText(String.valueOf(latest.device.rx-previous.device.rx));
			//(String.format("%.2f", latest.device.rx_mb-previous.device.rx_mb)+" MB");
			delta_tx.setText(String.valueOf(latest.device.tx-previous.device.tx));
			//(String.format("%.2f", latest.device.tx_mb-previous.device.tx_mb)+" MB");
		}

		HashSet<Integer> intersection2=new HashSet<Integer>(latest.apps.keySet());
		for(Integer uid:intersection2){
			String tag=latest.apps.get(uid).tag;
			if(latest.apps.get(uid).rx!=0 && latest.apps.get(uid).tx!=0){
				//Log.d("All Information", tag+"="+String.valueOf(latest.apps.get(uid).rx)+" "+String.valueOf(latest.apps.get(uid).tx));
			}
		}
	}



}