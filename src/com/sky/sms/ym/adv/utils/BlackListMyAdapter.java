package com.sky.sms.ym.adv.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sky.sms.ym.adv.domain.FiltInfo;
import com.sky.sms.ym.adv.main.R;

public class BlackListMyAdapter extends BaseAdapter {

	private LayoutInflater mInflater;   
    
	// 保存过滤号码的列表   
	List<FiltInfo>  filtNumList;
  
	public BlackListMyAdapter(Context context, List<FiltInfo>  filtNumList) {
		// 参数初始化    
        mInflater = LayoutInflater.from(context);   
        this.filtNumList = filtNumList;   
	}
	
	public int getCount() {
		return filtNumList.size();
	}

	public FiltInfo getItem(int position) {
		  return filtNumList.get(position);   
	}

	public long getItemId(int position) {
		return position;   
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		holder = new ViewHolder();
		FiltInfo filtInfo = filtNumList.get(position);
		filtInfo.setPostionIndex(position);
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.filt_tel_num_list, null);  
			holder.num = ((TextView) convertView.findViewById(R.id.filt_tel_num));
			holder.name = (TextView) convertView.findViewById(R.id.tel_num_to_name);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.num.setText(filtInfo.getNum());
		holder.name.setText(filtInfo.getName());
		convertView.setTag(holder);  
		return convertView;   
	}
	
	public void addItem() {
		
	}
	
	public void removeItem(int indexTemp) {
		if(indexTemp<0 || indexTemp>getCount()) {
			return;
		}
		filtNumList.remove(indexTemp);
		notifyDataSetChanged();   
	}
	
	
	static class ViewHolder{
		
		public CheckBox checkbox;
		
		public TextView num;// 标题
		
		public TextView name;// 摘要
		
	}
}

