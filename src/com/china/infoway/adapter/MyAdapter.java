package com.china.infoway.adapter;
/**
 * 未用，适配器已写入到对应的activity中
 * @author ChJan
 *
 */
import java.util.List;
import java.util.Map;
import com.china.infoway.R;
import com.china.infoway.utils.MyDatabaseHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class MyAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;
	private Context context;
	private Cursor cursor;
	private Class<?> context2;
	private SQLiteDatabase db;
	private MyDatabaseHelper helper;
	

	
	public MyAdapter(Context context, List<Map<String, Object>> data,Cursor cursor,Class<?> context2) {
		this.context=context;
		this.cursor = cursor;
		this.context2=context2;
		mInflater = LayoutInflater.from(context);
		this.mData = data;
		helper = new MyDatabaseHelper(context, MyDatabaseHelper.DATA_NAME, null,
				MyDatabaseHelper.DATA_VERSION);
		db = helper.getWritableDatabase();
	}
	
	public void refresh(List<Map<String, Object>> data) {

		this.mData = data;

		notifyDataSetChanged();

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

	public View getView(  final int position, View convertView, ViewGroup parent) {
		 ViewHolder holder = null; 
		//convertView为null的时候初始化convertView。    
		  final int position2=position;
		if (convertView == null) {    
			holder = new ViewHolder();    
			convertView = mInflater.inflate(R.layout.list_item, null); 
			holder.content = (RelativeLayout)convertView.findViewById(R.id.content);
			holder.phone = (TextView) convertView.findViewById(R.id.phone_content);
			holder.date_time = (TextView) convertView.findViewById(R.id.time_content); 
			holder.status = (ImageView) convertView.findViewById(R.id.status);
			holder.arrows = (ImageView) convertView.findViewById(R.id.arrows);
			holder.delete=(Button)convertView.findViewById(R.id.delete);
			holder.details=(Button)convertView.findViewById(R.id.details);
			
			holder.delete.setTag( position);
			convertView.setTag(holder);    
		} else {
			holder = (ViewHolder) convertView.getTag();    
		}
		holder.phone.setText(mData.get(position).get("phone").toString());
		holder.date_time.setText(mData.get(position).get("date_time").toString());
		if((Integer) mData.get(position).get("btn_count") == 1) {
			holder.status.setBackgroundResource(R.drawable.completed);
		} else if((Integer) mData.get(position).get("btn_count") == 0) {
			holder.status.setBackgroundResource(R.drawable.uncompleted);
		}
		
		//隐藏/显示详细内容
		holder.content.setOnClickListener(new View.OnClickListener(){
			

			public void onClick(View v) {
				
				View  details_view = ((View) v.getParent()).findViewById(R.id.details_view);
				ImageView arrows = (ImageView) v.findViewById(R.id.arrows);
				
				if(details_view.getVisibility()==View.VISIBLE){
					details_view.setVisibility(View.GONE);
					arrows.setImageDrawable(context.getResources().getDrawable(
							R.drawable.lease_list_item_arraw_unsp));
				}else{
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
				 
				 final int mPosition=position;
				 System.out.println("##################"+mPosition);
				new AlertDialog.Builder(context)
						.setTitle("提示").setMessage("是否删除此信息?")
						.setPositiveButton("是", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

								if (cursor.moveToPosition(mPosition)) {
									final String date_time = cursor.getString(cursor
											.getColumnIndex("date_time"));
									

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
													int rows = db
															.delete(MyDatabaseHelper.TABLE_NAME1,
																	"date_time = ?",
																	new String[] { date_time });
													if (rows > 0) {
														mData.remove(mPosition);
														
														Toast.makeText(
																context,
																"删除成功",
																Toast.LENGTH_SHORT)
																.show();													
														refresh(mData);
														MyAdapter.this.notifyDataSetChanged();
													} else {
														Toast.makeText(
																context,
																"删除失败",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											});
									builder.setNegativeButton("取消", null);
									builder.create().show();

								}

							}
						}).setNegativeButton("否", null).show();



			}});
		/**
		 * 进入详细页面
		 */
		holder.details.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// 得到单击的TextView文本
				Log.i("cursor", cursor.getCount() + "");
				
				if (cursor.moveToPosition(position2)) {

					Intent intent = new Intent(context,
							context2);
					intent.putExtra("location_time", cursor.getString(cursor
							.getColumnIndex("date_time")));
					context.startActivity(intent);
				
			}
			}
		});
		
		return convertView;  
	}
	
	public final class ViewHolder {
		public RelativeLayout content;
		public TextView phone;
		public TextView date_time;	
		public ImageView status;
		public ImageView arrows;
		public Button delete;
		public Button details;
	}
	

}
