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
	public String getSAVED() {
		return SAVED;
	}
	public String getCHECKED() {
		return CHECKED;
	}
	public String getCREATED() {
		return CREATED;
	}
	public String getRECOG() {
		return RECOG;
	}
	public String getKEY() {
		return KEY;
	}
	public String getIMG_PATH() {
		return IMG_PATH;
	}
	public String getTHUMB_PATH() {
		return THUMB_PATH;
	}
	
	
	
	
	
	private final String KEY="_id";
	private final String IMG_PATH="image";
	private final String THUMB_PATH="thumb";
	private final String RECOG="recog";
	private final String CHECKED="checked";
	private final String CREATED="created";
	private final String SAVED="saved";
	private final String ACCOUNTS="accounts";
	private final String PASSWORD="pass";
	
	private final String TAG="ImagedbUtil.java";
	
	private final String DABABASE_NAME="dbForQQ.db";
	private final String TABLE_NAME="qq";
//	注意创建表的SQL语句应该是：create table tableName ();所以要注意加空格，还有表名不能为table
	private final String TABLE_CREATED="create table "+TABLE_NAME+" (_id integer primary key autoincrement,accounts text not null, "
			+ "pass text not null,created text not null);";
	private final String TABLE_NOT_EXISTS_CREATED="create table if not exists "+TABLE_NAME+" (_id integer primary key autoincrement,image text not null, "
			+ "thumb text not null,recog text not null,checked text not null,saved text not null);";
	private Context mContext;
	private SQLiteDatabase mdb;
	private DatabaseHelper mdbHelper;
	private final int DATABASE_VERSION=1;
	
	public class DatabaseHelper extends SQLiteOpenHelper{
		
//		@Override
//		public void onOpen(SQLiteDatabase db) {
//			// TODO Auto-generated method stub
//			db.execSQL(TABLE_NOT_EXISTS_CREATED);
//		}

		public DatabaseHelper(Context context) {
			super(context, DABABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(TABLE_CREATED);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists path");
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
		values.put(CREATED, createdTime());
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
		return mdb.update(TABLE_NAME, values, KEY+"="+id, null)>0;
	}
	
	public boolean updateOcrAndSaved(int id,String Ocr,String saved){
		ContentValues values=new ContentValues();
		values.put(RECOG, Ocr);
		values.put(SAVED, saved);
		return mdb.update(TABLE_NAME, values, KEY+"="+id, null)>0;
	}
	
	public boolean updateChecked(int id,String checked){
		ContentValues values=new ContentValues();
		values.put(CHECKED,checked);
		return mdb.update(TABLE_NAME, values, KEY+"="+id, null)>0;
	}
	
	public boolean updateThumb(int id,String thumb){
		ContentValues values=new ContentValues();
		values.put(THUMB_PATH,thumb);
		return mdb.update(TABLE_NAME, values, KEY+"="+id, null)>0;
	}

	public boolean updatePathAndOcr(int id,String imagePath,String OcrResult){
		ContentValues values=new ContentValues();
		values.put(IMG_PATH, imagePath);
		values.put(RECOG, OcrResult);
		return mdb.update(TABLE_NAME, values, KEY+"="+id, null)>0;
	}
	
	public void addColumn() throws SQLException{
		String sql="alter table "+TABLE_NAME+" add "+RECOG+" text null;";
		mdb.execSQL(sql);
	}
	
	public String createdTime(){
		java.text.SimpleDateFormat sdf =new java.text.SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
		java.util.Date date=new java.util.Date(System.currentTimeMillis());
		String str=sdf.format(date);
		return str;
	}
}
