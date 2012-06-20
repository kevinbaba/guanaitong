package com.yapai.guanaitong.ui;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;
import com.yapai.guanaitong.db.LoginDb;
import com.yapai.guanaitong.net.MyHttpClient;
import com.yapai.guanaitong.struct.*;
import com.yapai.guanaitong.util.Config;
import com.yapai.guanaitong.util.JSONUtil;
import com.yapai.guanaitong.util.Util;

import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainBoard extends TabActivity {
	private final String TAG = "MainBoard";
	private RadioGroup group;
	private TabHost tabHost;
	private LinearLayout headerAndAcount;
	private TextView accountView;
	private ImageView headerView;
	private ImageView popuparrowView;
	private LinearLayout progress;
	private static TextView refresh;
	public static final String TAB_MAP = "tabMap";
	public static final String TAB_MES = "tabMes";
	public static final String TAB_SET = "tabSet";
	public static final String TAB_STATUS = "tabStatus";

	LoginDb mLoginDb;
	String mCurAccount;
	String mCurName;
	int mCurID;
	public HashMap<String, BitmapDrawable> accout2HeadImg;
	public myAdapter adapter;
	ListView listView;
	public PopupWindow pop;
	
	LoginWardProfile mLwp;
	public static List<LoginWards> mLwList;

	com.yapai.guanaitong.struct.LoginStruct login = null;
	final String WARD = "ward";
	final String GUARDIAN = "guardian";
	public static final String ACTION_WARD_CHANGE = "action.ward.change";
	public static final String ACTION_REFRESH = "action.refresh";
	public final String SWITCH_WARD_SUCCESS = "SUCCESS";
	public final String SWITCH_WARD_ERROR = "ERROR";
	public final String POSITION = "position";
	
	public final int MSG_SWITCH_WARD = 0;
	public final int MSG_GOT_CUR_HEAD = 1;
	
	private void safeReleaseCursor(Cursor cursor) {
		cursor.close();
		cursor = null;
	}
	
	BitmapDrawable getAccountHead(String account){
		BitmapDrawable exist = accout2HeadImg.get(account);
		if(exist != null)	return exist; 
		Cursor cursor = mLoginDb
				.getCursor(null, new String[] { account });
		final boolean accoutExists = cursor.getCount() > 0;
		if (accoutExists) {
			int headindex = cursor.getColumnIndexOrThrow(LoginDb.HEADER);
			String head = cursor.getString(headindex);
			safeReleaseCursor(cursor);
			if(Util.IsStringValuble(head)){
				try {
					FileInputStream fis = openFileInput(head);
					InputStream is = new BufferedInputStream(fis, 8192);
					BitmapDrawable bmpD = new BitmapDrawable(is);
					accout2HeadImg.put(account, bmpD);
					return bmpD;
				} catch (IOException e) {
					Log.e(TAG, "" + e);
				}
			}
		}
		return null;
	}
	
	public void prepareData(final String account, final String headPath, final String name) {
		if (!Util.IsStringValuble(headPath))
			return;
		Cursor cursor = mLoginDb
				.getCursor(null, new String[] { account });
		String headPathDb = "";
		final boolean accoutExists = cursor.getCount() > 0;
		if (accoutExists) {
			int headPathindex = cursor.getColumnIndexOrThrow(LoginDb.HEAD_PATH);
			headPathDb = cursor.getString(headPathindex);
		}
		safeReleaseCursor(cursor);
		if (headPath.equals(headPathDb)){
			return;
		}
		new Thread() {
			public void run() {
				String saveName = account + "." + Util.getSuffix(headPath);
				MyHttpClient mhc = new MyHttpClient(MainBoard.this);
				if (mhc.downLoadFile(Config.HOST + headPath, saveName)) {
					if (accoutExists) {
						mLoginDb.updateHead(account, headPath, saveName);
					} else {
						mLoginDb.insert(account, "", headPath, saveName);
					}
					if(account.equals(mCurAccount)){
						mHandler.sendEmptyMessage(MSG_GOT_CUR_HEAD);
					}
				}
			}
		}.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_board);
		mLoginDb = LoginDb.getDBInstanc(this);

		group = (RadioGroup) findViewById(R.id.main_radio);
		headerAndAcount = (LinearLayout) findViewById(R.id.headerAndAcount);
		headerView = (ImageView) findViewById(R.id.header);
		popuparrowView = (ImageView) findViewById(R.id.popuparrow);
		refresh = (TextView) findViewById(R.id.refresh);
		accountView = (TextView) findViewById(R.id.account);
		progress = (LinearLayout)findViewById(R.id.progress);

		login = MyApplication.login;
		accout2HeadImg = new HashMap<String, BitmapDrawable>();
		try {
			mLwp = JSONUtil.json2LoginWardProfile(login
					.getWard_profile());

			mCurID = login.getIdentity();
			mCurName = mLwp.getName();
			mCurAccount = mLwp.getPhone();
			MyApplication.account = mCurAccount;
			String headLwp = mLwp.getHead_48();

			if (GUARDIAN.equals(login.getLogin_by())) { // 如果是家庭组帐户登陆
			// LoginGuardian lg =
			// JSONUtil.json2LoginGuardian(login.getGuardian());
				mLwList = JSONUtil.json2LoginWardsList(login
						.getWards());
				for (int i = 0; i < mLwList.size(); i++) {
					LoginWards lw = mLwList.get(i);
					String phone = lw.getPhone();
					int id = lw.getId();
					int status = lw.getStatus();
					String nickName = lw.getNickName();
					String head = lw.getHead_48();
					String gender = lw.getGender();
					if (mCurAccount.equals(phone)) {
						if (Util.IsStringValuble(nickName)) { // 如果是家庭组登陆，使用nickName
							mCurName = nickName;
						}
					}
					prepareData(phone, head, nickName);
				}
				if (mLwList.size() > 1)
					popuparrowView.setVisibility(View.VISIBLE);
			}
			headerView.setBackgroundDrawable(getAccountHead(mCurAccount));
			accountView.setText(unionString(mCurName, mCurAccount));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendBroadcastRefresh();
			}
		});

		refresh.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					((TextView) v).setBackgroundResource(R.drawable.button_pressed);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					((TextView) v).setBackgroundResource(R.drawable.button_normal);
				}
				return false;
			}
		});

		headerAndAcount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popuparrowView.getVisibility() != View.VISIBLE
						|| popuparrowView.isEnabled() == false)
					return;
				if (pop == null) {
					if (adapter == null) {
						adapter = new myAdapter();
						listView = new ListView(MainBoard.this);
						pop = new PopupWindow(listView, headerAndAcount
								.getWidth(), LayoutParams.WRAP_CONTENT);
						listView.setAdapter(adapter);
						pop.showAsDropDown(headerAndAcount, 0, 1);
					} else {
						adapter.notifyDataSetChanged();
						pop = new PopupWindow(listView, headerAndAcount
								.getWidth(), LayoutParams.WRAP_CONTENT);
						pop.showAsDropDown(headerAndAcount, 0, 1);
					}
				} else {
					pop.dismiss();
					pop = null;
				}
			}
		});

		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_STATUS).setIndicator(TAB_STATUS)
				.setContent(new Intent(this, MainStatus.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MAP).setIndicator(TAB_MAP)
				.setContent(new Intent(this, MainMap.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MES).setIndicator(TAB_MES)
				.setContent(new Intent(this, MainMessage.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_SET).setIndicator(TAB_SET)
				.setContent(new Intent(this, MainSetting.class)));
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				closePopup();
				switch (checkedId) {
				case R.id.radio_button_status:
					tabHost.setCurrentTabByTag(TAB_STATUS);
					break;
				case R.id.radio_button_map:
					tabHost.setCurrentTabByTag(TAB_MAP);
					break;
				case R.id.radio_button_message:
					tabHost.setCurrentTabByTag(TAB_MES);
					break;
				case R.id.radio_button_setting:
					tabHost.setCurrentTabByTag(TAB_SET);
					break;

				default:
					break;
				}
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, this.getResources().getString(R.string.login_out));
		menu.add(0, 2, 2, this.getResources().getString(R.string.exit));
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			LoginOut();
		} else if (item.getItemId() == 2) {
			finish();
		}
		return true;
	}

	public void LoginOut() {
		// 进入登陆界面
		Intent intent = new Intent(MainBoard.this, Login.class);
		intent.putExtra(Login.ISLOGINOUT, true);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "" + e);
		}
		finish();
	}

	// 下拉框Adapter
	class myAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		int mAfterCurAccount;

		public myAdapter() {
			mInflater = LayoutInflater.from(MainBoard.this);
		}

		@Override
		public int getCount() {
			return mLwList.size() -1; //除掉当前ward
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.main_board_popup, null);
				holder = new Holder();
				holder.view = (TextView) convertView.findViewById(R.id.account);
				holder.button = (ImageView) convertView
						.findViewById(R.id.header);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (holder != null) {
				if(position == 0)
					mAfterCurAccount = 0;
				String phone = mLwList.get(position).getPhone();
				if(phone.equals(mCurAccount)){
					mAfterCurAccount = 1;
				}
				position += mAfterCurAccount;
				convertView.setId(position);
				holder.setId(position);
				
				phone = mLwList.get(position).getPhone();
				holder.view.setText(unionString(mLwList.get(position).getNickName(), phone));
				holder.button.setBackgroundDrawable(getAccountHead(phone));
				holder.view.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							menuClicked(v.getId());
						}
						return true;
					}
				});
				holder.button.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							menuClicked(v.getId());
						}
						return true;
					}
				});
			}
			return convertView;
		}

		void menuClicked(final int position) {
			closePopup();
			popuparrowView.setEnabled(false);
			progress.setVisibility(View.VISIBLE);
			// 新建线程切换用户
			new Thread() {
				public void run() {
					int id = mLwList.get(position).getId();
					MyHttpClient mhc = new MyHttpClient(MainBoard.this);
					String result = mhc.switchward(id);
					if (SWITCH_WARD_SUCCESS.equals(result)) {
						Message msg = new Message();
						msg.what = MSG_SWITCH_WARD;
						Bundle data = new Bundle();
						data.putInt(POSITION, position);
						msg.setData(data);
						mHandler.sendMessage(msg);
					} else if (SWITCH_WARD_ERROR.equals(result)) {
						mHandler.sendEmptyMessage(MSG_SWITCH_WARD);
					}
				}
			}.start();
		}

		class Holder {
			TextView view;
			ImageView button;

			void setId(int position) {
				view.setId(position);
				button.setId(position);
			}
		}

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SWITCH_WARD:
				Bundle data = msg.getData();
				if(data == null){ //失败
					Toast.makeText(
							MainBoard.this,
							MainBoard.this.getResources().getString(
									R.string.switch_ward_error), Toast.LENGTH_SHORT)
							.show();
				}else{// 服务端切换用户成功，设置界面
					int position = data.getInt(POSITION);
					String act = mLwList.get(position).getPhone();
					String name = mLwList.get(position).getNickName();
					accountView.setText(unionString(name, act));
					headerView.setBackgroundDrawable(getAccountHead(act));
					// 更新
					mCurAccount = act;
					mCurID = mLwList.get(position).getId();
					mCurName = mLwList.get(position).getNickName();

					sendBroadcastWardChange();
					// 这里用于跳到其它Activity时判断是否用户已经发生变化
					MyApplication.account = mCurAccount;
					
					popuparrowView.setEnabled(true);
					progress.setVisibility(View.INVISIBLE);
				}
				break;
			case MSG_GOT_CUR_HEAD: 
				headerView.setBackgroundDrawable(getAccountHead(mCurAccount));
				break;
			}
		}
	};

	public void sendBroadcastWardChange() {
		// 发送用户切换广播
		Intent intent = new Intent();
		intent.setAction(ACTION_WARD_CHANGE);
		intent.putExtra("ward", mCurAccount);
		MainBoard.this.sendBroadcast(intent);
	}
	
	public static void setRefreshStatus(int visibility, String hint){
		refresh.setVisibility(visibility);
		refresh.setText(hint);
	}

	public void sendBroadcastRefresh() {
		// 发送用户切换广播
		Intent intent = new Intent();
		intent.setAction(ACTION_REFRESH);
		MainBoard.this.sendBroadcast(intent);
	}

	public void closePopup() {
		if (pop != null) {
			pop.dismiss();
			pop = null;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		closePopup();
		return super.onTouchEvent(event);
	}

	public String unionString(String str1, String str2) {
		String union = "";
		if (Util.IsStringValuble(str1)) {
			union += str1 + "\n";
		}
		union += str2;
		return union;
	}
	
}
