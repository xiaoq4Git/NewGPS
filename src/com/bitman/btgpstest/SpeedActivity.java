package com.bitman.btgpstest;

import java.util.Random;
import java.util.Timer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class SpeedActivity extends Activity {
	public static final String TAG = SpeedActivity.class.getName();
	private MockLocationService bindService;

	private java.util.Timer timer;
	private Button bStart, bStop, bPause, bContinue;
	private RadioGroup group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(SpeedActivity.this, MockLocationService.class);
		Log.i(TAG, "bindService()");
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		initView();
	}

	public void initView() {
		bStart = (Button) this.findViewById(R.id.start);
		bPause = (Button) this.findViewById(R.id.pause);
		bContinue = (Button) this.findViewById(R.id.con);
		bStop = (Button) this.findViewById(R.id.stop);
		group = (RadioGroup) this.findViewById(R.id.speedGroup);
		group.setOnCheckedChangeListener(listener);
	}

	public void onChangeTrackActivityListener(View view) {
		Intent intent = new Intent(this, TrackActivity.class);

		startActivity(intent);
		this.finish();
	}

	public void OnStartClickListener(View view) {
		bStart.setVisibility(android.view.View.INVISIBLE);
		bPause.setVisibility(android.view.View.VISIBLE);
		bContinue.setVisibility(android.view.View.INVISIBLE);
		bStop.setVisibility(android.view.View.VISIBLE);
		Toast.makeText(this, "¿ªÊ¼", Toast.LENGTH_SHORT).show();		
		bindService.startMockLocation();
//		Intent home = new Intent(Intent.ACTION_MAIN);
//		home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		home.addCategory(Intent.CATEGORY_HOME);
//		startActivity(home);
	}

	public void onPauseClickListener(View view) {
		bStart.setVisibility(android.view.View.INVISIBLE);
		bPause.setVisibility(android.view.View.INVISIBLE);
		bContinue.setVisibility(android.view.View.VISIBLE);
		bStop.setVisibility(android.view.View.VISIBLE);
		Toast.makeText(this, "ÔÝÍ£", Toast.LENGTH_SHORT).show();
		bindService.stopMockLocation();
	}
	
	public void onContinueClickListener(View view){
		bStart.setVisibility(android.view.View.INVISIBLE);
		bPause.setVisibility(android.view.View.VISIBLE);
		bContinue.setVisibility(android.view.View.INVISIBLE);
		bStop.setVisibility(android.view.View.VISIBLE);
		Toast.makeText(this, "¼ÌÐø", Toast.LENGTH_SHORT).show();
		bindService.continueMockLocation();
	}

	public void onStopClickListener(View view) {
		bStart.setVisibility(android.view.View.VISIBLE);
		bPause.setVisibility(android.view.View.INVISIBLE);
		bContinue.setVisibility(android.view.View.INVISIBLE);
		bStop.setVisibility(android.view.View.INVISIBLE);
		Toast.makeText(this, "Í£Ö¹", Toast.LENGTH_SHORT).show();
		bindService.stopMockLocation();
	}

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected()");
			MockLocationService.MyBinder binder = (MockLocationService.MyBinder) service;
			bindService = binder.getService1();
		}
	};
	
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.fast:
				bindService.setPaceValue(0.000035);
				break;
			case R.id.normal:
				bindService.setPaceValue(0.00002);
				break;
			case R.id.slow:
				bindService.setPaceValue(0.000015);
				break;
			case R.id.other:
				timer = new Timer(true);
				timer.schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						Random rand = new Random();
						double unit = ((double) 4 - rand.nextFloat()*3) / 100000;
						bindService.setPaceValue(unit);
					}
				}, 0, 3 * 60 * 1000);
				break;
			default:
				break;
			}
		}
	};
}
