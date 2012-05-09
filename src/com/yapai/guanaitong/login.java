package com.yapai.guanaitong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.yapai.guanaitong.db.LoginDb;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class login extends Activity implements OnClickListener,OnFocusChangeListener,OnTouchListener {

	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.login_edit_pwd:
			if(hasFocus){//获得焦点，则获得密码
				String account=mAccountsEditText.getText().toString();
				if(account.equals("")){
					break;//
				}
				if(list.containsKey(account)){
					mPassEditText.setText(list.get(account));
				}
			}
			break;
		case R.id.login_edit_account:
			if(hasFocus){
				mAccountsEditText.setText("");
				mPassEditText.setText("");
			}
			break;
		}
	}
//	
	ListView listView;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.popupwindow:
			if(pop==null){
				if(adapter==null){
					adapter=new myAdapter();
					listView=new ListView(login.this);
					pop=new PopupWindow(listView, mAccountsEditText.getWidth(), LayoutParams.WRAP_CONTENT);
					listView.setAdapter(adapter);
					pop.showAsDropDown(mAccountsEditText);
				}
				else{
					account=list.keySet().toArray();
					adapter.notifyDataSetChanged();
					pop=new PopupWindow(listView, mAccountsEditText.getWidth(), LayoutParams.WRAP_CONTENT);
					pop.showAsDropDown(mAccountsEditText);
				}
			}
			else{
				pop.dismiss();
				pop=null;
			}
			break;
		case R.id.login_btn_login:
			if(mAccountsEditText.getText().toString().equals("")){
				break;
			}
			String account=mAccountsEditText.getText().toString();
			String pass=mPassEditText.getText().toString();
			LoginDb db=new LoginDb(login.this);
			db.open();
			Cursor cursor=db.getCursorArgs(new String[]{db.getKEY()}, new String[]{account});
			int keyindex=cursor.getColumnIndexOrThrow(db.getKEY());
			if(mRemPassCheck.isChecked()){
				//保存密码
				if(cursor.getCount()>0){
					int id=cursor.getInt(keyindex);
					safeReleaseCursor(cursor);
					db.update(id, pass);
					safeReleaseDatabase(db);
				}
				else {
					safeReleaseCursor(cursor);
					db.create(account, pass);
					safeReleaseDatabase(db);
				}
				list.put(account, pass);//重新替换或者添加记录
			}
			
			else{
				//不保存密码
				if(cursor.getCount()>0){
					int id=cursor.getInt(keyindex);
					safeReleaseCursor(cursor);
					db.update(id, "");
					safeReleaseDatabase(db);
				}
				else {
					safeReleaseCursor(cursor);
					db.create(account, "");
					safeReleaseDatabase(db);
				}
				
				list.put(account, "");//重新替换或者添加记录
			}
			mAccountsEditText.setText("");
			mPassEditText.setText("");
			break;
		}
	}

	LinearLayout popupLinear;
	ImageButton mPopupImageButton;
	public PopupWindow pop;
	public EditText mAccountsEditText;
	EditText mPassEditText;
	CheckBox mRemPassCheck;
	Button mLoginButton;
	public myAdapter adapter;
	public HashMap<String,String> list;
	Object[] account;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        prepare();
        mPopupImageButton=(ImageButton)findViewById(R.id.popupwindow);
        mRemPassCheck=(CheckBox)findViewById(R.id.login_cb_savepwd);
        mLoginButton=(Button)findViewById(R.id.login_btn_login);
        mAccountsEditText=(EditText)findViewById(R.id.login_edit_account);
        mPassEditText=(EditText)findViewById(R.id.login_edit_pwd);
        mPassEditText.setOnFocusChangeListener(this);
        mAccountsEditText.setOnFocusChangeListener(this);
        mPopupImageButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
    }
    
    private void prepare(){
    	list=new HashMap<String, String>();
    	LoginDb db=new LoginDb(this);
    	db.open();
    	Cursor cursor=db.getCursor(db.getKEY(),db.getACCOUNTS(),db.getPASSWORD());
    	int accountsindex=cursor.getColumnIndexOrThrow(db.getACCOUNTS());
    	int passindex=cursor.getColumnIndexOrThrow(db.getPASSWORD());
    	String accounts;
    	String pass;
    	if(cursor.getCount()>0){
    		do{
    			accounts=cursor.getString(accountsindex);
    			pass=cursor.getString(passindex);
    			list.put(accounts, pass);
        	}while(cursor.moveToNext());
    	}
    	safeReleaseCursor(cursor);
    	safeReleaseDatabase(db);
    }
    
    private void safeReleaseCursor(Cursor cursor){
    	cursor.close();
    	cursor=null;
    }
    
    
    private void safeReleaseDatabase(LoginDb db){
    	db.close();
    	db=null;
    }
    
    class myAdapter extends BaseAdapter {
    	LayoutInflater mInflater;
    	public myAdapter() {
    		mInflater=LayoutInflater.from(login.this);
    		account=list.keySet().toArray();
    		// TODO Auto-generated constructor stub
    	}

    	@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return account.length;
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
    			convertView=mInflater.inflate(R.layout.login_popup, null);
    			holder=new Holder();
    			holder.view=(TextView)convertView.findViewById(R.id.mQQ);
    			holder.button=(ImageButton)convertView.findViewById(R.id.mQQDelete);
    			convertView.setTag(holder);
    		}
    		else{
    			holder=(Holder) convertView.getTag();
    		}
    		if(holder!=null){
    			convertView.setId(position);
    			holder.setId(position);
    			holder.view.setText(account[position].toString());
    			holder.view.setOnTouchListener(new OnTouchListener() {
    				
    				@Override
    				public boolean onTouch(View v, MotionEvent event) {
    					// TODO Auto-generated method stub
    					if(pop != null){
	    					pop.dismiss();
	    					pop = null;
    					}
    					mAccountsEditText.setText(account[position].toString());
    					mPassEditText.setText(list.get(account[position]));
    					return true;
    				}
    			});
    			
    			holder.button.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					String accounts=account[position].toString();
    					list.remove(accounts);
    					LoginDb db=new LoginDb(login.this);
    					db.open();
    					Cursor cursor=db.getCursorArgs(new String[]{db.getKEY()}, new String[]{accounts});
    					int keyindex=cursor.getColumnIndexOrThrow(db.getKEY());
    					int id=cursor.getInt(keyindex);
    					cursor.close();
    					db.delete(id);
    					account=list.keySet().toArray();
    					adapter.notifyDataSetChanged();
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d("dddd", "onTouch:"+v+":"+event);
		return false;
	}
    
}