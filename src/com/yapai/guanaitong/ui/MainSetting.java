package com.yapai.guanaitong.ui;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;

public class MainSetting extends PreferenceActivity {
	final String TAG = "MainSetting";
	
	BroadcastReceiver mBr;
	public String account;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_setting);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		this.getListView().setBackgroundColor(getResources().getColor(R.color.default_background_color));
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPreferenceTreeClick:"+preference);
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "-------onBackPressed-----");
		moveTaskToBack(true);
		this.getParent().moveTaskToBack(true);
		// super.onBackPressed();
	}
	
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
		
		MainBoard.setRefreshStatus(View.INVISIBLE, "");
		MainBoard.setSwitchStatus(View.VISIBLE, null);
		
		super.onResume();
	}
	
	protected void onPause() {
		unregisterReceiver(mBr);
		super.onPause();
	}
	
}
