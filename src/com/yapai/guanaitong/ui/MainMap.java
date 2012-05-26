package com.yapai.guanaitong.ui;

import java.io.File;
import java.util.List;

import org.apache.http.cookie.Cookie;

import com.yapai.guanaitong.R;
import com.yapai.guanaitong.application.MyApplication;
import com.yapai.guanaitong.net.MyHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CacheManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainMap extends Activity {
	final String TAG = "MainMap";
	WebView wv;
	Handler handler;
	LinearLayout mProgress;
	final int MARGIN_HORI = 15;
	final int MARGIN_BOTTOM = 320;
	
	final long WEBVIEW_CACHE_TIME = (1000*60*60*24)*30L; //?天
	final String LAST_CLEAR_TIME = "lastClearTime";
	
//	String URL = "http://ditu.aliyun.com/jsdoc/map/example/phone/mark.html";
	String URL_INDEX = "http://m.uhome.co/api/position";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_map);
		
		mProgress = (LinearLayout)findViewById(R.id.progress);
		
		initWebView();

		handler=new Handler(){
        	public void handleMessage(Message msg)
    	    {//定义一个Handler，用于处理下载线程与UI间通讯
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case 0:
    	        	mProgress.setVisibility(View.VISIBLE);//显示进度条      	
    	        	break;
    	        case 1:
    	        	mProgress.setVisibility(View.INVISIBLE);
    	        	break;
    	        }
    	      }
    	      super.handleMessage(msg);
    	    }
        };
        
        loadURL(wv, URL_INDEX);
        
	}
	
	//js访问类
	//注：如需要调用js方法，使用类似：webView.loadUrl("javascript:fun()"); 
	private class Contact{
		int width;
		int height;
		public Contact(Context context){
	        //获得地图显示大小
	        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
	        Display dip = wm.getDefaultDisplay();
	        DisplayMetrics metric = new DisplayMetrics();
	        dip.getMetrics(metric);
	        width =metric.widthPixels;
	        height = metric.heightPixels;
	        float density = metric.density;
	        width = (int) (width / density - MARGIN_HORI);
	        height = (int) ((height - MARGIN_BOTTOM) / density);
		}
		
		public int getWidth(){
			Log.d(TAG, "width:"+width);
			return width;
		}
		public int getHeight(){
			Log.d(TAG, "height:"+height);
			return height;
		}
	}
	
	void addCookie(){
		List<Cookie> cookies = MyHttpClient.mHttpClient.getCookieStore().getCookies();  
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
            		handler.sendEmptyMessage(1);//如果全部载入,隐藏进度条
            	}   
                super.onProgressChanged(view, progress);   
            }   
        });
	}
	
    public void loadURL(final WebView view,final String url){
    	//如在非主线程中执行，会出现警告
//    	new Thread(){
//        	public void run(){
        		handler.sendEmptyMessage(0);//显示进度
        		view.loadUrl(url);//载入网页
        	}
//        }.start();
//    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	clearCache();
    	super.onDestroy();
    }
	
    //每隔"WEBVIEW_CACHE_TIME"时间清理一次cache
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

}
