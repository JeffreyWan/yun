package com.china.infoway;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.china.infoway.adapter.MessageAdapter;
import com.china.infoway.entry.MessageBean;
import com.china.infoway.net.HttpUtil;
import com.china.infoway.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GetMessageActivity extends Activity {
	
	private LinearLayout back;
	
	private Button main;

	private Button set;

	private Button exit;

	private Button more;

	private ListView mListView;

	private String data;
	
	private List<MessageBean> listData;
	
	private ProgressDialog mProgressDialog;
	
	private String[] readed;
	
	private String s;
	
	private SharedPreferences mPreferences;
	
	private MessageAdapter messageAdapter;
	
	private int j = 0;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				Gson gson = new Gson();
				Type type = new TypeToken<List<MessageBean>>() {
				}.getType();
				listData = gson.fromJson(data, type);
				List<MessageBean> messageBeans = new ArrayList<MessageBean>();
				if (listData != null) {
					for (MessageBean messageBean : listData) {
						if (messageBean.message_id != null) {
							messageBeans.add(messageBean);
						}
					}
				}
				messageAdapter = new MessageAdapter(GetMessageActivity.this, messageBeans, readed);
				mListView.setAdapter(messageAdapter);
				if(mProgressDialog != null)
					mProgressDialog.dismiss();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_LONG).show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");
		
		mPreferences = getSharedPreferences("phone", MODE_PRIVATE);
		s = mPreferences.getString("messagesIds", "");
		readed = s.split(",");
		
		findViewById();
		setListener();
		
		getMessage();
	}

	private void findViewById() {
		mListView = (ListView) findViewById(R.id.lv);
		back = (LinearLayout) findViewById(R.id.bespeak_btn_back);
		main = (Button) findViewById(R.id.t08);
		set = (Button) findViewById(R.id.t09);
		exit = (Button) findViewById(R.id.t10);
		more = (Button) findViewById(R.id.t11);
	}
	
	private void setListener() {
		
		back.setOnClickListener(new MyOnclickListener());
		main.setOnClickListener(new MyOnclickListener());
		set.setOnClickListener(new MyOnclickListener());
		exit.setOnClickListener(new MyOnclickListener());
		more.setOnClickListener(new MyOnclickListener());
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				for(int i = 0; i < readed.length; i++) {
					j = 0;
					if(readed[i].equals(listData.get(arg2).message_id)) {
						j++;
						break;
					}
				}
				
				if(j == 0) {
					s += listData.get(arg2).message_id + ",";
					Editor editor = mPreferences.edit();
					editor.putString("messagesIds", s);
					editor.commit();
					readed = s.split(",");
				}
				
				Intent intent = new Intent(GetMessageActivity.this, MessageActivity.class);
				intent.putExtra("message_title", listData.get(arg2).message_title);
				intent.putExtra("message_content", listData.get(arg2).message_content);
				intent.putExtra("message_time", listData.get(arg2).message_time);
				intent.putExtra("message_img", listData.get(arg2).message_img);
				intent.putExtra("message_music", listData.get(arg2).message_music);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		messageAdapter = new MessageAdapter(GetMessageActivity.this, listData, readed);
		mListView.setAdapter(messageAdapter);
	}
	
	private void getMessage() {
		final ProgressDialog progressDialog = new ProgressDialog(GetMessageActivity.this);
		progressDialog.setMessage("Loading...");
		RequestParams params = new RequestParams();
		params.put("phone", MainMenuActivity.tmpph);
		HttpUtil.get(Constants.URIAPI, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				progressDialog.show();
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				progressDialog.dismiss();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				Toast.makeText(getApplicationContext(), "网络异常,请确认网络状态", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			@Deprecated
			public void onSuccess(String content) {
				if(content == null) 
					handler.sendEmptyMessage(0);
				else {
					data = content;
					handler.sendEmptyMessage(1);
				}
			}
		});
	}
	
	/**
	 * 自定义点击事件监听器
	 * 
	 * @author ChJan
	 * 
	 */
	private class MyOnclickListener implements OnClickListener {

		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.bespeak_btn_back:
				GetMessageActivity.this.finish();
				break;
			case R.id.t08:
				GetMessageActivity.this.finish();
				ArrayList<Activity> arrayList = ExitClass.getInstance().getAa();
				for (Activity activity : arrayList) {
					if (activity instanceof DownloadActivity) {
						activity.finish();
					}
				}
				break;
			case R.id.t09:
				intent = new Intent();
				intent.setClass(GetMessageActivity.this, SetActivity.class);
				startActivity(intent);
				break;
			case R.id.t10:
				exitAlert("你确定退出本系统吗？\n（将无法收到新消息）");
				break;
			case R.id.t11:
				intent = new Intent();
				intent.setClass(GetMessageActivity.this, MoreActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}

		}

	}
	/**
	 * 菜单栏退出事件
	 * 
	 * @param msg
	 */

	private void exitAlert(String msg) {
		// 实例化AlertDialog.Builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// 设置显示信息
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						ExitClass.getInstance().exit();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
