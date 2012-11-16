package com.sky.sms.adv.main;

import java.security.Security;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sky.sms.adv.service.JSSEProvider;


// 邮件连通性测试界面
public class EmailConnectTestActivity extends Activity {
	
	private final int CANCLE = 0;
	
	private final int FAIL = 1;
	
	private final int SUCCESS = 2;
	
	private final int ERROR = 3;
	
	private Button connectTestButton = null;
	
	private String TAG = "EmailConnectTestActivity";
	
	private ProgressBar circleProgressBar = null;   
 
	private TextView infoTextView = null;
	
	private SharedPreferences settings = null;

	private Timer testTimer = null;
	
	private Handler progressBarHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case CANCLE: // 取消
				setResult(RESULT_CANCELED);
				infoTextView.setText(getResources().getString(R.string.email_connnect_cancling_str));
				circleProgressBar.setVisibility(View.INVISIBLE);
				break;
			case FAIL: // 失败
				setResult(RESULT_CANCELED);
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_connnect_test_error), Toast.LENGTH_LONG).show();
				infoTextView.setText(getResources().getString(R.string.email_connnect_test_error));
				connectTestButton.setText(getResources().getString(R.string.email_connnect_ok_button_str));
				circleProgressBar.setVisibility(View.INVISIBLE);
				break;
			case SUCCESS: // 完成
				setResult(RESULT_OK);
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_connnect_test_str_success), Toast.LENGTH_SHORT).show();
				infoTextView.setText(getResources().getString(R.string.email_connnect_test_str_success));
				connectTestButton.setText(getResources().getString(R.string.email_connnect_ok_button_str));
				circleProgressBar.setVisibility(View.INVISIBLE);
				break;
			case ERROR: // 错误
				setResult(RESULT_CANCELED);
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_connnect_address_error), Toast.LENGTH_SHORT).show();
				infoTextView.setText(getResources().getString(R.string.email_connnect_test_error));
				connectTestButton.setText(getResources().getString(R.string.email_connnect_ok_button_str));
				circleProgressBar.setVisibility(View.INVISIBLE);
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_connect_test);
		settings = getSharedPreferences("PERSONAL_SET", 0);
		connectTestButton = (Button) findViewById(R.id.connectTestButton);
		circleProgressBar = (ProgressBar) findViewById(R.id.emailConnectTestProgress);
		circleProgressBar.setIndeterminate(false);   
		infoTextView = (TextView) findViewById(R.id.emailConnectTestMessage);
		connectTestButton.setOnClickListener(buttonOnClickListener);
		connectTestButton.setText(getResources().getString(R.string.email_connnect_cancle_button_str));
		testTimer = new Timer();
		testTimer.schedule(task, new Date());
		
	}
	
	private OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String buttonContent = connectTestButton.getText().toString();
			if(buttonContent.equalsIgnoreCase(getResources().getString(R.string.email_connnect_cancle_button_str))) {
				if(testTimer != null) {
					testTimer.cancel();
					testTimer = null;
				}
				EmailConnectTestActivity.this.finish();
			} else if(buttonContent.equalsIgnoreCase(getResources().getString(R.string.email_connnect_ok_button_str))) {
				if(testTimer != null) {
					testTimer.cancel();
					testTimer = null;
				}
				EmailConnectTestActivity.this.finish();
			}
		}
	};

private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			circleProgressBar.setVisibility(View.VISIBLE);
			infoTextView.setText(getResources().getString(R.string.email_connnect_test_str));
			final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"; 
			Security.addProvider(new JSSEProvider());  
			final String userName = settings.getString("send_email_username", "steersgman@gmail.com");//发件地址
			final String password = settings.getString("send_email_password", "skysky12");//发件人密码
			Properties props = System.getProperties();  
			props.put("mail.smtp.connectiontimeout", "10000"); 
			props.put("mail.smtp.timeout", "10000");
			props.setProperty("mail.smtp.host", settings.getString("smtp_server", "smtp.gmail.com")); //smtp.126.com
			props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);  
			props.setProperty("mail.smtp.socketFactory.fallback", "false");  
			props.setProperty("mail.smtp.port", settings.getString("smtp_port", "465"));  //
			props.setProperty("mail.smtp.socketFactory.port", settings.getString("smtp_port", "465"));  //25
			props.put("mail.smtp.auth", "true");

			Session session = Session.getDefaultInstance(props, new Authenticator(){  
				protected PasswordAuthentication getPasswordAuthentication() { 
						return new PasswordAuthentication(userName, password);  
					}
			});  
			MimeMessage msg = new MimeMessage(session);  
			try {
				   msg.setFrom(new InternetAddress(userName));
				   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userName,false));  
				   msg.setSubject("Email Server Test");  
				   msg.setText("Email Server OK!");  
				   msg.setSentDate(new Date());  
				   Transport.send(msg); 
				   // 邮件服务器验证通过
				   settings.edit().putBoolean("email_server_checked", true).commit();
				   android.os.Message message = new android.os.Message();
				   message.what = SUCCESS;
				   message.obj = getResources().getString(R.string.email_connnect_ok_button_str);
				   progressBarHandler.sendMessage(message);
			} catch (AddressException e) {
				Log.v(TAG, e.getMessage());
				settings.edit().putBoolean("email_server_checked", false).commit();
				android.os.Message message = new android.os.Message();
				message.what = FAIL;
				progressBarHandler.sendMessage(message);
			} catch (MessagingException e) {
				Log.v(TAG, e.getMessage());
				settings.edit().putBoolean("email_server_checked", false).commit();
				android.os.Message message = new android.os.Message();
				message.what = ERROR;
				progressBarHandler.sendMessage(message);
			}  
		}
	};
	
}
