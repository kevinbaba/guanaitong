package com.yapai.guanaitong.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.beans.MessageStruct;
import com.yapai.guanaitong.db.MessageDb;
import com.yapai.guanaitong.net.MyHttpClient;
import com.yapai.guanaitong.util.Config;
import com.yapai.guanaitong.util.JSONUtil;
import com.yapai.guanaitong.util.Util;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainMessage extends ListActivity {
	private final String TAG = "MainMesage";
	MessageDb db;
	static LinearLayout progress;
	private ListView mListView;
	private MyAdapter mAdapter;
	private View foot;
	Button buttonMore;
	BroadcastReceiver mBr;
	public String account;

	private final int RANK_URGENT = 1;
	private final int RANK_HIGH = 2;
	private final int RANK_NORAML = 3;
	private final int RANK_UNSET = 4;

	Cursor cursor;
	int idIndex;
	int rankIndex;
	int msgIndex;
	int timeIndex;

	private final int GET_MSG_ONCETIME = 20;
	public int mGotpageNum = 0;
	List<MessageStruct> mMsgList;
	int mNewMessageCount;
	public final int GET_MSG_SUCCESS = 0;
	public final int GET_MSG_ERROR = 1;
	public final int GET_MSG_COUNT_NONE = 2;
	public final int GET_MSG_COUNT_ERROR = 3;
	public final int GET_MSG_COUNT_SUCCESS = 4;
	public final String MSG_GET_NUM = "count";

	public final static int ERROR = -1;
	public final static int BUSY = -2;

	void initCursor() {
		account = "null";
		cursor = db.getCursor(null, account);
		idIndex = cursor.getColumnIndexOrThrow(MessageDb.ID);
		rankIndex = cursor.getColumnIndexOrThrow(MessageDb.RANK);
		msgIndex = cursor.getColumnIndexOrThrow(MessageDb.MSG);
		timeIndex = cursor.getColumnIndexOrThrow(MessageDb.TIME);
	}

	void initView() {
		/* 将footview的布局转换成View对象 */
		foot = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.main_message_foot, null, false);

		buttonMore = (Button) foot.findViewById(R.id.more);

		/* 给button添加点击事件监听器 */
		buttonMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getMessage(mGotpageNum + 1);
			}
		});

		/* 给listview添加footview */
		mListView.addFooterView(foot);

		// 获取数据
		getMessage(1);

		mAdapter = new MyAdapter(this);

		/* listview添加适配器 */
		mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_message);
		db = MessageDb.getDBInstanc(this);
		mListView = getListView();
		progress = (LinearLayout) findViewById(R.id.progress);
		progress.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mMsgList = new ArrayList<MessageStruct>();

		initView();

	}

	// ListView 中某项被选中后的逻辑
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		showInfo(mMsgList.get(position).getWardID(), 
				mMsgList.get(position).getRank(), 
				mMsgList.get(position).getTime(),  
				mMsgList.get(position).getMsg());
	}

	/**
	 * listview中点击按键弹出对话框
	 */
	public void showInfo(int wardId, int rank, String time, String msg) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		//TODO
		switch (rank) {
		case RANK_NORAML:
			break;
		case RANK_HIGH:
			break;
		case RANK_URGENT:
			break;
		case RANK_UNSET:
			break;
		}
		dlg.setIcon(MainBoard.accoutID2HeadImg.get(wardId));
		dlg.setTitle(MainBoard.accoutID2Name.get(wardId));
		dlg.setMessage(msg+"\n"+time);
		dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dlg.show();

	}

	public final class ViewHolder {
		public ImageView head;
		public TextView name;
		public TextView msg;
		public TextView time;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mMsgList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.main_message_list,
						null);
				holder.head = (ImageView) convertView.findViewById(R.id.head);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.msg = (TextView) convertView.findViewById(R.id.msg);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int rank = (Integer) mMsgList.get(position).getRank();
			switch (rank) {
			case RANK_NORAML:
				convertView.setBackgroundColor(getResources().getColor(
						R.color.main_message_rank_normal));
				break;
			case RANK_HIGH:
				convertView.setBackgroundColor(getResources().getColor(
						R.color.main_message_rank_high));
				break;
			case RANK_URGENT:
				convertView.setBackgroundColor(getResources().getColor(
						R.color.main_message_rank_urgent));
				break;
			case RANK_UNSET:
				convertView.setBackgroundColor(getResources().getColor(
						R.color.main_message_rank_unset));
				break;
			}
			BitmapDrawable bmpD = null;
			if ((bmpD = MainBoard.accoutID2HeadImg.get((mMsgList.get(position)
					.getWardID()))) != null)
				holder.head.setBackgroundDrawable(bmpD);
			holder.name.setText(MainBoard.accoutID2Name.get((mMsgList
					.get(position).getWardID())));
			holder.msg.setText(mMsgList.get(position).getMsg());
			holder.time.setText(mMsgList.get(position).getTime());

			return convertView;
		}

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_MSG_SUCCESS:
				Bundle data = msg.getData();
				int count = data.getInt(MSG_GET_NUM);
				if (count < GET_MSG_ONCETIME) {
					buttonMore.setClickable(false);
					if (mGotpageNum == 1 && count == 0)
						buttonMore.setText("没有信息");
					else
						buttonMore.setText("没有更多信息了");
				}
				mAdapter.notifyDataSetChanged(); // 通知适配器重新适配
				progress.setVisibility(View.INVISIBLE);
				break;
			case GET_MSG_ERROR:
				Toast.makeText(MainMessage.this,
						getResources().getString(R.string.getting_msg_failed),
						Toast.LENGTH_SHORT).show();
				progress.setVisibility(View.INVISIBLE);
				break;
			case GET_MSG_COUNT_SUCCESS:
				Toast.makeText(MainMessage.this,
						mNewMessageCount + "条新信息，正在接收...", Toast.LENGTH_SHORT)
						.show();
				progress.setVisibility(View.INVISIBLE);
				getMessage(1);
				break;
			case GET_MSG_COUNT_NONE:
				Toast.makeText(
						MainMessage.this,
						getResources().getString(R.string.getting_new_msg_none),
						Toast.LENGTH_SHORT).show();
				progress.setVisibility(View.INVISIBLE);
				break;
			case GET_MSG_COUNT_ERROR:
				Toast.makeText(
						MainMessage.this,
						getResources().getString(
								R.string.getting_new_msg_failed),
						Toast.LENGTH_SHORT).show();
				progress.setVisibility(View.INVISIBLE);
				break;
			}
		}
	};

	public static int getMessageCount(Context context) {
		MyHttpClient mhc = new MyHttpClient(context);
		String result = mhc.getMessageCount();
		if (!Util.IsStringValuble(result)) {
			return ERROR;
		}
		return Integer.parseInt(result);
	}

	private void getMessage(final int page) {
		if (progress.getVisibility() == View.VISIBLE)
			return;
		progress.setVisibility(View.VISIBLE);
		if (page == 1) {
			mMsgList.clear();
		}
		new Thread() {
			public void run() {
				MyHttpClient mhc = new MyHttpClient(MainMessage.this);
				String result = mhc.getMessage(page, GET_MSG_ONCETIME);
				try {
					List<MessageStruct> msgList = JSONUtil
							.json2MessageContextList(result);
					if (msgList != null) {
						mMsgList.addAll(msgList);
						mGotpageNum = page;
						Message msg = new Message();
						msg.what = GET_MSG_SUCCESS;
						Bundle data = new Bundle();
						data.putInt(MSG_GET_NUM, msgList.size());
						msg.setData(data);
						mHandler.sendMessage(msg);
						return;
					}
				} catch (JSONException e) {
					Log.e(TAG, "" + e);
				}
				mHandler.sendEmptyMessage(GET_MSG_ERROR);
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		if (cursor != null)
			cursor.close();
		super.onDestroy();
	}

	protected void onResume() {
//		if (!MyApplication.account.equals(account)) {
//			account = MyApplication.account;
//			getMessage(1);
//		}
		mBr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "-------------onReceive:" + intent.getAction());
				String action = intent.getAction();
				if (action.equals(MainBoard.ACTION_WARD_CHANGE)) {
//					getMessage(1);
					MainBoard.setProgressVisible(View.INVISIBLE);
				}/* else if (action.equals(MainBoard.ACTION_REFRESH)) {
					if (progress.getVisibility() == View.VISIBLE)
						return;
					progress.setVisibility(View.VISIBLE);
					new Thread() {
						@Override
						public void run() {
							mNewMessageCount = getMessageCount(MainMessage.this);
							if (mNewMessageCount == ERROR) {
								mHandler.sendEmptyMessage(GET_MSG_COUNT_ERROR);
							} else if (mNewMessageCount == 0) {
								mHandler.sendEmptyMessage(GET_MSG_COUNT_NONE);
							} else {
								mHandler.sendEmptyMessage(GET_MSG_COUNT_SUCCESS);
							}
						}
					}.start();
				}*/
			}
		};
		registerReceiver(mBr, new IntentFilter(MainBoard.ACTION_WARD_CHANGE));
//		registerReceiver(mBr, new IntentFilter(MainBoard.ACTION_REFRESH));

//		MainBoard.setRefreshStatus(View.VISIBLE,
//				getResources().getString(R.string.get_new_message));
//		MainBoard.setSwitchStatus(View.INVISIBLE, "所有成员信息");

		super.onResume();
	}

	protected void onPause() {
		unregisterReceiver(mBr);
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "-------onBackPressed-----");
		moveTaskToBack(true);
		this.getParent().moveTaskToBack(true);
		// super.onBackPressed();
	}

}
