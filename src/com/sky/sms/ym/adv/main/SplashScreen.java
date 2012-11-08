package com.sky.sms.ym.adv.main;

import net.youmi.android.AdManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.sky.sms.ym.adv.service.MonitorSMSService;


public class SplashScreen extends Activity {

	private Thread mSplashThread;

	static {
		AdManager.init("427e7f99106039d8", "d58cb9c556103e01", 30, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
//		AdView adView = new AdView(this, Color.GRAY, Color.WHITE, 100);
//		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//		params.gravity = Gravity.CENTER_VERTICAL;
//		addContentView(adView, params);
		
//		AdView adView = new AdView(this); 
//		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);		
//		addContentView.addView(adView, params); 
		mSplashThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(3000);
					}
				} catch (InterruptedException ex) {

				}
				Intent intent = new Intent();
				intent.setClass(SplashScreen.this, LoginActivity.class);
				startActivity(intent);
				Intent intent2 = new Intent(getApplicationContext(), MonitorSMSService.class);
				startService(intent2);
				finish();
			}
		};
		mSplashThread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		if (evt.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (mSplashThread) {
				mSplashThread.notifyAll();
			}
		}
		return true;
	}
}
