package com.yapai.guanaitong.ui;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;

public class MainSetting extends PreferenceActivity {
	final String TAG = "MainSetting";
	
	BroadcastReceiver mBr;
	public String account;
	
	PreferenceManager mPm;
	CheckBoxPreference mGetMessageRealtime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_setting);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		this.getListView().setBackgroundColor(getResources().getColor(R.color.default_background_color));
		mPm = this.getPreferenceManager();
		mGetMessageRealtime = (CheckBoxPreference) mPm.findPreference("get_message_realtime");
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		Log.d(TAG, "onPreferenceTreeClick:"+preference);
		// 分别处理
		if(mGetMessageRealtime == preference){
			Boolean checked = mGetMessageRealtime.isChecked();
			//存
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "-------onBackPressed-----");
		moveTaskToBack(true);
		this.getParent().moveTaskToBack(true);
		// super.onBackPressed();
	}
	
	//TODO 从服务器获取设置，保存到哪？
	protected void onResume() {
		if(!MyApplication.account.equals(account)){
			account = MyApplication.account;
//			getUserStatus();
		}
		mBr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "-------------onReceive:"+intent.getAction());
				String action = intent.getAction();
				if(action.equals(MainBoard.ACTION_WARD_CHANGE)){
//					getUserStatus();
				}
				else if(action.equals(MainBoard.ACTION_REFRESH)){
//					getUserNewStatus();
				}
			}
		};
		registerReceiver(mBr, new IntentFilter(MainBoard.ACTION_WARD_CHANGE));
		registerReceiver(mBr, new IntentFilter(MainBoard.ACTION_REFRESH)); 
		
//		MainBoard.setRefreshStatus(View.INVISIBLE, "");
//		MainBoard.setSwitchStatus(View.VISIBLE, null);
		
		super.onResume();
	}
	
	protected void onPause() {
		unregisterReceiver(mBr);
		super.onPause();
	}
	
}
