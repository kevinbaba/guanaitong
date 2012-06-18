package com.yapai.guanaitong.service;

import com.yapai.guanaitong.ui.MainMessage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Kevin
 * ��Ϣ����
 * ÿ CHECK_CYCLE_TIME ���һ�Σ�������µ���Ϣ������notify
 */
public class MessageServer extends Service {
	final String TAG = "MessageServer";
	final int CHECK_CYCLE_TIME = 60 * 1000;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	//�������»����Ϣ��ID��û���򷵻�-1
	public int checkNewMessage(){
		synchronized (this) {
			Log.d(TAG, "checkNewMessage...");
			int num = MainMessage.getMessageCount(this);
			if(num == MainMessage.ERROR){
				Log.e(TAG, "checkNewMessage error");
				return -1;
			}
			//TODO
			return 0;
		}
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "create...");
		super.onCreate();
		
		//
        new Thread(){
	    	public void run()
	    	{
	    		while (true) {
	    			checkNewMessage();
	    			try {
						Thread.sleep(CHECK_CYCLE_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	}
        }.start();
	}

}
