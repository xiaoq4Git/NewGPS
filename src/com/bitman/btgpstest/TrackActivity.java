package com.bitman.btgpstest;

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
	private Button gpsReplay,gpsRecord;
	private EditText editText2;
	private MockGpsService bindGpsService;
	private static final String FILENAME = "/Gpsdata/record/";
	public static final String TAG = TrackActivity.class.getName();
	
	private List<Double> latList = new ArrayList<Double>();
	private List<Double> lonList = new ArrayList<Double>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);

		Intent intent = new Intent(TrackActivity.this, MockGpsService.class);
		 Log.i(TAG, "bindService()");
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		initView();
	}

	public void initView() {
		editText2 = (EditText) this.findViewById(R.id.et2);
		gpsReplay = (Button) this.findViewById(R.id.replay);
		gpsRecord = (Button) this.findViewById(R.id.record);
		
		gpsRecord.setOnClickListener(new MyGetOnClickListener());
		gpsReplay.setOnClickListener(new MySetOnClickListener());
	}

	public void onChangeSpeedActivityListener(View view) {
		Intent intent = new Intent(this, SpeedActivity.class);
		startActivity(intent);
		this.finish();
	}

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			 Log.i(TAG, "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MockGpsService.MyBinder binder = (MockGpsService.MyBinder) service;
			bindGpsService = binder.getService1();
		}
	};
	
	private class MyGetOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Log.i(TAG, "start record service");
            Intent intent = new Intent(TrackActivity.this,
                    RealGpsWriter.class);
            startService(intent);
			Toast.makeText(TrackActivity.this, "GPS采点开始...", Toast.LENGTH_SHORT).show();
			Intent home = new Intent(Intent.ACTION_MAIN);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			home.addCategory(Intent.CATEGORY_HOME);
			startActivity(home);
		}
	}

	private class MySetOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			File file = new File(Environment.getExternalStorageDirectory(), FILENAME + editText2.getText().toString());
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					StringBuffer sb = new StringBuffer();
					Pattern p = Pattern.compile("lat=\"(.+)\".lon=\"(.+)\"");
					while ((line = br.readLine()) != null) {
						Matcher m = p.matcher(line);
						if (m.find()) {
							latList.add(Double.parseDouble(m.group(1)));
							lonList.add(Double.parseDouble(m.group(2)));
						}
					}
					bindGpsService.setLatSendList(latList);
					bindGpsService.setLonSendList(lonList);
					bindGpsService.startMockLocation();
					Toast.makeText(TrackActivity.this, "读取GPS记录成功，开始回放..", Toast.LENGTH_LONG).show();
					Intent home = new Intent(Intent.ACTION_MAIN);
					home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					home.addCategory(Intent.CATEGORY_HOME);
					startActivity(home);
				} catch (Exception e) {
					Toast.makeText(TrackActivity.this, "读取失败", Toast.LENGTH_SHORT).show();
				}
			} else {
				// 此时SDcard不存在或者不能进行读写操作的
				Toast.makeText(TrackActivity.this, "此时SDcard不存在或者不能进行读写操作", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
