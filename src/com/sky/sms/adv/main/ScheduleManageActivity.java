package com.sky.sms.adv.main;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sky.sms.adv.dao.MyMessageDao;
import com.sky.sms.adv.domain.DataException;
import com.sky.sms.adv.domain.MyMessage;
import com.sky.sms.adv.utils.DaoFactory;

public class ScheduleManageActivity extends ListActivity  {

	private List<String>  data =  null;
	
	private List<MyMessage> scheduleList = null;
	
	private ListView newsListView = null;

	private MyMessageDao myMessageDao = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.my_schedule_list_form_title);
//		setContentView(R.layout.my_listview);
		myMessageDao = DaoFactory.getMessageDao(this);
		scheduleList = new ArrayList<MyMessage>();
		newsListView = (ListView)getListView();
//		RelativeLayout footView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.my_schedule_list_form_title_footer, null);
//		newsListView.addFooterView(footView);  //<-这句一定要放在setAdapter之前
		
		data = new ArrayList<String>();
		data.add("查看");
		data.add("修改");
		data.add("删除");
		addList();
	}

	public void addList() {
		try {
			scheduleList = myMessageDao.getMyMessageList(0);
		} catch (DataException e) {
			e.printStackTrace();
		} 
		newsListView.setAdapter(new MyAdapter(this, scheduleList));
		newsListView.setOnItemLongClickListener(longClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		addList();
	}
	
	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				final int index, long arg3) {
			ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.my_schedule_oper_dialog, data);
			final MyMessage sendGPSTask = scheduleList.get(index);
			AlertDialog dialog = new AlertDialog.Builder(ScheduleManageActivity.this)
        	.setTitle(sendGPSTask.getName())
        	.setAdapter(adapter, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.putExtra("SCHEDULETASK", sendGPSTask);
					switch(which) {
					case 0: // 查看
						Intent intent1 = new Intent(getApplicationContext(), InputFiltTelNumActivity.class);
						startActivityForResult(intent1,0);
						break;
					case 1:  // 修改
						Intent intent2 = new Intent(getApplicationContext(), ImportFiltNumActivity.class);
						startActivityForResult(intent2,1);
						break;
					case 2:// 删除
						AlertDialog dialog2 = new AlertDialog.Builder(ScheduleManageActivity.this)
			        	.setTitle("确认删除？")
				        .setMessage("删除日程：" + sendGPSTask.getName())//设置内容
				        .setPositiveButton("确定",//设置确定按钮
			                new DialogInterface.OnClickListener() {//设置事件监听
			                    public void onClick(DialogInterface dialog, int whichButton) {
			                    	String taskId = sendGPSTask.getPhone();
			                    	try {
										myMessageDao.deleteFiltNum(taskId);
										scheduleList.remove(index);
										
									} catch (DataException e) {
										e.printStackTrace();
									} // 删除delflag = 1 的任务
//			                    	onResume();
			                    }
			                }).setNegativeButton("取消", //设置“取消”按钮
			                new DialogInterface.OnClickListener() {
			                    public void onClick(DialogInterface dialog, int whichButton) {
			                        //点击"取消"按钮之后什么也不做
			               }
				    }).create();//创建按钮
				    // 显示对话框
				    dialog2.show();
						break;
					default:
						break;
					}
				}
			})
            .create();//创建
			dialog.show();
			return false;
		}
	};
	

	class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;   
	    
		// 保存所有文章信息的列表   
	    private List<MyMessage> scheduleList;   
	  
		MyAdapter(Context context, List<MyMessage> scheduleList) {
			// 参数初始化    
	        mInflater = LayoutInflater.from(context);   
	        this.scheduleList = scheduleList;   
		}
		
		public int getCount() {
			return scheduleList.size();
		}

		public MyMessage getItem(int position) {
			  return scheduleList.get(position);   
		}

		public long getItemId(int position) {
			return position;   
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			MyMessage sendGPSTask = null;
			holder = new ViewHolder();   
			sendGPSTask = getItem(position);
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.my_schedule_list, null);  
				holder.title = ((TextView)convertView.findViewById(R.id.title));
				holder.description = (TextView)convertView.findViewById(R.id.description);
				holder.icon = (ImageView) convertView.findViewById(R.id.news_icon);   
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(sendGPSTask.getName());
			holder.description.setText(sendGPSTask.getPhone());
			convertView.setTag(holder);  
			return convertView;   
		}
	}
	
	static class ViewHolder{
		
		public TextView title;// 标题
		
		public TextView description;// 摘要
		
		public ImageView icon;// 缩略图

	}
}

