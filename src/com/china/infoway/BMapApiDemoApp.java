package com.china.infoway;

import android.app.Application;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;

public class BMapApiDemoApp extends Application {
	
	public static double latitude;
	
	public static double longitude;
	
	public static String currAddrStr = "位置不详";

	static BMapApiDemoApp mDemoApp;

	BMapManager mBMapMan = null;

	String mStrKey = "01331AFA954E7E300428A5F0C9C829E0E16F87A3";
	boolean m_bKeyRight = true; // 授权Key正确，验证通过

	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;
	private String mData;
	public TextView mTv;
	public NotifyLister mNotifyer = null;
	public Vibrator mVibrator01;

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {
		public void onGetNetworkState(int iError) {
			Toast.makeText(BMapApiDemoApp.mDemoApp.getApplicationContext(),
					"您的网络出错啦！", Toast.LENGTH_LONG).show();
		}

		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(BMapApiDemoApp.mDemoApp.getApplicationContext(),
						"请在BMapApiDemoApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG)
						.show();
				BMapApiDemoApp.mDemoApp.m_bKeyRight = false;
			}
		}
	}

	@Override
	public void onCreate() {
		
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

		mDemoApp = this;

		mBMapMan = new BMapManager(this);

		mBMapMan.init(this.mStrKey, new MyGeneralListener());

		mLocationClient = new LocationClient(this);

		mLocationClient.setAK("A6b6c9a0054b86dc1e7e5d9f8ec55165");
		mGeofenceClient = new GeofenceClient(this);

		super.onCreate();
	}

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

//	public void logMsg(String str) {
//		try {
//			mData = str;
//			if (mTv != null) {
//				mTv.setText(mData);
//				Log.i("地址", mData);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public class MyLocationListener implements BDLocationListener {
//
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null)
//				return;
//			logMsg(location.getAddrStr());
//			latitude = location.getLatitude();
//			longitude = location.getLongitude();
//			Log.i("---------location------------", "---getAddrStr----");
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null)
//				return;
//			logMsg(poiLocation.getAddrStr());
//			Log.i("-----------poiLocation----------", "---getAddrStr----");
//		}
//
//	}

	public class NotifyLister extends BDNotifyListener {
		@Override
		public void onNotify(BDLocation mLocation, float distance) {
			mVibrator01.vibrate(1000);
		}
	}
}
