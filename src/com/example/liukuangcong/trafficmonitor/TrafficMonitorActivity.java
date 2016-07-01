package com.example.liukuangcong.trafficmonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.example.liukuangcong.trafficmonitor.R;
import com.example.liukuangcong.trafficmonitor.AppsListAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrafficMonitorActivity extends Activity {
	TextView latest_rx=null;
	TextView latest_tx=null;
	TextView previous_rx=null;
	TextView previous_tx=null;
	TextView delta_rx=null;
	TextView delta_tx=null;
	TrafficSnapshot latest=null;
	TrafficSnapshot previous=null;
	List<String> values;
	List<String> recieveList;
	List<String> sentList;
	ListView listview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		latest_rx=(TextView)findViewById(R.id.latest_rx);
		latest_tx=(TextView)findViewById(R.id.latest_tx);
		previous_rx=(TextView)findViewById(R.id.previous_rx);
		previous_tx=(TextView)findViewById(R.id.previous_tx);
		delta_rx=(TextView)findViewById(R.id.delta_rx);
		delta_tx=(TextView)findViewById(R.id.delta_tx);
		listview=(ListView)findViewById(R.id.list);


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

		getWorkingActivity();
	}



	public void getWorkingActivity(){
		HashSet<Integer> intersection=new HashSet<Integer>(latest.apps.keySet());
		values=new ArrayList<String>();
		recieveList=new ArrayList<String>();
		sentList=new ArrayList<String>();
		for(Integer uid:intersection){
			String tag=latest.apps.get(uid).tag;
			if(latest.apps.get(uid).rx!=0 || latest.apps.get(uid).tx!=0){
				String s=tag;
				values.add(s);
				recieveList.add(String.valueOf(latest.apps.get(uid).rx));
				sentList.add(String.valueOf(latest.apps.get(uid).tx));			

			}
		}

		listview.setAdapter(new AppsListAdapter(this, values));
		listview.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				AlertDialog.Builder alert = prompt(arg2);
			}
		});
		Log.d("get into working activity","size="+values.size());
	}


	public AlertDialog.Builder prompt(int position){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle( Html.fromHtml("<font color='#FFFFFF'>Check Usage</font>"));
		alert.setMessage("Recieve = "+recieveList.get(position)+" Sent = "+sentList.get(position));

		alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});


		AlertDialog dialog = alert.show();
		Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		return alert;
	}





}