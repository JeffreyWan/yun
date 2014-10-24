package com.china.infoway.app;

import com.china.infoway.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {
	
	private EditText et_phone_number;
	private EditText et_password;
	private Button btn_clear_text1;
	private Button btn_clear_text2;
	private CheckBox cb_auto_login;
	private CheckBox cb_save_pwd;
	private Button btn_login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}
	
	private void findViewById() {
		et_phone_number = (EditText) findViewById(R.id.phoneEdit);
		et_password = (EditText) findViewById(R.id.pwdEdit);
		btn_clear_text1 = (Button) findViewById(R.id.btn_clear_text1);
		btn_clear_text2 = (Button) findViewById(R.id.btn_clear_text2);
		cb_auto_login = (CheckBox) findViewById(R.id.infoway_autologin);
		cb_save_pwd = (CheckBox) findViewById(R.id.infoway_savepwd);
		btn_login = (Button) findViewById(R.id.loginBtn);
	}
	
	private void setListener() {
		btn_login.setOnClickListener(this);
		cb_auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					cb_save_pwd.setChecked(true);
				}
			}
		});
	}

	private void init() {}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			
			break;

		default:
			break;
		}
	}
	
	
}
