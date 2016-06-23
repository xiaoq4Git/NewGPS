package com.codoon.btgpstest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TrackActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);
	}
	
	public void onChangeSpeedActivityListener(View view){
		Intent intent = new Intent(this,SpeedActivity.class);
		startActivity(intent);
		this.finish();
	}
}
