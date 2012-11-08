package com.sky.sms.ym.adv.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.sky.sms.ym.adv.domain.MyMessage;
import com.sky.sms.ym.adv.main.R;

public class MyAdapter extends BaseAdapter {

	private LayoutInflater mInflater;   
    
    private List<MyMessage> myMessageList;   
    
    private Context context = null;
    
    private long id;
    
    public MyAdapter(Context context, List<MyMessage> myMessageList) {
    	// 参数初始化                                                                 
    	this.context = context;
        mInflater = LayoutInflater.from(context);   
        this.myMessageList = myMessageList;   
    }
    
	@Override
	public int getCount() {
		return myMessageList.size();
	}
	

	@Override
	public MyMessage getItem(int position) {
		return myMessageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final MyMessage myMessage = getItem(position);
		id = myMessage.getId();
		holder = new ViewHolder();   
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.message_list_item, null);  
			holder.icon = (ImageView) convertView.findViewById(R.id.message_image);   
			holder.phone = ((TextView)convertView.findViewById(R.id.message_from_phone));
			holder.name = (TextView)convertView.findViewById(R.id.message_from_name);
			holder.copyflag = (TextView)convertView.findViewById(R.id.message_copy_flag);
			holder.checkbox = (CheckBox)convertView.findViewById(R.id.message_checkbox);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.icon.setImageResource(R.drawable.sms_secutity);
		holder.phone.setText(myMessage.getPhone());
		holder.name.setText(myMessage.getName());
		int syncFlag = myMessage.getSyncFlag();
		if(syncFlag == 0) {
			holder.copyflag.setText(context.getResources().getString(R.string.sms_sync_no_str));  
		} else {
			holder.copyflag.setText(context.getResources().getString(R.string.sms_sync_yes_str));  
		}
		holder.checkbox.setChecked(myMessage.isChecked()); 
		myMessage.setListIndex(position);
		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						myMessage.setChecked(isChecked);
			      }
			 });
		convertView.setTag(holder);  
		return convertView;   
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}



	static class ViewHolder {
		
    	public ImageView icon;// 缩略图
    	
		public TextView phone;// 标题
		
		public TextView name;// 摘要
		
		public TextView copyflag;// 摘要
		
		public CheckBox checkbox;// 摘要
		
	}

}
