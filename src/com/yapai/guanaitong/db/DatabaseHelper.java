package com.yapai.guanaitong.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private final static String DABABASE_NAME = "guanaitong.db";
	private final static int DATABASE_VERSION = 2;


	public DatabaseHelper(Context context) {
		super(context, DABABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(LoginDb.TABLE_LOGIN_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists "+LoginDb.TABLE_LOGIN_NAME);
		onCreate(db);
	}

}
