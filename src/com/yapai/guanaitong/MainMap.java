package com.yapai.guanaitong;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainMap extends Activity {
	WebView wv;
	Handler handler;
	LinearLayout mProgress;
	
//	String URL = "http://ditu.aliyun.com/jsdoc/map/example/phone/mark.html";
	String URL = "file:///android_asset/ditu.html";
	
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
        
        //获得屏幕大小
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        Display dip = wm.getDefaultDisplay();
        int width =dip.getWidth() - 16;
        int height = dip.getHeight() - 200;
        URL = URL + "?width="+width+"&height="+height;
        
        loadURL(wv, URL);
        
	}
	
	void initWebView(){
		wv=(WebView)findViewById(R.id.wv);
		wv.setBackgroundColor(0);
		wv.setBackgroundResource(R.drawable.default_bg);
        wv.getSettings().setJavaScriptEnabled(true);//可用JS
        wv.setScrollBarStyle(0);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        wv.setWebViewClient(new WebViewClient(){   
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            	loadURL(view,url);//载入网页
                return true;   
            }//重写点击动作,用webview载入
 
        });
        wv.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view,int progress){//载入进度改变而触发 
             	if(progress==100){
            		handler.sendEmptyMessage(1);//如果全部载入,隐藏进度对话框
            	}   
                super.onProgressChanged(view, progress);   
            }   
        });
        
	}
	
    public void loadURL(final WebView view,final String url){
//    	new Thread(){
//        	public void run(){
        		handler.sendEmptyMessage(0);//显示进度
        		view.loadUrl(url);//载入网页
        	}
//        }.start();
//    }

}
