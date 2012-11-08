package com.sky.sms.ym.adv.main;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.sky.sms.ym.adv.dao.MyMessageDao;
import com.sky.sms.ym.adv.domain.DataException;
import com.sky.sms.ym.adv.domain.MyMessage;
import com.sky.sms.ym.adv.engine.UpdateManager;
import com.sky.sms.ym.adv.service.SendEmailThread;
import com.sky.sms.ym.adv.utils.DaoFactory;
import com.sky.sms.ym.adv.utils.DateUtil;
import com.sky.sms.ym.adv.utils.MyAdapter;
import com.sky.sms.ym.adv.utils.StringUtil;

public class SmsManagerMainActivity extends ListActivity implements  OnCreateContextMenuListener {

	private Context context = null;
	
	private MyMessageDao myMessageDao = null;

	private List<MyMessage> myMessageList = null;

	private PopupWindow popup;

	private ListView listView;

	private GridView toolbarGrid, mGridView, mTitleGridView;

	private LinearLayout mLayout;

	private TextView title1, title2, title3;

	private int titleIndex;

	private ViewFlipper mViewFlipper;

	/*-- Toolbar底部菜单选项下标--*/
	private final int TOOLBAR_ITEM_SELETE_ALL = 0;// 全部选择

	private final int TOOLBAR_ITEM_COPY = 1;// 备份

	private final int TOOLBAR_ITEM_DELETE = 2;// 删除

	private final int TOOLBAR_ITEM_PREFERENCE = 3;// 首选项

	/*------  首选项菜单  -----*/
	    /*--- 个人设置菜单     ---*/
	private final int SYSTEMFILTSET = 0; // 过滤设置
	
	private final int AUTORUNSET = 1; // 随机启动
	
	private final int RESTPASSWOR = 2; // 密码重置
	
	private final int SMSRELOAD = 3; // 系统刷新

	/*--- 同步设置菜单     ---*/
	private final int EMAILSYNCSET = 0;// 邮件服务器设置
	
	private final int RECEIVEEMAILSET = 1;// 邮件接收设置
	
	private final int SYSTEMHELP = 2; // 帮助
	
	private final int SYSTEMEXIT = 3;// 系统退出
	
	/** 底部菜单图片 **/     //message_select_all_icon
	private int[] menu_toolbar_image_array = { R.drawable.message_select_all_icon, R.drawable.message_backup_dia, R.drawable.message_del_icon,	R.drawable.system_preference_icon };

	/** 底部菜单文字 **/

	private int i = 0;

	private AlertDialog messageOperSureDialog = null;

	private SharedPreferences settings = null;

	private MyAdapter myListViewAdapter = null;

	private AlertDialog dlg = null;

	private Timer updateUITimer = null;

	private int smsSelectedIndex = 0;
	
	private MyMessage myMessage = null;
	
