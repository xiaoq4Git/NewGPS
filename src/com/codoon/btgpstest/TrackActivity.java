package com.codoon.btgpstest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TrackActivity extends Activity {
	private Button gpsRecord;
	private EditText editText2;
	private MockGpsService bindGpsService;
	private List<Double> latList = new ArrayList<Double>();
	private List<Double> lonList = new ArrayList<Double>();
	private static final String FILENAME = "/gpsdata/Sichuan.gpx";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);
		
		Intent intent = new Intent(TrackActivity.this, MockGpsService.class);
//		Log.i(TAG, "bindService()");
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		initView();
	}
	
	public void initView(){
		editText2 = (EditText)this.findViewById(R.id.et2);
		gpsRecord = (Button)this.findViewById(R.id.record);
		gpsRecord.setOnClickListener(new MySetOnClickListener());
	}
	
	public void onChangeSpeedActivityListener(View view){
		Intent intent = new Intent(this,SpeedActivity.class);
		startActivity(intent);
		this.finish();
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
//			Log.i(TAG, "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
//			Log.i(TAG, "onServiceConnected()");
			MockGpsService.MyBinder binder = (MockGpsService.MyBinder) service;
			bindGpsService = binder.getService1();
		}
	};
	
	private class MySetOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			File file = new File(Environment.getExternalStorageDirectory(), FILENAME);
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					StringBuffer sb = new StringBuffer();
					Pattern p = Pattern.compile("lat=\"(.+)\" lon=\"(.+)\"");
					while ((line = br.readLine()) != null) {
						Matcher m = p.matcher(line);
						if(m.find()){
							latList.add(Double.parseDouble(m.group(1)));
							lonList.add(Double.parseDouble(m.group(2)));
						}
					}
					System.out.println("-------------------->" + latList.size());
					System.out.println("-------------------->" + lonList.size());
					bindGpsService.setLatSendList(latList);
					bindGpsService.setLonSendList(lonList);
					bindGpsService.startMockLocation();
//					editText2.setText(sb.toString());
					Toast.makeText(TrackActivity.this, "��ȡGPS��ɹ�", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(TrackActivity.this, "��ȡʧ��", Toast.LENGTH_SHORT).show();
				}
			} else {
				// ��ʱSDcard�����ڻ��߲��ܽ��ж�д������
				Toast.makeText(TrackActivity.this, "��ʱSDcard�����ڻ��߲��ܽ��ж�д����", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
