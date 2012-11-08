package com.sky.sms.ym.adv.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sky.sms.ym.adv.dao.MyMessageDao;
import com.sky.sms.ym.adv.domain.DataException;
import com.sky.sms.ym.adv.utils.DaoFactory;

public class InputFiltTelNumActivity extends Activity implements OnClickListener{

	private Button saveButton = null;
	
	private Button cancleButton = null;
	
	private EditText inputTeiNum = null;
	
	private MyMessageDao myMessageDao = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_filt_tel_num);
		myMessageDao = DaoFactory.getMessageDao(this);
		saveButton = (Button) findViewById(R.id.okButton);
		cancleButton = (Button) findViewById(R.id.cancleButton);
		inputTeiNum = (EditText) findViewById(R.id.user_input_filt_num);
		saveButton.setOnClickListener(this);
		cancleButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.okButton:
			String num = inputTeiNum.getText().toString();
			if(num == null || "".equals(num)) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_filt_num_is_null), Toast.LENGTH_SHORT).show();
				return;
			} else {
				try {
					myMessageDao.addFiltNum(num, num);
					setResult(RESULT_OK);
					this.finish();
				} catch (DataException e) {
					System.out.println("InputFiltTelNumActivity add new filt num " + e.getMessage());
				}
			}
			break;
		case R.id.cancleButton:
			this.finish();
			break;
		default :
			break;
		}
	}
	
}
