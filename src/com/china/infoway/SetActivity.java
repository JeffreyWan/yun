package com.china.infoway;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.china.infoway.utils.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class SetActivity extends Activity {

	private EditText oldPwd, newPwd, surePwd;
	private Button sureBtn, cancelBtn;
	private LinearLayout changePasswordBtn;

	private SharedPreferences spPreferences;

	// 登录成功时保存的手机号码
	private String tmpph;

	// 输入的旧密码、新密码、确认密码
	private String oldpwd;
	private String newpwd;
	private String surepwd;

	private String pwd;

	// 登录的网址

	// private String passwordUri =
	// "http://192.168.0.115/infoway_qiandao/jiekou/pwd.update.php?phone=";
	private String passwordUri = Constants.passwordUri;

	protected void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);
		// 设置标题
		setTitle("远程现场管理系统-系统设置");
		setContentView(R.layout.change_password);

		oldPwd = (EditText) findViewById(R.id.change_password_old);
		newPwd = (EditText) findViewById(R.id.change_password_new);
		surePwd = (EditText) findViewById(R.id.change_password_new_two);
		oldPwd.setText(pwd);
		sureBtn = (Button) findViewById(R.id.sureBtn);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		changePasswordBtn = (LinearLayout) findViewById(R.id.change_password_back);

		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		tmpph = spPreferences.getString("phone", "");
		oldpwd = spPreferences.getString("pwd", pwd);
	}

	protected void onStart() {
		super.onStart();
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SetActivity.this.finish();
			}
		});
		sureBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				oldpwd = oldPwd.getText().toString();
				newpwd = newPwd.getText().toString();
				surepwd = surePwd.getText().toString();
				if (!newpwd.equals(surepwd)) {
					Toast.makeText(SetActivity.this, "新密码和确认密码输入不一致，请重新输入！",
							Toast.LENGTH_LONG).show();
					return;
				} else {
					isSetAvailable();
				}
			}
		});

		changePasswordBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				SetActivity.this.finish();
			}
		});

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				Toast.makeText(SetActivity.this, "恭喜你，密码修改成功！",
						Toast.LENGTH_SHORT).show();
				Editor editor = spPreferences.edit();
				editor.putString("oldpwd", oldpwd);
				editor.putString("newpwd", newpwd);
				editor.putString("surepwd", surepwd);
				editor.commit();

				int index = 0;
				ArrayList<Activity> arrayList = ExitClass.getInstance().getAa();
				for (Activity activity : arrayList) {
					if (activity instanceof MainMenuActivity) {
						index = arrayList.indexOf(activity);
					}
				}
				for (int i = index + 1; i < arrayList.size(); i++) {
					arrayList.get(i).finish();
				}

				break;
			case 201:
				Toast.makeText(SetActivity.this, "对不起，原始密码输入错误，请重新输入！",
						Toast.LENGTH_LONG).show();
				break;
			case 202:
				Toast.makeText(SetActivity.this, "修改失败，请重试!！",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	};

	private void isSetAvailable() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				String url = passwordUri + tmpph + "&oldpwd=" + oldpwd
						+ "&newpwd=" + newpwd;

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
					} else {
						mHandler.sendEmptyMessage(202);
					}
				}
			}

		}.start();
	}

}
