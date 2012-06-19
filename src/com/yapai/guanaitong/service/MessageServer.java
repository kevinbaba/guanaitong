package com.yapai.guanaitong.service;

import com.yapai.guanaitong.ui.Login;
import com.yapai.guanaitong.ui.MainMessage;
import com.yapai.guanaitong.util.Util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * @author Kevin 消息推送 每 CHECK_CYCLE_TIME 检测一次，如果有新的消息，则发送notify
 */
public class MessageServer extends Service {
	final String TAG = "MessageServer";
	final int CHECK_CYCLE_TIME = 60 * 1000;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	// 返回最新获得信息的ID，没有则返回-1
	public void checkNewMessage() {
		new Thread() {
			public void run() {
				while (true) {
					int num = MainMessage.getMessageCount(MessageServer.this);
					Log.d(TAG, "checkNewMessage..." + num);
					if (num == MainMessage.ERROR) {
						Log.e(TAG, "checkNewMessage error");
					}
					try {
						Thread.sleep(CHECK_CYCLE_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Login.SUCCESS:
				checkNewMessage();
			default:
				break;
			}
		}
	};

	void checkAccount() {
		final String lastLogin = Login.getLastLogin(this);
		if (!Util.IsStringValuble(lastLogin)) {
			stopSelf();
			return;
		}
		final String pwd = Login.getAccountPwd(this, lastLogin);
		if (!Util.IsStringValuble(pwd)) {
			stopSelf();
			return;
		}
		Log.d(TAG, "checkAccount lastLogin:" + lastLogin + ",pwd:" + pwd);
		new Thread() {
			public void run() {
				Login.checkAccount(MessageServer.this, mHandler, lastLogin, pwd);
			}
		}.start();

	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (Login.mlogined) {
			checkNewMessage();
		} else {
			checkAccount();
		}
	}

}
