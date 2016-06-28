package com.bitman.btgpstest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Iterator;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class RealGpsWriter extends Service {
	public static final String TAG = MockGpsService.class.getName();
	private LocationManager mLocationManager;
	private Calendar date = Calendar.getInstance();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
//		Location location = mLocationManager.getLastKnownLocation(bestProvider);
//		updateView(location);
		Log.i(TAG, "GPSwriter..  onCreate");
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// �ж�GPS�Ƿ���������
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			Toast.makeText(this, "�뿪��GPS����...", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "�뿪��GPS����...");
			// ���ؿ���GPS�������ý���
			return;
		}

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "GPSwriter..  onStart");
//		mLocationManager.addGpsStatusListener(listener);
//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
				String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
				// ��ȡλ����Ϣ
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER
				Location location = mLocationManager.getLastKnownLocation(bestProvider);
				updateView(location);
				// ����״̬
				mLocationManager.addGpsStatusListener(listener);
				// �󶨼�������4������
				// ����1���豸����GPS_PROVIDER��NETWORK_PROVIDER����
				// ����2��λ����Ϣ�������ڣ���λ����
				// ����3��λ�ñ仯��С���룺��λ�þ���仯������ֵʱ��������λ����Ϣ
				// ����4������
				// ��ע������2��3���������3��Ϊ0�����Բ���3Ϊ׼������3Ϊ0����ͨ��ʱ������ʱ���£�����Ϊ0������ʱˢ��

				// 1�����һ�Σ�����Сλ�Ʊ仯����1�׸���һ�Σ�
				// ע�⣺�˴�����׼ȷ�ȷǳ��ͣ��Ƽ���service��������һ��Thread����run��sleep(10000);Ȼ��ִ��handler.sendMessage(),����λ��
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.app_name),
				System.currentTimeMillis());

		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(this, TrackActivity.class), 0);
		notification.setLatestEventInfo(this, "GPS�ɵ�ģ��", "Running...", pendingintent);
		startForeground(0x111, notification);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
		mLocationManager.removeUpdates(locationListener);

//		Intent sevice = new Intent(this, RealGpsWriter.class);
//		this.startService(sevice);
	}

	private void updateView(Location location) {
		if (location != null) {

			File file = new File(Environment.getExternalStorageDirectory(),
					"/Gpsdata/mock/" + "(GPSinfo)" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)
							+ "-" + date.get(Calendar.DAY_OF_MONTH) + ".txt");

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

				try {
					FileOutputStream fos = new FileOutputStream(file, true);

					fos.write(("lon=\"" + location.getLongitude() + "\", lat=\"" + location.getLatitude()
							+ "\", sea level=\"" + location.getAltitude() + "\"\n").getBytes());
					fos.close();
				} catch (Exception e) {
					Log.i(TAG, "д���ļ�ʧ��");
				}
			} else {
				Log.i(TAG, "��ʱSDcard�����ڻ��߲��ܽ��ж�д����");
			}
		} else {
			;
			;
		}
	}

	private LocationListener locationListener = new LocationListener() {

		/**
		 * λ����Ϣ�仯ʱ����
		 */
		public void onLocationChanged(Location location) {
			updateView(location);
			Log.i(TAG, "ʱ�䣺" + location.getTime());
			Log.i(TAG, "���ȣ�" + location.getLongitude());
			Log.i(TAG, "γ�ȣ�" + location.getLatitude());
			Log.i(TAG, "���Σ�" + location.getAltitude());
		}

		/**
		 * GPS״̬�仯ʱ����
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS״̬Ϊ�ɼ�ʱ
			case LocationProvider.AVAILABLE:
				Log.i(TAG, "��ǰGPS״̬Ϊ�ɼ�״̬");
				break;
			// GPS״̬Ϊ��������ʱ
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG, "��ǰGPS״̬Ϊ��������״̬");
				break;
			// GPS״̬Ϊ��ͣ����ʱ
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "��ǰGPS״̬Ϊ��ͣ����״̬");
				break;
			}
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderEnabled(String provider) {
			Location location = mLocationManager.getLastKnownLocation(provider);
			Log.i(TAG, "GPS��ʼ����..");
			updateView(location);
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "GPS�ر�..");
			updateView(null);
		}

	};

	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// ��һ�ζ�λ
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "��һ�ζ�λ");
				break;
			// ����״̬�ı�
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i(TAG, "����״̬�ı�");
				// ��ȡ��ǰ״̬
				GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
				// ��ȡ���ǿ�����Ĭ�����ֵ
				int maxSatellites = gpsStatus.getMaxSatellites();
				// ����һ��������������������
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("��������" + count + "������");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "��λ����");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "��λ����");
				break;
			}
		};
	};

	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// ���ö�λ��ȷ�� Criteria.ACCURACY_COARSE�Ƚϴ��ԣ�Criteria.ACCURACY_FINE��ȽϾ�ϸ
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// �����Ƿ�Ҫ���ٶ�
		criteria.setSpeedRequired(false);
		// �����Ƿ�������Ӫ���շ�
		criteria.setCostAllowed(false);
		// �����Ƿ���Ҫ��λ��Ϣ
		criteria.setBearingRequired(false);
		// �����Ƿ���Ҫ������Ϣ
		criteria.setAltitudeRequired(false);
		// ���öԵ�Դ������
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}
}
