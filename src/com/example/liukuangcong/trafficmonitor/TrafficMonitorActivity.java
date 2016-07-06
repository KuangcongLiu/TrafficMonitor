package com.example.liukuangcong.trafficmonitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.example.liukuangcong.trafficmonitor.R;
import com.example.liukuangcong.trafficmonitor.AppsListAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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

	EditText editText;
	TextView textView4;
	TextView textView3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		Intent trafficIntent = new Intent(TrafficMonitorActivity.this, TrafficService.class);
		startService(trafficIntent);

		
		/*editText.setOnKeyListener(new View.OnKeyListener() {


			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
					switch (arg1) {
					case KeyEvent.KEYCODE_ENTER:
						try {
							calcDataInSpecificTime();
						} catch (ParseException e) {
							e.printStackTrace();
						}
						//InputMethodManager mgr = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
						//mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});*/

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



	public void calcDataInSpecificTime(View v){
		editText=(EditText)findViewById(R.id.editText1);
		String ms=editText.getText().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm",Locale.ENGLISH);
		Date date = null;
		try {
			date = sdf.parse(ms);
		} catch (ParseException e) {
			Log.d("Error with date",ms);
			e.printStackTrace();
		}
		long enteredTime = date.getTime();

		DatabaseOperations db=new DatabaseOperations(this);
		Cursor cr=db.readDataBase(db,this);
		long listTime;

		if(cr.moveToFirst()){
			cr.moveToNext();cr.moveToNext();
			
			do{
				listTime=Long.parseLong(cr.getString(3));
				if(Math.abs(listTime-enteredTime)<10000){
					break;
				}
			}while(cr.moveToNext());
		}

		long rx=Long.parseLong(cr.getString(1));
		long tx=Long.parseLong(cr.getString(2));
		cr.moveToNext();
		long next_rx=Long.parseLong(cr.getString(1));
		long next_tx=Long.parseLong(cr.getString(2));

		long deltarx=next_rx-rx;
		long deltatx=next_tx-tx;

		textView3=(TextView)findViewById(R.id.textView3);
		textView4=(TextView)findViewById(R.id.textView4);
		
		textView3.setText(deltarx+"");
		textView4.setText(deltatx+"");

	}




}