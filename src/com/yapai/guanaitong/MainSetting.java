package com.yapai.guanaitong;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class MainSetting extends PreferenceActivity {
	final String TAG = "MainSetting";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_setting);
		getWindow().setBackgroundDrawableResource(R.drawable.default_bg);
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPreferenceTreeClick:"+preference);
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
}
