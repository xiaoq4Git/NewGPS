package com.codoon.btgpstest;

import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class MockGpsService extends Service {
	public static final String TAG = MockGpsService.class.getName();
	private static final int UPDATE_TIME = 1000;
	private MyBinder myBinder = new MyBinder();
	private Handler handler = new Handler();
	private LocationManager mLocationManager;
	private static double unit = 0;
	private static double paceValue = 0.000035;
	private static double latitude = 0;
	private static double longitude = 0;
	private Random rad = new Random();;

	public MockGpsService() {
		Log.i(TAG, "服务初始化");
		init();
	}

	public void init() {
		latitude = 30.5525326188;
		longitude = 104.0329972433;
	}
	
	public static void setPaceValue(double speed) {
		paceValue = speed;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		Log.i(TAG, "BindService-->onBind()");
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, true, true, true, 0,
				/* magic */5);
		mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
		return myBinder;
	}

	public class MyBinder extends Binder {
		public MockGpsService getService1() {
			return MockGpsService.this;
		}
	}

	public void startMockLocation() {
		Log.i(TAG, "start mock");
		handler.post(update_thread);
	}

	public void pauseMockLocation() {
		Log.i(TAG, "pause mock");
		handler.removeCallbacks(update_thread);
	}

	public void continueMockLocation() {
		Log.i(TAG, "continue mock");
		handler.post(update_thread);
	}

	public void stopMockLocation() {
		Log.i(TAG, "stop mock");
		handler.removeCallbacks(update_thread);
		mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
		mLocationManager.removeTestProvider(LocationManager.NETWORK_PROVIDER);
		init();
	}

	Runnable update_thread = new Runnable() {
		public void run() {
//			unit = unit + 0.000035;
			unit += paceValue;
			if (unit > 1)
				unit = 0;
			setMockLocation(LocationManager.GPS_PROVIDER);
			setMockLocation(LocationManager.NETWORK_PROVIDER);
			handler.postDelayed(update_thread, UPDATE_TIME);
		}
	};

	private void setMockLocation(String PROVIDER) {
		// mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
		mLocationManager.addTestProvider(
				// LocationManager.GPS_PROVIDER,
				PROVIDER, "requiresNetwork" == "", "requiresSatellite" == "", "requiresCell" == "",
				"hasMonetaryCost" == "", "supportsAltitude" == "", "supportsSpeed" == "", "supportsBearing" == "",

				android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);

		// Location newLocation = new Location(LocationManager.GPS_PROVIDER);
		Location newLocation = new Location(PROVIDER);
		newLocation.setLatitude(latitude + unit);
		newLocation.setLongitude(longitude + unit);
		newLocation.setAltitude(500 + rad.nextFloat() * 50);
		newLocation.setAccuracy(50.f);
		newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
		newLocation.setTime(System.currentTimeMillis());
		// mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER,
		// true);
		mLocationManager.setTestProviderEnabled(PROVIDER, true);
		// mLocationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
		mLocationManager.setTestProviderStatus(PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
		// mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER,
		// newLocation);
		mLocationManager.setTestProviderLocation(PROVIDER, newLocation);
		Log.i(TAG, "la:" + newLocation.getLatitude());
		Log.i(TAG, "lo:" + newLocation.getLongitude());
		Log.i(TAG, "sea level:" + newLocation.getAltitude());
	}
}
