package com.china.infoway;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.china.infoway.utils.Constants;
import com.china.infoway.utils.PhoneInfoHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class RegisterActivity extends Activity {
	
	private EditText phoneEdit;
	private Button registerButton;
	
	private Button btn_clear_text;
	
	private ProgressDialog mProgressDialog;
	
	private SharedPreferences spPreferences;
	// 输入的手机号码
	private String phone = null;
	// 注册的网址

	// private String registerUri =
	// "http://192.168.0.115/infoway_qiandao/jiekou/bind.insert.php?bind_phone=";
	private String registerUri = Constants.registerUri;

	protected void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);
		// 设置标题
		setTitle("远程现场管理系统-注册界面");
		setContentView(R.layout.register);
		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);

		if (ReadConfig()) {
			Intent intent = new Intent(RegisterActivity.this,
					LoginActivity.class);
			startActivity(intent);
			this.finish();
		}
		phoneEdit = (EditText) findViewById(R.id.infophone);
		registerButton = (Button) findViewById(R.id.registerBtn);
		
		btn_clear_text = (Button) findViewById(R.id.btn_clear_text);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		btn_clear_text.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				phoneEdit.setText("");
			}
		});
		
		registerButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				phone = phoneEdit.getText().toString();
				if (phone.length() != 11 || TextUtils.isEmpty(phone)) {
					Toast.makeText(RegisterActivity.this,
							"你输入的手机号码有误，请检查后重新输入！", Toast.LENGTH_SHORT).show();
				} else {
					if (!AppUtils.checkNet(RegisterActivity.this)) {
						Toast.makeText(RegisterActivity.this, "未连接网络,请打开网络",
								Toast.LENGTH_SHORT).show();
						AppUtils.netErrorAlert(RegisterActivity.this);
						return;
					}

					isPhoneAvailable();
				}
			}
		});
	}

	private Handler mHandler = new Handler() {

		private PhoneInfoHelper phoneInfoHelper;
		private SQLiteDatabase db;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				// 检测GPS定位是否开启
				if (!AppUtils.isOPen(RegisterActivity.this)) {
					AppUtils.setGPSAlert(RegisterActivity.this);
					return;
				}
				Toast.makeText(RegisterActivity.this, "恭喜你，绑定成功！",
						Toast.LENGTH_SHORT).show();
				Editor editor = spPreferences.edit();
				editor.putString("phone", phone);
				editor.commit();

				// 保存到本地数据库
				ContentValues contentValues = new ContentValues();
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PHONENNUM,
						phone);
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_AM_START, "");
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_AM_END, "");
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_AM_TIME, "");
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PM_START, "");
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PM_END, "");
				contentValues.put(PhoneInfoHelper.PHONEINFO_COL_PM_TIME, "");
				contentValues.put(PhoneInfoHelper.CHREATE_TIME, "");

				phoneInfoHelper = new PhoneInfoHelper(RegisterActivity.this,
						PhoneInfoHelper.PHONEINFO_DATA_NAME, null,
						PhoneInfoHelper.PHONEINFO_DATA_VERSION);
				db = phoneInfoHelper.getWritableDatabase();
				db.insert(PhoneInfoHelper.PHONEINFO_TABLE_NAME, null,
						contentValues);
				if (db != null) {
					db.close();
				}
				if(mProgressDialog != null)
					mProgressDialog.dismiss();
				Intent intent = new Intent(RegisterActivity.this,
						MainMenuActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();

				break;
			case 201:
				if(mProgressDialog != null)
					mProgressDialog.dismiss();
				Toast.makeText(RegisterActivity.this, "你的手机号码已被注册，请输入新的手机号！",
						Toast.LENGTH_LONG).show();
				break;
			case 202:
				if(mProgressDialog != null)
					mProgressDialog.dismiss();
				Toast.makeText(RegisterActivity.this, "你输入的不是员工号码,请重新输入！",
						Toast.LENGTH_LONG).show();
				break;
			case 203:
				if(mProgressDialog != null)
					mProgressDialog.dismiss();
				Toast.makeText(RegisterActivity.this, "注册失败，请重试!",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	private boolean ReadConfig() {
		String str = spPreferences.getString("phone", "");
		if (str.length() == 11) {
			return true;
		}
		return false;
	}

	private void isPhoneAvailable() {
		
		mProgressDialog.show();
		
		new Thread() {

			@Override
			public void run() {
				super.run();

				String url = registerUri + phone;
				HttpPost httpRequest = new HttpPost(url);
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
					String statue = null;
					try {
						statue = EntityUtils.toString(httpResponse.getEntity());
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (statue.equals("0")) {
						mHandler.sendEmptyMessage(200);
					} else if (statue.equals("1")) {
						mHandler.sendEmptyMessage(201);
					} else if (statue.equals("2")) {
						mHandler.sendEmptyMessage(202);
					} else {
						mHandler.sendEmptyMessage(203);
					}
				}
			}

		}.start();
	}

}
