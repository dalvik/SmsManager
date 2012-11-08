package com.sky.sms.ym.adv.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sky.sms.ym.adv.dao.MyMessageDao;
import com.sky.sms.ym.adv.domain.DataException;
import com.sky.sms.ym.adv.domain.FiltInfo;
import com.sky.sms.ym.adv.utils.DaoFactory;

public class ImportFiltNumActivity extends ListActivity implements OnClickListener{

	private ListView listView = null;
	
	private List<FiltInfo> contactsNumList = null; 
		 
	private MyAdapter cantactsTelNumAdapter = null;
	
	private Button agreeImportButton = null;

	private Button cancleImportButton = null;
	
	private MyMessageDao myMessageDao = null;
	
	private String TAG = "ImportFiltNumActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_filt_num_list);
		listView = getListView();
		myMessageDao = DaoFactory.getMessageDao(this);
		agreeImportButton = (Button) findViewById(R.id.agree_import_button);
		cancleImportButton = (Button) findViewById(R.id.cancle_import_button);
		agreeImportButton.setOnClickListener(this);
		cancleImportButton.setOnClickListener(this);
		ContentResolver contentResolver = getContentResolver();
		String orderBy = PhoneLookup.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, orderBy);   
	    cursor.moveToFirst();
	    contactsNumList = new ArrayList<FiltInfo>();
	    FiltInfo filtInfo = null;
	     while(!cursor.isAfterLast()){
		    	// 取得联系人名字 
	            int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);      
	            String name = cursor.getString(nameFieldColumnIndex);    
	         // 取得联系人ID      
	            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));      
	            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,   
	                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
	            phoneCursor.moveToFirst();
	            while(!phoneCursor.isAfterLast()){
	            	String strPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));      
	                filtInfo = new FiltInfo(strPhoneNumber, name);
	                contactsNumList.add(filtInfo);
	                phoneCursor.moveToNext();
	            }
	            if(phoneCursor != null) {
	            	phoneCursor.close();
	            }
				cursor.moveToNext();
			}
	     if(cursor!= null) {
	    	 cursor.close();
	     }
	     cantactsTelNumAdapter = new MyAdapter(getApplicationContext(), contactsNumList);
	     listView.setAdapter(cantactsTelNumAdapter);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.agree_import_button:
			List<FiltInfo>  list = getSelectItem(contactsNumList);
			if(list.size()<= 0) {
				return ;
			}
			try {
				for(FiltInfo filtInfo:list){
					if(filtInfo.isChecked()) {
						String num = filtInfo.getNum().replace("-", "").trim();
						myMessageDao.addFiltNum(num, num);
					}
				}
				setResult(RESULT_OK);
				this.finish();
			} catch (DataException e) {
				Log.e(TAG, "onClick " + e.getMessage());
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error_str) , Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.cancle_import_button:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	public List<FiltInfo> getSelectItem(List<FiltInfo> contactsNumList) {
		List<FiltInfo> tempList  = new ArrayList<FiltInfo>();
		for(FiltInfo filtInfo:contactsNumList){
			if(filtInfo.isChecked()) {
				tempList.add(filtInfo);
			}
		}
		return tempList;
	}
	class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;   
	    
		List<FiltInfo> contactsNumList;
	    
	    public MyAdapter(Context context, List<FiltInfo> contactsNumList) {
	    	// 参数初始化                                                                 
	        mInflater = LayoutInflater.from(context);   
	        this.contactsNumList = contactsNumList;   
	    }
	    
		@Override
		public int getCount() {
			return contactsNumList.size();
		}
		

		@Override
		public FiltInfo getItem(int position) {
			return contactsNumList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			final FiltInfo filtInfo = getItem(position);
			holder = new ViewHolder();   
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.tel_num_list_item, null);  
				holder.checkbox = (CheckBox)convertView.findViewById(R.id.num_checkbox);
				holder.name = (TextView)convertView.findViewById(R.id.name_from_contacts);
				holder.num = ((TextView)convertView.findViewById(R.id.num_from_contacts));
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(filtInfo != null) {
				holder.num.setText(filtInfo.getNum());
				holder.name.setText(filtInfo.getName());
			}
			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						filtInfo.setChecked(isChecked);
				      }
				 });
			convertView.setTag(holder);  
			return convertView;   
		}
	}
	
	static class ViewHolder {
		
		public CheckBox checkbox;// 摘要
		
		public TextView name;// 摘要
		
		public TextView num;// 标题
	}
}
