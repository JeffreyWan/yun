package com.china.infoway.adapter;
/**
 * 未用，适配器已写入到对应的activity中
 * @author ChJan
 *
 */
import java.util.List;
import java.util.Map;

import com.china.infoway.R;
import com.china.infoway.provider.DBHelper;
import com.china.infoway.provider.MessageTables;
import com.china.infoway.utils.MyDatabaseHelper;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Adapter extends BaseAdapter {

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
//		if (db!=null&&db.isOpen()) {
//			db.close();
//		}
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
			holder.arrows = (ImageView) convertView.findViewById(R.id.arrows);
			holder.delete = (Button) convertView.findViewById(R.id.delete);
			holder.details = (Button) convertView.findViewById(R.id.details);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// if((boolean) mData.get(position).get("click_count").equals("0")) {
		// holder.imageView.setBackgroundResource(R.drawable.meikan);
		// }
		if ((Integer) mData.get(position).get("click_count") == 1) {
			Log.i("1", "1");
			// holder.imageView.setBackgroundResource(R.drawable.ok);
			holder.status_image.setVisibility(View.INVISIBLE);
		} else if ((Integer) mData.get(position).get("click_count") == 0) {
			Log.i("0", "0");
			holder.status_image.setBackgroundResource(R.drawable.meikan);
		}

		String string = mData.get(position).get("message_title").toString();
		if (string.length() > 5)
			string = string.substring(0, 5) + "...";
		holder.message_title.setText(string);

		String string2 = mData.get(position).get("message_content").toString();
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
					arrows.setImageDrawable(context.getResources().getDrawable(
							R.drawable.lease_list_item_arraw_unsp));
				} else {
					details_view.setVisibility(View.VISIBLE);
					arrows.setImageDrawable(context.getResources().getDrawable(
							R.drawable.lease_list_item_arraw_sp));
				}
			}
		});
		/**
		 * 删除
		 */
		holder.delete.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				final int mPosition = position2;

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

										int n = cr.delete(uri,
												"message_title=?", args);

										if (n == 1) {
											mData.remove(position2);
											Toast.makeText(context, "删除成功",
													Toast.LENGTH_SHORT).show();
											Adapter.this.notifyDataSetChanged();
										}
									}
								}).setNegativeButton("否", null).show();

			}
		});
		/**
		 * 进入详细页面
		 */
		holder.details.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Map<String, Object> map = mData.get(position2);

				Intent intent = new Intent(context, context2);
				intent.putExtra("message_title",
						(String) map.get("message_title"));
				intent.putExtra("message_content",
						(String) map.get("message_content"));
				intent.putExtra("message_time",
						(String) map.get("message_time"));
				intent.putExtra("message_img", (String) map.get("message_img"));
				intent.putExtra("message_music",
						(String) map.get("message_music"));

				ContentValues values = new ContentValues();
				values.put("message_title", (String) map.get("message_title"));
				values.put("message_content",
						(String) map.get("message_content"));
				values.put("message_time", (String) map.get("message_time"));
				values.put("message_img", (String) map.get("message_img"));
				values.put("message_music", (String) map.get("message_music"));
				values.put("click_count", 1);

				db.update("aa", values, "message_time = ?",
						new String[] { (String) map.get("message_time") });
				
				close();
				context.startActivity(intent);
			}

		});

		return convertView;
	}

	public final class ViewHolder {
		public RelativeLayout content;
		public ImageView status_image;
		public TextView message_title;
		public TextView publish_time;
		public TextView message_content;
		public ImageView arrows;
		public Button delete;
		public Button details;

	}

	public void close() {
		if (helper != null) {
			helper.close();
		}
		if (db != null&&db.isOpen()) {
			db.close();
		}
		
	}

}
