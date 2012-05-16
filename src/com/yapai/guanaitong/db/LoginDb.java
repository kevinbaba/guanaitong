package com.yapai.guanaitong.db;

import com.yapai.guanaitong.MyApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LoginDb {
	private final String TAG="LoginDb";

	static LoginDb instanc;
	private SQLiteDatabase mdb;
	
	public final static String KEY="_id";
	public final static String ACCOUNTS="accounts";
	public final static String PASSWORD="pass";
		
	public final static String TABLE_LOGIN_NAME="login";
//	注意创建表的SQL语句应该是：create table tableName ();所以要注意加空格，还有表名不能为table
	public final static String TABLE_LOGIN_CREATE_SQL=
			" create table if not exists "+TABLE_LOGIN_NAME
			+" ( "
			+KEY+" integer primary key autoincrement, "
			+ACCOUNTS+" text not null, "
			+PASSWORD+" text not null "
			+" ); ";
	
	public LoginDb(Context mContext) {
		super();
		mdb=MyApplication.mdbHelper.getWritableDatabase();
	}
	
	public static LoginDb getDBInstanc(Context context) {
		if (instanc == null) {
			instanc = new LoginDb(context);
		}
		return instanc;
	}
	
	public long insert(String accounts,String pass){
		ContentValues values=new ContentValues();
		values.put(ACCOUNTS, accounts);
		values.put(PASSWORD, pass);
		return mdb.insert(TABLE_LOGIN_NAME, null,values);
	}
	
//	删除对应id的所有记录
	public boolean delete(int id){
		return mdb.delete(TABLE_LOGIN_NAME, KEY+"="+id, null)>0;
	}
	
	public Cursor getCursor(String... args){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, args, null, null, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor getCursorArgs(String[] args,String[]selection){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, args, ACCOUNTS+"=?", selection, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public boolean update(int id,String password){
		ContentValues values=new ContentValues();
		values.put(PASSWORD,password);
		return mdb.update(TABLE_LOGIN_NAME, values, KEY+"="+id, null)>0;
	}

}
