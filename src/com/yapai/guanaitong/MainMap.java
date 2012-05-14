package com.yapai.guanaitong;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainMap extends Activity {
	WebView wv;
	Handler handler;
	ProgressDialog pd;
//	final String URL = "http://ditu.aliyun.com/jsdoc/map/example/phone/mark.html";
	final String URL = "file:///android_asset/ditu.html";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_map);
		
		initWebView();

		handler=new Handler(){
        	public void handleMessage(Message msg)
    	    {//定义一个Handler，用于处理下载线程与UI间通讯
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case 0:
    	        	pd.show();//显示进度对话框        	
    	        	break;
    	        case 1:
    	        	pd.hide();//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
    	        	break;
    	        }
    	      }
    	      super.handleMessage(msg);
    	    }
        };
        
        loadURL(wv, URL);
        
	}
	
	void initWebView(){
		wv=(WebView)findViewById(R.id.wv);
//        wv.getSettings().setJavaScriptEnabled(true);//可用JS
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
        
    	pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("地图载入中，请稍候！");
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
