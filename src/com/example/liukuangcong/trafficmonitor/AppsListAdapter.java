package com.example.liukuangcong.trafficmonitor;

import java.util.List;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AppsListAdapter extends ArrayAdapter<String> {
	// List context
	private final Context context;
	// List values
	private final List<String> values;

	public AppsListAdapter(Context context, List<String> values) {
		super(context, R.layout.main2, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.listcotent, parent, false);
		
		TextView appName = (TextView) rowView.findViewById(R.id.name);
		appName.setText(values.get(position));
		
		return rowView;
	}
	
	
}