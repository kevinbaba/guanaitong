package com.yapai.guanaitong.ui;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;
import com.yapai.guanaitong.beans.*;
import com.yapai.guanaitong.db.LoginDb;
import com.yapai.guanaitong.net.MyHttpClient;
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
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
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
import android.widget.AdapterView.OnItemClickListener;

public class MainBoard extends TabActivity {
	private final String TAG = "MainBoard";
	private RadioGroup mRadioGroup;
	private TabHost tabHost;
	private static GridView accountsGridView;
	private HorizontalScrollView mScrollView;
	private static LinearLayout progress;
	public static final String TAB_MAP = "tabMap";
	public static final String TAB_MES = "tabMes";
	public static final String TAB_SET = "tabSet";
	public static final String TAB_STATUS = "tabStatus";
	public static final String GOTOMESSAGE = "goto_message";

	boolean mGotoMessage;

	LoginDb mLoginDb;
	static String mCurAccount;
	static String mCurName;
	int mCurID;
	public static HashMap<Integer, BitmapDrawable> accoutID2HeadImg;
	public static HashMap<Integer, String> accoutID2Name;
	private myAdapter adapter;
	private ListView listView;
	private PopupWindow pop;
	private TextView user_phone;
	private ImageButton dail;
	private ImageButton sms;

	LoginWardProfile mLwp;
	public static List<LoginWards> mLwList;

	com.yapai.guanaitong.beans.LoginStruct login = null;
	public static final String ACTION_WARD_CHANGE = "action.ward.change";
	public static final String ACTION_REFRESH = "action.refresh";
	public final String SWITCH_WARD_SUCCESS = "SUCCESS";
	public final String SWITCH_WARD_ERROR = "ERROR";
	public final String POSITION = "position";

	public final int MSG_SWITCH_WARD = 0;
	public final int MSG_GOT_HEAD = 1;
	public final int MSG_DELAY = 2;
	public final int DELAY_TIME = 200;

	private final int CELL_WIDTH = 100;
	private final int CELL_HSPACING = 5;

	int mPrePosition = 0;

	private void safeReleaseCursor(Cursor cursor) {
		cursor.close();
		cursor = null;
	}

	BitmapDrawable getAccountHeadByID(int id) {
		BitmapDrawable exist = accoutID2HeadImg.get(id);
		if (exist != null)
			return exist;
		Cursor cursor = mLoginDb.getCursorByID(null,
				new String[] { String.valueOf(id) });
		final boolean accoutExists = cursor.getCount() > 0;
		if (accoutExists) {
			int headindex = cursor.getColumnIndexOrThrow(LoginDb.HEADER);
			String head = cursor.getString(headindex);
			safeReleaseCursor(cursor);
			if (Util.IsStringValuble(head)) {
				try {
					FileInputStream fis = openFileInput(head);
					InputStream is = new BufferedInputStream(fis, 8192);
					BitmapDrawable bmpD = new BitmapDrawable(is);
					accoutID2HeadImg.put(id, bmpD);
					return bmpD;
				} catch (IOException e) {
					Log.e(TAG, "" + e);
				}
			}
		}
		return null;
	}

	public void prepareHead(final int id, final String account,
			final String headPath) {
		if (!Util.IsStringValuble(headPath))
			return;
		Cursor cursor = mLoginDb.getCursorByID(null,
				new String[] { String.valueOf(id) });
		String headPathDb = "";
		final boolean idExists = cursor.getCount() > 0;
		if (idExists) {
			int headPathindex = cursor.getColumnIndexOrThrow(LoginDb.HEAD_PATH);
			headPathDb = cursor.getString(headPathindex);
		}
		safeReleaseCursor(cursor);
		if (headPath.equals(headPathDb)) {
			getAccountHeadByID(id);
			return;
		}
		new Thread() {
			public void run() {
				String saveName = account + "." + Util.getSuffix(headPath);
				MyHttpClient mhc = new MyHttpClient(MainBoard.this);
				if (mhc.downLoadFile(Config.HOST + headPath, saveName)) {
					if (idExists) {
						mLoginDb.updateHead(id, headPath, saveName);
					} else {
						//家庭组成员
						mLoginDb.insert(id, account, "", false, headPath, saveName);
					}
					mHandler.sendEmptyMessage(MSG_GOT_HEAD);
					getAccountHeadByID(id);
				}
			}
		}.start();
	}

