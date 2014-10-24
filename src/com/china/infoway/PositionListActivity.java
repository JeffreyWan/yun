package com.china.infoway;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.china.infoway.utils.Constants;
import com.china.infoway.utils.MyDatabaseHelper;

public class PositionListActivity extends Activity implements OnClickListener {

	private ImageView iv_back;

	private Button btn_with_map;

	private Button btn_qiandao;

	private TextView tv_addr;

	private String statue;

	private SQLiteDatabase db;
	private MyDatabaseHelper helper;

	private Vibrator mVibrator = null;
	private LocationClient mLocClient;
	
	private ProgressDialog mProgressDialog;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				Toast.makeText(PositionListActivity.this, "签到成功",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position_list);
		
		findViewById();
		setListener();
		init();

		mLocClient = ((BMapApiDemoApp) getApplication()).mLocationClient;
		LocationService.mTv = tv_addr;
		mVibrator =(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		((BMapApiDemoApp)getApplication()).mVibrator01 = mVibrator;
		
		if(!mLocClient.isStarted()) {
			mLocClient.start();
		} else {
			mLocClient.requestLocation();
		}
	}

	private void findViewById() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		btn_with_map = (Button) findViewById(R.id.btn_with_map);
		btn_qiandao = (Button) findViewById(R.id.btn_qiandao);
		tv_addr = (TextView) findViewById(R.id.tv_addr);
	}

	private void setListener() {
		iv_back.setOnClickListener(this);
		btn_with_map.setOnClickListener(this);
		btn_qiandao.setOnClickListener(this);
	}

	private void init() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");
	}

	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_with_map:
			if (intent == null)
				intent = new Intent();
			intent.setClass(PositionListActivity.this, PositionActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_qiandao:
			if(tv_addr.getText().toString().equals("正在获取位置信息...")) {
				Toast.makeText(getApplicationContext(), "正在获取位置信息...", Toast.LENGTH_SHORT).show();
				return;
			}
			isPositionAvailable();
			break;
		}
	}

	private void isPositionAvailable() {
		mProgressDialog.show();
		new Thread() {

			@Override
			public void run() {
				String url = Constants.positionUri + MainMenuActivity.tmpph
						+ "&latitude=" + BMapApiDemoApp.latitude
						+ "&longitude=" + BMapApiDemoApp.longitude + "&address=" + URLEncoder.encode(tv_addr.getText().toString().trim()) + "&type=1";
				Log.i("-----latitude-----", BMapApiDemoApp.latitude + "");
				Log.i("-----longitude---", BMapApiDemoApp.longitude + "");
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
						statue = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
						// 插入数据
						ContentValues values = new ContentValues();
						values.put(MyDatabaseHelper.COL_COMPANYNAME, "");
						values.put(MyDatabaseHelper.COL_CONTACTPERSON, "");
						values.put(MyDatabaseHelper.COL_CONTACTPHONE, "");
						values.put(MyDatabaseHelper.COL_PENDINGTHINGS, "");
						values.put(MyDatabaseHelper.COL_WORKRESULTS, "");
						values.put(MyDatabaseHelper.COL_REMARK, "");
						values.put(MyDatabaseHelper.COL_TIME, "");
						values.put(MyDatabaseHelper.COL_LATITUDE,
								MainMenuActivity.latitude);// 纬度
						values.put(MyDatabaseHelper.COL_LONGITUDE,
								MainMenuActivity.longitude);// 经度
						values.put(MyDatabaseHelper.COL_COUNT, 0);
						values.put(MyDatabaseHelper.COL_STATUE, statue);

						values.put(MyDatabaseHelper.COL_TIME2, getCurrTime());
						values.put(MyDatabaseHelper.COL_ADDR, tv_addr.getText().toString().trim());
						// 获得对象
						helper = new MyDatabaseHelper(
								PositionListActivity.this,
								MyDatabaseHelper.DATA_NAME, null,
								MyDatabaseHelper.DATA_VERSION);
						db = helper.getWritableDatabase();
						// 执行插入的操作
						long row = db.insert(MyDatabaseHelper.TABLE_NAME1,
								null, values);
						if (row > 0) {
							handler.sendEmptyMessage(0);
						}
						Intent intent = new Intent(PositionListActivity.this,
								InformationAppActivity.class);
						startActivity(intent);
						if (db != null)
							db.close();
						PositionListActivity.this.finish();

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

	private String getCurrTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sDateFormat.format(new Date());
	}
}
