package com.yapai.guanaitong.ui;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;
import com.yapai.guanaitong.db.LoginDb;
import com.yapai.guanaitong.net.MyHttpClient;
import com.yapai.guanaitong.service.MessageServer;
import com.yapai.guanaitong.util.EncryptUtil;
import com.yapai.guanaitong.util.JSONUtil;
import com.yapai.guanaitong.util.Util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Login extends Activity implements OnClickListener,OnFocusChangeListener,OnTouchListener {

	LoginDb db;
	RelativeLayout mRelative;
	ImageButton mPopupImageButton;
	public PopupWindow pop;
	public EditText mAccountsEditText;
	EditText mPassEditText;
	CheckBox mRemPassCheck;
	CheckBox mAutoLoadCheck;
	Button mLoginButton;
	public myAdapter adapter;
	public HashMap<String,String> list;
	String mAccount_last_logined;
	Object[] accounts;
	TextView mNotify;
	ListView listView;
	InputMethodManager imm;
	String mPassToken;

	final String TAG = "login";
	final String SUCCESSRETURN = "SUCCESS";
	final String LASTLOGIN = "lastLogin";
	final static String AUTOLOAD = "autoLoad";
	final String LOADINFO = "result";
	final String USERID = "userid";
	final String ACCOUNT = "account";
	final String PASSWORD = "password";
	final int CONNECTTIMEOUT = 0;
	final int FAILED = 1;
	final int SUCCESS = 2;
	
	final static String ISLOGINOUT = "isLoginOut";
	boolean isLoginOut;
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch(v.getId()){
		case R.id.login_edit_pwd:
			if(hasFocus){//获得焦点，则获得密码
				String account=mAccountsEditText.getText().toString();
				if(account.equals("")){
					break;//
				}
				if(list.containsKey(account)){
					mPassEditText.setText(list.get(account));
				}else{
					mPassEditText.setText("");
				}
				mPassEditText.selectAll();
			}
			break;
		case R.id.login_edit_account:
			if(hasFocus){
//				mAccountsEditText.setText("");
//				mPassEditText.setText("");
				mAccountsEditText.selectAll();
			}
			break;
		}
	}

	void LoadSuccess(Message msg) {
    	Bundle data = msg.getData();
    	String account = data.getString(ACCOUNT);
    	String pass = data.getString(PASSWORD);

		Cursor cursor = db.getCursor(new String[] { LoginDb.ACCOUNTS }, new String[] { account });
		if (mRemPassCheck.isChecked()) {
			// 保存密码
			if (cursor.getCount() > 0) {
				db.update(account, pass);
			} else {
				db.insert(account, pass);
			}
			list.put(account, pass);// 重新替换或者添加记录
		} else {
			// 不保存密码
			if (cursor.getCount() > 0) {
				db.update(account, "");
			}
			list.put(account, "");// 重新替换或者添加记录
		}
		safeReleaseCursor(cursor);

		// 保存最后登陆的用户
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LASTLOGIN, account);
		editor.putBoolean(AUTOLOAD, mAutoLoadCheck.isChecked());
		editor.commit();

		// 进入主界面,启动服务
		Intent intent = new Intent(this, MainBoard.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "" + e);
		}
		intent.setClass(this, MessageServer.class);
		this.startService(intent);

		finish();
	}
	
	void setUIenable(boolean enable){
		if(enable){
			mAccountsEditText.setEnabled(true);
			mAccountsEditText.setFocusable(true);
			mPassEditText.setEnabled(true);	
			mPassEditText.setFocusable(true);
			mLoginButton.setEnabled(true);
			mRemPassCheck.setClickable(true);
			mAutoLoadCheck.setClickable(true);
			mPopupImageButton.setClickable(true);
		}else{
			mAccountsEditText.setEnabled(false);
			mAccountsEditText.setFocusable(false);
			mPassEditText.setEnabled(false);
			mPassEditText.setFocusable(false);
			mLoginButton.setEnabled(false);
			mRemPassCheck.setClickable(false);
			mAutoLoadCheck.setClickable(false);
			mPopupImageButton.setClickable(false);
		}
	}
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what)
            {
            case CONNECTTIMEOUT:
            	mNotify.setText("连接服务器失败");
            	break;
            case FAILED:
            	Bundle data = msg.getData();
            	String result = data.getString(LOADINFO);
            	mNotify.setText(result);
            	break;
            case SUCCESS:
            	LoadSuccess(msg);
            	break;
            }
			setUIenable(true);
			
			super.handleMessage(msg);
		}
	};
	
	void checkAccount(final String account, final String password) {
		new Thread() {
			public void run() {
				String pass = password;
				MyHttpClient mhc = new MyHttpClient(Login.this);
				
				mPassToken = mhc.GetPasswordToken();
				Log.d(TAG, "mPassToken:"+mPassToken);
				if (! Util.IsStringValuble(mPassToken)) {
					mHandler.sendEmptyMessage(CONNECTTIMEOUT);
					return;
				}
				
				//set Cookie
				((MyApplication)Login.this.getApplication()).setCookies(); 
				
				//加密密码
				if(pass.length() != 32){
					//条件：用户密码最大长度必须小于３２位
					pass = EncryptUtil.md5(pass);
				}
				String tokenPass = EncryptUtil.md5(mPassToken+pass);
				String result = mhc.CheckAccount(account, tokenPass);
				if (! Util.IsStringValuble(result)) {
					mHandler.sendEmptyMessage(CONNECTTIMEOUT);
					return;
				} else {
					com.yapai.guanaitong.struct.Login login = null;
					try {
						login = JSONUtil.json2Login(result);
						MyApplication.login = login;
					} catch (JSONException e) {
						Log.e(TAG, ""+e);
						return;
					}
					String info = login.getInfo();
					if (SUCCESSRETURN.equals(info)) {
					Bundle data = new Bundle();
					data.putString(LOADINFO, info);
					data.putString(ACCOUNT, account);
					data.putString(PASSWORD, pass);
					Message msg = new Message();
					msg.setData(data);
					msg.what = SUCCESS;
					mHandler.sendMessage(msg);
					} else {
						Bundle data = new Bundle();
						data.putString(LOADINFO, info);
						Message msg = new Message();
						msg.setData(data);
						msg.what = FAILED;
						mHandler.sendMessage(msg);
						return;
					}
				}
			}
		}.start();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.popupwindow:
			if(pop==null){
				if(adapter==null){
					adapter=new myAdapter();
					listView=new ListView(Login.this);
					pop=new PopupWindow(listView, mAccountsEditText.getWidth(), LayoutParams.WRAP_CONTENT);
					listView.setAdapter(adapter);
					pop.showAsDropDown(mAccountsEditText);
				}
				else{
					accounts=list.keySet().toArray();
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
			String account=mAccountsEditText.getText().toString();
			String pass=mPassEditText.getText().toString();
			if(account.equals("") || pass.equals("")){
				mNotify.setText("用户名和密码不能为空");
				break;
			}
			
			imm.hideSoftInputFromWindow(mAccountsEditText.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(mPassEditText.getWindowToken(), 0);
			
			// 验证用户
			mNotify.setText("正在验证用户...");
			setUIenable(false);
			checkAccount(account, pass);
			break;
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        isLoginOut = intent.getBooleanExtra(ISLOGINOUT, false);
        Log.d(TAG, "isLoginOut:"+isLoginOut);
        setContentView(R.layout.login);
		db = LoginDb.getDBInstanc(this);
        prepareAccountsList();
        mRelative = (RelativeLayout)findViewById(R.id.mRelativeLayout);
        mPopupImageButton=(ImageButton)findViewById(R.id.popupwindow);
        mRemPassCheck=(CheckBox)findViewById(R.id.login_cb_savepwd);
        mAutoLoadCheck = (CheckBox)findViewById(R.id.login_cb_autoload);
        mLoginButton=(Button)findViewById(R.id.login_btn_login);
        mAccountsEditText=(EditText)findViewById(R.id.login_edit_account);
        mPassEditText=(EditText)findViewById(R.id.login_edit_pwd);
        mNotify = (TextView)findViewById(R.id.Notify);
        mRelative.setOnTouchListener(this);
        mPassEditText.setOnFocusChangeListener(this);
        mAccountsEditText.setOnFocusChangeListener(this);
        mPopupImageButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        
        SharedPreferences settings=getPreferences(Activity.MODE_PRIVATE);
        String lastLogin = settings.getString(LASTLOGIN, null);
        Boolean autoLoad = settings.getBoolean(AUTOLOAD, false);
        String lastLoginpwd = list.get(lastLogin);
        
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //默认不显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        if(lastLogin != null && lastLoginpwd != null){
        	mAccountsEditText.setText(lastLogin);
        	mPassEditText.setText(lastLoginpwd);
            if(autoLoad && !isLoginOut){
            	onClick(mLoginButton);
            }
        }
    }
    
    // 准备已保存的用户列表
    private void prepareAccountsList(){
    	list=new HashMap<String, String>();
    	Cursor cursor=db.getCursor(null);
    	int accountsindex=cursor.getColumnIndexOrThrow(LoginDb.ACCOUNTS);
    	int passindex=cursor.getColumnIndexOrThrow(LoginDb.PASSWORD);
    	String account;
    	String pass;
    	if(cursor.getCount()>0){
    		do{
    			account=cursor.getString(accountsindex);
    			pass=cursor.getString(passindex);
    			list.put(account, pass);
        	}while(cursor.moveToNext());
    	}
    	safeReleaseCursor(cursor);
    }
    
    private void safeReleaseCursor(Cursor cursor){
    	cursor.close();
    	cursor=null;
    }
    
    //下拉框Adapter
    class myAdapter extends BaseAdapter {
    	LayoutInflater mInflater;
    	public myAdapter() {
    		mInflater=LayoutInflater.from(Login.this);
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
    			holder.view.setText(accounts[position].toString());
    			holder.view.setOnTouchListener(new OnTouchListener() {
    				
    				@Override
    				public boolean onTouch(View v, MotionEvent event) {
    					// TODO Auto-generated method stub
    					if(pop != null){
	    					pop.dismiss();
	    					pop = null;
    					}
    					mAccountsEditText.setText(accounts[position].toString());
    					mPassEditText.setText(list.get(accounts[position]));
    					return true;
    				}
    			});
    			
    			holder.button.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					String account=accounts[position].toString();
    					list.remove(account);
    					db.delete(account);
    					accounts=list.keySet().toArray();
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
		// 收起下拉框和软键盘
		if(pop != null){
			pop.dismiss();
			pop = null;
		}
		imm.hideSoftInputFromWindow(mAccountsEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mPassEditText.getWindowToken(), 0);
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
    
}
