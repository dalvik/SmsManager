package com.sky.sms.ym.adv.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

// 接收系统重启后的广播
public class SystemRebootReceiver extends BroadcastReceiver {

	private SharedPreferences settings = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		settings = context.getSharedPreferences("PERSONAL_SET", 0);
		if ("android.intent.action.BOOT_COMPLETED".equalsIgnoreCase(intent.getAction())) {
			int flag = settings.getInt("AUTO_RUN_FLAG", 0);
			if (flag == 1) {
				Intent i = new Intent();
				i.setClass(context, MonitorSMSService.class);
				context.startService(i);
			}
		}
	}
}
