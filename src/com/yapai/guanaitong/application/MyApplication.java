package com.yapai.guanaitong.application;

import java.util.List;

import org.apache.http.HttpVersion;  
import org.apache.http.client.HttpClient;  
import org.apache.http.conn.ClientConnectionManager;  
import org.apache.http.conn.scheme.PlainSocketFactory;  
import org.apache.http.conn.scheme.Scheme;  
import org.apache.http.conn.scheme.SchemeRegistry;  
import org.apache.http.conn.ssl.SSLSocketFactory;  
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;  
import org.apache.http.params.BasicHttpParams;  
import org.apache.http.params.HttpParams;  
import org.apache.http.params.HttpProtocolParams;  
import org.apache.http.protocol.HTTP;  

import com.yapai.guanaitong.db.DatabaseHelper;
  
import android.app.Application;
import com.yapai.guanaitong.struct.LoginStruct;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class MyApplication extends Application {
	private final String TAG = "MyApplication";
    public static DefaultHttpClient httpClient; 
    public static DatabaseHelper mdbHelper;
    public static LoginStruct login = null;
    public static String account;
    public static boolean needClearCache = false;
    public static int width, height;
    public static float density;
    
    @Override  
    public void onCreate() {
        super.onCreate();
        httpClient = createHttpClient();
        mdbHelper=new DatabaseHelper(this);
		//TODO 更好的方式获取大小
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        Display dip = wm.getDefaultDisplay();
        DisplayMetrics metric = new DisplayMetrics();
        dip.getMetrics(metric);
        width =metric.widthPixels;
        height = metric.heightPixels;
        density = metric.density;
        Log.d(TAG, "width:"+width+",height:"+height +",density:"+density);
    }
      
    @Override  
    public void onLowMemory() {  
        super.onLowMemory();  
        this.shutdownHttpClient(); 
        mdbHelper.close();
    }  
      
    @Override  
    public void onTerminate() {  
        super.onTerminate();  
        this.shutdownHttpClient();  
        mdbHelper.close();
    }  
      
    //创建HttpClient实例  
    private DefaultHttpClient createHttpClient() {  
        HttpParams params = new BasicHttpParams();  
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);  
        HttpProtocolParams.setUseExpectContinue(params, true);  
          
        SchemeRegistry schReg = new SchemeRegistry();  
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));  
          
        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg);  
          
        return new DefaultHttpClient(connMgr, params);  
    }  
      
    //关闭连接管理器并释放资源  
    private void shutdownHttpClient() {  
        if (httpClient != null && httpClient.getConnectionManager() != null) {  
            httpClient.getConnectionManager().shutdown();  
        }  
    }  
      
    //对外提供HttpClient实例  
    public HttpClient getHttpClient() {
        return httpClient;  
    }
    
}
