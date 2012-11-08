package com.sky.sms.ym.adv.main;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.sky.sms.ym.adv.utils.StringUtil;

public class EmaiReceiveSetActivity extends Activity {

	private EditText receiveAddress = null;

	private SharedPreferences settings = null;

	private String receiveEmailAddress = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail_sync_receiver_set);
		settings = getSharedPreferences("PERSONAL_SET", 0);
		receiveAddress = (EditText) findViewById(R.id.mailSyncReceiver);
		receiveEmailAddress = settings.getString("receive_email_username", "");
		receiveAddress.setText(receiveEmailAddress);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		String email = receiveAddress.getText().toString();
		if(!"".equals(email) && !email.equals(receiveEmailAddress)) {
			if(StringUtil.isEmail(email)) {
				settings.edit().putString("receive_email_username", email).commit();
			} else {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.receive_mail_address_error_str), Toast.LENGTH_LONG).show();
			}
		}
	}
}
