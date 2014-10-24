package com.china.infoway;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.china.infoway.utils.Constants;
import com.china.infoway.utils.DateUtils;
import com.china.infoway.utils.PhoneInfoHelper;

@SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
public class LocationService extends Service {

	private double latitude, longitude;
	private TimerTask timerTask;
	// ��¼�ɹ�ʱ������ֻ�����
	private String tmpph;
	// ��ȡ����ʱ��
	private String time;
	// ��λʱ����ַ
	private String positionUri = Constants.positionUri;
	private String setTimeUri = Constants.setTimeUri;
	private SharedPreferences spPreferences;
	private String strResult;
	private PhoneInfoHelper phoneInfoHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private String am_start = "5";
	private String am_end = "12";
	private String am_time = "5";
	private String pm_start = "13";
	private String pm_time = "5";
	private String pm_end = "24";
	private int flag; // 0��ʾ���磬1��ʾ����
	private boolean bool = true;// true��ʾ�������뱾��������ͬ ��false��ʾ�������뱾�����ݲ���ͬ
	private String pm_time_server;
	private String pm_end_server;
	private String pm_start_server;
	private String am_time_server;
	private String am_end_server;
	private String am_start_server;
	private double currentTime_public;
	private LocationClient locationClient;
	
	public static TextView mTv;
	private String mData;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void logMsg(String str) {
		try {
			mData = str;
			if (mTv != null) {
				mTv.setText(mData);
				Log.i("��ַ", mData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				getSetTime();
				break;
			case 2:
				if (locationClient != null && locationClient.isStarted()) {
					Log.i("LocSDK3", locationClient.isStarted() + "");
					locationClient.requestLocation();
				} else {
					Log.d("LocSDK3", "locClient is null or not started");
				}
				break;
			}
		}

	};

	@Override
	public void onCreate() {

		ExitClass.getInstance().addService(this);
		super.onCreate();

		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		tmpph = spPreferences.getString("phone", "");
		locationClient = ((BMapApiDemoApp) getApplication()).mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		// option.setScanSpan(5000);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		// option.disableCache(true);// ��ֹ���û��涨λ
		// option.setPoiNumber(5); // ��෵��POI����
		// option.setPoiDistance(1000); // poi��ѯ����
		// option.setPoiExtraInfo(true); // �Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
		locationClient.setLocOption(option);
		if(!locationClient.isStarted()) {
			locationClient.start();
		} else {
			locationClient.requestLocation();
		}
		// ע��λ�ü�����
		locationClient.registerLocationListener(new BDLocationListener() {

			public void onReceivePoi(BDLocation location) {

			}

			public void onReceiveLocation(BDLocation location) {
				if (location == null) {
					return;
				}
				
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				BMapApiDemoApp.latitude = latitude;
				BMapApiDemoApp.longitude = longitude;
				BMapApiDemoApp.currAddrStr = location.getAddrStr();
				
				logMsg(location.getAddrStr());
				
				
				Log.e("@@@@@@@@@@@@@@@@@", Calendar.getInstance().getTime()
						+ "����Ϊ��" + latitude + "γ��Ϊ:" + longitude + "addr:" + location.getAddrStr());
				// ��ȡ��ʱ��ʱ��(24Сʱ��)
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				time = sDateFormat.format(new Date());
				isPositionAvailable();
				Log.i("---------------------------", "----------------------------");
			}
		});

		getSqlData();

		getCurrentTime();

		if (flag == 0) {

			start_schedule_am();

		} else {

			start_schedule_pm();

		}
//		new Thread() {
//			public void run() {
//				while (true) {
//					locationClient.requestLocation();
//					try {
//						sleep(1000 * 60);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			};
//		}.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	// ��ȡ��ǰʱ��
	@SuppressWarnings("deprecation")
	private double getCurrentTime() {
		// long time = System.currentTimeMillis();
		Date date = new Date();
		int hour = date.getHours();
		int minute = date.getMinutes();
		int second = date.getSeconds();

		currentTime_public = hour + (double) minute / 60 + (double) second
				/ 3600;

		if (hour >= 0 && hour < 12) {
			flag = 0;

		} else if (hour >= 12 && hour <= 24) {
			flag = 1;

		}
		return currentTime_public;
	}

	// ִ�ж�ʱ����
	private void start_schedule_am() {
		final Timer timer = new Timer();
		timerTask = new TimerTask() {

			@Override
			public void run() {

				if (!bool) {
					if (timer != null) {
						Log.e("%%%%", bool + "");
						timerTask.cancel();
						timer.cancel();

						start_schedule_am();
						bool = true;
						return;

					}
				}

				if (getCurrentTime() >= Double.valueOf(am_end)) {

					timerTask.cancel();
					timer.cancel();
					stopSelf();

					return;
				}
				if (!AppUtils.checkNet(getApplicationContext())) {
					Log.d("data", "����δ����");
					return;
				}
				Log.d("data", "������ʼ");

				if (getCurrentTime() > Double.valueOf(am_start)) {
					mHandler.sendEmptyMessage(1);
				}
				mHandler.sendEmptyMessage(2);
			}
		};

		timer.schedule(timerTask, 0, Long.valueOf(am_time) * 60 * 1000);
	}

	// ִ�ж�ʱ����
	private void start_schedule_pm() {

		final Timer timer = new Timer();
		timerTask = new TimerTask() {

			@Override
			public void run() {

				if (!bool) {
					if (timer != null) {
						Log.e("%%%%", bool + "");
						timerTask.cancel();
						timer.cancel();
						start_schedule_pm();
						bool = true;
						return;

					}
				}

				if ((getCurrentTime() - 12) >= Double.valueOf(pm_end)) {

					timerTask.cancel();
					timer.cancel();
					stopSelf();

					return;
				}
				if (!AppUtils.checkNet(getApplicationContext())) {
					Log.d("data", "����δ����");
					return;
				}
				Log.d("data", "������ʼ");
				if ((getCurrentTime() - 12) > Double.valueOf(pm_start)) {
					mHandler.sendEmptyMessage(1);
				}
				mHandler.sendEmptyMessage(2);

			}
		};

		timer.schedule(timerTask, 0, Long.valueOf(pm_time) * 60 * 1000);

	}

	// �ϴ���γ����Ϣ
	private void isPositionAvailable() {
		
		new Thread() {
			@Override
			public void run() {
				
				String url;
				
				if(BMapApiDemoApp.currAddrStr == null || BMapApiDemoApp.currAddrStr.equals("")) {
					url = positionUri + tmpph + "&latitude=" + latitude
							+ "&longitude=" + longitude + "&a_duartime=" + time + "&address=" + URLEncoder.encode("λ�ò���");
				} else {
					url = positionUri + tmpph + "&latitude=" + latitude
							+ "&longitude=" + longitude + "&a_duartime=" + time + "&address=" + URLEncoder.encode(BMapApiDemoApp.currAddrStr);
				}
				
				String mUrl = url.replaceAll(" ", "%20");

				Log.v("mUrl", mUrl);

				HttpPost httpRequest = new HttpPost(mUrl);
				HttpResponse httpResponse = null;
				try {
					httpResponse = new DefaultHttpClient().execute(httpRequest);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				int res = httpResponse.getStatusLine().getStatusCode();
				if (res == 200) {
					Log.e("----------success-----", "success");
				}
			}
		}.start();
	}

	// ��ȡ���������õĶ�λʱ����

	private void getSetTime() {
		String setTimeUrl = setTimeUri + tmpph;
		String url = setTimeUrl.replaceAll(" ", "%20");

		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				Log.e("-----------strResult------", strResult);
				jsonParser(strResult);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void jsonParser(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			String status_server = jsonObject.getString("status");
			Log.e("************", status_server);

			if (status_server.equals("0")) {
				Log.e("Status", "Failure");
			} else if (status_server.equals("1")) { // �ɹ�
				Log.e("Status", "success");
				JSONObject jsonObject1 = jsonObject.getJSONObject("am");

				am_start_server = jsonObject1.getString("start");
				am_end_server = jsonObject1.getString("end");
				am_time_server = jsonObject1.getString("time");

				JSONObject jsonObject2 = jsonObject.getJSONObject("pm");

				pm_start_server = jsonObject2.getString("start");
				pm_end_server = jsonObject2.getString("end");
				pm_time_server = jsonObject2.getString("time");

				if (am_start_server.equals(am_start)
						&& am_end_server.equals(am_end)
						&& am_time_server.equals(am_time)
						&& pm_start_server.equals(pm_start)
						&& pm_end_server.equals(pm_end)
						&& pm_time_server.equals(pm_time)) {
					Log.e("&&&&&&&&&&&&&&&&&", "&&&&&&&&&&&&&&&&&&&&&&&&&&");
					bool = true;
				} else {
					Log.e("####################",
							"##############################");
					bool = false;
					// ��ֵ
					am_start = am_start_server;
					am_end = am_end_server;
					am_time = am_time_server;
					pm_start = pm_start_server;
					pm_end = pm_end_server;
					pm_time = pm_time_server;

					// ���浽�������ݿ�
					ContentValues contentValues = new ContentValues();
					contentValues.put(PhoneInfoHelper.PHONEINFO_COL_AM_START,
							am_start_server);
					contentValues.put(PhoneInfoHelper.PHONEINFO_COL_AM_END,
							am_end_server);
					contentValues.put(PhoneInfoHelper.PHONEINFO_COL_AM_TIME,
							am_time_server);
					contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PM_START,
							pm_start_server);
					contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PM_END,
							pm_end_server);
					contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PM_TIME,
							pm_time_server);
					contentValues.put(PhoneInfoHelper.CHREATE_TIME, DateUtils.Date2String(new Date()));

					phoneInfoHelper = new PhoneInfoHelper(LocationService.this,
							PhoneInfoHelper.PHONEINFO_DATA_NAME, null,
							PhoneInfoHelper.PHONEINFO_DATA_VERSION);
					db = phoneInfoHelper.getWritableDatabase();
					db.beginTransaction();
					try {
						db.update(PhoneInfoHelper.PHONEINFO_TABLE_NAME,
								contentValues, "phonenum=?", new String[] { tmpph });
						db.setTransactionSuccessful();
					} catch (Exception e) {
					}finally{
						db.endTransaction();
					}
					if (db != null) {
						db.close();
					}
				}
			} else {
				Log.e("Status", "ϵͳ����");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ��ȡ�������ݿ��ʱ��������
	private void getSqlData() {
		phoneInfoHelper = new PhoneInfoHelper(LocationService.this,
				PhoneInfoHelper.PHONEINFO_DATA_NAME, null,
				PhoneInfoHelper.PHONEINFO_DATA_VERSION);
		db = phoneInfoHelper.getWritableDatabase();
		cursor = db.query(PhoneInfoHelper.PHONEINFO_TABLE_NAME, null,
				"phonenum = ?", new String[] { tmpph }, null, null, null);
		cursor.getCount();

		if (cursor.moveToFirst()) {

			am_start = cursor.getString(cursor.getColumnIndex("am_start"));
			am_end = cursor.getString(cursor.getColumnIndex("am_end"));
			am_time = cursor.getString(cursor.getColumnIndex("am_time"));
			pm_start = cursor.getString(cursor.getColumnIndex("pm_start"));
			pm_end = cursor.getString(cursor.getColumnIndex("pm_end"));
			pm_time = cursor.getString(cursor.getColumnIndex("pm_time"));
		}

		if (db != null) {
			cursor.close();
			db.close();
		}
	}
}
