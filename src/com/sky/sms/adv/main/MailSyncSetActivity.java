package com.sky.sms.adv.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.sky.sms.adv.utils.StringUtil;

public class MailSyncSetActivity extends Activity {

	private Spinner securityTypeSpinner = null;
	
	private SharedPreferences settings = null;
	
	private EditText smtpServerText = null;
	
	private EditText smtpServerPort = null;
	
	private EditText smtpSendUser = null;
	
	private EditText smtpPassword = null;
	
	private ImageButton nextSetpImageButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail_sync_set);
		settings = getSharedPreferences("PERSONAL_SET", 0);
		String smtpServer = settings.getString("smtp_server", "");
		//设置SMTP Server
		smtpServerText = (EditText) findViewById(R.id.smtpServer);
		smtpServerText.setText(smtpServer);
		// 设置端口
		smtpServerPort = (EditText) findViewById(R.id.sendEmailPort);
		String smtpPort = settings.getString("smtp_port", "");
		smtpServerPort.setText(smtpPort);
		//  设置securitytype
		securityTypeSpinner = (Spinner) findViewById(R.id.securityType);
		int defaultSelect = settings.getInt("security_type_default_select", 0);
		securityTypeSpinner.setSelection(defaultSelect);
		// 发送账号
		smtpSendUser = (EditText) findViewById(R.id.sendEmailUserName);
		String sendUserName = settings.getString("send_email_username", "");
		smtpSendUser.setText(sendUserName);
		// 发送密码
		smtpPassword = (EditText) findViewById(R.id.sendUserPassword);
		String sendUserPassword = settings.getString("send_email_password", "");
		smtpPassword.setText(sendUserPassword);
		
		// 下一步 按钮
		nextSetpImageButton = (ImageButton) findViewById(R.id.validServerButton);
		nextSetpImageButton.setOnClickListener(nextButtonOnclickListener);
	}
	
	private OnClickListener nextButtonOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String smtpServer = smtpServerText.getText().toString();
			if(smtpServer == null || "".equals(smtpServer)) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_email_server_isnull), Toast.LENGTH_LONG).show();
				return;
			} 
			String smtpPort = smtpServerPort.getText().toString();
			if(smtpPort == null || "".equals(smtpPort)) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_email_port_isnull), Toast.LENGTH_LONG).show();
				return;
			} 
//			String securityType = securityTypeSpinner.getSelectedItem().toString();
			int index = securityTypeSpinner.getSelectedItemPosition();
			
			String smtpSendUserName = smtpSendUser.getText().toString();
			if(smtpSendUserName == null || "".equals(smtpSendUserName)) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_email_isnull), Toast.LENGTH_LONG).show();
				return;
			} 
			if(!StringUtil.isEmail(smtpSendUserName)) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_isnot_email), Toast.LENGTH_LONG).show();
				return;
			}
			String smtpSendPassword = smtpPassword.getText().toString();
			settings.edit().putString("smtp_server", smtpServer).putString("smtp_port", smtpPort).putInt("security_type_default_select", index).putString("send_email_username", smtpSendUserName).putString("send_email_password", smtpSendPassword).commit();
			Intent intent = new Intent();
			intent.setClass(MailSyncSetActivity.this, EmailConnectTestActivity.class);
			startActivityForResult(intent, 0);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == RESULT_OK) {
			this.finish();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		settings.edit().putString("smtp_server", smtpServerText.getText().toString()).putString("smtp_port", smtpServerPort.getText().toString())
		.putInt("security_type_default_select", securityTypeSpinner.getSelectedItemPosition()).putString("send_email_username", smtpSendUser.getText().toString())
		.putString("send_email_password", smtpPassword.getText().toString()).commit();
	}
	
}
