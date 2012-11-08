package com.sky.sms.ym.adv.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sky.sms.ym.adv.dao.MyMessageDao;
import com.sky.sms.ym.adv.domain.DataException;
import com.sky.sms.ym.adv.domain.FiltInfo;
import com.sky.sms.ym.adv.utils.DaoFactory;

public class ModifySmsFiltNumActivity extends Activity implements OnClickListener{
	
	private Button modifyOkButton = null;

	private Button modifyCancleButton = null;
	
	private EditText newInputTelNum = null;
	
	private MyMessageDao myMessageDao = null;
	
	private String oldNum = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_filt_tel_num);
		myMessageDao = DaoFactory.getMessageDao(this);
		modifyOkButton = (Button) findViewById(R.id.modifyOkButton);
		modifyCancleButton = (Button) findViewById(R.id.modifyCancleButton);
		newInputTelNum = (EditText) findViewById(R.id.modify_filt_num);
		Intent intent = getIntent();
		FiltInfo filtInfo = (FiltInfo) intent.getSerializableExtra("FILT_NUM");
		if(filtInfo != null) {
			oldNum = filtInfo.getNum();
			newInputTelNum.setText(oldNum);
		}
		modifyOkButton.setOnClickListener(this);
		modifyCancleButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.modifyOkButton:
			String newNum = newInputTelNum.getText().toString();
			if(newNum == null || "".equals(newNum)) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_filt_num_is_null), Toast.LENGTH_SHORT).show();
				return;
			} else {
				try {
					myMessageDao.addFiltNum(oldNum,newNum);
					this.finish();
				} catch (DataException e) {
					System.out.println("InputFiltTelNumActivity add new filt num " + e.getMessage());
				}
			}
			break;
		case R.id.modifyCancleButton:
			this.finish();
			break;
		default :
			break;
		}
	}
	
}