	private void setAccountsGridView() {
		if (adapter == null) {
			adapter = new myAdapter();
			accountsGridView.setAdapter(adapter);
			accountsGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
//					Log.d(TAG, "----------------position:"+position+",id:"+id);
					int wardId = mLwList.get(position).getId();
					if(wardId == mCurID) return;
					gridViewClicked(position);
					accountsGridView.getChildAt(mPrePosition).setBackgroundColor(0);
				}
			});
			int width = (int) (adapter.getCount()
					* (CELL_WIDTH + CELL_HSPACING) * MyApplication.density);
			LayoutParams params = new LayoutParams(width,
					LayoutParams.WRAP_CONTENT);
			accountsGridView.setLayoutParams(params);
			accountsGridView
					.setColumnWidth((int) (CELL_WIDTH * MyApplication.density));
			accountsGridView
					.setHorizontalSpacing((int) (CELL_HSPACING * MyApplication.density));
			accountsGridView.setStretchMode(GridView.NO_STRETCH);
			accountsGridView.setNumColumns(adapter.getCount());
			//TODO 界面初始化的时候View还没有实例化，这里的方法有点勉强,应该有更好更直接的办法
			mHandler.sendEmptyMessageDelayed(MSG_DELAY, DELAY_TIME);

		}

	}
	
	void doDail(){
		try{
			Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCurAccount));
			startActivity(phoneIntent);
		}catch (Exception e) {
			Log.e(TAG, ""+e);
		}
	}
	
	void doSms(){
		try{
			Intent phoneIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mCurAccount));
			startActivity(phoneIntent);
		}catch (Exception e) {
			Log.e(TAG, ""+e);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mGotoMessage = intent.getBooleanExtra(GOTOMESSAGE, false);

		setContentView(R.layout.main_board);
		mLoginDb = LoginDb.getDBInstanc(this);

		mRadioGroup = (RadioGroup) findViewById(R.id.main_radio);
		accountsGridView = (GridView) findViewById(R.id.accountsGridView);
		mScrollView = (HorizontalScrollView) findViewById(R.id.mScrollView);
		mScrollView.setHorizontalScrollBarEnabled(true);
		user_phone = (TextView) findViewById(R.id.user_phone);
		dail = (ImageButton) findViewById(R.id.dail);
		dail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doDail();
			}
		});
		sms = (ImageButton) findViewById(R.id.sms);
		sms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doSms();
			}
		});
		progress = (LinearLayout) findViewById(R.id.progress);

		progress.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		login = MyApplication.login;
		accoutID2HeadImg = new HashMap<Integer, BitmapDrawable>();
		accoutID2Name = new HashMap<Integer, String>();
		try {
			mLwp = JSONUtil.json2LoginWardProfile(login.getWard_profile());

			mCurID = login.getIdentity();
			mCurName = mLwp.getName();
			mCurAccount = mLwp.getPhone();
			MyApplication.account = mCurAccount;
			String headLwp = mLwp.getHead_48();

			if (Login.GUARDIAN.equals(login.getLogin_by())) { // 如果是家庭组帐户登陆
				Log.d(TAG, "Login by---->GUARDIAN");
				mLwList = JSONUtil.json2LoginWardsList(login.getWards());
				for (int i = 0; i < mLwList.size(); i++) {
					LoginWards lw = mLwList.get(i);
					String phone = lw.getPhone();
					int id = lw.getId();
					int status = lw.getStatus();
					String name = lw.getName();
					String nickName = lw.getNickName();
					String head = lw.getHead_48();
					String gender = lw.getGender();
					accoutID2Name.put(id, getFinalName(nickName, name, phone));
					prepareHead(id, phone, head);
				}
//				LoginGuardian lG = JSONUtil.json2LoginGuardian(login.getGuardian());
			}else if(Login.WARD.equals(login.getLogin_by())){
				Log.d(TAG, "Login by---->WARD");
				mLwList = new ArrayList <LoginWards>();
				LoginWards lw = new LoginWards();
				lw.setPhone(mCurAccount);
				lw.setId(mCurID);
				lw.setName(mCurName);
				lw.setNickName(mCurName);
				lw.setHead_48(headLwp);
				mLwList.add(lw);
				accoutID2Name.put(mCurID, getFinalName(mCurName, mCurName, mCurAccount));
				prepareHead(mCurID, mCurAccount, headLwp);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		setAccountsGridView();

		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_STATUS).setIndicator(TAB_STATUS)
				.setContent(new Intent(this, MainStatus.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MAP).setIndicator(TAB_MAP)
				.setContent(new Intent(this, MainMap.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MES).setIndicator(TAB_MES)
				.setContent(new Intent(this, MainMessage.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_SET).setIndicator(TAB_SET)
				.setContent(new Intent(this, MainSetting.class)));
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
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

		if (mGotoMessage) {
			mRadioGroup.check(R.id.radio_button_message);
		}
	}

	public static String getFinalName(String nickName, String name, String phone) {
		if (Util.IsStringValuble(nickName))
			return nickName;
		if (Util.IsStringValuble(name))
			return name;
		return phone;
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

	class myAdapter extends BaseAdapter {
		LayoutInflater mInflater;

		public myAdapter() {
			mInflater = LayoutInflater.from(MainBoard.this);
		}

		@Override
		public int getCount() {
			return mLwList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.main_board_gridview,
						null);
				holder = new Holder();
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.header = (ImageView) convertView
						.findViewById(R.id.header);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (holder != null) {
				String phone = mLwList.get(position).getPhone();
				convertView.setId(position);
				holder.setId(position);

				phone = mLwList.get(position).getPhone();
				holder.name.setText(getFinalName(mLwList.get(position)
						.getNickName(), mLwList.get(position).getName(), phone));
				BitmapDrawable bitD = getAccountHeadByID(mLwList.get(position).getId());
				if(bitD != null){
					holder.header.setBackgroundDrawable(getAccountHeadByID(mLwList
							.get(position).getId()));
				}else{
					holder.header.setBackgroundResource(R.drawable.header);
				}
			}
			return convertView;
		}

		class Holder {
			TextView name;
			ImageView header;

			void setId(int position) {
				name.setId(position);
				header.setId(position);
			}
		}

	}

	void gridViewClicked(final int position) {
		setProgressVisible(View.VISIBLE);
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
				} else {
					mHandler.sendEmptyMessage(MSG_SWITCH_WARD);
				}
			}
		}.start();
	}
	
	static void setProgressVisible(int visible) {
		progress.setVisibility(visible);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SWITCH_WARD:
				Bundle data = msg.getData();
				if (data.isEmpty()) { // 失败
					Toast.makeText(
							MainBoard.this,
							MainBoard.this.getResources().getString(
									R.string.switch_ward_error),
							Toast.LENGTH_SHORT).show();
					accountsGridView.getChildAt(mPrePosition)
					.setBackgroundColor(getResources().getColor(R.color.button_selected));
					progress.setVisibility(View.INVISIBLE);
				} else {// 服务端切换用户成功，设置界面
					int position = data.getInt(POSITION);
					String phone = mLwList.get(position).getPhone();
					String name = mLwList.get(position).getNickName();
					// 更新
					mCurAccount = phone;
					mCurID = mLwList.get(position).getId();
					mCurName = accoutID2Name.get(mCurID);

					sendBroadcastWardChange();
					// 这里用于跳到其它Activity时判断是否用户已经发生变化
					MyApplication.account = mCurAccount;

					// 在二级activity关闭该progress
					// progress.setVisibility(View.INVISIBLE);
					
					accountsGridView.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.button_selected));
					mPrePosition = position;
					
					user_phone.setText(mCurAccount);

				}
				break;
			case MSG_GOT_HEAD:
				adapter.notifyDataSetChanged();
				break;
			case MSG_DELAY:
				View v = accountsGridView.getChildAt(0);
				Log.d(TAG, "=========>accountsGridView.getChildAt(0):"+v);
				if(v != null){
					v.setBackgroundColor(getResources().getColor(R.color.button_selected));
					user_phone.setText(mCurAccount);
				}else{
					mHandler.sendEmptyMessageDelayed(MSG_DELAY, DELAY_TIME);
				}
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		closePopup();
		return super.onTouchEvent(event);
	}

}
