package com.yapai.guanaitong.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MessageServer extends Service {
	final String TAG = "MessageServer";
	final int CHECK_CYCLE_TIME = 60 * 1000;
	int msgIDSaved;	//数据库中最新的信息

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//返回最新获得信息的ID，没有则返回-1
	public int getNewMessage(){
		synchronized (this) {
			Log.d(TAG, "getNewMessage...");
			return -1;
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
	    			getNewMessage();
	    			try {
						Thread.sleep(CHECK_CYCLE_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	}
        }.start();
	}

}
