package com.yapai.guanaitong.db;

import com.yapai.guanaitong.application.MyApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageDb {
	private final String TAG="MessageDb";

	static MessageDb instanc;
	private SQLiteDatabase mdb;
	
	public static final String ACCOUNT = "account";
	public static final String ID="_id";
	public static final String RANK="rank";
	public static final String MSG="msg";
	public static final String TIME="time";
		
	public final static String TABLE_MESSAGE_NAME="message";
//	ע�ⴴ�����SQL���Ӧ���ǣ�create table tableName ();����Ҫע��ӿո񣬻��б�������Ϊtable
	public final static String TABLE_MESSAGE_CREATE_SQL=
			" create table if not exists "+TABLE_MESSAGE_NAME
			+" ( "
			+ACCOUNT+" text primary key, "
			+ID+" integer , "
			+RANK+" integer not null, "
			+MSG+" text not null, "
			+TIME+" text not null "
			+" ); ";
	
	public MessageDb(Context mContext) {
		super();
		mdb=MyApplication.mdbHelper.getWritableDatabase();
	}
	
	public static MessageDb getDBInstanc(Context context) {
		if (instanc == null) {
			instanc = new MessageDb(context);
		}
		return instanc;
	}
	
	public long insert(String account, int id,int rank, String msg, String time){
		ContentValues values=new ContentValues();
		values.put(ACCOUNT, account);
		values.put(ID, id);
		values.put(RANK, rank);
		values.put(MSG, msg);
		values.put(TIME, time);
		return mdb.insert(TABLE_MESSAGE_NAME, null,values);
	}
		
//	ɾ����Ӧid�����м�¼
	public boolean delete(String account, int id){
		//TODO 
		return mdb.delete(TABLE_MESSAGE_NAME, ID+"="+id, null)>0;
	}
	
	public Cursor getCursor(String[] columns, String account){
		Cursor mCursor=mdb.query(TABLE_MESSAGE_NAME, columns, ACCOUNT+"="+account, null, null, null, ID+" desc");
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}

}

