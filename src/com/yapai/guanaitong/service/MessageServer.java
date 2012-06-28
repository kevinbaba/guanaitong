package com.yapai.guanaitong.service;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.ui.Login;
import com.yapai.guanaitong.ui.MainBoard;
import com.yapai.guanaitong.ui.MainMessage;
import com.yapai.guanaitong.util.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * @author Kevin ��Ϣ���� ÿ CHECK_CYCLE_TIME ���һ�Σ�������µ���Ϣ������notify
 */
public class MessageServer extends Service {
	final String TAG = "MessageServer";
	final int CHECK_CYCLE_TIME = 60 * 1000;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private Bitmap getResIcon(int resId) {
		Drawable icon = getResources().getDrawable(resId);
		if (icon instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) icon;
			return bd.getBitmap();
		} else {
			return null;
		}
	}

	private Bitmap generatorCountIcon(Bitmap icon, int count) {
		// ��ʼ������
		int iconSize = (int) getResources().getDimension(
				android.R.dimen.app_icon_size);
		Log.d(TAG, "the icon size is " + iconSize);
		Bitmap countIcon = Bitmap.createBitmap(iconSize, iconSize,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(countIcon);

		// ����ͼƬ
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// ������
		iconPaint.setFilterBitmap(true);// ������Bitmap�����˲���������������ѡ��Drawableʱ�����п���ݵ�Ч��
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		// ���ÿ���ݺ�ʹ���豸���ı��־�
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.GREEN);
		countPaint.setTextSize(20f);
		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText(String.valueOf(count), iconSize - 18, 25, countPaint);
		return countIcon;
	}
	
	//TODO �ظ�����Ϣ�Ͳ�Ҫ�ٷ�notify������, ��Ҫ����������һ�±�ʶ������ʱ��
	//TODO ͼ�����������ʾ
	public void sendNotifycation(int num){
		String msg = num+"������Ϣ";
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);               
		Notification n = new Notification(R.drawable.ic_launcher, msg, System.currentTimeMillis());             
		n.flags = Notification.FLAG_AUTO_CANCEL;                
		Intent i = new Intent(this, MainBoard.class);
		i.putExtra(MainBoard.GOTOMESSAGE, true);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(
		        this, 
		        R.string.app_name, 
		        i, 
		        PendingIntent.FLAG_UPDATE_CURRENT);
		                 
		n.setLatestEventInfo(
		        this,
		        getResources().getString(R.string.app_name), 
		        msg, 
		        contentIntent);
		nm.notify(R.string.app_name, n);
	}

	// �������»����Ϣ��ID��û���򷵻�-1
	public void checkNewMessage() {
		new Thread() {
			public void run() {
				while (true) {
					int num = MainMessage.getMessageCount(MessageServer.this);
					Log.d(TAG, "checkNewMessage..." + num);
					if (num == MainMessage.ERROR) {
						Log.e(TAG, "checkNewMessage error");
					}
					if (num > -1) {
						sendNotifycation(num);
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
	
	//TODO ��������ʱ��doTask

	void doTask(){
		if (Login.mlogined) {
			checkNewMessage();
		} else {
			checkAccount();
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		doTask();
	}

}
