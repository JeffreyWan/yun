package com.china.infoway;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;

import com.china.infoway.entry.MessageBean;
import com.china.infoway.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UpdateService extends Service {

	WakeLock mWakeLock;

	Vibrator vibrator;

	private SharedPreferences spPreferences;
	private SharedPreferences preferencesMessageId;
	// 登录成功时保存的手机号码
	private String tmpph;

	// 请求新消息后返回的结果
	private String strResult;

	// // 上一次更新数据时，数据的最新时间
	// private String lastTime = null;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {

		ExitClass.getInstance().addService(this);

		super.onCreate();

		acquireWakeLock();

		Log.e("service", "service");

		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		tmpph = spPreferences.getString("phone", "");

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				MyThread mthread = new MyThread();
				mthread.start();
				Log.e("mthread", "mthread");
			}
		};
		timer.schedule(task, 5*60 * 1000, 3 * 60 * 1000);

	}

	private class MyThread extends Thread {

		@Override
		public void run() {
			// 获取lastTime,如果没有写入则为空串
			String lastTime = spPreferences.getString("lastTime", "");
			preferencesMessageId = getSharedPreferences("phone", MODE_PRIVATE);
			String message = preferencesMessageId.getString("messagesIds", "");
			String[] readed = message.split(",");

			String uriAPI = Constants.URIAPI + "&phone=" + tmpph + "&date=" + lastTime;
			String url = uriAPI.replaceAll(" ", "%20");

			HttpGet httpRequest = new HttpGet(url);
			try {
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					strResult = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
					Gson gson = new Gson();
					Type type = new TypeToken<List<MessageBean>>() {}.getType();
					List<MessageBean> messageBeans = gson.fromJson(strResult, type);
					
					if (strResult.equals("0") || isListAllIn(messageBeans, readed)) {
						return;
					} else {

						addNotificaction();

						vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
						long[] pattern = { 100, 400, 100, 400 }; // OFF/ON/OFF/ON...
						vibrator.vibrate(pattern, -1);
						// -1不重复，非-1为从pattern的指定下标开始重复

					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加一个notification
	 */
	@SuppressWarnings("deprecation")
	private void addNotificaction() {
		NotificationManager manager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 创建一个Notification
		Notification notification = new Notification();
		// 设置显示在手机最上边的状态栏的图标
		notification.icon = R.drawable.tlj_logo;
		// 当当前的notification被放到状态栏上的时候，提示内容
		notification.tickerText = "温馨提示：有新消息，请注意查收。";

		/***
		 * notification.contentIntent:一个PendingIntent对象，当用户点击了状态栏上的图标时，
		 * 该Intent会被触发 notification.contentView:我们可以不在状态栏放图标而是放一个view
		 * notification.deleteIntent 当当前notification被移除时执行的intent
		 * notification.vibrate 当手机震动时，震动周期设置
		 */
		// 添加声音提示
		notification.defaults = Notification.DEFAULT_SOUND;
		// audioStreamType的值必须AudioManager中的值，代表着响铃的模式
		notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;

		// 下边的两个方式可以添加音乐
		// notification.sound =
		// Uri.parse("file:///sdcard/notification/ringer.mp3");
		// notification.sound =
		// Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
		Intent intent = new Intent(this, MainMenuActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		// 点击状态栏的图标出现的提示信息设置
		notification.setLatestEventInfo(this, "温馨提示：", "您有新消息，请注意查收。",
				pendingIntent);
		manager.notify(0316, notification);

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		releaseWakeLock();
	}

	// 申请设备电源锁
	private void acquireWakeLock() {
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "");
			if (null != mWakeLock) {
				mWakeLock.acquire();
			}
		}
	}

	// 释放设备电源锁
	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
	
	/**
	 * 判断获取服务器现场支持数据是否与手机本地数据完全匹配
	 * @param messageBeans -服务器数据
	 * @param readed -本地数据
	 * @return 
	 */
	private boolean isListAllIn(List<MessageBean> messageBeans, String[] readed){
		List<String> messgeIds = new ArrayList<String>();
		List<String> exsitedIds = new ArrayList<String>();
		for (MessageBean messageBean : messageBeans) {
			if (messageBean.message_id != null) {
				messgeIds.add(messageBean.message_id);
			}
		}
		for (int i = 0; i < readed.length; i++) {
			if (readed[i] != null && !readed[i].equals("null")) {
				exsitedIds.add(readed[i]);
			}
		}
		if (exsitedIds.containsAll(messgeIds)) {
			return true;
		}
		return  false;
	}

}
