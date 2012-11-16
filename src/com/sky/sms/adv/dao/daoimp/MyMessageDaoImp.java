package com.sky.sms.adv.dao.daoimp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import com.sky.sms.adv.dao.MyMessageDao;
import com.sky.sms.adv.domain.DataException;
import com.sky.sms.adv.domain.FiltInfo;
import com.sky.sms.adv.domain.MyMessage;
import com.sky.sms.adv.main.R;
import com.sky.sms.adv.utils.Constants;
import com.sky.sms.adv.utils.DBHelperUtil;
import com.sky.sms.adv.utils.StringUtil;

public class MyMessageDaoImp implements MyMessageDao {

	private Context context = null;
	
	private SQLiteDatabase db = null;
	
	public MyMessageDaoImp(Context context) {
		this.context = context;
	}
	
	public List<MyMessage> getMyMessageList(int offSet) throws DataException {
		db = DBHelperUtil.getDb(context);
		List<MyMessage> myMessageList = new LinkedList<MyMessage>();
		Cursor cursor = null;
		try {
			cursor = db.query(Constants.MESSAGE_TABLE, new String[]{"id", "phone",  "content", "time", "copyflag", "delflag"}, null, null, null, null, null, null);//
			cursor.moveToFirst();
			MyMessage myMessage = null;
			while(!cursor.isAfterLast()){
				myMessage = new MyMessage();
				myMessage.setId(cursor.getLong(0));
				String phoneNum = cursor.getString(1);
				myMessage.setPhone(phoneNum);
				String name = getName(context, phoneNum);
				if("".equals(name)) {
					name = context.getResources().getString(R.string.contacts_name_isnot_exists_str);
				}
				myMessage.setName(name);
				myMessage.setContent(StringUtil.decodeUnicode(cursor.getString(2)));
				myMessage.setReceive_time(cursor.getLong(3));
				myMessage.setSyncFlag(cursor.getInt(4));
				myMessage.setDelFlag(cursor.getInt(5));
				myMessageList.add(myMessage);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			System.out.println("getMyMessageList= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return myMessageList;
	}
	
	@Override
	public List<MyMessage> getMyMessageListByID(long id) throws DataException {
		db = DBHelperUtil.getDb(context);
		List<MyMessage> myMessageList = new LinkedList<MyMessage>();
		Cursor cursor = null;
		try {
			cursor = db.query(Constants.MESSAGE_TABLE, new String[]{"id", "phone", "content", "time", "copyflag", "delflag"}, "id>" + id, null, null, null, null, null);//
			cursor.moveToFirst();
			MyMessage myMessage = null;
			while(!cursor.isAfterLast()){
				myMessage = new MyMessage();
				myMessage.setId(cursor.getLong(0));
				String phoneNum = cursor.getString(1);
				myMessage.setPhone(phoneNum);
				String name = getName(context, phoneNum);
				if("".equals(name)) {
					name = context.getResources().getString(R.string.contacts_name_isnot_exists_str);
				}
				myMessage.setName(name);
				myMessage.setContent(StringUtil.decodeUnicode(cursor.getString(2)));
				myMessage.setReceive_time(cursor.getLong(3));
				myMessage.setSyncFlag(cursor.getInt(4));
				myMessage.setDelFlag(cursor.getInt(5));
				myMessageList.add(myMessage);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			System.out.println("getMyMessageListByID= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return myMessageList;
	}
	
	// 根据电话号码查询用户名字
	public String getName(Context context, String phoneNumber){
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		String[] projection1 = new String[]{PhoneLookup.DISPLAY_NAME};
		Cursor cursor1 = context.getContentResolver().query(uri, projection1, null,null,null);
		if (cursor1.moveToFirst()) {
			return cursor1.getString(cursor1.getColumnIndex(PhoneLookup.DISPLAY_NAME));
//			int phoneNameIndex = cursor1.getColumnIndex(PhoneLookup.DISPLAY_NAME);
//		     String phoneNameStr = cursor1.getString(phoneNameIndex);
//		     System.out.println("name="+ name + " phoneNumIndex=" + phoneNameIndex + " phoneNameStr=" + phoneNameStr);
//			return name;
	}
	return "";
}
	
	@Override
	public int getTotalSmsNumber() throws DataException {
		db = DBHelperUtil.getDb(context);
		int id = 0;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select count(*) from " + Constants.MESSAGE_TABLE, null);
			cursor.moveToFirst();
			id = cursor.getInt(0);
		} catch (Exception e) {
			System.out.println("getTotalSmsNumber= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return id;
	}

	
	@Override
	public void addMyMessage(MyMessage myMessage) throws DataException {
		db = DBHelperUtil.getDb(context);
//		Toast.makeText(context, myMessage.getContent(), Toast.LENGTH_SHORT).show();
		String sql = "insert into " + Constants.MESSAGE_TABLE + " (phone, content, time, copyflag, delflag) values('"+myMessage.getPhone()+"', '"+StringUtil.toUnicode(myMessage.getContent())+"', '"+System.currentTimeMillis()+"', 0, 0)";;
//		System.out.println("SQL" + sql);
		try {
			db.execSQL(sql);
		} catch (Exception e){
			System.out.println("addMyMessage= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		}
	}

	@Override
	public void delMyMessage(long id) throws DataException {
		db = DBHelperUtil.getDb(context);
		String sql = "delete from " + Constants.MESSAGE_TABLE + " where id = " + id;
		try {
			db.execSQL(sql);
		} catch (Exception e){
			System.out.println("delMyMessage= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		}
	}
	
	
	@Override
	public void delAllMyMessage(List<MyMessage> myMessageList) throws DataException {
		for(MyMessage myMessage:myMessageList) {
			try {
				delMyMessage(myMessage.getId());
			} catch (Exception e) {
				System.out.println("delAllMyMessage= " + e.getMessage());
				DBHelperUtil.closeDb(db);
				throw new DataException(e.getMessage());
			}
		}
	}
	
	
	@Override
	public void updateSyncedFlag(List<MyMessage> myMessageList)
			throws DataException {
		db = DBHelperUtil.getDb(context);
		for(MyMessage myMessage:myMessageList) {
			long id = myMessage.getId();
			String sql = "update " + Constants.MESSAGE_TABLE + " set copyflag = 1 where id = " + id;
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				System.out.println("updateSyncedFlag= " + e.getMessage());
				DBHelperUtil.closeDb(db);
				throw new DataException(e.getMessage());
			}
		}
	}

	@Override
	public void addFiltNum(String oldNum, String newNum) throws DataException {
		if(isFiltNum(oldNum)) {
			db = DBHelperUtil.getDb(context);
			String sql = "update " + Constants.MESSAGE_FILT + " set num = '" +newNum + "' where num = " + oldNum;
			try {
				db.execSQL(sql);
			} catch (Exception e){
				System.out.println("update= " + e.getMessage());
				DBHelperUtil.closeDb(db);
				throw new DataException(e.getMessage());
			}
		} else {
			updateFiltNum(newNum);
		}
	}
	
	@Override
	public boolean isFiltNum(String num) throws DataException {
		db = DBHelperUtil.getDb(context);
		Cursor cursor = null;
		try {//  SELECT num FROM black_filt WHERE num1 = 9176465888
			cursor = db.query(Constants.MESSAGE_FILT, new String[]{"num"}, "num = " + num, null, null, null, null, null);//
			cursor.moveToFirst();
			if(!cursor.isAfterLast()){
				return true;
			}
		} catch (Exception e) {
			System.out.println("isFiltNum= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return false;
	}

	@Override
	public List<FiltInfo> getFiltNum() throws DataException {
		List<FiltInfo> filtNumList = new ArrayList<FiltInfo>();
		db = DBHelperUtil.getDb(context);
		Cursor cursor = null;
		try {
			cursor = db.query(Constants.MESSAGE_FILT, new String[]{"num"}, null, null, null, null, null, null);//
			cursor.moveToFirst();
			FiltInfo filtInfo = null;
			while(!cursor.isAfterLast()){
				String num = cursor.getString(0);
				String name = getName(context, num);
				if("".equals(name)) {
					name = context.getResources().getString(R.string.contacts_name_isnot_exists_str);
				}
				filtInfo = new FiltInfo(num, name);
				filtNumList.add(filtInfo);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			System.out.println("getFiltNum= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return filtNumList;
	}

	@Override
	public void updateFiltNum(String num) throws DataException {
		db = DBHelperUtil.getDb(context);
		ContentValues values = new ContentValues();
		values.put("num",  num);
		try {
			db.replace(Constants.MESSAGE_FILT, null, values);
		} catch (SQLException e) {
			System.out.println("updateFiltNum= " + e.getMessage());
		} 
	}

	@Override
	public void deleteFiltNum(String num) throws DataException {
		db = DBHelperUtil.getDb(context);
		String sql = "delete from " + Constants.MESSAGE_FILT + " where num = " + num;
		try {
			db.execSQL(sql);
		} catch (Exception e){
			System.out.println("deleteFiltNum= " + e.getMessage());
			DBHelperUtil.closeDb(db);
			throw new DataException(e.getMessage());
		}
	}
	
}
