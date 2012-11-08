package com.sky.sms.ym.adv.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.sky.sms.ym.adv.domain.MyMessage;

// 短信阅读界面
public class ReadMessageActivity extends Activity {

	private TextView senderName = null;

	private TextView senderTelNum = null;

	private TextView sendContent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_message);
		Intent intent = getIntent();
		if (intent == null) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.load_error), Toast.LENGTH_LONG).show();
			finish();
		}
		Bundle bundle = intent.getExtras();
		MyMessage myMessage = (MyMessage) bundle.getSerializable("MYMESSGAE");
		senderName = (TextView) findViewById(R.id.messageSender);
		senderTelNum = (TextView) findViewById(R.id.senderTelNum);
		sendContent = (TextView) findViewById(R.id.messageContent);
		senderName.setText(myMessage.getName());
		senderTelNum.setText(myMessage.getPhone());
		sendContent.setText(myMessage.getContent());
//		System.out.println(myMessage.isChecked() + "   " + myMessage.getContent());
		// MyMessage myMessage = (MyMessage)
		// intent.getSerializableExtra("MYMESSGAE");
	}
}
