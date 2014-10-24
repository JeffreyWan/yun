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
	// ��¼�ɹ�ʱ������ֻ�����
	private String tmpph;

	// ��������Ϣ�󷵻صĽ��
	private String strResult;

	// // ��һ�θ�������ʱ�����ݵ�����ʱ��
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
			// ��ȡlastTime,���û��д����Ϊ�մ�
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
						// -1���ظ�����-1Ϊ��pattern��ָ���±꿪ʼ�ظ�

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
	 * ���һ��notification
	 */
	@SuppressWarnings("deprecation")
	private void addNotificaction() {
		NotificationManager manager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// ����һ��Notification
		Notification notification = new Notification();
		// ������ʾ���ֻ����ϱߵ�״̬����ͼ��
		notification.icon = R.drawable.tlj_logo;
		// ����ǰ��notification���ŵ�״̬���ϵ�ʱ����ʾ����
		notification.tickerText = "��ܰ��ʾ��������Ϣ����ע����ա�";

		/***
		 * notification.contentIntent:һ��PendingIntent���󣬵��û������״̬���ϵ�ͼ��ʱ��
		 * ��Intent�ᱻ���� notification.contentView:���ǿ��Բ���״̬����ͼ����Ƿ�һ��view
		 * notification.deleteIntent ����ǰnotification���Ƴ�ʱִ�е�intent
		 * notification.vibrate ���ֻ���ʱ������������
		 */
		// ���������ʾ
		notification.defaults = Notification.DEFAULT_SOUND;
		// audioStreamType��ֵ����AudioManager�е�ֵ�������������ģʽ
		notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;

		// �±ߵ�������ʽ�����������
		// notification.sound =
		// Uri.parse("file:///sdcard/notification/ringer.mp3");
		// notification.sound =
		// Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
		Intent intent = new Intent(this, MainMenuActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		// ���״̬����ͼ����ֵ���ʾ��Ϣ����
		notification.setLatestEventInfo(this, "��ܰ��ʾ��", "��������Ϣ����ע����ա�",
				pendingIntent);
		manager.notify(0316, notification);

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		releaseWakeLock();
	}

	// �����豸��Դ��
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

	// �ͷ��豸��Դ��
	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
	
	/**
	 * �жϻ�ȡ�������ֳ�֧�������Ƿ����ֻ�����������ȫƥ��
	 * @param messageBeans -����������
	 * @param readed -��������
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
