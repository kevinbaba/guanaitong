package com.yapai.guanaitong.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDb {
	public String getPASSWORD() {
		return PASSWORD;
	}
	public String getACCOUNTS() {
		return ACCOUNTS;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public String getKEY() {
		return KEY;
	}
	
	private final String KEY="_id";
	private final String CREATETIME="created";
	private final String ACCOUNTS="accounts";
	private final String PASSWORD="pass";
	
	private final String TAG="LoginDb";
	
	private final String DABABASE_NAME="login.db";
	private final String TABLE_NAME="login";
//	注意创建表的SQL语句应该是：create table tableName ();所以要注意加空格，还有表名不能为table
	private final String TABLE_CREATETIME="create table "+TABLE_NAME+" (_id integer primary key autoincrement,accounts text not null, "
			+ "pass text not null,created text not null);";
	private Context mContext;
	private SQLiteDatabase mdb;
	private DatabaseHelper mdbHelper;
	private final int DATABASE_VERSION=1;
	
	public class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DABABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(TABLE_CREATETIME);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists "+TABLE_NAME);
			onCreate(db);
		}
	}
//	
	public LoginDb(Context mContext) {
		super();
		this.mContext = mContext;
		
	}
	public LoginDb open() throws SQLiteException{
		mdbHelper=new DatabaseHelper(mContext);
		mdb=mdbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mdbHelper.close();
	}
	
	public long create(String accounts,String pass){
		ContentValues values=new ContentValues();
		values.put(ACCOUNTS, accounts);
		values.put(PASSWORD, pass);
		values.put(CREATETIME, createTime());
		return mdb.insert(TABLE_NAME, null,values);
	}
	
//	删除对应id的所有记录
	public boolean delete(int id){
		return mdb.delete(TABLE_NAME, KEY+"="+id, null)>0;
	}
	
	public Cursor getCursor(String... args){
		Cursor mCursor=mdb.query(TABLE_NAME, args, null, null, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor getCursorArgs(String[] args,String[]selection){
		Cursor mCursor=mdb.query(TABLE_NAME, args, ACCOUNTS+"=?", selection, null, null, null);
		if(mCursor!=null&&!mCursor.isFirst())
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public boolean update(int id,String password){
		ContentValues values=new ContentValues();
		values.put(PASSWORD,password);
		values.put(CREATETIME, createTime());
		return mdb.update(TABLE_NAME, values, KEY+"="+id, null)>0;
	}
	
	public String createTime(){
		java.text.SimpleDateFormat sdf =new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Date date=new java.util.Date(System.currentTimeMillis());
		String str=sdf.format(date);
		return str;
	}
}