	private TimerTask updateUITask = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 4;
			smsSyncHandler.sendMessage(message);
		}
	};
	
	private String TAG = "SmsManagerMainActivity";

	// 短信删除提示信息句柄
	public Handler smsSyncHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case 1: // 成功
				myListViewAdapter.notifyDataSetChanged();
				Toast.makeText(SmsManagerMainActivity.this,getResources().getString(R.string.sms_sync_success_message_str), Toast.LENGTH_SHORT).show();
				break;
			case 2:// 失败
				 Toast.makeText(SmsManagerMainActivity.this, getResources().getString(R.string.sms_sync_error_message_str), Toast.LENGTH_SHORT).show();
				break;
			case 3:// 全部耍新界面
				try {
					synchronized (context) {
						myMessageList = myMessageDao.getMyMessageList(0);
					}
				} catch (DataException e) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.system_run_error), Toast.LENGTH_SHORT).show();
					final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);    
				    am.restartPackage(getPackageName()); 
					return;
				}
				listView.setAdapter(myListViewAdapter);
				myListViewAdapter.notifyDataSetChanged();
				break;
			case 4:// 部分耍新界面
				try {
					synchronized (context) {
						List<MyMessage> myMessageListTemp = myMessageDao.getMyMessageListByID(myListViewAdapter.getId());
						if(myMessageListTemp.size()>0) {
							if(myMessageList != null && myMessageList.size()>0){
								myMessageList.addAll(myMessageListTemp);
								myListViewAdapter.notifyDataSetChanged();
							}  else {
								myMessageList = new LinkedList<MyMessage>();
								myMessageList.addAll(myMessageListTemp);
								myListViewAdapter = new MyAdapter(getApplicationContext(),myMessageList);
								listView.setAdapter(myListViewAdapter);
								myListViewAdapter.notifyDataSetChanged();
							}
						}
					}
				} catch (DataException e) {
					Log.e(TAG, "partly refresh " + e.getMessage());
				}
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		context = this;
		//06-15 15:17:01.271: ERROR/AndroidRuntime(387): java.lang.RuntimeException: Unable to start activity ComponentInfo{com.sky.sms.adv.main/com.waps.OffersWebView}: android.content.res.Resources$NotFoundException: Resource ID #0x0

		settings = getSharedPreferences("PERSONAL_SET", 0);
		myMessageDao = DaoFactory.getMessageDao(context);
		if(updateUITimer == null) {
			updateUITimer = new Timer();
			updateUITimer.scheduleAtFixedRate(updateUITask, DateUtil.formatTime(System.currentTimeMillis() + 20000l), 10 * 1000);
		}
		listView = (ListView)getListView();
		initPopupMenu();// 初始化Popup Menu菜单
		// 创建底部菜单 Toolbar
		toolbarGrid = (GridView) findViewById(R.id.gridView_toolbar);
		toolbarGrid.setSelector(R.drawable.menu_selected_pic);
		toolbarGrid.setBackgroundResource(R.drawable.menu_bg2);// 设置背景
		toolbarGrid.setNumColumns(4);// 设置每行列数
		toolbarGrid.setGravity(Gravity.CENTER);// 位置居中
		toolbarGrid.setVerticalSpacing(10);// 垂直间隔
		toolbarGrid.setHorizontalSpacing(10);// 水平间隔
		String[] menu_toolbar_name_array = { getResources().getString(R.string.bottom_select_menu_str), getResources().getString(R.string.bottom_backup_menu_str), getResources().getString(R.string.bottom_delete_menu_str), getResources().getString(R.string.bottom_preference_menu_str) };
		toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array, menu_toolbar_image_array));// 设置菜单Adapter
		/** 监听底部菜单选项 **/
		toolbarGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case TOOLBAR_ITEM_SELETE_ALL: // 全选 或者反选
					selectAllOrNot(i++);
					break;
				case TOOLBAR_ITEM_COPY: // 备份
					final List<MyMessage> copyMessageList = getSelectBackupMessage(myMessageList);
					if (copyMessageList.size() <= 0) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_select_backup_sms_str), Toast.LENGTH_SHORT).show();
						break;/// return or break;
					} else {
						boolean checked = settings.getBoolean("email_server_checked", false);
						if(!checked) {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_server_not_checked_str), Toast.LENGTH_SHORT).show();
							break;
						}
						messageOperSureDialog = new AlertDialog.Builder(SmsManagerMainActivity.this)
						.setIcon(R.drawable.message_backup_dia)
						.setTitle(getResources().getString(R.string.sms_backup_sure_dialog_title_str))
						.setMessage(getResources().getString(R.string.sms_backup_dialog_message_str))
						.setPositiveButton(getResources().getString(R.string.sure_sms_backup_str), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										boolean checked = settings.getBoolean("email_server_checked", false);
										if(!checked) {
											Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_server_not_checked_str), Toast.LENGTH_SHORT).show();
											return;
										}
										// 点击"确定"
										startSycnSMS(copyMessageList);
										messageOperSureDialog.dismiss();
									}
								})
						.setNegativeButton(getResources().getString(R.string.cancle_sms_backup_str), // 设置“取消”按钮
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int whichButton) {
										// 点击"取消"按钮之后退出程序
										messageOperSureDialog.dismiss();
									}
								}).create();
				messageOperSureDialog.show();
					}
					break;
				case TOOLBAR_ITEM_DELETE: // 删除
					final List<MyMessage> delMessageList = getSelectDeleteMessage(myMessageList);
					if (delMessageList.size() <= 0) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.sms_select_delete_null), Toast.LENGTH_SHORT).show();
					} else {
						new AlertDialog.Builder(SmsManagerMainActivity.this)
						.setIcon(R.drawable.message_to_del_icon)
						.setTitle(getResources().getString(R.string.sms_delete_confirm))
						.setMessage(getResources().getString(R.string.sms_delete_really))
						.setPositiveButton(getResources().getString(R.string.sms_delete_yes),
								new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int whichButton) {
								// 点击删除短信
								delAllSelectMessage(myMessageList);
							}
						}).setNegativeButton(getResources().getString(R.string.sms_delete_cancle), // 设置“取消”按钮
								new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
							}
						}).show();
					}
					break;
				case TOOLBAR_ITEM_PREFERENCE: // 首选项
					if (popup != null) {
						if (popup.isShowing()) {
							popup.dismiss();
						} else {
							popup.showAtLocation(listView, Gravity.BOTTOM, 0, 60);
							mViewFlipper.startFlipping(); // 播放动画
						}
					}
					break;
				}
			}
		});
		listView.setAdapter(myListViewAdapter);
		listView.setOnItemLongClickListener(listItemLongClick);
		listView.setOnCreateContextMenuListener(this);
		//检查新版本
        UpdateManager.getUpdateManager().checkAppUpdate(this, false);
       
	}
	
	/**
	 * 创建Popup Menu菜单
	 */
	private void initPopupMenu() {
		// 创建动画
		mViewFlipper = new ViewFlipper(this);
		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.menu_in));
		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.menu_out));
		mLayout = new LinearLayout(SmsManagerMainActivity.this);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		// 标题选项栏
		mTitleGridView = new GridView(SmsManagerMainActivity.this);
		mTitleGridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		mTitleGridView.setSelector(R.color.alpha_00);
		mTitleGridView.setNumColumns(2);
		mTitleGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mTitleGridView.setVerticalSpacing(1);
		mTitleGridView.setHorizontalSpacing(1);
		mTitleGridView.setGravity(Gravity.CENTER);
		MenuTitleAdapter mta = new MenuTitleAdapter(this, new String[] {getResources().getString(R.string.person_setting_str), getResources().getString(R.string.sync_setting_str) }, 16, 0xFFFFFFFF);
		mTitleGridView.setAdapter(mta);
		mTitleGridView.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View view,
					int index, long arg3) {
				onChangeItem(view, index);
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		mTitleGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				onChangeItem(view, index);
			}
		});

		// 子选项栏
		mGridView = new GridView(SmsManagerMainActivity.this);
		mGridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		mGridView.setSelector(R.drawable.menu_selected_pic);
		mGridView.setNumColumns(4);
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mGridView.setVerticalSpacing(10);
		mGridView.setHorizontalSpacing(10);
		mGridView.setPadding(10, 10, 10, 10);
		mGridView.setGravity(Gravity.CENTER);
		mGridView.setAdapter(getMenuAdapter(new String[] { getResources().getString(R.string.system_filt_set), getResources().getString(R.string.auto_start), getResources().getString(R.string.password_reset), getResources().getString(R.string.sms_sycn_refresh)}, new int[] { R.drawable.system_filt_set_icon, R.drawable.start_auto_icon, R.drawable.password_reset_icon, R.drawable.sms_sync_refresh_icon}));
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				switch (titleIndex) {
				case 0:
					// 密码重置
					if (index == RESTPASSWOR) {
						popup.dismiss();
						passwordReset();
					}
					// 开机启动
					else if (index == AUTORUNSET) {
						popup.dismiss();
						final CharSequence[] upSendItems = { getResources().getString(R.string.not_allow_auto_start), getResources().getString(R.string.allow_auto_start) };
						final int upSendRate = settings.getInt("AUTO_RUN_FLAG", 0);
						AlertDialog dialog = new AlertDialog.Builder(
						SmsManagerMainActivity.this)
						.setIcon(R.drawable.start_auto_icon)
						.setTitle(getResources().getString(R.string.auto_start))
						.setSingleChoiceItems(upSendItems, upSendRate,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int item) {
										settings.edit().putInt("AUTO_RUN_FLAG",item).commit();
										dialog.dismiss();
									}
								}).setNegativeButton("取消", // 设置“取消”按钮
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int whichButton) {
										// 点击"取消"选择
										dialog.dismiss();
									}
								}).create();
						dialog.show();
					} else if (index == SYSTEMFILTSET) { // 过滤设置 
						    Intent intent = new Intent(getApplicationContext(), SmsFiltSetActivity.class);
							startActivity(intent);
							popup.dismiss();
						} else if (index == SMSRELOAD) {// 刷新
							System.out.println("fresh//////");
							try {
								myMessageList = myMessageDao.getMyMessageList(0);
								myListViewAdapter = new MyAdapter(getApplicationContext(),myMessageList);
								listView.setAdapter(myListViewAdapter);
							} catch (DataException e) {
								Log.e(TAG, "refresh " + e.getMessage());
							}
							popup.dismiss();
						}
					break;
				case 1:
					if (index == EMAILSYNCSET) {// 邮箱设置
						Intent intent = new Intent(SmsManagerMainActivity.this, MailSyncSetActivity.class);
						startActivity(intent);
						popup.dismiss();
					} else if (index == RECEIVEEMAILSET) {// 接收设置
						Intent intent = new Intent(SmsManagerMainActivity.this, EmaiReceiveSetActivity.class);
						startActivity(intent);
						popup.dismiss();
					}
					// 帮助 使用说明
					else if (index == SYSTEMHELP) {
						popup.dismiss();
						Intent intent = new Intent(SmsManagerMainActivity.this, SMSManagerUseGuide.class);
						startActivity(intent);
						/*
						//点击“确定”转向登陆框
		                LayoutInflater factory = LayoutInflater.from(SmsManagerMainActivity.this);
		                //得到自定义对话框
		                final View MyDialogView = factory.inflate(R.layout.download_notify, null);
		                //创建对话框
		                new AlertDialog.Builder(SmsManagerMainActivity.this)
		                .setTitle(getResources().getString(R.string.down_load_title))
		                .setCancelable(false)
		                .setView(MyDialogView)//设置自定义对话框的样式
		                .setPositiveButton(getResources().getString(R.string.down_load_button),  
		                new DialogInterface.OnClickListener() {//设置事件监听
		                    public void onClick(DialogInterface dialog, int whichButton) {
//								Intent intent = new Intent(SmsManagerMainActivity.this, SMSManagerUseGuide.class);
//								startActivity(intent);
		                    }
		                }).create().show();//显示
		                */
					}
					// 退出
					else if (index == SYSTEMEXIT) {
						popup.dismiss();
						systemExit();
					}
					break;
				}
			}
		});
		mLayout.addView(mTitleGridView);
		mLayout.addView(mGridView);
		mViewFlipper.addView(mLayout);
		mViewFlipper.setFlipInterval(60000);
		// 创建Popup
		popup = new PopupWindow(mViewFlipper, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		popup.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_bg));// 设置menu菜单背景
		popup.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
		popup.update();
		// 设置默认项
		title1 = (TextView) mTitleGridView.getItemAtPosition(0);
		title1.setBackgroundColor(0x00);
	}

	
	/**
	 * 改变选中后效果
	 * @param arg1 item对象
	 * @param arg2 item下标
	 */
	private void onChangeItem(View arg1, int arg2) {
		titleIndex = arg2;
		switch (titleIndex) {
		case 0:
			title1 = (TextView) arg1;
			title1.setBackgroundColor(0x00);
			if (title2 != null) {
				title2.setBackgroundResource(R.drawable.toolbar_menu_release);
			}
			if (title3 != null) {
				title3.setBackgroundResource(R.drawable.toolbar_menu_release);
			}
			mGridView.setAdapter(getMenuAdapter(new String[] { getResources().getString(R.string.system_filt_set), getResources().getString(R.string.auto_start), getResources().getString(R.string.password_reset), getResources().getString(R.string.sms_sycn_refresh)}, new int[] { R.drawable.system_filt_set_icon,	R.drawable.start_auto_icon, R.drawable.password_reset_icon, R.drawable.sms_sync_refresh_icon}));
			break;
		case 1:
			title2 = (TextView) arg1;
			title2.setBackgroundColor(0x00);
			if (title1 != null) {
				title1.setBackgroundResource(R.drawable.toolbar_menu_release);
			}
			if (title3 != null) {
				title3.setBackgroundResource(R.drawable.toolbar_menu_release);
			}
			mGridView.setAdapter(getMenuAdapter(new String[] {getResources().getString(R.string.mail_set_str),getResources().getString(R.string.receive_mail_set), getResources().getString(R.string.system_help_str), getResources().getString(R.string.system_exit_str)}, new int[] {R.drawable.mail_set_icon, R.drawable.receive_mail_set_icon, R.drawable.system_help_icon, R.drawable.system_exit_icon}));
			break;
		case 2:
			title3 = (TextView) arg1;
			title3.setBackgroundColor(0x00);
			if (title2 != null)
				title2.setBackgroundResource(R.drawable.toolbar_menu_release);
			if (title1 != null)
				title1.setBackgroundResource(R.drawable.toolbar_menu_release);
			break;
		}
	}


	/**
	 * 构造邮箱同步菜单Adapter
	 * @param menuNameArray 名称
	 * @param imageResourceArray 图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,R.layout.menu_item, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	
	/**
	 * 定义长按事件监听
	 */
	public OnItemLongClickListener listItemLongClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int index, long arg3) {
			smsSelectedIndex = index;
			return false;
		}
	};

	// 删除选中的全部短信
	public void delAllSelectMessage(List<MyMessage> messageList) {
		int i = 0;
		for (Iterator<MyMessage> iterator = messageList.iterator(); iterator.hasNext();) {
			MyMessage myMessage = iterator.next();
			if(myMessage.isChecked()) {
				try {
					myMessageDao.delMyMessage(myMessage.getId());
					iterator.remove();
					i++;
				} catch (DataException e) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_del_fail_str), Toast.LENGTH_SHORT).show();
				}
			}
		}
		myListViewAdapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), i + " " + getResources().getString(R.string.message_del_sucess_str), Toast.LENGTH_SHORT).show();
	}

	// 全选或者反选
	public void selectAllOrNot(int i) {
		int length = listView.getChildCount();
		if(i%2 ==0) {
			for(int j = 0; j < length; j++){
	            View view = listView.getChildAt(j);
	            CheckBox cb = (CheckBox)view.findViewById(R.id.message_checkbox);
	            cb.setChecked(true);
		    }
		}else {
			for(int j = 0; j < length; j++){
	            View view = listView.getChildAt(j);
	            CheckBox cb = (CheckBox)view.findViewById(R.id.message_checkbox);
	            cb.setChecked(false);
		    }
		}
	}
	
	// 删除选中的一个短信
	public void delSelectMessage(long id, int index) throws DataException {
		myMessageDao.delMyMessage(id);
		myMessageList.remove(index);
		myListViewAdapter.notifyDataSetChanged();
	}

	// 返回被选择的短信
	public ArrayList<MyMessage> getSelectDeleteMessage(List<MyMessage> messageList) {
		ArrayList<MyMessage> myMessageList = new ArrayList<MyMessage>();
		for (MyMessage message : messageList) {
			if (message.isChecked()) {
				myMessageList.add(message);
			}
		}
		return myMessageList;
	}
	
	// 返回被选择的短信
	public ArrayList<MyMessage> getSelectBackupMessage(List<MyMessage> messageList) {
		ArrayList<MyMessage> myMessageList = new ArrayList<MyMessage>();
		for (MyMessage message : messageList) {
			if (message.isChecked()) {
				message.setSyncFlag(1);
				myMessageList.add(message);
			}
		}
		return myMessageList;
	}

	// 同步短信进度条
	public void startSycnSMS(final List<MyMessage> myMessageList) {
		//点击“确定”开始同步
		final ProgressDialog m_Dialog = new ProgressDialog(SmsManagerMainActivity.this);
    	m_Dialog.setIcon(R.drawable.sms_sync_icon);
    	m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	m_Dialog.setTitle(getResources().getString(R.string.sms_sync_dialog_title_str));
    	m_Dialog.setMessage(getResources().getString(R.string.sms_sync_dialog_message_str));
    	m_Dialog.setIndeterminate(false);
    	m_Dialog.show();
        new Thread() {
          public void run() {
            try {
            	String content = StringUtil.getSMSToString(myMessageList);
            	SendEmailThread sendEmailThread = new SendEmailThread(context);
            	sendEmailThread.sendEmail(content);
            	myMessageDao.updateSyncedFlag(myMessageList);
            	Message message = new Message();
                message.what = 1;
                smsSyncHandler.sendMessage(message);
                System.out.println("success");
              } catch (Exception e) {
                Log.v(TAG, e.getMessage());
                Message message = new Message();
                message.what = 2;
                smsSyncHandler.sendMessage(message);
               }  finally {
                  m_Dialog.dismiss();
              }
            }
        }.start();
	}
	
	// 用户密码重置
	public void passwordReset() {
		LayoutInflater factory = LayoutInflater.from(SmsManagerMainActivity.this);
		// 得到自定义对话框
		final View MyDialogView = factory.inflate(R.layout.first_login_dialog, null);
		// 创建对话框
		dlg = new AlertDialog.Builder(SmsManagerMainActivity.this)
		.setTitle(getResources().getString(R.string.password_reset))
		.setIcon(R.drawable.login_icon)
		.setView(MyDialogView)// 设置自定义对话框的样式
		.setPositiveButton(getResources().getString(R.string.password_ret_confirm_str), // 设置"确定"按钮
		new DialogInterface.OnClickListener() {// 设置事件监听
			public void onClick(DialogInterface dialog,
					int whichButton) {
				final EditText userName = (EditText) MyDialogView
						.findViewById(R.id.firstPassword);
				final EditText password = (EditText) MyDialogView
						.findViewById(R.id.secondPassword);
				String name = userName.getText().toString()
						.trim();
				String pwd = password.getText().toString()
						.trim();
				if (name == null || pwd == null
						|| name.length() <= 0
						|| pwd.length() <= 0) {
					Toast.makeText(
							SmsManagerMainActivity.this,
							getResources().getString(
									R.string.password_is_null),
							Toast.LENGTH_SHORT).show();
					try {
						Field field = dlg.getClass()
								.getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						// 将mShowing变量设为false，表示对话框已关闭
						field.set(dialog, false);
						dialog.dismiss();
					} catch (Exception e) {
						Log.v(TAG, e.getMessage());
					}
				} else if (!name.equalsIgnoreCase(pwd)) {
					Toast.makeText(
							SmsManagerMainActivity.this,
							getResources().getString(R.string.password_not_equal),
							Toast.LENGTH_SHORT).show();
					try {
						Field field = dlg.getClass()
								.getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						// 将mShowing变量设为false，表示对话框已关闭
						field.set(dialog, false);
						dialog.dismiss();
					} catch (Exception e) {
						Log.v(TAG, e.getMessage());
					}
				} else {
					settings.edit().putString("PASSWORD1", name).putString("PASSWORD2", pwd).putBoolean("IS_FIRST_LOGIN", false).commit();
					try {
						Field field = dlg.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						// 将mShowing变量设为false，表示对话框已关闭 true
						// 表示未关闭
						Toast.makeText(SmsManagerMainActivity.this,getResources().getString(R.string.password_reset_ok),
								Toast.LENGTH_SHORT).show();
						field.set(dialog, true);
						dialog.dismiss();
					} catch (Exception e) {
						Log.v(TAG, e.getMessage());
					}
				}
			}
		}).setNegativeButton(getResources().getString(R.string.password_ret_cancle_str), // 设置“取消”按钮
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int whichButton) {
				// 点击"取消"按钮之后退出程序
				try {
					Field field = dlg.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true);
					// 将mShowing变量设为false，表示对话框已关闭
					field.set(dialog, true);
					dialog.dismiss();
				} catch (Exception e) {
					Log.v(TAG, e.getMessage());
				}
			}
		}).create();// 创建
		dlg.show();// 显示
	}

	public void systemExit() {
		AlertDialog systemExitSureDialog = new AlertDialog.Builder(SmsManagerMainActivity.this)
		.setIcon(R.drawable.system_exit_icon)
		.setTitle(getResources().getString(R.string.exit_system_dialog_confirm_str))
		.setMessage(getResources().getString(R.string.exit_system_confirm_str))
		.setPositiveButton(getResources().getString(R.string.confirm_exit_system_str), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);    
					    am.restartPackage(getPackageName()); 
						// 点击"确定"
						messageOperSureDialog.dismiss();
					}
				})
		.setNegativeButton(getResources().getString(R.string.cancle_exit_system_str), // 设置“取消”按钮
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int whichButton) {
						// 点击"取消"按钮之后退出程序
					}
				}).create();
		systemExitSureDialog.show();
	}
	
	// 恢复短信
	public void recoverSMSToInbox(MyMessage myMessage){
		ContentValues values = new ContentValues();
		values.put("address",myMessage.getPhone());
		values.put("type", "2");
		values.put("read", "1");
		values.put("body",myMessage.getContent());
		values.put("date", DateUtil.formatyyMMDDHHmmss(myMessage.getReceive_time()));
		getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		myMessage = myMessageList.get(smsSelectedIndex);
		menu.setHeaderIcon(R.drawable.message_security_icon);
		menu.setHeaderTitle(getResources().getString(R.string.sms_sender_str) + myMessage.getName());
		menu.add(0, 0, 0, getResources().getString(R.string.message_copy));
		menu.add(0, 1, 1, getResources().getString(R.string.message_read));
		menu.add(0, 2, 2, getResources().getString(R.string.message_recovery));
		menu.add(0, 3, 3, getResources().getString(R.string.message_del));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 0: // 备份
			messageOperSureDialog = new AlertDialog.Builder(SmsManagerMainActivity.this)
			.setIcon(R.drawable.message_backup_dia)
			.setTitle(getResources().getString(R.string.sms_backup_sure_dialog_title_str))
			.setMessage(getResources().getString(R.string.sms_backup_dialog_message_str))
			.setPositiveButton(getResources().getString(R.string.sure_sms_backup_str), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							boolean checked = settings.getBoolean("email_server_checked", false);
							if(!checked) {
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_server_not_checked_str), Toast.LENGTH_SHORT).show();
								return;
							}
							// 点击"确定"
							List<MyMessage> myMessageList = new ArrayList<MyMessage>();
							myMessage.setSyncFlag(1);
							myMessageList.add(myMessage);
							startSycnSMS(myMessageList);
							messageOperSureDialog.dismiss();
						}
					})
			.setNegativeButton(getResources().getString(R.string.cancle_sms_backup_str), // 设置“取消”按钮
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int whichButton) {
							// 点击"取消"按钮之后退出程序
							messageOperSureDialog.dismiss();
						}
					}).create();
			messageOperSureDialog.show();
			break;
		case 3: // 删除
			messageOperSureDialog = new AlertDialog.Builder(
					SmsManagerMainActivity.this)
					.setIcon(R.drawable.message_to_del_icon)
					.setTitle(getResources().getString(R.string.sms_delete_confirm))
					.setMessage(getResources().getString(R.string.sms_delete_really))
					.setPositiveButton(getResources().getString(R.string.sms_delete_yes),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {
									try {
										delSelectMessage(myMessage.getId(), smsSelectedIndex);
										Toast.makeText(getApplicationContext(), 1 + " " + getResources().getString(R.string.message_del_sucess_str), Toast.LENGTH_SHORT).show();
									} catch (DataException e) {
										Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_del_fail_str), Toast.LENGTH_SHORT).show();
									} finally {
										messageOperSureDialog.dismiss();
									}
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.sms_delete_cancle), // 设置“取消”按钮
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {
									// 点击"取消"按钮之后退出程序
									messageOperSureDialog
											.dismiss();
								}
							}).create();
			messageOperSureDialog.show();
			break;
		case 1:// 查看
			messageOperSureDialog = new AlertDialog.Builder(
					SmsManagerMainActivity.this)
					.setIcon(R.drawable.message_read_icon)
					.setTitle(getResources().getString(R.string.sms_detail_dialog_title_str))
					.setMessage(getResources().getString(R.string.sms_sender_str)
									+ myMessage.getName()
									+ "\n"
									+ getResources().getString(R.string.select_backup_sms_come_from_str)
									+ myMessage.getPhone()
									+ "\n"
									+ getResources().getString(R.string.sms_send_time_str)
									+ DateUtil.formatyyMMDDHHmmss(myMessage.getReceive_time()) + "\n"
									+ getResources().getString(R.string.sms_send_content_str)
									+ myMessage.getContent())
					.setPositiveButton(getResources().getString(R.string.sms_reader_confirm_str),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {
									// 点击"取消"选择
									messageOperSureDialog.dismiss();
								}
							}).create();
			messageOperSureDialog.show();
			break;
		case 2://恢复至收件箱
			messageOperSureDialog = new AlertDialog.Builder(
			SmsManagerMainActivity.this)
			.setIcon(R.drawable.message_recovery_dia)
			.setTitle(getResources().getString(R.string.message_recovery_dialog_confirm_title_str))
			.setMessage(getResources().getString(R.string.message_recovery_confirm_message_str))
			.setPositiveButton(getResources().getString(R.string.message_recovery_confirm_str),
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int whichButton) {
							try {
								delSelectMessage(myMessage.getId(), smsSelectedIndex);
								recoverSMSToInbox(myMessage);
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_recovery_success_str), Toast.LENGTH_SHORT).show();
							} catch (DataException e) {
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_recovery_error_str), Toast.LENGTH_SHORT).show();
							} finally {
								messageOperSureDialog.dismiss();
							}
						}
					})
			.setNegativeButton(
					getResources().getString(R.string.message_recovery_cancle_str), // 设置“取消”按钮
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int whichButton) {
							// 点击"取消"按钮之后退出程序
							messageOperSureDialog
									.dismiss();
						}
					}).create();
			messageOperSureDialog.show();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			myMessageDao = DaoFactory.getMessageDao(this);
			myMessageList = myMessageDao.getMyMessageList(0);
			myListViewAdapter = new MyAdapter(getApplicationContext(),myMessageList);
			listView.setAdapter(myListViewAdapter);
		} catch (DataException e) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.system_run_error), Toast.LENGTH_SHORT).show();
			final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);    
		    am.restartPackage(getPackageName()); 
			return;
		}
	}
	
	
	/**
	 * 自定义Adapter，下面的菜单
	 * 
	 */
	public class MenuTitleAdapter extends BaseAdapter {
		private Context mContext;
		private int fontColor;
		private TextView[] title;

		/**
		 * 构建菜单项
		 * @param context上下文
		 * @param titles标题
		 * @param fontSize字体大小
		 * @param color字体颜色
		 */
		public MenuTitleAdapter(Context context, String[] titles, int fontSize,
				int color) {
			this.mContext = context;
			this.fontColor = color;
			this.title = new TextView[titles.length];
			for (int i = 0; i < titles.length; i++) {
				title[i] = new TextView(mContext);
				title[i].setText(titles[i]);
				title[i].setTextSize(fontSize);
				title[i].setTextColor(fontColor);
				title[i].setGravity(Gravity.CENTER);
				title[i].setPadding(10, 10, 10, 10);
				title[i].setBackgroundResource(R.drawable.toolbar_menu_release);
			}
		}

		public int getCount() {
			return title.length;
		}

		public Object getItem(int position) {

			return title[position];
		}

		public long getItemId(int position) {

			return title[position].getId();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = title[position];
			} else {
				v = convertView;
			}
			return v;
		}

	}
 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(updateUITimer != null) {
			updateUITimer.cancel();
			updateUITimer = null;
		}
	}
	
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
	
	class UpdateUIThread extends Thread {
		
		private int notifyType;
		
		public UpdateUIThread(int notifyType) {
			this.notifyType = notifyType;
		}
		
		public void run() {
			 Message message = new Message();
             message.what = notifyType;
             smsSyncHandler.sendMessage(message);
		}
	}
}