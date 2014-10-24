package com.china.infoway;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.china.infoway.provider.MessageTables;
import com.china.infoway.utils.Constants;
import com.china.infoway.utils.DateUtils;
import com.china.infoway.utils.PhoneInfoHelper;

@SuppressLint("HandlerLeak")
public class MainMenuActivity extends Activity {

	private double exittime = 0;

	private Button t06;
	
	private TextView tv_companyName;

	Intent intent;

	// �汾URL
	private String verUrl = Constants.verUrl;
	// ��ȡʱ������url
	private String setTimeUri = Constants.setTimeUri;

	private SharedPreferences spPreferences;
	// ��¼�ɹ�ʱ������ֻ�����
	public static String tmpph;
	public static String companyName;
	
	private Editor editor = null;
	// ��һ�θ�������ʱ�����ݵ�����ʱ��
	private String lastTime = null;
	private boolean hasNews = false;

	// ��������Ϣ�󷵻صĽ��
	private String strResult;

	Timer tExit = new Timer();

	private final int menu_back = Menu.FIRST;
	private final int menu_quit = Menu.FIRST + 1;

	private String currentVersion;
	
	public static double latitude = 0.0;
	
	public static double longitude = 0.0;
	
	private ProgressDialog mProgressDialog;

	public void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		//�򿪽�����
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");

		NotificationManager m_NotificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);

		m_NotificationManager.cancel(0316);

		t06 = (Button) findViewById(R.id.t06);
		tv_companyName = (TextView) findViewById(R.id.tv_companyName);

		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		tmpph = spPreferences.getString("phone", "");
		companyName = spPreferences.getString(Constants.PREFS_KEY_COMPANY_NAME, "�ɶ���·��");
		editor = spPreferences.edit();
		
		tv_companyName.setText(companyName);

		// KIMI 0313
		intent = new Intent(MainMenuActivity.this, UpdateService.class);
		startService(intent);
		
//		intent = new Intent(MainMenuActivity.this, LocationService.class);
//		startService(intent);

		getSetTime();

//		MyThread mthread = new MyThread();
//		mthread.start();

//		try {
//			mthread.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		currentVersion = packInfo.versionName;

