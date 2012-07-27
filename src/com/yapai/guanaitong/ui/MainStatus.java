package com.yapai.guanaitong.ui;

import org.json.JSONException;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;
import com.yapai.guanaitong.beans.Status;
import com.yapai.guanaitong.net.MyHttpClient;
import com.yapai.guanaitong.util.JSONUtil;
import com.yapai.guanaitong.util.Util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainStatus extends Activity implements OnTouchListener{
	final String TAG = "MainStatus";
	final int LOAD_COMPLETE = 0;
	final int LOAD_ERROR = 1;
	final int LOAD_TIMEOUT = 2;

	final String STATUS_NOTREADY = "NOT_READY";
	final int CHECK_NEW_STATUS_CYCLE_TIME = 3000;
	final int CHECK_NEW_STATUS_TIMES = 40;

	Status st;
	TextView poweron;
	TextView report_time;
	TextView powervolumn;
	TextView status;
	TextView fmduration;
	TextView fmfavorite;
	TextView callout;
	TextView callin;
	TextView safe_region_out;
	TextView safe_region_in;
	LinearLayout progress;
	TextView loadinghint;
	private Button refresh;

	BroadcastReceiver mBr;
	public String account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_status);
		poweron = (TextView) findViewById(R.id.poweron);
		report_time = (TextView) findViewById(R.id.report_time);
		powervolumn = (TextView) findViewById(R.id.powervolumn);
		status = (TextView) findViewById(R.id.status);
		fmduration = (TextView) findViewById(R.id.fmduration);
		fmfavorite = (TextView) findViewById(R.id.fmfavorite);
		callout = (TextView) findViewById(R.id.callout);
		callin = (TextView) findViewById(R.id.callin);
		safe_region_out = (TextView) findViewById(R.id.safe_region_out);
		safe_region_in = (TextView) findViewById(R.id.safe_region_in);
		loadinghint = (TextView) findViewById(R.id.loadinghint);
		progress = (LinearLayout) findViewById(R.id.progress);
		progress.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getUserNewStatus();
			}
		});
	}

	protected void onResume() {
		if (!MyApplication.account.equals(account)) {
			account = MyApplication.account;
			getUserStatus();
		}
		mBr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "-------------onReceive:" + intent.getAction());
				String action = intent.getAction();
				if (action.equals(MainBoard.ACTION_WARD_CHANGE))
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
				report_time.setText(st.getReportTime());
				powervolumn.setText(st.getSysPowerVolumn());
				status.setText(st.getSysStatus());
				fmduration.setText(st.getSysFmDuration());
				fmfavorite.setText(st.getSysFmFavorite());
				callout.setText(st.getSysCallOut());
				callin.setText(st.getSysCallIn());
				safe_region_out.setText(st.getSafeRegionOut());
				safe_region_in.setText(st.getSafeRegionIn());
				break;
			case LOAD_ERROR:
				Toast.makeText(
						MainStatus.this,
						MainStatus.this.getResources().getString(
								R.string.get_msg_error), Toast.LENGTH_SHORT)
						.show();
				break;
			case LOAD_TIMEOUT:
				Toast.makeText(
						MainStatus.this,
						MainStatus.this.getResources().getString(
								R.string.get_msg_timeout), Toast.LENGTH_SHORT)
						.show();
				break;
			}
			progress.setVisibility(View.INVISIBLE);
			super.handleMessage(msg);
		}
	};

	void getUserNewStatus() {
		if (progress.getVisibility() == View.VISIBLE)
			return;
		loadinghint.setText(getResources().getString(R.string.getting_newest_status)
				+ "\n"
				+ getResources().getString(R.string.wait_tip));
		progress.setVisibility(View.VISIBLE);
		MainBoard.setProgressVisible(View.INVISIBLE);
		new Thread() {
			@Override
			public void run() {
				MyHttpClient mhc = new MyHttpClient(MainStatus.this);
				String endTime = mhc.getUserNewStatus(null);
				if (!Util.IsStringValuble2(endTime)) {
					mHandler.sendEmptyMessage(LOAD_ERROR);
					return;
				}
				int checkTimes = 0;
				while (true) {
					if( checkTimes > CHECK_NEW_STATUS_TIMES){
						mHandler.sendEmptyMessage(LOAD_TIMEOUT);
						break;
					}
					checkTimes ++;
					try {
						Thread.sleep(CHECK_NEW_STATUS_CYCLE_TIME);
					} catch (InterruptedException e) {
						Log.d(TAG, "" + e);
					}
					String result = mhc.getUserNewStatus(endTime);
					if (!Util.IsStringValuble(result)) {
						mHandler.sendEmptyMessage(LOAD_ERROR);
						break;
					}
					if (STATUS_NOTREADY.equals(result)) {
						continue;
					}
					try {
						st = JSONUtil.json2Status(result);
						mHandler.sendEmptyMessage(LOAD_COMPLETE);
					} catch (JSONException e) {
						Log.e(TAG, "" + e);
						mHandler.sendEmptyMessage(LOAD_ERROR);
					}
					break;
				}
			}
		}.start();
	}

	void getUserStatus() {
		progress.setVisibility(View.VISIBLE);
		MainBoard.setProgressVisible(View.INVISIBLE);
		new Thread() {
			@Override
			public void run() {
				MyHttpClient mhc = new MyHttpClient(MainStatus.this);
				String result = mhc.getUserStatus();
				if (!Util.IsStringValuble2(result)) {
					mHandler.sendEmptyMessage(LOAD_ERROR);
					return;
				}
				try {
					st = JSONUtil.json2Status(result);
					mHandler.sendEmptyMessage(LOAD_COMPLETE);
				} catch (JSONException e) {
					Log.e(TAG, "" + e);
					mHandler.sendEmptyMessage(LOAD_ERROR);
				}
				super.run();
			}
		}.start();
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "-------onBackPressed-----");
		moveTaskToBack(true);
		this.getParent().moveTaskToBack(true);
		// super.onBackPressed();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
