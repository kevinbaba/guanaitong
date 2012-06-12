package com.yapai.guanaitong.ui;

import org.json.JSONException;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.net.MyHttpClient;
import com.yapai.guanaitong.struct.Status;
import com.yapai.guanaitong.util.JSONUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class MainStatus extends Activity {
	final String TAG = "MainStatus";
	final int LOAD_COMPLETE = 0;
	
	Status st;
	TextView poweron;
	TextView poweroff;
	TextView powervolumn;
	TextView status;
	TextView fmduration;
	TextView fmfavorite;
	TextView callout;
	TextView callin;
	TextView safe_region_out;
	TextView safe_region_in;
	
	BroadcastReceiver mBr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_status);
		poweron = (TextView)findViewById(R.id.poweron);
		poweroff = (TextView)findViewById(R.id.poweroff);
		powervolumn = (TextView)findViewById(R.id.powervolumn);
		status = (TextView)findViewById(R.id.status);
		fmduration = (TextView)findViewById(R.id.fmduration);
		fmfavorite = (TextView)findViewById(R.id.fmfavorite);
		callout = (TextView)findViewById(R.id.callout);
		callin = (TextView)findViewById(R.id.callin);
		safe_region_out = (TextView)findViewById(R.id.safe_region_out);
		safe_region_in = (TextView)findViewById(R.id.safe_region_in);
		getUserStatus();
	}
	
	protected void onResume() {
		mBr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.d(TAG, "-------------onReceive");
				getUserStatus();
			}
		};
		registerReceiver(mBr, new IntentFilter(MainBoard.ACTION_WARD_CHANGE)); 
		
		super.onResume();
	}
	
	protected void onPause() {
		unregisterReceiver(mBr);
		super.onPause();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_COMPLETE:
				poweron.setText(st.getSysPoweron());
				poweroff.setText(st.getSysPoweroff());
				powervolumn.setText(st.getSysPowerVolumn());
				status.setText(st.getSysStatus());
				fmduration.setText(st.getSysFmDuration());
				fmfavorite.setText(st.getSysFmFavorite());
				callout.setText(st.getSysCallOut());
				callin.setText(st.getSysCallIn());
				safe_region_out.setText(st.getSafeRegionOut());
				safe_region_in.setText(st.getSafeRegionIn());
				break;
			}
			super.handleMessage(msg);
		}
	};

	void getUserStatus() {
		new Thread() {
			@Override
			public void run() {
				MyHttpClient mhc = new MyHttpClient(MainStatus.this);
				String result = mhc.getUserStatus();
				try {
					st = JSONUtil.json2Status(result);
					mHandler.sendEmptyMessage(LOAD_COMPLETE);
				} catch (JSONException e) {
					Log.e(TAG, "" + e);
				}
				super.run();
			}
		}.start();
	}
}
