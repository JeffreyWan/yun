package com.china.infoway.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.china.infoway.R;
import com.china.infoway.entry.MessageBean;

public class MessageAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<MessageBean> data;
	
	private String[] readed;
	
	public MessageAdapter(Context context, List<MessageBean> data, String[] readed) {
		this.context = context;
		this.data = data;
		this.readed = readed;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.message_list, null);
			holder.message_title = (TextView) convertView.findViewById(R.id.message_title);
			holder.message_content = (TextView) convertView.findViewById(R.id.message_content);
			holder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
			holder.iv_state = (ImageView) convertView.findViewById(R.id.iv_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.message_title.setText(data.get(position).message_title);
		if (data.get(position) == null || data.get(position).message_content == null ) {
			holder.message_content.setText("");
		}else if(data.get(position) != null && data.get(position).message_content != null && 
				data.get(position).message_content.length() > 11)
			holder.message_content.setText(data.get(position).message_content.substring(0, 10) + "...");
		else
			holder.message_content.setText(data.get(position).message_content);
		holder.publish_time.setText(data.get(position).message_time);
		
		for(int i = 0; i < readed.length; i++) {
			if(readed[i].equals(data.get(position).message_id)) {
				holder.iv_state.setVisibility(View.INVISIBLE);
			}
		}
//		if(position == 0 || position == 1 || position == 2)
//			holder.iv_state.setVisibility(View.VISIBLE);
//		else
//			holder.iv_state.setVisibility(View.INVISIBLE);
		
		return convertView;
	}
	
	static class ViewHolder {
		
		TextView message_title;
		
		TextView message_content;
		
		TextView publish_time;
		
		ImageView iv_state;
	}
}
