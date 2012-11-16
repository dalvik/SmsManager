package com.sky.sms.adv.main;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

// 用户登陆界面
public class LoginActivity extends Activity {

	private SharedPreferences settings = null;
	
//	private ProgressDialog m_Dialog = null;
	
	private AlertDialog dlg = null;
	
	private String TAG = "LoginActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences("PERSONAL_SET", 0);
//		Intent i = new Intent(getApplicationContext(), ListViewForLoading.class);
//		startService(i);
		boolean isFirstLogin = settings.getBoolean("IS_FIRST_LOGIN", true);
		if(isFirstLogin) {
			firstLogin();
		} else {
			secondLogin();
		}
	}
	
	// 第一次登陆需要用户设置密码
	public void firstLogin() {
        LayoutInflater factory = LayoutInflater.from(LoginActivity.this);
        //得到自定义对话框
        final View MyDialogView = factory.inflate(R.layout.first_login_dialog, null);
        //创建对话框
        dlg = new AlertDialog.Builder(LoginActivity.this).setTitle(getResources().getString(R.string.login_dialog_str))
        .setIcon(R.drawable.login_icon)
        .setView(MyDialogView)//设置自定义对话框的样式
        .setPositiveButton(getResources().getString(R.string.login__str), //设置"确定"按钮
        new DialogInterface.OnClickListener() {//设置事件监听
            public void onClick(DialogInterface dialog, int whichButton) {
            	  final EditText userName = (EditText) MyDialogView.findViewById(R.id.firstPassword);
            	  final EditText password = (EditText) MyDialogView.findViewById(R.id.secondPassword);
            	  String name = userName.getText().toString().trim();
                  String pwd = password.getText().toString().trim();
                  if(name== null || pwd == null || name.length()<=0 || pwd.length()<=0) {
                	  Toast.makeText(LoginActivity.this, getResources().getString(R.string.password_is_null), Toast.LENGTH_LONG).show();
                	try {
                	    Field field  =  dlg.getClass() .getSuperclass().getDeclaredField("mShowing");
                	    field.setAccessible( true );
                	    // 将mShowing变量设为false，表示对话框已关闭 
                	    field.set(dialog, false);
                	    dialog.dismiss();
                	} catch  (Exception e) {
                		Log.v(TAG, e.getMessage());
                	}
                  } else if(!name.equalsIgnoreCase(pwd)) {
                	  	Toast.makeText(LoginActivity.this, getResources().getString(R.string.password_not_equal), Toast.LENGTH_LONG).show();
                    	try {
                    	    Field field  =  dlg.getClass() .getSuperclass().getDeclaredField("mShowing");
                    	    field.setAccessible( true );
                    	    // 将mShowing变量设为false，表示对话框已关闭 
                    	    field.set(dialog, false);
                    	    dialog.dismiss();
                    	} catch  (Exception e) {
                    		Log.v(TAG, e.getMessage());
                    	}
                 } else {
	          		settings.edit().putString("PASSWORD1", name).putString("PASSWORD2", pwd).putBoolean("IS_FIRST_LOGIN", false).commit();
	                startParaSet(1);
	                try {
                	    Field field  =  dlg.getClass() .getSuperclass().getDeclaredField("mShowing");
                	    field.setAccessible( true );
                	    // 将mShowing变量设为false，表示对话框已关闭 
                	    field.set(dialog, true);
                	    dialog.dismiss();
                		Toast.makeText(LoginActivity.this, getResources().getString(R.string.password_set_ok), Toast.LENGTH_LONG).show();
                	} catch  (Exception e) {
                		Log.v(TAG, e.getMessage());
                	}
                 }
            }
        }).setNegativeButton(getResources().getString(R.string.cancle_login__str), //设置“取消”按钮
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //点击"取消"按钮之后退出程序
            	 try {
             	    Field field  =  dlg.getClass() .getSuperclass().getDeclaredField("mShowing");
             	    field.setAccessible( true );
             	    // 将mShowing变量设为false，表示对话框已关闭 
             	    field.set(dialog, true);
             	    dialog.dismiss();
             	} catch  (Exception e) {
             		Log.v(TAG, e.getMessage());
             	}
            	LoginActivity.this.finish();
            }
        })
        .create();//创建
        dlg.show();//显示
	}

	// 登陆 页面 转向
	public void secondLogin() {
		Dialog dialog = new AlertDialog.Builder(LoginActivity.this)
		.setIcon(R.drawable.login_icon)
        .setTitle(getResources().getString(R.string.login__notify_str))//设置标题
        .setCancelable(false)
        .setMessage(getResources().getString(R.string.login__notify_message_str))//设置内容
        .setPositiveButton(getResources().getString(R.string.login__str),//设置确定按钮
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //点击“确定”转向登陆框
                LayoutInflater factory = LayoutInflater.from(LoginActivity.this);
                //得到自定义对话框
                final View MyDialogView = factory.inflate(R.layout.second_login_dialog, null);
                //创建对话框
                AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).setTitle(getResources().getString(R.string.login_dialog_str))
                .setView(MyDialogView)//设置自定义对话框的样式
                .setIcon(R.drawable.login_icon)
                .setPositiveButton(getResources().getString(R.string.login__str), //设置"确定"按钮
                new DialogInterface.OnClickListener() {//设置事件监听
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	//输入完成后，点击“确定”开始登陆
                    	final ProgressDialog m_Dialog = new ProgressDialog(LoginActivity.this);
//                    	m_Dialog.setIcon(R.drawable.login_icon);
                    	m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    	m_Dialog.setCancelable(false);
//                    	m_Dialog.setTitle(getResources().getString(R.string.login_title_str));
                    	m_Dialog.setMessage(getResources().getString(R.string.login_message));
                    	m_Dialog.setIndeterminate(false);
                    	m_Dialog.show();
                        new Thread() {
                          public void run() {
                          	final EditText password = (EditText) MyDialogView.findViewById(R.id.password);
                          	String pwd = password.getText().toString().trim();
                          	int loginResult = 0;
                            try {
                            	loginResult = toLogin(pwd);
                                sleep(800);
                              } catch (Exception e) {
                                e.printStackTrace();
                              }  finally {
                                  //登录结束，取消m_Dialog对话框
                                  m_Dialog.dismiss();
                              }
                              // 登录结束， 负责页面跳转
                              startParaSet(loginResult);
                          }
                        }.start();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancle_login__str), //设置“取消”按钮
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //点击"取消"按钮之后退出程序
                    	LoginActivity.this.finish();
                    }
                })
                .create();//创建
                dlg.show();//显示
            }
        }).setNeutralButton(getResources().getString(R.string.cancle_login__str),
        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton)  {
	            //点击"退出"按钮之后推出程序
	        	LoginActivity.this.finish();
	        }
	}).create();//创建按钮
	// 显示对话框
	    dialog.show();
	}
	
	private void startParaSet(int result) {
		Intent intent = new Intent();
		if(result == 1) {
			intent.setClass(LoginActivity.this, SmsManagerMainActivity.class);
		} else {
			intent.setClass(LoginActivity.this, ErrorLoginActivity.class);
		}
		startActivity(intent);
		finish();
	}
	
	private int toLogin(String password) {
		int i = 0;
		String pwd = settings.getString("PASSWORD1", "");
		if(password.equalsIgnoreCase(pwd)) {
			i = 1; // 管理员
		} 
		return i;
	}
}
