package com.yapai.guanaitong;

import java.util.HashMap;

import com.yapai.guanaitong.Login.myAdapter;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class MainBoard extends TabActivity{
	private RadioGroup group;
	private TabHost tabHost;
	private LinearLayout headerAndAcount;
	private TextView account;
	private ImageView header;
	public static final String TAB_MAP="tabMap";
	public static final String TAB_MES="tabMes";
	public static final String TAB_SET="tabSet";
	public static final String TAB_STATUS="tabStatus";
	
	Object[] accounts;
	public HashMap<String,String> list;
	public myAdapter adapter;
	ListView listView;
	public PopupWindow pop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_board);
		
		list=new HashMap<String, String>();
		/////TEST
		list.put("123", "abc");
		list.put("456", "def");
		/////
		
		group = (RadioGroup)findViewById(R.id.main_radio);
		headerAndAcount = (LinearLayout) findViewById(R.id.headerAndAcount);
		header = (ImageView) findViewById(R.id.header);
		account = (TextView) findViewById(R.id.account);
		account.setText(MyApplication.account);
		
		headerAndAcount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pop==null){
					if(adapter==null){
						adapter=new myAdapter();
						listView=new ListView(MainBoard.this);
						pop=new PopupWindow(listView, headerAndAcount.getWidth(), LayoutParams.WRAP_CONTENT);
						listView.setAdapter(adapter);
						pop.showAsDropDown(headerAndAcount);
					}
					else{
						accounts=list.keySet().toArray();
						adapter.notifyDataSetChanged();
						pop=new PopupWindow(listView, headerAndAcount.getWidth(), LayoutParams.WRAP_CONTENT);
						pop.showAsDropDown(headerAndAcount);
					}
				}
				else{
					pop.dismiss();
					pop=null;
				}
			}
		});
		
		tabHost = getTabHost();
	    tabHost.addTab(tabHost.newTabSpec(TAB_STATUS)
	    		.setIndicator(TAB_STATUS)
	    		.setContent(new Intent(this,MainStatus.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MAP)
	                .setIndicator(TAB_MAP)
	                .setContent(new Intent(this,MainMap.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_MES)
	                .setIndicator(TAB_MES)
	                .setContent(new Intent(this,MainMessage.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_SET)
	    		.setIndicator(TAB_SET)
	    		.setContent(new Intent(this,MainSetting.class)));
	    group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
	}
	
    //下拉框Adapter
    class myAdapter extends BaseAdapter {
    	LayoutInflater mInflater;
    	public myAdapter() {
    		mInflater=LayoutInflater.from(MainBoard.this);
    		accounts=list.keySet().toArray();
    		// TODO Auto-generated constructor stub
    	}

    	@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return accounts.length;
    	}

    	@Override
    	public Object getItem(int position) {
    		// TODO Auto-generated method stub
    		return null;
    	}

    	@Override
    	public long getItemId(int position) {
    		// TODO Auto-generated method stub
    		return position;
    	}

    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		// TODO Auto-generated method stub
    		Holder holder=null;
    		if(convertView==null){
    			convertView=mInflater.inflate(R.layout.main_board_popup, null);
    			holder=new Holder();
    			holder.view=(TextView)convertView.findViewById(R.id.account);
    			holder.button=(ImageView)convertView.findViewById(R.id.header);
    			convertView.setTag(holder);
    		}
    		else{
    			holder=(Holder) convertView.getTag();
    		}
    		if(holder!=null){
    			convertView.setId(position);
    			holder.setId(position);
    			holder.view.setText(accounts[position].toString());
    			holder.button.setBackgroundResource(R.drawable.header);
    			holder.view.setOnTouchListener(new OnTouchListener() {
    				@Override
    				public boolean onTouch(View v, MotionEvent event) {
    					menuClicked(position);
    					return true;
    				}
    			});
    			holder.button.setOnTouchListener(new OnTouchListener() {
    				@Override
    				public boolean onTouch(View v, MotionEvent event) {
    					menuClicked(position);
    					return true;
    				}
    			});
    		}
    		return convertView;
    	}
    	
    	void menuClicked(int position){
			if(pop != null){
				pop.dismiss();
				pop = null;
			}
			account.setText(accounts[position].toString());
			//TODO	设置头像		
			header.setBackgroundResource(R.drawable.header);
    	}
    	
    	class Holder{
    		TextView view;
    		ImageView button;
    		
    		void setId(int position){
    			view.setId(position);
    			button.setId(position);
    		}
    	}

    }
}
