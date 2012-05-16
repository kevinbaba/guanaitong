package com.yapai.guanaitong;

import java.util.HashMap;
import com.yapai.guanaitong.db.LoginDb;
import com.yapai.guanaitong.net.MyHttpClient;
import com.yapai.guanaitong.util.EncryptUtil;

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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener,OnFocusChangeListener,OnTouchListener {

	LoginDb db;
	RelativeLayout mRelative;
	ImageButton mPopupImageButton;
	public PopupWindow pop;
	public EditText mAccountsEditText;
	EditText mPassEditText;
	CheckBox mRemPassCheck;
	Button mLoginButton;
	public myAdapter adapter;
	public HashMap<String,String> list;
	String mAccount_last_logined;
	Object[] account;
	TextView mNotify;
	ListView listView;

	final String TAG = "login";
	final String FAILEDRETURN = "failed";
	final String LASTLOGIN = "lastLogin";
	final String USERID = "userid";
	final String ACCOUNT = "account";
	final String PASSWORD = "password";
	final int CONNECTTIMEOUT = 0;
	final int ACCOUNTORPWDERROR = 1;
	final int SUCCESS = 2;
	
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
    	String uerID = data.getString(USERID);
    	String account = data.getString(ACCOUNT);
    	String pass = data.getString(PASSWORD);

		MyApplication.userName = account;
		MyApplication.userID = Integer.parseInt(uerID);

		Cursor cursor = db.getCursorArgs(new String[] { LoginDb.KEY },
				new String[] { account });
		int keyindex = cursor.getColumnIndexOrThrow(LoginDb.KEY);
		if (mRemPassCheck.isChecked()) {
			// 保存密码
			if (cursor.getCount() > 0) {
				int id = cursor.getInt(keyindex);
				db.update(id, pass);
			} else {
				db.insert(account, pass);
			}
			list.put(account, pass);// 重新替换或者添加记录
		} else {
			// 不保存密码
			if (cursor.getCount() > 0) {
				int id = cursor.getInt(keyindex);
				db.update(id, "");
			}
			list.put(account, "");// 重新替换或者添加记录
		}
		safeReleaseCursor(cursor);

		// 保存最后登陆的用户
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LASTLOGIN, account);
		editor.commit();

		// 进入主界面
		Intent intent = new Intent(this, MainBoard.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "" + e);
		}

		finish();
	}
	
	void setUIenable(boolean enable){
		if(enable){
			mAccountsEditText.setEnabled(true);
			mPassEditText.setEnabled(true);	
			mLoginButton.setEnabled(true);
			mRemPassCheck.setClickable(true);
			mPopupImageButton.setClickable(true);
		}else{
			mAccountsEditText.setEnabled(false);
			mPassEditText.setEnabled(false);	
			mLoginButton.setEnabled(false);
			mRemPassCheck.setClickable(false);
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
            case ACCOUNTORPWDERROR:
            	mNotify.setText("用户名或密码错误");
            	break;
            case SUCCESS:
            	LoadSuccess(msg);
            	break;
            }
			setUIenable(true);
			
			super.handleMessage(msg);
		}
	};
	
	void checkAccount(final String account, final String pass) {
		new Thread() {
			public void run() {

				MyHttpClient mhc = new MyHttpClient();
				String result = mhc.CheckAccount(account, pass);
				if (result == null) {
					// Toast.makeText(this, "连接服务器失败",
					// Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(CONNECTTIMEOUT);
					return;
				} else if (FAILEDRETURN.equals(result)) {
					// Toast.makeText(this, "用户名或密码错误",
					// Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(ACCOUNTORPWDERROR);
					return;
				} else {
					Bundle data = new Bundle();
					data.putString(USERID, result);
					data.putString(ACCOUNT, account);
					data.putString(PASSWORD, pass);
					Message msg = new Message();
					msg.setData(data);
					msg.what = SUCCESS;
					mHandler.sendMessage(msg);
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
			String account=mAccountsEditText.getText().toString();
			String pass=mPassEditText.getText().toString();
			if(account.equals("") || pass.equals("")){
				mNotify.setText("用户名和密码不能为空");
				break;
			}
			
			if(pass.length() != 32){
				//条件：用户密码最大长度必须小于３２位
				pass = EncryptUtil.md5(pass);
			}

			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
        setContentView(R.layout.login);
		db = LoginDb.getDBInstanc(this);
        prepareAccountsList();
        mRelative = (RelativeLayout)findViewById(R.id.mRelativeLayout);
        mPopupImageButton=(ImageButton)findViewById(R.id.popupwindow);
        mRemPassCheck=(CheckBox)findViewById(R.id.login_cb_savepwd);
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
        String lastLoginpwd = list.get(lastLogin);
        if(lastLogin != null && lastLoginpwd != null){
        	mAccountsEditText.setText(lastLogin);
        	mPassEditText.setText(lastLoginpwd);
        }
        
        //默认不显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    private void prepareAccountsList(){
    	list=new HashMap<String, String>();
    	Cursor cursor=db.getCursor(LoginDb.KEY,LoginDb.ACCOUNTS,LoginDb.PASSWORD);
    	int accountsindex=cursor.getColumnIndexOrThrow(LoginDb.ACCOUNTS);
    	int passindex=cursor.getColumnIndexOrThrow(LoginDb.PASSWORD);
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
    }
    
    private void safeReleaseCursor(Cursor cursor){
    	cursor.close();
    	cursor=null;
    }
    
    class myAdapter extends BaseAdapter {
    	LayoutInflater mInflater;
    	public myAdapter() {
    		mInflater=LayoutInflater.from(Login.this);
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
    					Cursor cursor=db.getCursorArgs(new String[]{LoginDb.KEY}, new String[]{accounts});
    					int keyindex=cursor.getColumnIndexOrThrow(LoginDb.KEY);
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
		if(pop != null){
			pop.dismiss();
			pop = null;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
    
}
