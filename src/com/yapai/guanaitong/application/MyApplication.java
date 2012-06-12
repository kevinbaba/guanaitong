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
import com.yapai.guanaitong.struct.Login;
import android.util.Log;

public class MyApplication extends Application {
	private final String TAG = "MyApplication";
    public static DefaultHttpClient httpClient; 
    BasicCookieStore cookieStore = new BasicCookieStore();
    public static DatabaseHelper mdbHelper;
    public static Login login = null;
    public static String account;
	public static Cookie cookie = null; 
    
    @Override  
    public void onCreate() {  
        super.onCreate();  
        httpClient = new DefaultHttpClient();  
        mdbHelper=new DatabaseHelper(this);
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
    private HttpClient createHttpClient() {  
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
    
    public void setCookies(){
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();  
		Log.d(TAG, "cookies:"+cookies);
		if (!cookies.isEmpty()) {  
		    for (int i = 0; i < cookies.size(); i++) {  
		        cookie = cookies.get(i);
		        cookieStore.addCookie(cookie);
		    }  
		}
		httpClient.setCookieStore(cookieStore);
    }

}