//		new Handler().post(new Runnable() {
//			public void run() {
//
//				// ����Http get ����
//				HttpGet httpRequest = new HttpGet(verUrl);
//
//				try {
//					// ����HTTP request
//					HttpResponse httpResponse = new DefaultHttpClient()
//							.execute(httpRequest);
//					if (httpResponse.getStatusLine().getStatusCode() == 200) {
//						String strResult = EntityUtils.toString(httpResponse
//								.getEntity());
//
//						if (Double.valueOf(currentVersion) < Double
//								.valueOf(strResult)) {
//							// verBool = true;
//
//							Dialog dialog = new AlertDialog.Builder(
//									MainMenuActivity.this)
//									.setIcon(android.R.drawable.btn_star)
//									.setTitle("����汾����")
//									.setMessage("�����µ������Ŷ���ף������ذ�~~")
//									.setPositiveButton(
//											"����",
//											new DialogInterface.OnClickListener() {
//												public void onClick(
//														DialogInterface dialog,
//														int which) {
//													Uri uri = Uri
//															.parse(Constants.downloadUri);
//													Intent it = new Intent(
//															Intent.ACTION_VIEW,
//															uri);
//													startActivity(it);
//												}
//
//											})
//									.setNeutralButton(
//											"�Ժ���˵",
//											new DialogInterface.OnClickListener() {
//
//												public void onClick(
//														DialogInterface dialog,
//														int which) {
//												}
//
//											}).create();
//							dialog.show();
//						} else {
//							Toast.makeText(getApplicationContext(),
//									"��ϲ�����������°汾��", Toast.LENGTH_SHORT).show();
//
//						}
//					}
//				} catch (ClientProtocolException e1) {
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		});

	}

	// ���ö�ʱ��
	private void setTimerScheduler() {

		// ��ȡ���ӹ�����
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent("com.infoway.MY_ACTION");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				MainMenuActivity.this, 0, intent, 0);

		PendingIntent pendingIntent1 = PendingIntent.getBroadcast(
				MainMenuActivity.this, 1, intent, 0);
		if (!am_start.equals("null")) {
			// ��������
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					Long.valueOf(am_start) * 60 * 60 * 1000, pendingIntent);
		}
		if (!pm_start.equals("null")) {

			alarmManager.set(AlarmManager.RTC_WAKEUP,
					(Long.valueOf(pm_start) + 12) * 60 * 60 * 1000,
					pendingIntent1);
		}
	}

	@Override
	protected void onStart() {

		super.onStart();

//		Timer timer = new Timer();
//		TimerTask task = new TimerTask() {
//			@Override
//			public void run() {
//				MyThread mthread = new MyThread();
//				mthread.start();
//			}
//		};
//		timer.schedule(task, 5 * 60 * 1000, 5 * 60 * 1000);
//
//		if (hasNews) {
//			Log.e("new", "new");
//			t06.setBackgroundResource(R.drawable.t06new_bg);
//
//		} else {
//			Log.e("old", "old");
//			t06.setBackgroundResource(R.drawable.t06_bg);
//		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, menu_back, 0, "����");
		menu.add(0, menu_quit, 0, "�˳�");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case menu_back:
			finish();
			break;
		case menu_quit:
			exitAlert("��ȷ���˳���ϵͳ��");
			break;
		}
		return true;
	}

	// ǩ��ҳ��
	public void t01(View view) {
		// ��������Ƿ���ͨ
		if( !AppUtils.checkNet( this ) )
		{
			AppUtils.netErrorAlert( this );
			return;
		}
		//���GPS��λ�Ƿ���
		if( !AppUtils.isOPen( this ) )
		{
			AppUtils.setGPSAlert( this );
			return;
		}
		
		intent = new Intent();
		intent.setClass(MainMenuActivity.this, PositionListActivity.class);
		startActivity(intent);
	}

	// �ֳ���Ϣҳ��
	public void t05(View view) {
		// ��������Ƿ���ͨ
		if( !AppUtils.checkNet( this ) )
		{
			AppUtils.netErrorAlert( this );
			return;
		}
		intent = new Intent();
		intent.setClass(MainMenuActivity.this, InformationAppActivity.class);
		startActivity(intent);
	}

	// �ֳ�֧��
	public void t06(View view) {
		hasNews = false;
		intent = new Intent();
		intent.setClass(MainMenuActivity.this, GetMessageActivity.class);
		startActivity(intent);
	}

	// ����
	// public void t03(View view) {
	// intent = new Intent();
	// intent.setClass(MainMenuActivity.this, MoreActivity.class);
	// startActivity(intent);
	// }
	// ҵ����Ϣ
	public void t02(View view) {
		if(intent == null) {
			intent = new Intent();
		}
		intent.setClass(MainMenuActivity.this, PicAndVoiActivity.class);
		startActivity(intent);
	}

	// ����ҳ��
	public void t04(View view) {
		intent = new Intent();
		intent.setClass(MainMenuActivity.this, HelpActivity.class);
		startActivity(intent);
	}

	public void t08(View view) {
		Toast.makeText(MainMenuActivity.this, "������ҳ", Toast.LENGTH_SHORT)
				.show();
	}

	public void t09(View view) {
		intent = new Intent();
		intent.setClass(MainMenuActivity.this, SetActivity.class);
		startActivity(intent);
	}

	public void t10(View view) {
		exitAlert("��ȷ���˳���ϵͳ��\n�����޷��յ�����Ϣ��");
	}

	public void t11(View view) {
		intent = new Intent();
		intent.setClass(MainMenuActivity.this, MoreActivity.class);
		startActivity(intent);
	}

	private void exitAlert(String msg) {
		// ʵ����AlertDialog.Builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// ������ʾ��Ϣ
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						ExitClass.getInstance().exit();

					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// ��һ�η��ؼ�Toast��ʾ,�ٰ�һ�η��ؼ��˳�����

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - exittime) > 2000) {
				Toast.makeText(MainMenuActivity.this, "�ٰ�һ�κ�̨����",
						Toast.LENGTH_SHORT).show();
				exittime = System.currentTimeMillis();
			} else {
				//ExitClass.getInstance().exit();
				
				Intent intent=new Intent(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
		}
		return true;
	}

