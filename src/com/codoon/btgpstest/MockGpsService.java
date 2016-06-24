package com.codoon.btgpstest;

import java.util.ArrayList;
import java.util.List;
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
	private static final int UPDATE_TIME = 2000;
	private MyBinder myBinder = new MyBinder();
	private Handler handler = new Handler();
	private LocationManager mLocationManager;
	private static double LATITUDE = 0;
	private static double LONGITUDE = 0;
	private Random rad = new Random();;

	private static List<Double> latSendList = new ArrayList<Double>();
	private static List<Double> lonSendList = new ArrayList<Double>();
	private int count = 0;
	

	public static void setLatSendList(List<Double> latList) {
		latSendList = latList;
	}

	public static void setLonSendList(List<Double> lonList) {
		lonSendList = lonList;
	}

	public MockGpsService() {
		Log.i(TAG, "服务初始化");
//		init();
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		Log.i(TAG, "GpsService-->onBind()");
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
		handler.post(update_thread2);
	}

	public void pauseMockLocation() {
		Log.i(TAG, "pause mock");
		handler.removeCallbacks(update_thread2);
	}

	public void continueMockLocation() {
		Log.i(TAG, "continue mock");
		handler.post(update_thread2);
	}

	public void stopMockLocation() {
		Log.i(TAG, "stop mock");
		handler.removeCallbacks(update_thread2);
		mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
		mLocationManager.removeTestProvider(LocationManager.NETWORK_PROVIDER);
//		init();
	}

	Runnable update_thread2 = new Runnable() {
		public void run() {
			LATITUDE = latSendList.get(count);
			LONGITUDE = lonSendList.get(count);
			setMockLocation(LocationManager.GPS_PROVIDER);
			setMockLocation(LocationManager.NETWORK_PROVIDER);
			handler.postDelayed(update_thread2, UPDATE_TIME);
			count++;
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
		newLocation.setLatitude(LATITUDE);
		newLocation.setLongitude(LONGITUDE);
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
