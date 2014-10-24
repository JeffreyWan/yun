package com.china.infoway;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.china.infoway.entry.LoginResult;
import com.china.infoway.net.HttpUtil;
import com.china.infoway.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity implements OnClickListener {

	public ProgressDialog mydDialog = null;

	private Button btn_clear_text1;
	
	private Button btn_clear_text2;
	
	private LinearLayout loginbackBtn;
	private EditText phoneEdit, pwdEdit;
	private Button loginBtn;
	private SharedPreferences spPreferences;

	private String tmpph;

	private String pwd;

	private String loginUri = Constants.loginUri;

	private CheckBox isRemenber;

	private CheckBox isLoginSelf;
	
	public String loginResult;
	
	private String imei;
	
	public static LoginResult mLoginResult;

	public void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mLoginResult = new LoginResult();
		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
//		imei = "a00000362c2a0f";
//		imei = "868128015362361";
		
		btn_clear_text1 = (Button) findViewById(R.id.btn_clear_text1);
		btn_clear_text1.setOnClickListener(this);
		
		btn_clear_text2 = (Button) findViewById(R.id.btn_clear_text2);
		btn_clear_text2.setOnClickListener(this);
		
		phoneEdit = (EditText) findViewById(R.id.phoneEdit);
		pwdEdit = (EditText) findViewById(R.id.pwdEdit);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginbackBtn = (LinearLayout) findViewById(R.id.login_btn_back);

		tmpph = spPreferences.getString("phone", "");
		phoneEdit.setText(tmpph);

		isRemenber = (CheckBox) findViewById(R.id.infoway_savepwd);
		isLoginSelf = (CheckBox) findViewById(R.id.infoway_autologin);

		if (spPreferences.getBoolean("reme_pass", false)) {
			isRemenber.setChecked(true);
			pwdEdit.setText(spPreferences.getString("pwd", ""));
			isRemenber.setButtonDrawable(R.drawable.check_on);
		}

		if (spPreferences.getBoolean("auto_login", false)) {
			isLoginSelf.setChecked(true);
			pwdEdit.setText(spPreferences.getString("pwd", ""));
			pwd = pwdEdit.getText().toString();
			isRemenber.setButtonDrawable(R.drawable.check_on);
			isLoginSelf
					.setButtonDrawable(R.drawable.check_on);
			doLogin();
		}

		getCheckBoxListener();
	}

	protected void onStart() {

		super.onStart();

		loginbackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LoginActivity.this.finish();
			}
		});

		loginBtn.setOnClickListener(this);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Gson gson = new Gson();
				Type type = new TypeToken<LoginResult>() {}.getType();
				mLoginResult = gson.fromJson(loginResult.trim(), type);
				if(mLoginResult.status.equals("0"))
					Toast.makeText(LoginActivity.this, "手机号错误",
							Toast.LENGTH_LONG).show();
				else if(mLoginResult.status.equals("1"))
					Toast.makeText(LoginActivity.this, "密码错误",
							Toast.LENGTH_LONG).show();
				else if(mLoginResult.status.equals("2")) {
					// 检测GPS定位是否开启
					if (!AppUtils.isOPen(LoginActivity.this)) {
						AppUtils.setGPSAlert(LoginActivity.this);
						return;
					}
					// 存入数据
					 Editor editor = spPreferences.edit();
					 editor.putString("phone", phoneEdit.getText().toString().trim());
					 editor.putString(Constants.PREFS_KEY_COMPANY_NAME, mLoginResult.companyName);
					 editor.commit();

					Intent intent = new Intent(LoginActivity.this,
							MainMenuActivity.class);
					startActivity(intent);
				}
				break;
			default:
				Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void isLoginAvailable() {
		if (!AppUtils.checkNet(LoginActivity.this)) {
			Toast.makeText(LoginActivity.this, "未连接网络,请打开网络",
					Toast.LENGTH_SHORT).show();
			AppUtils.netErrorAlert(LoginActivity.this);
			return;
		}

		mydDialog = ProgressDialog.show(LoginActivity.this,
				"请稍等              ", "正在登录...               ", true);
		new Thread() {

			@Override
			public void run() {
				super.run();

				String url = loginUri + "&phone=" + phoneEdit.getText() + "&pwd=" + pwd + "&verify=" + imei;

				HttpPost httpRequest = new HttpPost(url);
				HttpResponse httpResponse = null;
				try {
					httpResponse = new DefaultHttpClient().execute(httpRequest);

					int res = httpResponse.getStatusLine().getStatusCode();
					if (res == 200) {
						try {
							loginResult = EntityUtils.toString(httpResponse
									.getEntity());
							mHandler.sendEmptyMessage(1);
						} catch (ParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {

					// 卸载所创建的myDialog对象
					mydDialog.dismiss();
				}
			}
		}.start();
	}
	
	private void doLogin() {
		RequestParams params = new RequestParams();
		params.put("verify", imei);
		params.put("phone", phoneEdit.getText().toString().trim());
		params.put("pwd", pwd);
		String s = loginUri + "&phone=" + phoneEdit.getText() + "&pwd=" + pwd + "&verify=" + imei;
		System.out.println(s);
//		HttpUtil.get(loginUri, params, new JsonHttpResponseHandler() {
//			
//			@Override
//			public void onStart() {
//				mydDialog = ProgressDialog.show(LoginActivity.this, "", "Loading...", true);
//				super.onStart();
//			}
//			
//			@Override
//			public void onFinish() {
//				mydDialog.dismiss();
//				super.onFinish();
//			}
//			
//			@Override
//			public void onSuccess(int statusCode, JSONObject response) {
//				try {
//					mLoginResult.status = response.get("status") + "";
//					if(mLoginResult.status.equals("2")) 
//						mLoginResult.companyName = response.get("companyName") + "";
//					mHandler.sendEmptyMessage(1);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//			
//			@Override
//			public void onFailure(int statusCode, Throwable e,
//					JSONObject errorResponse) {
//				Toast.makeText(getApplicationContext(), "网络不佳哦亲~", Toast.LENGTH_SHORT).show();
//			}
//		});
		HttpUtil.get(loginUri, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				mydDialog = ProgressDialog.show(LoginActivity.this, "", "Loading...", true);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mydDialog.dismiss();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Toast.makeText(getApplicationContext(), "网络不佳哦亲~", Toast.LENGTH_SHORT).show();
				super.onFailure(arg0, arg1, arg2, arg3);
			}
			
			@Override
			@Deprecated
			public void onSuccess(String content) {
				super.onSuccess(content);
				loginResult = content;
				mHandler.sendEmptyMessage(1);
			}
		});
	}

	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.btn_clear_text1:
			phoneEdit.setText("");
			break;
		case R.id.btn_clear_text2:
			pwdEdit.setText("");
			break;
		case R.id.loginBtn:

			pwd = pwdEdit.getText().toString();
			if (pwd.length() == 0) {
				Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_LONG)
						.show();
			} else {

				Editor editor = spPreferences.edit();
				if (isLoginSelf.isChecked()) {
					String password = pwdEdit.getText().toString();
					editor.putString("pwd", password).commit();
					editor.putBoolean("auto_login", true).commit();
					editor.putBoolean("reme_pass", true).commit();
				} else {
					if (isRemenber.isChecked()) {
						String password = pwdEdit.getText().toString();
						editor.putString("pwd", password).commit();
						editor.putBoolean("auto_login", false).commit();
						editor.putBoolean("reme_pass", true).commit();
					} else {
						editor.remove("pwd").commit();
						editor.putBoolean("auto_login", false).commit();
						editor.putBoolean("reme_pass", false).commit();
					}
				}
//				isLoginAvailable();
				doLogin();
			}

			break;

		default:
			break;
		}

	}

	private void getCheckBoxListener() {
		isRemenber.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					isRemenber
							.setButtonDrawable(R.drawable.check_on);
				} else {
					isRemenber
							.setButtonDrawable(R.drawable.check_off);
				}
			}
		});

		isLoginSelf.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					isRemenber.setChecked(true);
					isLoginSelf
							.setButtonDrawable(R.drawable.check_on);
					isRemenber
							.setButtonDrawable(R.drawable.check_on);
				} else {
					isLoginSelf
							.setButtonDrawable(R.drawable.check_off);
					isRemenber
							.setButtonDrawable(R.drawable.check_off);
				}
			}
		});

	}
}