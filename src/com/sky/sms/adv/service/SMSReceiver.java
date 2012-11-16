package com.sky.sms.adv.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.sky.sms.adv.dao.MyMessageDao;
import com.sky.sms.adv.domain.DataException;
import com.sky.sms.adv.domain.MyMessage;
import com.sky.sms.adv.utils.DaoFactory;

// 短信过滤
public class SMSReceiver extends BroadcastReceiver {

	private MyMessageDao myMessageDao = null;
	
	private String TAG = "SMSReceiver";
	
	private static final String strAction = "android.provider.Telephony.SMS_RECEIVED";   
	  
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(strAction)) {
				Bundle bundle = intent.getExtras();
				if (bundle == null) {
					return;
				}
				// 获取收到的信息
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] msg = new SmsMessage[pdus.length];
				//读取收件箱中的短信
				for (int i = 0; i < pdus.length; i++) {
					msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				String num = msg[0].getDisplayOriginatingAddress();
	            // 2、再根据电话号码查出联系人名称
				myMessageDao = DaoFactory.getMessageDao(context);
				try {
					boolean flag =  myMessageDao.isFiltNum(num);
					if(flag) {
						abortBroadcast();
						// 3、再删除短信
						long threadId = getThreadId(context);   
			            Uri mUri=Uri.parse("content://sms/conversations/" + threadId);  
			            context.getContentResolver().delete(mUri, null, null);
			            // 4、再保存数据库
						MyMessage myMessage = new MyMessage(num, msg[0].getDisplayMessageBody());
						myMessageDao.addMyMessage(myMessage);
					} 
				} catch (DataException e) {
					Log.v(TAG, e.getMessage());
				}
		  }
	}
	


	private long getThreadId(Context context) {  
        long threadId = 0;  
        String SMS_READ_COLUMN = "read";  
	    String WHERE_CONDITION = SMS_READ_COLUMN + " = 0";  
	    String SORT_ORDER = "date DESC";
	    int count = 0;
	    Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[] { "thread_id", "address", "person" }, WHERE_CONDITION, null, SORT_ORDER);  
	    if (cursor != null) {
			try {
				count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					threadId = cursor.getLong(0);
				}
			} finally {
				cursor.close();
			}
		}
	    return threadId;  
	}  

}
