package com.yapai.guanaitong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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
	
//	String URL = "http://ditu.aliyun.com/jsdoc/map/example/phone/mark.html";
	String URL_INDEX = "file:///android_asset/index.html";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_map);
		
		mProgress = (LinearLayout)findViewById(R.id.progress);
		
		initWebView();

		handler=new Handler(){
        	public void handleMessage(Message msg)
    	    {//����һ��Handler�����ڴ��������߳���UI��ͨѶ
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case 0:
    	        	mProgress.setVisibility(View.VISIBLE);//��ʾ������      	
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
	
	//js����
	//����Ҫ����js������ʹ�����ƣ�webView.loadUrl("javascript:fun()"); 
	private class Contact{
		int width;
		int height;
		public Contact(Context context){
	        //��õ�ͼ��ʾ��С
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
	
	void initWebView(){
		wv=(WebView)findViewById(R.id.wv);
		wv.requestFocus();
		wv.setBackgroundColor(0);
		wv.setBackgroundResource(R.drawable.default_bg);
        wv.getSettings().setJavaScriptEnabled(true);//����JS
        wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
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
            		handler.sendEmptyMessage(1);//���ȫ������,���ؽ��ȶԻ���
            	}   
                super.onProgressChanged(view, progress);   
            }   
        });
        
	}
	
    public void loadURL(final WebView view,final String url){
//    	new Thread(){
//        	public void run(){
        		handler.sendEmptyMessage(0);//��ʾ����
        		view.loadUrl(url);//������ҳ
        	}
//        }.start();
//    }

}
