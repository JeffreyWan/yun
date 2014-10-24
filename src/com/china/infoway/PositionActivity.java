package com.china.infoway;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.china.infoway.utils.Constants;
import com.china.infoway.utils.MyDatabaseHelper;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class PositionActivity extends MapActivity implements LocationListener {

	MapView mMapView = null;
	LocationListener mLocationListener = null;// onResume时注册此listener，onPause时需要Remove
	MyLocationOverlay mLocationOverlay = null; // 定位图层
	MapController mMapController = null;

	private Button submitBtn;
	private String statue;
	private SharedPreferences spPreferences;

	// 定义经度longitude、纬度latitude
	private float latitude, longitude;
	// 登录成功时保存的手机号码
	private String tmpph;
	// 获取到的时间
	private String time;
	// 定位时的网址
	// private String
	private String positionUri = Constants.positionUri;
	// http请求时的url格式

	private SQLiteDatabase db;
	private MyDatabaseHelper helper;
	private Handler handler;
	
	private ProgressDialog mProgressDialog;

	protected void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);
		// 设置标题
		setContentView(R.layout.position);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");

		BMapApiDemoApp app1 = (BMapApiDemoApp) this.getApplication();
		if (app1.mBMapMan == null) {
			app1.mBMapMan = new BMapManager(getApplication());
			app1.mBMapMan.init(app1.mStrKey,
					new BMapApiDemoApp.MyGeneralListener());
		}
		app1.mBMapMan.start();
		// 如果使用地图SDK，请初始化地图Activity
		super.initMapActivity(app1.mBMapMan);

		mMapView = (MapView) findViewById(R.id.bmapView);

		mMapView.setBuiltInZoomControls(true);
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		mMapView.setDrawOverlayWhenZooming(true);

		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		mMapController = mMapView.getController();

		// 添加定位图层
		mLocationOverlay = new MyLocationOverlay(this, mMapView);

		mLocationOverlay.enableMyLocation(); // 启用定位
		mLocationOverlay.enableCompass(); // 启用指南针

		mMapView.getOverlays().add(mLocationOverlay);

		// 中国地图中心点
		GeoPoint ptInit = new GeoPoint((int) (latitude * 1e6),
				(int) (longitude * 1e6));

		// 设置地图中心点
		mMapController.setCenter(ptInit);

		// 设置地图缩放级别
		mMapController.setZoom(16);
		mMapView.getController().animateTo(ptInit);

		Toast.makeText(this, "正在定位...", Toast.LENGTH_SHORT).show();

		// 注册定位事件
		mLocationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
				if (location != null) {
					latitude = (float) location.getLatitude();
					longitude = (float) location.getLongitude();

					GeoPoint pointHere = new GeoPoint((int) (latitude * 1e6),
							(int) (longitude * 1e6));
					mMapView.getController().animateTo(pointHere);
					mMapController.setZoom(16);

					// submit显示
					submitBtn.setClickable(true);
					submitBtn.setVisibility(View.VISIBLE);

				}
			}
		};
		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		tmpph = spPreferences.getString("phone", "");
		submitBtn = (Button) findViewById(R.id.submitBtn);
		TextPaint tp = submitBtn.getPaint();
		tp.setFakeBoldText(true);
		submitBtn.setClickable(false);
		submitBtn.setVisibility(View.INVISIBLE);

		/**
		 * 拖动按钮
		 */
		DisplayMetrics dm = getResources().getDisplayMetrics();
		final int screenWidth = dm.widthPixels;
		final int screenHeight = dm.heightPixels - 30;

		submitBtn.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY, originalX, originalY;

			public boolean onTouch(View v, MotionEvent event) {

				int ea = event.getAction();
				switch (ea) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					originalX = lastX;
					originalY = lastY;
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;

					int left = v.getLeft() + dx;
					int top = v.getTop() + dy;
					int right = v.getRight() + dx;
					int bottom = v.getBottom() + dy;

					if (left < 0) {
						left = 0;
						right = left + v.getWidth();
					}

					if (right > screenWidth) {
						right = screenWidth;
						left = right - v.getWidth();
					}

					if (top < 0) {
						top = 0;
						bottom = top + v.getHeight();
					}

					if (bottom > screenHeight) {
						bottom = screenHeight;
						top = bottom - v.getHeight();
					}

					v.layout(left, top, right, bottom);

					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					if (lastX == originalX && lastY == originalY) {
						onclick();
					}

					break;
				}
				return false;
			}
		});

		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					Toast.makeText(PositionActivity.this, "签到成功",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private void onclick() {

		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		time = sDateFormat.format(new Date());

		if (latitude == 0.0 || longitude == 0.0) {
			Toast.makeText(PositionActivity.this, "位置的经纬度坐标不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			isPositionAvailable();
		}
	}

	private void isPositionAvailable() {
		mProgressDialog.show();
		new Thread() {

			@Override
			public void run() {
				String url = positionUri + tmpph + "&latitude=" + longitude
						+ "&longitude=" + latitude + "&type=1";
				String mUrl = url.replaceAll(" ", "%20");

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
					try {
						statue = EntityUtils.toString(httpResponse.getEntity());
						// 插入数据
						ContentValues values = new ContentValues();
						values.put(MyDatabaseHelper.COL_COMPANYNAME, "");
						values.put(MyDatabaseHelper.COL_CONTACTPERSON, "");
						values.put(MyDatabaseHelper.COL_CONTACTPHONE, "");
						values.put(MyDatabaseHelper.COL_PENDINGTHINGS, "");
						values.put(MyDatabaseHelper.COL_WORKRESULTS, "");
						values.put(MyDatabaseHelper.COL_REMARK, "");
						values.put(MyDatabaseHelper.COL_TIME, "");
						values.put(MyDatabaseHelper.COL_LATITUDE, latitude);// 纬度
						values.put(MyDatabaseHelper.COL_LONGITUDE, longitude);// 经度
						values.put(MyDatabaseHelper.COL_COUNT, 0);
						values.put(MyDatabaseHelper.COL_STATUE, statue);

						values.put(MyDatabaseHelper.COL_TIME2, time);
						// 获得对象
						helper = new MyDatabaseHelper(PositionActivity.this,
								MyDatabaseHelper.DATA_NAME, null,
								MyDatabaseHelper.DATA_VERSION);
						db = helper.getWritableDatabase();
						// 执行插入的操作
						long row = db.insert(MyDatabaseHelper.TABLE_NAME1,
								null, values);
						if (row > 0) {
							handler.sendEmptyMessage(0);
						}
						Intent intent = new Intent(PositionActivity.this,
								InformationAppActivity.class);
						startActivity(intent);
						if (db != null)
							db.close();
						PositionActivity.this.finish();

					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if(mProgressDialog != null) {
							mProgressDialog.dismiss();
						}
					}
				}
			}
		}.start();
	}

	@Override
	protected void onPause() {
		BMapApiDemoApp app = (BMapApiDemoApp) this.getApplication();
		app.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		mLocationOverlay.disableMyLocation();
		mLocationOverlay.disableCompass(); // 关闭指南针
		app.mBMapMan.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		BMapApiDemoApp app = (BMapApiDemoApp) this.getApplication();
		// 注册定位事件，定位后将地图移动到定位点
		app.mBMapMan.getLocationManager().requestLocationUpdates(
				mLocationListener);
		mLocationOverlay.enableMyLocation();
		mLocationOverlay.enableCompass(); // 打开指南针
		app.mBMapMan.start();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onLocationChanged(Location arg0) {

	}

}
