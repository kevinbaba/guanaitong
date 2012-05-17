package com.yapai.guanaitong;

import java.util.HashMap;

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
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainBoard extends TabActivity{
	private RadioGroup group;
	private TabHost tabHost;
	private LinearLayout title;
	private TextView account;
	private ImageView header;
	public static final String TAB_MAP="tabMap";
	public static final String TAB_MES="tabMes";
	public static final String TAB_SET="tabSet";
	public static final String TAB_MORE="tabMore";
	
	Object[] accounts;
	public HashMap<String,String> list;
	public PopupWindow pop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_board);
		group = (RadioGroup)findViewById(R.id.main_radio);
		title = (LinearLayout) findViewById(R.id.title);
		header = (ImageView) findViewById(R.id.header);
		account = (TextView) findViewById(R.id.account);
		account.setText(MyApplication.account);
		
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
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
	
    //ÏÂÀ­¿òAdapter
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
    			holder.button=(ImageButton)convertView.findViewById(R.id.header);
    			convertView.setTag(holder);
    		}
    		else{
    			holder=(Holder) convertView.getTag();
    		}
    		if(holder!=null){
    			convertView.setId(position);
    			holder.setId(position);
    			holder.view.setText(accounts[position].toString());
    			holder.view.setOnTouchListener(new OnTouchListener() {
    				
    				@Override
    				public boolean onTouch(View v, MotionEvent event) {
    					// TODO Auto-generated method stub
    					if(pop != null){
	    					pop.dismiss();
	    					pop = null;
    					}
    					//TODO
    					return true;
    				}
    			});

    		}
    		return convertView;
    	}
    	
    	class Holder{
    		TextView view;
    		ImageButton button;
    		
    		void setId(int position){
    			view.setId(position);
    			button.setId(position);
    		}
    	}

    }
}
