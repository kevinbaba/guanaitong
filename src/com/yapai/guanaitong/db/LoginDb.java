package com.yapai.guanaitong.db;

import com.yapai.guanaitong.application.MyApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LoginDb {
	private final String TAG="LoginDb";

	static LoginDb instanc;
	private SQLiteDatabase mdb;
	
	public final static String ID="id";
	public final static String ACCOUNTS="accounts";
	public final static String PASSWORD="pass";
	public final static String HEAD_PATH = "head_path";
	public final static String HEADER = "header";
	public final static String LOGIN_TIME = "login_time";
		
	public final static String TABLE_LOGIN_NAME="login";
//	注意创建表的SQL语句应该是：create table tableName ();所以要注意加空格，还有表名不能为table
	public final static String TABLE_LOGIN_CREATE_SQL=
			" create table if not exists "+TABLE_LOGIN_NAME
			+" ( "
			+ID+" int primary key , "
			+ACCOUNTS+" text , "
			+PASSWORD+" text , "
			+HEAD_PATH+" text , "
			+HEADER+" text , "
			+LOGIN_TIME+" long "
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
	
	public boolean insert(int id, String accounts,String pass, String headPath, String head){
		Cursor cursor = getCursorByID(null,new String[]{ String.valueOf(id) });
		if(cursor.getCount() > 0){
			return update(id, accounts, pass, headPath, head);
		}
		ContentValues values=new ContentValues();
		values.put(ID, id);
		values.put(ACCOUNTS, accounts);
		values.put(PASSWORD, pass);
		values.put(HEAD_PATH, headPath);
		values.put(HEADER, head);
		return mdb.insert(TABLE_LOGIN_NAME, null,values)>0;
	}
	
//	删除用户的所有记录
	public boolean delete(String account){
		return mdb.delete(TABLE_LOGIN_NAME, ACCOUNTS+"="+account, null)>0;
	}
	
	public Cursor getCursor(String[] columns){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, columns, null, null, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor getCursorByAccount(String[] columns,String[]selectionAccount){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, columns, ACCOUNTS+"=?", selectionAccount, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor getCursorByID(String[] columns,String[]selectionID){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, columns, ID+"=?", selectionID, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor getCursor4LastTime(String[] columns){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, columns, null, null, null, null, LOGIN_TIME+ " desc");
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public boolean update(int id, String account,String password, String headPath, String head){
		ContentValues values=new ContentValues();
		values.put(ID, id);
		values.put(ACCOUNTS, account);
		values.put(PASSWORD, password);
		values.put(HEAD_PATH, headPath);
		values.put(HEADER, head);
		return mdb.update(TABLE_LOGIN_NAME, values, ID+"="+id, null)>0;
	}
	
	public boolean updatePwd(String account,String password){
		ContentValues values=new ContentValues();
		values.put(PASSWORD,password);
		return mdb.update(TABLE_LOGIN_NAME, values, ACCOUNTS+"="+account, null)>0;
	}
	
	public boolean updateHead(int id, String headPath, String head){
		ContentValues values=new ContentValues();
		values.put(HEAD_PATH, headPath);
		values.put(HEADER, head);
		return mdb.update(TABLE_LOGIN_NAME, values, ID+"="+id, null)>0;
	}
	
	public boolean updateLoginTime(String account,long LoginTime){
		ContentValues values=new ContentValues();
		values.put(LOGIN_TIME,LoginTime);
		return mdb.update(TABLE_LOGIN_NAME, values, ACCOUNTS+"="+account, null)>0;
	}
}
