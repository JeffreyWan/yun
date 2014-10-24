package com.china.infoway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.china.infoway.provider.DBHelper;
import com.china.infoway.provider.MessageTables;
import com.china.infoway.utils.Constants;
import com.china.infoway.utils.MyDatabaseHelper;

@SuppressLint("HandlerLeak")
public class DownloadActivity extends Activity {

	private List<Map<String, Object>> listData = null;
	private Adapter mAdapter = null;
	private ListView list = null;
	private Cursor cursor = null;

	private SharedPreferences spPreferences;
	// 登录成功时保存的手机号码
	private String tmpph;

	private Editor editor = null;
	// 上一次更新数据时，数据的最新时间
	private String lastTime = null;

	// 请求新消息后返回的结果
	private String strResult;

	private SQLiteDatabase db;
	private DBHelper helper;
	private int click_count;

	Timer tExit = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
		}
	};

	private LinearLayout back;
	private Button main;
	private Button set;
	private Button exit;
	private Button more;

	public void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.download);

		NotificationManager m_NotificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);

		m_NotificationManager.cancel(0316);

		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		tmpph = spPreferences.getString("phone", "");
		editor = spPreferences.edit();

		list = (ListView) findViewById(R.id.lv);
		back = (LinearLayout) findViewById(R.id.bespeak_btn_back);
		main = (Button) findViewById(R.id.t08);
		set = (Button) findViewById(R.id.t09);
		exit = (Button) findViewById(R.id.t10);
		more = (Button) findViewById(R.id.t11);

		back.setOnClickListener(new MyOnclickListener());
		main.setOnClickListener(new MyOnclickListener());
		set.setOnClickListener(new MyOnclickListener());
		exit.setOnClickListener(new MyOnclickListener());
		more.setOnClickListener(new MyOnclickListener());

		// 注册上下文信息
		registerForContextMenu(list);

		listData = new ArrayList<Map<String, Object>>();
		// initData();

		// mAdapter = new Adapter(DownloadActivity.this, listData,
		// MessageActivity.class);
		// list.setAdapter(mAdapter);

		// helper = new DBHelper(this, DBHelper.DATABASE_NAME, null,
		// DBHelper.DATABASE_VERSION);
		// db = helper.getWritableDatabase();

	}

	@Override
	protected void onStart() {

		super.onStart();
		MyThread mthread = new MyThread();
		mthread.start();
		try {
			mthread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Timer timer = new Timer();
		// task = new TimerTask() {
		// @Override
		// public void run() {
		// new MyThread().start();
		// }
		// };
		// timer.schedule(task, 10000, 10000);
		initData();
	}

	@Override
	protected void onStop() {
		super.onStop();
		task.cancel();
		Log.e("Stop", String.valueOf(list.getCount()));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (cursor != null) {
			cursor.close();
		}
		if (db != null && db.isOpen()) {
			db.close();
		}
		if (helper != null) {
			helper.close();
		}

	}

	// 显示更新页面
	private void initData() {
		// 访问本地SQLite数据库中消息表的Uri
		Uri uri = MessageTables.CONTENT_URI;
		// 要选择消息表中的列
		String[] projection = { "_id", "message_title", "message_content",
				"message_time", "message_img", "message_music", "click_count" };
		// 获得ContentResolver实例
		ContentResolver cr = getContentResolver();
		// 查询放回游标
		cursor = cr.query(uri, projection, null, null, null);

		if (cursor == null)
			return;
		listData.clear();
		boolean flag = cursor.moveToFirst();
		while (flag) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message_title",
					cursor.getString(cursor.getColumnIndex("message_title")));
			map.put("message_content",
					cursor.getString(cursor.getColumnIndex("message_content")));
			map.put("message_time",
					cursor.getString(cursor.getColumnIndex("message_time")));
			map.put("message_img",
					cursor.getString(cursor.getColumnIndex("message_img")) + "");
			map.put("message_music",
					cursor.getString(cursor.getColumnIndex("message_music"))
							+ "");
			map.put("click_count",
					cursor.getInt(cursor.getColumnIndex("click_count")));

			Log.i("click_count111================",
					cursor.getInt(cursor.getColumnIndex("click_count")) + "");
			listData.add(map);
			flag = cursor.moveToNext();
		}
		cursor.close();
		Log.i("listData111", listData.size() + "");
		mAdapter = new Adapter(DownloadActivity.this, listData,
				MessageActivity.class);
		mAdapter.notifyDataSetChanged();
		list.setAdapter(mAdapter);
	}

	private class MyThread extends Thread {

		@Override
		public void run() {
			// 获取lastTime,如果没有写入则为空串
			lastTime = spPreferences.getString("lastTime", "");

			String uriAPI = Constants.URIAPI + tmpph + "&date=" + lastTime;
			String url = uriAPI.replaceAll(" ", "%20");

			HttpGet httpRequest = new HttpGet(url);
			try {
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					strResult = EntityUtils.toString(httpResponse.getEntity());
					Log.e("strResult", strResult);
					if (strResult.equals("0")) {
						return;
					} else {
						mHandler.sendEmptyMessage(100);
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

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			jsonParser(strResult);
			Log.e("--------------", "---------------");
		}
	};

	public void jsonParser(String jsonStr) {
		try {
			JSONObject jsonObject = null;
			JSONArray jsonArray = new JSONArray(jsonStr);
			String tempTime = null;
			// 获得访问数据接口ContentResolver
			ContentResolver cr = getContentResolver();
			// 访问数据的Uri
			Uri uri = MessageTables.CONTENT_URI;
			int length = jsonArray.length();

			for (int i = 0; i < length; i++) {
				jsonObject = jsonArray.getJSONObject(i);

				// 实例化ContentValues
				ContentValues values = new ContentValues();
				values.put("message_title", jsonObject.get("message_title")
						.toString());
				values.put("message_content", jsonObject.get("message_content")
						.toString());
				values.put("message_time", jsonObject.get("message_time")
						.toString());
				values.put("message_img", jsonObject.get("message_img")
						.toString());
				values.put("message_music", jsonObject.get("message_music")
						.toString());
				values.put("click_count", click_count);

				// 插入到数据库
				cr.insert(uri, values);

				if (i == length - 1) {
					tempTime = jsonObject.get("message_time").toString();
					// 保存信息的最后时间
					editor.putString("lastTime", tempTime == null ? ""
							: tempTime);
					editor.commit();
					Log.e("lastTime", "lastTime I saved is: " + tempTime);
				}
			}

			initData();

			list.setAdapter(mAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自定义适配器
	 */
	private class Adapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<Map<String, Object>> mData;
		private Context context;
		private Class<?> context2;
		private DBHelper helper;
		private SQLiteDatabase db;

		public Adapter(Context context, List<Map<String, Object>> data,
				Class<?> context2) {
			this.context = context;
			this.context2 = context2;
			mInflater = LayoutInflater.from(context);
			this.mData = data;
			helper = new DBHelper(context, MyDatabaseHelper.DATA_NAME, null,
					MyDatabaseHelper.DATA_VERSION);
			// db = helper.getWritableDatabase();
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int position) {
			return mData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final int position2 = position;
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.message_list, null);
				holder.content = (RelativeLayout) convertView
						.findViewById(R.id.content);
				holder.status_image = (ImageView) convertView
						.findViewById(R.id.iv_state);
				holder.message_title = (TextView) convertView
						.findViewById(R.id.message_title);
				holder.publish_time = (TextView) convertView
						.findViewById(R.id.publish_time);
				holder.message_content = (TextView) convertView
						.findViewById(R.id.message_content);
				holder.delete = (Button) convertView.findViewById(R.id.delete);
				holder.details = (Button) convertView
						.findViewById(R.id.details);
				holder.deleteLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.deleteLinearLayout);
				holder.detailsLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.detailsLinearLayout);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// if((boolean) mData.get(position).get("click_count").equals("0"))
			// {
			// holder.imageView.setBackgroundResource(R.drawable.meikan);
			// }
			if ((Integer) mData.get(position).get("click_count") == 1) {
				Log.i("1", "1");
				// holder.imageView.setBackgroundResource(R.drawable.ok);
				// holder.status_image.setVisibility(View.INVISIBLE);
				holder.status_image.setBackgroundResource(0);
			} else if ((Integer) mData.get(position).get("click_count") == 0) {
				Log.i("0", "0");
				holder.status_image.setBackgroundResource(R.drawable.meikan);
			}

			String string = mData.get(position).get("message_title").toString();
			if (string.length() > 5)
				string = string.substring(0, 5) + "...";
			holder.message_title.setText(string);

			String string2 = mData.get(position).get("message_content")
					.toString();
			if (string2.length() > 15)
				string2 = string2.substring(0, 12) + "...";
			holder.message_content.setText(string2);
			holder.publish_time.setText(mData.get(position).get("message_time")
					.toString());

			// 隐藏/显示详细内容
			holder.content.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					View details_view = ((View) v.getParent())
							.findViewById(R.id.details_view);
					ImageView arrows = (ImageView) v.findViewById(R.id.arrows);

					if (details_view.getVisibility() == View.VISIBLE) {
						details_view.setVisibility(View.GONE);
						arrows.setImageDrawable(context.getResources()
								.getDrawable(
										R.drawable.lease_list_item_arraw_unsp));
					} else {
						details_view.setVisibility(View.VISIBLE);
						arrows.setImageDrawable(context.getResources()
								.getDrawable(
										R.drawable.lease_list_item_arraw_sp));
					}
				}
			});
			/**
			 * 删除
			 */
			holder.delete.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					delete(position2);

				}
			});

			holder.deleteLinearLayout
					.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {

							delete(position2);

						}
					});
			/**
			 * 进入详细页面
			 */
			holder.details.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					details(position2);
				}

			});

			holder.detailsLinearLayout
					.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {

							details(position2);
						}

					});

			return convertView;
		}

		private void delete(int position) {
			final int mPosition = position;

			new AlertDialog.Builder(context)
					.setTitle("提示")
					.setMessage("是否删除此信息?")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									Map<String, Object> map = mData
											.get(mPosition);

									String title = (String) map
											.get("message_title");
									String[] args = new String[1];
									args[0] = title;

									// 访问数据的Uri
									Uri uri = MessageTables.CONTENT_URI;

									// 获得访问数据接口ContentResolver
									ContentResolver cr = context
											.getContentResolver();

									int n = cr.delete(uri, "message_title=?",
											args);

									if (n == 1) {
										mData.remove(mPosition);
										Toast.makeText(context, "删除成功",
												Toast.LENGTH_SHORT).show();
										mAdapter.notifyDataSetChanged();
									}
								}
							}).setNegativeButton("否", null).show();
		}

		private void details(int position) {

			Map<String, Object> map = mData.get(position);

			Intent intent = new Intent(context, context2);
			intent.putExtra("message_title", (String) map.get("message_title"));
			intent.putExtra("message_content",
					(String) map.get("message_content"));
			intent.putExtra("message_time", (String) map.get("message_time"));
			intent.putExtra("message_img", (String) map.get("message_img"));
			intent.putExtra("message_music", (String) map.get("message_music"));

			ContentValues values = new ContentValues();
			values.put("message_title", (String) map.get("message_title"));
			values.put("message_content", (String) map.get("message_content"));
			values.put("message_time", (String) map.get("message_time"));
			values.put("message_img", (String) map.get("message_img"));
			values.put("message_music", (String) map.get("message_music"));
			values.put("click_count", 1);
			helper = new DBHelper(context, MyDatabaseHelper.DATA_NAME, null,
					MyDatabaseHelper.DATA_VERSION);
			db = helper.getWritableDatabase();
			db.update("aa", values, "message_time = ?",
					new String[] { (String) map.get("message_time") });

			close();
			context.startActivity(intent);
		}

		public final class ViewHolder {
			public RelativeLayout content;
			public ImageView status_image;
			public TextView message_title;
			public TextView publish_time;
			public TextView message_content;
			public Button delete;
			public Button details;
			public LinearLayout deleteLinearLayout;
			public LinearLayout detailsLinearLayout;

		}

		public void close() {
			if (db != null && db.isOpen()) {
				db.close();
			}
			if (helper != null) {
				helper.close();
			}

		}

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
				DownloadActivity.this.finish();
				break;
			case R.id.t08:
				DownloadActivity.this.finish();
				break;
			case R.id.t09:
				intent = new Intent();
				intent.setClass(DownloadActivity.this, SetActivity.class);
				startActivity(intent);
				break;
			case R.id.t10:
				exitAlert("你确定退出本系统吗？\n（将无法收到新消息）");
				break;
			case R.id.t11:
				intent = new Intent();
				intent.setClass(DownloadActivity.this, MoreActivity.class);
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
