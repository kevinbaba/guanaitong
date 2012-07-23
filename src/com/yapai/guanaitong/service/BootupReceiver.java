/**
 * 开机即启动服务以接收信息
 */
package com.yapai.guanaitong.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent();
		i.setClass(context, MessageServer.class);
		context.startService(i);

	}

}
