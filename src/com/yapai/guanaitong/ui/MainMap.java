package com.yapai.guanaitong.ui;

import java.nio.MappedByteBuffer;
import java.util.List;

import org.apache.http.cookie.Cookie;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;
import com.yapai.guanaitong.net.MyHttpClient;
import com.yapai.guanaitong.util.Config;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMap extends Activity implements OnClickListener, OnTouchListener{
	final String TAG = "MainMap";
	static WebView wv;
	static TextView newestPos;
	static TextView path;
	static TextView range;
	static Handler handler;
	LinearLayout mProgress;
	TextView loadinghint;
	final int MARGIN_HORI = 15;
	final int MARGIN_VER = 280;
	
	final static int DISPLAY_PROGRESSBAR = 0;
	final static int HIDE_PROGRESSBAR = 1;
	
	final long WEBVIEW_CACHE_TIME = (1000*60*60*24)*30L; //?��
	final String LAST_CLEAR_TIME = "lastClearTime";
	String mLoadingHint;
	
//	String URL_INDEX = "file:///android_asset/index.html";
	String URL_INDEX = Config.MAP_URL_INDEX;
	
	int width, height;
	private String mCurUrl;
	BroadcastReceiver mBr;
	private String account;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_map);
		
		mProgress = (LinearLayout)findViewById(R.id.progress);
		loadinghint = (TextView)findViewById(R.id.loadinghint);
		newestPos = (TextView)findViewById(R.id.newest_pos);
		path = (TextView)findViewById(R.id.path);
		range = (TextView)findViewById(R.id.range);
		
		newestPos.setOnClickListener(this);
		path.setOnClickListener(this);
		range.setOnClickListener(this);
		newestPos.setOnTouchListener(this);
		path.setOnTouchListener(this);
		range.setOnTouchListener(this);
		
		initWebView();

		handler=new Handler(){
        	public void handleMessage(Message msg)
    	    {//����һ��Handler�����ڴ��������߳���UI��ͨѶ
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case DISPLAY_PROGRESSBAR:
    	        	if(mLoadingHint != null)
    	        		loadinghint.setText(mLoadingHint);
    	        	mProgress.setVisibility(View.VISIBLE);//��ʾ������
    	        	newestPos.setEnabled(false);
    	        	path.setEnabled(false);
    	        	range.setEnabled(false);
    	        	wv.setClickable(false);
    	        	break;
    	        case HIDE_PROGRESSBAR:
    	        	mProgress.setVisibility(View.INVISIBLE);
    	        	newestPos.setEnabled(true);
    	        	path.setEnabled(true);
    	        	range.setEnabled(true);
    	        	wv.setClickable(true);
    	        	break;
    	        }
    	      }
    	      super.handleMessage(msg);
    	    }
        };
        
        getDisplaySize();
        
        String url = URL_INDEX+"?"+"width="+width+"&height="+height;
        mCurUrl = url;
        
	}
	
	void getDisplaySize(){
        //��õ�ͼ��ʾ��С
		//TODO ���õķ�ʽ��ȡ��С
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        Display dip = wm.getDefaultDisplay();
        DisplayMetrics metric = new DisplayMetrics();
        dip.getMetrics(metric);
        width =metric.widthPixels;
        height = metric.heightPixels;
        float density = metric.density;
        Log.d(TAG, "width:"+width+",height:"+height +",density:"+density);
        width = (int) (width / density - MARGIN_HORI);
        height = (int) ((height - MARGIN_VER) / density);
	}
	
	void addCookie(){
		List<Cookie> cookies = MyApplication.cookieStore.getCookies();  
		if (! cookies.isEmpty()){  
		    CookieSyncManager.createInstance(this);  
		    CookieManager cookieManager = CookieManager.getInstance();  
		        //sync all the cookies in the httpclient with the webview by generating cookie string  
		    for (Cookie cookie : cookies){  
		        String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();  
		        cookieManager.setCookie(cookie.getDomain(), cookieString);  
		        CookieSyncManager.getInstance().sync();  
		    }  
		}  
	}
	
	void initWebView(){
		addCookie();
		
		wv=(WebView)findViewById(R.id.wv);
		wv.requestFocus();

		wv.setBackgroundColor(0);
//		wv.setBackgroundResource(R.drawable.default_bg);
		
        wv.getSettings().setJavaScriptEnabled(true);//����JS
//        wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //��cache
        wv.addJavascriptInterface(new Contact(this), "contact");
        wv.setScrollBarStyle(/*View.SCROLLBARS_OUTSIDE_OVERLAY*/0);//���������Ϊ0���ǲ������������ռ䣬��������������ҳ��
        wv.setWebViewClient(new WebViewClient(){   
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            	loadURL(view,url);//������ҳ
                return true;   
            }//��д�������,��webview����
 
        });
        wv.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view,int progress){//������ȸı������ 
             	if(progress==100){
            		handler.sendEmptyMessage(HIDE_PROGRESSBAR);//���ȫ������,���ؽ�����
            	}   
                super.onProgressChanged(view, progress);   
            }
        });
	}
	
    public void loadURL(final WebView view,final String url){
    	//���ڷ����߳���ִ�У�����־���
//    	new Thread(){
//        	public void run(){
        		handler.sendEmptyMessage(DISPLAY_PROGRESSBAR);//��ʾ����
        		mCurUrl = url;
        		view.loadUrl(url);//������ҳ
        	}