//	private class MyThread extends Thread {
//
//		@Override
//		public void run() {
//			// ��ȡlastTime,���û��д����Ϊ�մ�
//			lastTime = spPreferences.getString("lastTime", "");
//
//			String uriAPI = Constants.URIAPI + tmpph + "&date=" + lastTime;
//			String url = uriAPI.replaceAll(" ", "%20");
//
//			HttpGet httpRequest = new HttpGet(url);
//			try {
//				HttpResponse httpResponse = new DefaultHttpClient()
//						.execute(httpRequest);
//				if (httpResponse.getStatusLine().getStatusCode() == 200) {
//					strResult = EntityUtils.toString(httpResponse.getEntity());
//
//					if (strResult.equals("0")) {
//						return;
//					} else {
//						hasNews = true;
//						mHandler.sendEmptyMessage(100);
//						// t06.setBackgroundResource(R.drawable.t06new_bg);
//					}
//				}
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			jsonParser(strResult);
			if (msg.what == 100) {
				t06.setBackgroundResource(R.drawable.t06new_bg);
				Log.e("HEHE", "HEHE");
			}
		}
	};

	private String strResult2;

	private String am_start_server;

	private String pm_start_server;

	private String am_start;

	private String pm_start;

	private PhoneInfoHelper phoneInfoHelper;

	private SQLiteDatabase db;

	public void jsonParser(String jsonStr) {
		try {
			JSONObject jsonObject = null;
			JSONArray jsonArray = new JSONArray(jsonStr);
			String tempTime = null;
			// ��÷������ݽӿ�ContentResolver
			ContentResolver cr = getContentResolver();
			// �������ݵ�Uri
			Uri uri = MessageTables.CONTENT_URI;
			int length = jsonArray.length();

			for (int i = 0; i < length; i++) {
				jsonObject = jsonArray.getJSONObject(i);

				// ʵ����ContentValues
				ContentValues values = new ContentValues();
				values.put("message_title", jsonObject.get("message_title")
						.toString());
				values.put("message_content", jsonObject.get("message_content")
						.toString());
				values.put("message_time", jsonObject.get("message_time")
						.toString());
				values.put("message_img", jsonObject.get("message_img")
						.toString());
				values.put("message_music", jsonObject.get("message_music")
						.toString());
				values.put("click_count", 0);
				// ���뵽���ݿ�
				cr.insert(uri, values);

				if (i == length - 1) {
					tempTime = jsonObject.get("message_time").toString();
					// ������Ϣ�����ʱ��
					editor.putString("lastTime", tempTime == null ? ""
							: tempTime);
					editor.commit();
					Log.e("lastTime", "lastTime I saved is: " + tempTime);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ��ȡ���������õĶ�λʱ����

	private void getSetTime() {
		mProgressDialog.show();
		new Thread() {
			public void run() {
				String setTimeUrl = setTimeUri + tmpph;
				String url = setTimeUrl.replaceAll(" ", "%20");

				HttpGet httpRequest = new HttpGet(url);
				try {
					HttpResponse httpResponse = new DefaultHttpClient()
							.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						strResult2 = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
						Log.e("-----------strResult2------", strResult2);
						jsonParserSetTime(strResult2);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		
	}

	// ���������浽����
	public void jsonParserSetTime(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			String status = jsonObject.getString("status");
			Log.e("************", status);

			if (status.equals("0")) {
				Log.e("Status", "Failure");
			} else if (status.equals("1")) {
				Log.e("Status", "success");
				JSONObject jsonObject1 = jsonObject.getJSONObject("am");

				am_start_server = jsonObject1.getString("start");
				String am_end_server = jsonObject1.getString("end");
				String am_time_server = jsonObject1.getString("time");

				JSONObject jsonObject2 = jsonObject.getJSONObject("pm");

				pm_start_server = jsonObject2.getString("start");
				String pm_end_server = jsonObject2.getString("end");
				String pm_time_server = jsonObject2.getString("time");

				am_start = am_start_server;
				pm_start = pm_start_server;
				Log.e("%%%%%%%%%%%%%%", pm_start);
				
				setTimerScheduler();

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
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PHONENNUM, tmpph);
				contentValues.put(PhoneInfoHelper.CHREATE_TIME, DateUtils.Date2String(new Date()));

				phoneInfoHelper = new PhoneInfoHelper(MainMenuActivity.this,
						PhoneInfoHelper.PHONEINFO_DATA_NAME, null,
						PhoneInfoHelper.PHONEINFO_DATA_VERSION);
				db = phoneInfoHelper.getWritableDatabase();
				Cursor cursor = db.query(PhoneInfoHelper.PHONEINFO_TABLE_NAME, null,
						"phonenum = ?", new String[] { tmpph }, null, null, null);
				if (cursor.getCount() == 0) {
					db.beginTransaction();
					try {
						db.insert(PhoneInfoHelper.PHONEINFO_TABLE_NAME, null, contentValues);
						db.setTransactionSuccessful();
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						db.endTransaction();
					}
				}else{
					db.beginTransaction();
					try {
						db.update(PhoneInfoHelper.PHONEINFO_TABLE_NAME, contentValues,
								"phonenum=?", new String[] { tmpph });
						db.setTransactionSuccessful();
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						db.endTransaction();
					}
				}
//				db.query(PhoneInfoHelper.PHONEINFO_TABLE_NAME, contentValues, "_phonenum=?", new String[]{tmpph}, null, null,null );
				if (db != null) {
					db.close();
				}
				
			} else {
				Log.e("Status", "ϵͳ����");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}
}
