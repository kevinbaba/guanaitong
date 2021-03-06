package com.yapai.guanaitong.ui;

import java.util.List;

import org.apache.http.cookie.Cookie;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;
import com.yapai.guanaitong.util.Config;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMap extends Activity implements OnClickListener{
	final String TAG = "MainMap";
	static WebView wv;
	static Button newestPos;
	static Button path;
	static Button range;
	static Handler handler;
	LinearLayout mProgress;
	TextView loadinghint;
	final float PERCENT_HORI = 0.95f;
	final float PERCENT_VER = 0.62f;
	
	final static int DISPLAY_PROGRESSBAR = 0;
	final static int HIDE_PROGRESSBAR = 1;
	
	final long WEBVIEW_CACHE_TIME = (1000*60*60*24)*30L; //?天
	final String LAST_CLEAR_TIME = "lastClearTime";
	String mLoadingHint;
	
//	String URL_INDEX = "file:///android_asset/index.html";
	String URL_INDEX = Config.MAP_URL_INDEX;
	String MAP_URL_TRACK = Config.MAP_URL_TRACK;
	
	private String mCurUrl;
	BroadcastReceiver mBr;
	private String account;
	
	int width, height;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_map);
		
		mProgress = (LinearLayout)findViewById(R.id.progress);
		loadinghint = (TextView)findViewById(R.id.loadinghint);
		newestPos = (Button)findViewById(R.id.newest_pos);
		path = (Button)findViewById(R.id.path);
		range = (Button)findViewById(R.id.range);
		
		newestPos.setOnClickListener(this);
		path.setOnClickListener(this);
		range.setOnClickListener(this);
		
		mProgress.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		initWebView();

		handler=new Handler(){
        	public void handleMessage(Message msg)
    	    {//定义一个Handler，用于处理下载线程与UI间通讯
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case DISPLAY_PROGRESSBAR:
    	        	if(mLoadingHint != null)
    	        		loadinghint.setText(mLoadingHint);
    	        	mProgress.setVisibility(View.VISIBLE);//显示进度条
					MainBoard.setProgressVisible(View.INVISIBLE);
    	        	break;
    	        case HIDE_PROGRESSBAR:
    	        	mProgress.setVisibility(View.INVISIBLE);
    	        	break;
    	        }
    	      }
    	      super.handleMessage(msg);
    	    }
        };
        
        setMapSize();
        
        String url = URL_INDEX+"?"+"width="+width+"&height="+height;
        mCurUrl = url;
        
	}
	
	void setMapSize(){
        //设置地图显示大小
        width = (int) (MyApplication.width / MyApplication.density * PERCENT_HORI);
        height = (int) (MyApplication.height / MyApplication.density * PERCENT_VER);
	}
	
	void addCookie(){
		List<Cookie> cookies = Login.cookieStore.getCookies();  
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
		//为WebView添加Cookie
		addCookie();
		
		wv=(WebView)findViewById(R.id.wv);
		wv.requestFocus();

		wv.setBackgroundColor(0);
//		wv.setBackgroundResource(R.drawable.default_bg);
		
        wv.getSettings().setJavaScriptEnabled(true);//可用JS
        wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //打开cache
        wv.addJavascriptInterface(new Contact(this), "contact");
        wv.setScrollBarStyle(/*View.SCROLLBARS_OUTSIDE_OVERLAY*/0);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        wv.setWebViewClient(new WebViewClient(){   
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            	loadURL(view,url);//载入网页
                return true;   
            }//重写点击动作,用webview载入
 
        });
        wv.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view,int progress){//载入进度改变而触发 
             	if(progress==100){
            		handler.sendEmptyMessage(HIDE_PROGRESSBAR);//如果全部载入,隐藏进度条
            	}   
                super.onProgressChanged(view, progress);   
            }
        });
		clearCache();
	}
	
    public void loadURL(final WebView view,final String url){
    	//如在非主线程中执行，会出现警告
//    	new Thread(){
//        	public void run(){
        		handler.sendEmptyMessage(DISPLAY_PROGRESSBAR);//显示进度
        		mCurUrl = url;
        		view.loadUrl(url);//载入网页
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
				Log.d(TAG, "-------------onReceive:"+intent.getAction());
				String action = intent.getAction();
				if(action.equals(MainBoard.ACTION_WARD_CHANGE))
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

	private void clearCache() {
		if(MyApplication.needClearCache) {
			wv.clearCache(true);
		}
//	    //每隔"WEBVIEW_CACHE_TIME"时间清理一次cache
//		SharedPreferences settings=getPreferences(Activity.MODE_PRIVATE);
//		SharedPreferences.Editor editor = settings.edit();
//        long lastClearTime = settings.getLong(LAST_CLEAR_TIME, 0);
//        long now = System.currentTimeMillis();
//        if (lastClearTime == 0) {
//        	lastClearTime = now;
//        	editor.putLong(LAST_CLEAR_TIME, now);
//        	editor.commit();
//        }
//        Log.d(TAG, "now:"+now+",lastClearTime:"+lastClearTime+" WEBVIEW_CACHE_TIME:"+WEBVIEW_CACHE_TIME);
//        if (now - lastClearTime > WEBVIEW_CACHE_TIME){
//        	Log.d(TAG, "clearCache...");
//        	wv.clearCache(true);
//        	editor.putLong(LAST_CLEAR_TIME, now);
//        	editor.commit();
//        }
	}
	
	@Override
	public void onClick(View v) {
		if(v == newestPos){
			getNewPosition();
			mLoadingHint = (String) getResources().getText(R.string.getting_newest_pos)
					+ "\n"
					+ getResources().getString(R.string.wait_tip);
			handler.sendEmptyMessage(DISPLAY_PROGRESSBAR);
		}else if(v == path){
	        String url = MAP_URL_TRACK+"?"+"width="+width+"&height="+height;
			loadURL(wv, url);
		}else if(v == range){
			Log.d(TAG, "333");
		}
	}

	public void getNewPosition(){
		wv.loadUrl("javascript:api_ajax.getNewPosition()");
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
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "-------onBackPressed-----");
		moveTaskToBack(true);
		this.getParent().moveTaskToBack(true);
		// super.onBackPressed();
	}

}