//        }.start();
//    }
    
	protected void onResume() {
		if(!MyApplication.account.equals(account)){
			account = MyApplication.account;
			loadURL(wv, mCurUrl);
		}
		mBr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.d(TAG, "-------------onReceive");
				loadURL(wv, mCurUrl);
			}
		};
		registerReceiver(mBr, new IntentFilter(MainBoard.ACTION_WARD_CHANGE)); 
		
		super.onResume();
	}
	
	protected void onPause() {
		unregisterReceiver(mBr);
		super.onPause();
	}
	
    @Override
    protected void onDestroy() {
    	clearCache();
    	super.onDestroy();
    }
	
    //ÿ��"WEBVIEW_CACHE_TIME"ʱ������һ��cache
	private void clearCache() {
		SharedPreferences settings=getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
        long lastClearTime = settings.getLong(LAST_CLEAR_TIME, 0);
        long now = System.currentTimeMillis();
        if (lastClearTime == 0) {
        	lastClearTime = now;
        	editor.putLong(LAST_CLEAR_TIME, now);
        	editor.commit();
        }
        Log.d(TAG, "now:"+now+",lastClearTime:"+lastClearTime+" WEBVIEW_CACHE_TIME:"+WEBVIEW_CACHE_TIME);
        if (now - lastClearTime > WEBVIEW_CACHE_TIME){
        	Log.d(TAG, "clearCache...");
        	wv.clearCache(true);
        	editor.putLong(LAST_CLEAR_TIME, now);
        	editor.commit();
        }
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		if(v == newestPos){
			if (action == MotionEvent.ACTION_DOWN)
				newestPos.setBackgroundResource(R.drawable.button_pressed);
			else if(action == MotionEvent.ACTION_UP)
				newestPos.setBackgroundResource(R.drawable.button_normal);
		}else if(v == path){
			if (action == MotionEvent.ACTION_DOWN)
				path.setBackgroundResource(R.drawable.button_pressed);
			else if(action == MotionEvent.ACTION_UP)
				path.setBackgroundResource(R.drawable.button_normal);
		}else if(v == range){
			if (action == MotionEvent.ACTION_DOWN)
				range.setBackgroundResource(R.drawable.button_pressed);
			else if(action == MotionEvent.ACTION_UP)
				range.setBackgroundResource(R.drawable.button_normal);
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		if(v == newestPos){
			getNewPosition();
			mLoadingHint = (String) getResources().getText(R.string.getting_newest_pos);
			handler.sendEmptyMessage(DISPLAY_PROGRESSBAR);
		}else if(v == path){
			getNewPositionTest();
		}else if(v == range){
			Log.d(TAG, "333");
		}
	}

	public void getNewPosition(){
		wv.loadUrl("javascript:api_ajax.getNewPosition()");
	}
	
	public void getNewPositionTest(){
		wv.loadUrl("javascript:api_ajax.getNewPositionTest()");
	}
 
	private class Contact {
		Context mContext;
		public Contact(Context context){
			mContext = context;
		}
		
		public void getNewPositionResult(final String result){
			if("SUCCESS".equals(result)){
				Toast.makeText(mContext, 
						mContext.getResources().getString(R.string.getting_newest_pos_sucess),
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(mContext, 
						result,
						Toast.LENGTH_SHORT).show();				
			}
//			newestPos.setText(mContext.getResources().getText(R.string.newest_pos));
			handler.sendEmptyMessage(HIDE_PROGRESSBAR);
		}
	}

}
