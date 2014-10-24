package com.china.infoway;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class HelpActivity extends Activity {
	
	private LinearLayout back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitClass.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		back = (LinearLayout) findViewById(R.id.help_btn_back);
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				HelpActivity.this.finish();
			}
		});
	}
}
