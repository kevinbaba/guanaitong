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
	private TextView account;
	private ImageView header;
	private ImageView popuparrow;
	private static ImageButton refresh;
	public static final String TAB_MAP = "tabMap";
	public static final String TAB_MES = "tabMes";
	public static final String TAB_SET = "tabSet";
	public static final String TAB_STATUS = "tabStatus";

	LoginDb mLoginDb;
	Object[] accounts;
	String mCurAccount;
	String mCurName;
	int mCurID;
	BitmapDrawable mCurHeadImg;
	public HashMap<String, Integer> accout2Id;
	public HashMap<String, String> accout2Name;
	public HashMap<String, BitmapDrawable> accout2HeadImg;
	public myAdapter adapter;
	ListView listView;
	public PopupWindow pop;

	com.yapai.guanaitong.struct.Login login = null;
	final String WARD = "ward";
	final String GUARDIAN = "guardian";
	public static final String ACTION_WARD_CHANGE = "action.ward.change";
	public static final String ACTION_REFRESH = "action.refresh";
	public final String SWITCH_WARD_SUCCESS = "SUCCESS";
	public final String SWITCH_WARD_ERROR = "ERROR";

	private void safeReleaseCursor(Cursor cursor) {
		cursor.close();
		cursor = null;
	}
	
	BitmapDrawable getAccountHead(String account){
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
					return bmpD;
				} catch (IOException e) {
					Log.e(TAG, "" + e);
				}
			}
		}
		return null;
	}
	
	public void saveHead2Db(final String account, final String headPath) {
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
					accout2HeadImg.put(account, getAccountHead(account));
					//TODO 实时头像更新
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
		header = (ImageView) findViewById(R.id.header);
		popuparrow = (ImageView) findViewById(R.id.popuparrow);
		refresh = (ImageButton) findViewById(R.id.refresh);
		account = (TextView) findViewById(R.id.account);

		login = MyApplication.login;
		accout2Id = new HashMap<String, Integer>();
		accout2Name = new HashMap<String, String>();
		accout2HeadImg = new HashMap<String, BitmapDrawable>();
		try {
			LoginWardProfile lwp = JSONUtil.json2LoginWardProfile(login
					.getWard_profile());

			mCurID = login.getIdentity();
			mCurName = lwp.getName();
			mCurAccount = lwp.getPhone();
			MyApplication.account = mCurAccount;
			String headLwp = lwp.getHead_48();

			if (GUARDIAN.equals(login.getLogin_by())) { // 如果是家庭组帐户登陆
			// LoginGuardian lg =
			// JSONUtil.json2LoginGuardian(login.getGuardian());
				List<LoginWards> LWList = JSONUtil.json2LoginWardsList(login
						.getWards());
				for (int i = 0; i < LWList.size(); i++) {
					LoginWards lw = LWList.get(i);
					String phone = lw.getPhone();
					int id = lw.getId();
					int status = lw.getStatus();
					String nickName = lw.getNickName();
					String head = lw.getHead_48();
					String gender = lw.getGender();
					if (!mCurAccount.equals(phone)) {
						accout2Id.put(phone, id);
						accout2Name.put(phone, nickName);
						saveHead2Db(phone, head);
						accout2HeadImg.put(phone, getAccountHead(phone));
					} else {
						if (Util.IsStringValuble(nickName)) { // 如果是家庭组登陆，使用nickName
							mCurName = nickName;
						}
						if (Util.IsStringValuble(head)) { // 同上
							headLwp = head;
						}
					}
				}
				if (LWList.size() > 1)
					popuparrow.setVisibility(View.VISIBLE);
			}
			saveHead2Db(mCurAccount, headLwp);
			accout2HeadImg.put(mCurAccount, getAccountHead(mCurAccount));
			account.setText(unionString(mCurName, mCurAccount));

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
					((ImageButton) v).setImageDrawable(getResources()
							.getDrawable(R.drawable.refresh_pressed));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					((ImageButton) v).setImageDrawable(getResources()
							.getDrawable(R.drawable.refresh_normal));
				}
				return false;
			}
		});

		headerAndAcount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popuparrow.getVisibility() != View.VISIBLE)
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
						accounts = accout2Id.keySet().toArray();
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

	public static void setRefreshVisible(boolean visible) {
		if (visible)
			refresh.setVisibility(View.VISIBLE);
		else
			refresh.setVisibility(View.GONE);
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

		public myAdapter() {
			mInflater = LayoutInflater.from(MainBoard.this);
			accounts = accout2Id.keySet().toArray();
		}

		@Override
		public int getCount() {
			return accounts.length;
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
		public View getView(final int position, View convertView,
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
				convertView.setId(position);
				holder.setId(position);
				String phone = accounts[position].toString();
				holder.view.setText(unionString(accout2Name.get(phone), phone));
				holder.button.setBackgroundDrawable(accout2HeadImg.get(phone));
				holder.view.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							menuClicked(position);
						}
						return true;
					}
				});
				holder.button.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							menuClicked(position);
						}
						return true;
					}
				});
			}
			return convertView;
		}

		void menuClicked(final int position) {
			closePopup();
			// 新建线程切换用户
			new Thread() {
				public void run() {
					String act = accounts[position].toString();
					int id = accout2Id.get(act);
					MyHttpClient mhc = new MyHttpClient(MainBoard.this);
					String result = mhc.switchward(id);
					if (SWITCH_WARD_SUCCESS.equals(result)) {
						mHandler.sendEmptyMessage(position);
					} else if (SWITCH_WARD_ERROR.equals(result)) {
						mHandler.sendEmptyMessage(-1);
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
			case -1:// 失败
				Toast.makeText(
						MainBoard.this,
						MainBoard.this.getResources().getString(
								R.string.switch_ward_error), Toast.LENGTH_SHORT)
						.show();
				break;
			default: // 服务端切换用户成功，设置界面
				String act = accounts[msg.what].toString();
				String name = accout2Name.get(act);
				account.setText(unionString(name, act));
				header.setBackgroundDrawable(accout2HeadImg.get(act));
				// 更新
				accout2Id.put(mCurAccount, mCurID);
				mCurAccount = act;
				mCurID = accout2Id.get(act);
				accout2Id.remove(act);

				sendBroadcastWardChange();
				// 这里用于跳到其它Activity时判断是否用户已经发生变化
				MyApplication.account = mCurAccount;
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
