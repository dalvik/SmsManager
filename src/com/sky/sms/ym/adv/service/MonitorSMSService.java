package com.sky.sms.ym.adv.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.sky.sms.ym.adv.main.LoginActivity;
import com.sky.sms.ym.adv.main.R;

public class MonitorSMSService extends Service{

	private NotificationManager notificationManager;   

	private PendingIntent pendIntent = null; 

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);   
		pendIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);   
		Notification notification = new Notification(); 
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.icon = R.drawable.icon; // 设置在状态栏显示的图标
	    notification.tickerText = getResources().getString(R.string.server_starting); // 设置在状态栏显示的内容   
	    // 设置通知显示的参数   
	    notification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), getResources().getString(R.string.server_running), pendIntent);  
	    notificationManager.notify(0, notification); // 执行通知.
//	    ContentResolver contentResolver = getContentResolver();// Context 环境下getContentResolver() 
//		Handler handler = new SMSHandler(); 
//		ContentObserver m_SMSObserver = new SMSObserver(handler); 
//		contentResolver.registerContentObserver(Uri.parse("content://sms"),true, m_SMSObserver); 
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//把基本类功能性地应用起来 
		//Register to observe SMS in outbox,we can observe SMS in other location by changing Uri string, such as inbox, sent, draft, outbox, etc.) 

		// some Available Uri string  for sms. 
		/*  
		  String strUriInbox = "content://sms/inbox";//SMS_INBOX:1 
		  String strUriFailed = "content://sms/failed";//SMS_FAILED:2 
		  String strUriQueued = "content://sms/queued";//SMS_QUEUED:3 
		  String strUriSent = "content://sms/sent";//SMS_SENT:4 
		  String strUriDraft = "content://sms/draft";//SMS_DRAFT:5 
		  String strUriOutbox = "content://sms/outbox";//SMS_OUTBOX:6 
		  String strUriUndelivered = "content://sms/undelivered";//SMS_UNDELIVERED 
		  String strUriAll = "content://sms/all";//SMS_ALL 
		  String strUriConversations = "content://sms/conversations";//you can delete one conversation by thread_id 
		  String strUriAll = "content://sms"//you can delete one message by _id 
		*/ 
	}
	
	class SMSObserver extends ContentObserver 
	{ 
	    private Handler m_handle = null; 

	    public SMSObserver(Handler handle) 
	    { 
	        super(handle); 
	        m_handle = handle; 
	    } 

	    public void onChange(boolean bSelfChange) 
	    { 
	        super.onChange(bSelfChange); 
	        //Send message to Activity 
	        Message msg = new Message(); 
	        msg.obj = "xxxxxxxxxx"; 
	        m_handle.sendMessage(msg); 
			String strUriInbox = "content://sms/inbox"; 
			Uri uriSms = Uri.parse(strUriInbox);  //If you want to access all SMS, just replace the uri string to "content://sms/" 
			Cursor c = getApplication().getContentResolver().query(uriSms, null, null, null, null); 
			// delete all sms here when every new sms occures. 
			while (c.moveToNext()) { 
		       //Read the contents of the SMS; 
		       for(int i =0; i < c.getColumnCount(); i++)  { 
		            String strColumnName = c.getColumnName(i); 
		            String strColumnValue = c.getString(i); 
		            System.out.println(strColumnName + "  " + strColumnValue);
		       } 
		       String uri = "content://sms"; 
		       getApplication().getContentResolver().delete(Uri.parse(uri), null, null);        
		   } 
	    } 
	} 
}
