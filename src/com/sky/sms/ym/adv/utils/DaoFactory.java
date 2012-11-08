package com.sky.sms.ym.adv.utils;

import android.content.Context;

import com.sky.sms.ym.adv.dao.MyMessageDao;
import com.sky.sms.ym.adv.dao.daoimp.MyMessageDaoImp;

public class DaoFactory {

	public static MyMessageDao messageDao = null;

	public synchronized static  MyMessageDao getMessageDao(Context context) {
		if (messageDao == null) {
			messageDao = new MyMessageDaoImp(context);
		}
		return messageDao;
	}
}
