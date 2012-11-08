package com.sky.sms.ym.adv.main;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sky.sms.ym.adv.dao.MyMessageDao;
import com.sky.sms.ym.adv.domain.DataException;
import com.sky.sms.ym.adv.domain.FiltInfo;
import com.sky.sms.ym.adv.utils.BlackListMyAdapter;
import com.sky.sms.ym.adv.utils.DaoFactory;

public class SmsFiltSetActivity extends ListActivity{

	private Button manulCreateButton = null;
	
	private Button importButton = null;
	
	private Button deleteButton = null;
	
	private List<FiltInfo> filtNumList =  null;

	private MyMessageDao myMessageDao = null;
	
	private ListView filtNumListView;
	
	private BlackListMyAdapter filtNumAdapter = null;
	
	private int smsFiltNumIndex = 0;
	
	private FiltInfo filtInfo = null; 
		
	private AlertDialog dlg = null;
	
	private String TAG = "SmsFiltSetActivity";
	
	public Handler smsFiltHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case 1: // 成功
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.single_filt_num_delete_success), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_filt_tel_num_list);
		System.out.println("oncreate.....");
		filtNumListView = (ListView)getListView();
		myMessageDao = DaoFactory.getMessageDao(this);
		manulCreateButton = (Button)findViewById(R.id.manual_create_filt_num);
		importButton = (Button) findViewById(R.id.import_filt_form_contact);
		deleteButton = (Button) findViewById(R.id.delete_more_filt_num);
		try {
			filtNumList = myMessageDao.getFiltNum();
		} catch (DataException e) {
			Log.e(TAG, "onCreate " + e.getMessage());
		}
		manulCreateButton.setOnClickListener(clickListener);
		importButton.setOnClickListener(clickListener);
		deleteButton.setOnClickListener(clickListener);
		filtNumAdapter = new BlackListMyAdapter(getApplicationContext(), filtNumList);
		filtNumListView.setAdapter(filtNumAdapter);
		filtNumListView.setOnItemLongClickListener(longClickListener);
		filtNumListView.setOnCreateContextMenuListener(createContextMenuListener);
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.manual_create_filt_num:
				Intent intent = new Intent(getApplicationContext(), InputFiltTelNumActivity.class);
				startActivityForResult(intent,0);
				break;
			case R.id.import_filt_form_contact:
				Intent intent1 = new Intent(getApplicationContext(), ImportFiltNumActivity.class);
				startActivityForResult(intent1,1);
				break;
			case R.id.delete_more_filt_num:
				Intent intent2 = new Intent(getApplicationContext(), DeleteFiltNumFiltActivity.class);
				startActivityForResult(intent2,2);
				break;
			}
		}
	};
	
	
	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			smsFiltNumIndex = position;
			return false;
		}
	};
	
	
	private OnCreateContextMenuListener createContextMenuListener = new OnCreateContextMenuListener() {
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			filtInfo = filtNumList.get(smsFiltNumIndex);
			menu.setHeaderTitle(filtInfo.getName());
			menu.setHeaderIcon(R.drawable.del_filt_num);
			menu.add(0, 0, 0, getResources().getString(R.string.single_filt_num_modify));
			menu.add(0, 1, 1, getResources().getString(R.string.single_filt_num_delete));
		
		}
	};
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 0: // modify
			Intent intent = new Intent(getApplicationContext(), ModifySmsFiltNumActivity.class);
			intent.putExtra("FILT_NUM", filtInfo);
			startActivityForResult(intent, 1);
			break;
		case 1: //delete
			dlg = new AlertDialog.Builder(SmsFiltSetActivity.this)
			.setTitle(getResources().getString(R.string.single_filt_num_delete_confirm_title))
			.setIcon(R.drawable.filt_num_del_icon)
			.setMessage(getResources().getString(R.string.single_filt_num_delete_confirm_message))
			.setPositiveButton(getResources().getString(R.string.single_filt_num_yes_delete),
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int whichButton) {
							try {
								myMessageDao.deleteFiltNum(filtInfo.getNum());
								filtNumAdapter.removeItem(smsFiltNumIndex);
								Message message = new Message();
								message.what = 1;
								smsFiltHandler.sendMessage(message);
							} catch (DataException e) {
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.single_filt_num_delete_error), Toast.LENGTH_SHORT).show();
							} finally {
								dlg.dismiss();
							}
						}
					})
			.setNegativeButton(
					getResources().getString(R.string.single_filt_num_no_delete), // 设置“取消”按钮
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int whichButton) {
							// 点击"取消"按钮之后退出程序
							dlg.dismiss();
						}
					}).create();
			dlg.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println(requestCode + " " + resultCode);
		if(requestCode == 0 || requestCode == 1 || requestCode == 2 && resultCode == RESULT_OK) {
			try {
				filtNumList = myMessageDao.getFiltNum();
				filtNumAdapter = new BlackListMyAdapter(getApplicationContext(), filtNumList);
				filtNumListView.setAdapter(filtNumAdapter);
			} catch (DataException e) {
				Log.e(TAG, "onActivityResult " + e.getMessage());
			}
		}
	}
	
	
	/**
	 * 横屏竖屏切换
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		 switch (newConfig.orientation)  {   
            //更改为LANDSCAPE 
            case (Configuration.ORIENTATION_LANDSCAPE):  
                 //如果转换为横向屏时，有要做的事，请写在这里
                  break;   
            //更改为PORTRAIT
            case (Configuration.ORIENTATION_PORTRAIT):   
                 //如果转换为竖向屏时，有要做的事，请写在这里 
                  break;   
         }
	}
	
}
