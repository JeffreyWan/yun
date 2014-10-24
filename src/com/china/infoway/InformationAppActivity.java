package com.china.infoway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.china.infoway.utils.MyDatabaseHelper;

public class InformationAppActivity extends Activity {

	// 快捷修改shift+alt+r
	// private final static int MITEM_DELETE_STU = 0xA2;

	private SQLiteDatabase db;
	private MyDatabaseHelper helper;
	// private String date_time;

	private Cursor cursor = null;
	String contactpersonStr = null;
	String contactphoneStr = null;
	String pendingthingsStr = null;
	String workresultsStr = null;
	String otherremarkStr = null;
	String companynameStr = null;

	private ListView list;

	// 实时定位时成功保存的经纬度信息
	Intent intent;
	protected int position = 0;

	private List<Map<String, Object>> adapterList;

	private LinearLayout back;

	private SharedPreferences spPreferences;

	private String phone;
	private View main;
	private Button exit;
	private Button set;
	private Button more;

	protected void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_list);

		list = (ListView) findViewById(R.id.list);
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
		// 注册上下文菜单
		registerForContextMenu(list);
		// 获取手机号码
		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);
		phone = spPreferences.getString("phone", "");

		// intent = getIntent();
		// click_counStr = intent.getIntExtra("click_count", 1);

		helper = new MyDatabaseHelper(this, MyDatabaseHelper.DATA_NAME, null,
				MyDatabaseHelper.DATA_VERSION);
		db = helper.getWritableDatabase();

		// 显示当前数据
		fillData();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}

	// 显示更新页面
	private void fillData() {

		adapterList = new ArrayList<Map<String, Object>>();
		// 查询数据
		cursor = db.query(MyDatabaseHelper.TABLE_NAME1, null, null, null, null,
				null, "date_time desc");
		Log.i("listcount", cursor.getCount() + "");
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Map<String, Object> map = new HashMap<String, Object>();
				String date_time = cursor.getString(cursor
						.getColumnIndex("date_time"));
				int btn_count = cursor.getInt(cursor
						.getColumnIndex("btn_count"));

				Log.i("date_time", date_time);
				map.put("phone", phone);
				map.put("date_time", date_time);
				map.put("btn_count", btn_count);
				adapterList.add(map);
				cursor.moveToNext();
			}
		}
		Log.i("adapterList", adapterList.size() + "");
		MyAdapter myAdapter = new MyAdapter(InformationAppActivity.this,
				adapterList, cursor, UpdateInformationActivity.class);
		list.setAdapter(myAdapter);

	}

	protected void onDestroy() {
		super.onDestroy();
		if (db != null || db.isOpen()) {
			db.close();
		}
	}

	protected void onResume() {
		super.onResume();
		System.out.println("OnResume");
	}

	/*
	 * 适配器
	 */

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<Map<String, Object>> mData;
		private Context context;
		private Cursor cursor;
		private Class<?> context2;
		private SQLiteDatabase db;
		private MyDatabaseHelper helper;

		public MyAdapter(Context context, List<Map<String, Object>> data,
				Cursor cursor, Class<?> context2) {
			this.context = context;
			this.cursor = cursor;
			this.context2 = context2;
			mInflater = LayoutInflater.from(context);
			this.mData = data;
			helper = new MyDatabaseHelper(context, MyDatabaseHelper.DATA_NAME,
					null, MyDatabaseHelper.DATA_VERSION);
			db = helper.getWritableDatabase();
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

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			// convertView为null的时候初始化convertView。
			final int position2 = position;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder.content = (RelativeLayout) convertView
						.findViewById(R.id.content);
				holder.phone = (TextView) convertView
						.findViewById(R.id.phone_content);
				holder.date_time = (TextView) convertView
						.findViewById(R.id.time_content);
				holder.status = (ImageView) convertView
						.findViewById(R.id.status);
				// holder.arrows = (ImageView)
				// convertView.findViewById(R.id.arrows);
				holder.delete = (Button) convertView.findViewById(R.id.delete);
				holder.details = (Button) convertView
						.findViewById(R.id.details);
				holder.deleteLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.deleteLinearLayout);
				holder.detailsLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.detailsLinearLayout);

				holder.delete.setTag(position);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.phone.setText(mData.get(position).get("phone").toString());
			holder.date_time.setText(mData.get(position).get("date_time")
					.toString());
			if ((Integer) mData.get(position).get("btn_count") == 1) {
				holder.status.setBackgroundResource(R.drawable.completed);
			} else if ((Integer) mData.get(position).get("btn_count") == 0) {
				holder.status.setBackgroundResource(R.drawable.uncompleted);
			}

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
			db.close();
			helper.close();
			return convertView;
		}

		/**
		 * 删除
		 */
		private void delete(final int position) {
			final int mPosition = position;
			System.out.println("##################" + mPosition);
			new AlertDialog.Builder(context)
					.setTitle("提示")
					.setMessage("是否删除此信息?")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									if (cursor.moveToPosition(position)) {
										// final String date_time =
										// cursor.getString(cursor
										// .getColumnIndex("date_time"));
										final String statue = cursor.getString(cursor
												.getColumnIndex("statue"));
										AlertDialog.Builder builder = new AlertDialog.Builder(
												context);
										builder.setTitle("删除提示");
										builder.setIcon(android.R.drawable.alert_dark_frame);
										String msg = "确定要删除";

										if (cursor.moveToPosition(mPosition)) {
											msg += "\n公司名称:"
													+ cursor.getString(cursor
															.getColumnIndex("company_name"));
											msg += "\n联系人:"
													+ cursor.getString(cursor
															.getColumnIndex("contact_person"));
											msg += "\n联系方式:"
													+ cursor.getString(cursor
															.getColumnIndex("contact_phone"));
											msg += "\n待办事由:"
													+ cursor.getString(cursor
															.getColumnIndex("pending_things"));
											msg += "\n工作结果:"
													+ cursor.getString(cursor
															.getColumnIndex("work_results"));
											msg += "\n其他备注:"
													+ cursor.getString(cursor
															.getColumnIndex("other_remark"));
										}

										builder.setMessage(msg);
										builder.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {

													public void onClick(
															DialogInterface dialog,
															int which) {
														db = helper
																.getWritableDatabase();
														int rows = db
																.delete(MyDatabaseHelper.TABLE_NAME1,
																		"statue = ?",
																		new String[] { statue });
														if (rows > 0) {
															mData.remove(position);

															Toast.makeText(
																	context,
																	"删除成功",
																	Toast.LENGTH_SHORT)
																	.show();
															fillData();
															MyAdapter.this
																	.notifyDataSetChanged();
														} else {
															Toast.makeText(
																	context,
																	"删除失败",
																	Toast.LENGTH_SHORT)
																	.show();
														}
														db.close();
														helper.close();
													}
												});
										builder.setNegativeButton("取消", null);
										builder.create().show();

									}

								}
							}).setNegativeButton("否", null).show();

		}

		private void details(int position) {
			// 得到单击的TextView文本
			Log.i("cursor", cursor.getCount() + "");

			if (cursor.moveToPosition(position)) {

				Intent intent = new Intent(context, context2);
				intent.putExtra("location_time",
						cursor.getString(cursor.getColumnIndex("date_time")));
				intent.putExtra("statue",
						cursor.getString(cursor.getColumnIndex("statue")));
				Log.i("statuestatue22222222",
						cursor.getString(cursor.getColumnIndex("statue")));
				InformationAppActivity.this.startActivityForResult(intent, 1);
			}
		}

		public final class ViewHolder {
			public RelativeLayout content;
			public TextView phone;
			public TextView date_time;
			public ImageView status;
			// public ImageView arrows;
			public Button delete;
			public Button details;
			public LinearLayout deleteLinearLayout;
			public LinearLayout detailsLinearLayout;
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
				InformationAppActivity.this.finish();
				break;
			case R.id.t08:

				InformationAppActivity.this.finish();
				break;
			case R.id.t09:

				intent = new Intent();
				intent.setClass(InformationAppActivity.this, SetActivity.class);
				startActivity(intent);
				break;
			case R.id.t10:

				exitAlert("你确定退出本系统吗？\n（将无法收到新消息）");
				break;
			case R.id.t11:
				intent = new Intent();
				intent.setClass(InformationAppActivity.this, MoreActivity.class);
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
