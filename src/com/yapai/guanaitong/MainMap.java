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
    	    {//����һ��Handler�����ڴ��������߳���UI��ͨѶ
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case 0:
    	        	pd.show();//��ʾ���ȶԻ���        	
    	        	break;
    	        case 1:
    	        	pd.hide();//���ؽ��ȶԻ��򣬲���ʹ��dismiss()��cancel(),�����ٴε���show()ʱ����ʾ�ĶԻ���СԲȦ���ᶯ��
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
//        wv.getSettings().setJavaScriptEnabled(true);//����JS
        wv.setScrollBarStyle(0);//���������Ϊ0���ǲ������������ռ䣬��������������ҳ��
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
        
    	pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("��ͼ�����У����Ժ�");
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
