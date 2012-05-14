package com.yapai.guanaitong;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainBoard extends TabActivity{
	private RadioGroup group;
	private TabHost tabHost;
	private TextView title;
	public static final String TAB_MAP="tabMap";
	public static final String TAB_MES="tabMes";
	public static final String TAB_SET="tabSet";
	public static final String TAB_MORE="tabMore";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_board);
		group = (RadioGroup)findViewById(R.id.main_radio);
		title = (TextView) findViewById(R.id.title);
		title.setText(MyApplication.userName+"ÒÑµÇÂ½");
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_MAP)
	                .setIndicator(TAB_MAP)
	                .setContent(new Intent(this,MainMap.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_MES)
	                .setIndicator(TAB_MES)
	                .setContent(new Intent(this,MainMessage.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_SET)
	    		.setIndicator(TAB_SET)
	    		.setContent(new Intent(this,MainSetting.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_MORE)
	    		.setIndicator(TAB_MORE)
	    		.setContent(new Intent(this,MainMore.class)));
	    group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_button0:
					tabHost.setCurrentTabByTag(TAB_MAP);
					break;
				case R.id.radio_button1:
					tabHost.setCurrentTabByTag(TAB_MES);
					break;
				case R.id.radio_button2:
					tabHost.setCurrentTabByTag(TAB_SET);
					break;
				case R.id.radio_button3:
					tabHost.setCurrentTabByTag(TAB_MORE);
					break;

				default:
					break;
				}
			}
		});
	}
}
