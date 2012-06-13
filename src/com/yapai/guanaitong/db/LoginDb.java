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
	
	public final static String ACCOUNTS="accounts";
	public final static String PASSWORD="pass";
	public final static String HEAD_PATH = "head_path";
	public final static String HEADER = "header";
		
	public final static String TABLE_LOGIN_NAME="login";
//	ע�ⴴ�����SQL���Ӧ���ǣ�create table tableName ();����Ҫע��ӿո񣬻��б�������Ϊtable
	public final static String TABLE_LOGIN_CREATE_SQL=
			" create table if not exists "+TABLE_LOGIN_NAME
			+" ( "
			+ACCOUNTS+" text primary key , "
			+PASSWORD+" text , "
			+HEAD_PATH+" text , "
			+HEADER+" text "
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
	
	public long insert(String accounts,String pass, String headPath, String head){
		ContentValues values=new ContentValues();
		values.put(ACCOUNTS, accounts);
		values.put(PASSWORD, pass);
		values.put(HEAD_PATH, headPath);
		values.put(HEADER, head);
		return mdb.insert(TABLE_LOGIN_NAME, null,values);
	}
	
//	ɾ���û������м�¼
	public boolean delete(String account){
		return mdb.delete(TABLE_LOGIN_NAME, ACCOUNTS+"="+account, null)>0;
	}
	
	public Cursor getCursor(String[] columns){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, columns, null, null, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor getCursor(String[] columns,String[]selectionAccount){
		Cursor mCursor=mdb.query(TABLE_LOGIN_NAME, columns, ACCOUNTS+"=?", selectionAccount, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public boolean updatePwd(String account,String password){
		ContentValues values=new ContentValues();
		values.put(PASSWORD,password);
		return mdb.update(TABLE_LOGIN_NAME, values, ACCOUNTS+"="+account, null)>0;
	}
	
	public boolean updateHead(String account, String headPath, String head){
		ContentValues values=new ContentValues();
		values.put(HEAD_PATH, headPath);
		values.put(HEADER, head);
		return mdb.update(TABLE_LOGIN_NAME, values, ACCOUNTS+"="+account, null)>0;
	}

}
