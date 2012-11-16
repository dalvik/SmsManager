package com.sky.sms.adv.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelperUtil extends SQLiteOpenHelper {

	private static ThreadLocal<SQLiteDatabase> dbLocal = new ThreadLocal<SQLiteDatabase>();

	private static final int DATABASE_VERSION = 1;

	private static DBHelperUtil dbUtil = null;

	// 创建GPS数据表
	private static final String CREATE_MESSAGE_CREATE = "create table "
			+ Constants.MESSAGE_TABLE
			+ "( id INTEGER PRIMARY KEY AUTOINCREMENT, phone text, content text, time REAL, copyflag INTEGER, delflag INTEGER)";

	private static final String CREARE_MEASSAGE_FILT = "create table " + Constants.MESSAGE_FILT + " (num text)";
	
	public static synchronized SQLiteDatabase getDb(Context context) {
		SQLiteDatabase db = dbLocal.get();
		if (db == null || !db.isOpen()) {
			if (dbUtil == null) {
				dbUtil = new DBHelperUtil(context);
			}
			db = dbUtil.getWritableDatabase();
			dbLocal.set(db);
		}
		return db;
	}

	public DBHelperUtil(Context context) {
		super(context, Constants.MESSAGE_DB, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_MESSAGE_CREATE);
			db.execSQL(CREARE_MEASSAGE_FILT);
//			db.execSQL("insert into "
//					+ Constants.MESSAGE_TABLE
//					+ "(id, phone,  content, time, copyflag, delflag)values(1, '11111111111', '"+StringUtil.toUnicode("message content")+"', '"
//					+ System.currentTimeMillis() + "', 0, 0)");
//			db.execSQL("insert into "
//					+ Constants.MESSAGE_TABLE
//					+ "(id, phone,  content, time, copyflag, delflag)values(2, '22222222222', '"+StringUtil.toUnicode("message content")+"', '"
//					+ System.currentTimeMillis() + "', 0, 0)");
//			db.execSQL("insert into "
//					+ Constants.MESSAGE_TABLE
//					+ "(id, phone,  content, time, copyflag, delflag)values(3, '33333333333',  '"+StringUtil.toUnicode("message content")+"', '"
//					+ System.currentTimeMillis() + "', 0, 0)");
//			db.execSQL("insert into "
//					+ Constants.MESSAGE_TABLE
//					+ "(id, phone,  content, time, copyflag, delflag)values(4, '44444444444',  '"+StringUtil.toUnicode("message content")+"', '"
//					+ System.currentTimeMillis() + "', 0, 0)");
			// db.execSQL("insert into " + Constants.MESSAGE_TABLE +
			// "(id, phone, name, content, time, copyflag, delflag)values(5, '55555555555', 'name5', 'message content', '"+System.currentTimeMillis()+"', 0, 0)");
			// db.execSQL("insert into " + Constants.MESSAGE_TABLE +
			// "(id, phone, name, content, time, copyflag, delflag)values(6, '66666666666', 'name6', 'message content', '"+System.currentTimeMillis()+"', 0, 0)");
			// db.execSQL("insert into " + Constants.MESSAGE_TABLE +
			// "(id, phone, name, content, time, copyflag, delflag)values(7, '77777777777', 'name7', 'message content', '"+System.currentTimeMillis()+"', 0, 0)");
			// db.execSQL("insert into " + Constants.MESSAGE_TABLE +
			// "(id, phone, name, content, time, copyflag, delflag)values(8, '88888888888', 'name8', 'message content', '"+System.currentTimeMillis()+"', 0, 0)");
			// db.execSQL("insert into " + Constants.MESSAGE_TABLE +
			// "(id, phone, name, content, time, copyflag, delflag)values(9, '99999999999', 'name9', 'message content', '"+System.currentTimeMillis()+"', 0, 0)");
			// db.execSQL("insert into " + Constants.MESSAGE_TABLE +
			// "(id, phone, name, content, time, copyflag, delflag)values(10, '10000000000', 'name10', 'message content', '"+System.currentTimeMillis()+"', 0, 0)");
			// db.execSQL("insert into " + Constants.MESSAGE_TABLE +
			// "(id, phone, name, content, time, copyflag, delflag)values(11, '20000000000', 'name11', 'message content', '"+System.currentTimeMillis()+"', 0, 0)");
		} catch (SQLException e) {
			Log.v(Constants.TAG_EXCEPTION,
					"DBHelperUtil onCreate " + e.toString() + "-=-=-=--");
		}
	}

	public static void closeDb(SQLiteDatabase db){
		if(db != null) {
			db.close();
			db = null;
		}
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Constants.MESSAGE_DB);
		onCreate(db);
	}

}
