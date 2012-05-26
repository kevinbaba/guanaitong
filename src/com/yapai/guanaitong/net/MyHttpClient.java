package com.yapai.guanaitong.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.yapai.guanaitong.application.MyApplication;

public class MyHttpClient {
	public static DefaultHttpClient mHttpClient;
	final String TAG = "MyHttpClient";
//	final String HOST = "http://192.168.0.244/";
//	final String HOST = "http://192.168.1.100/";
//	final String HOST = "http://192.168.2.115/";
	public final static String HOST = "http://m.uhome.co/";
	
	final String LOGIN_URL = "http://m.uhome.co/api/login";
	final String GET_PASSWORD_TOKEN_URL = "http://m.uhome.co/api/get_token";
	final String STATUS_URL = "http://m.uhome.co/api/status";
//	final String LOGIN_URL = HOST;
//	final String GET_PASSWORD_TOKEN_URL = HOST;
	
	public static Cookie cookie = null; 
	Context mContext;
	
	public MyHttpClient(Context context) {
		mContext = context;
		mHttpClient = MyApplication.httpClient;    //获取HttpClient实例  
	}
	
	//Login----------------------------------------------------
	public String CheckAccount(String name, String pwd){
		String result = null;
		Map<String, String> params = new HashMap<String, String>();
		params.put("account", name);
		params.put("password", pwd);
		result = connectGetString(params, LOGIN_URL);
		return result;
	}
	
	public String GetPasswordToken(){
		String result = null;
		result = connectGetString(null, GET_PASSWORD_TOKEN_URL);
		return result;
	}
	//Login end----------------------------------------------------
	
	//Status ----------------------------------------------------
	public String getUserStatus(){
		return null;
	}
	//Status end----------------------------------------------------
	
	public HttpEntity connectGetEntity(Map<String, String> params, String uhost){
		try{
			String host = (uhost != null)?uhost:HOST;
			String paramsStr = mapToStringParams(params);
			String url;
			if (paramsStr == null){
				url = host;
			}else{
				url = host+ "?" + mapToStringParams(params);
			}
		    Log.d(TAG, "URL:"+url);
		    HttpGet get = new HttpGet(url);
		    HttpResponse response = mHttpClient.execute(get);  
		    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		        return response.getEntity();
		    }
		} catch (Exception e) {  
		    e.printStackTrace();  
		}
		return null;  
	}
	
	public String connectGetString(Map<String, String> params, String uhost) {
		try {
			HttpEntity entiry = connectGetEntity(params, uhost);
			if (entiry == null)
				return null;
			InputStream is = entiry.getContent();
			String result = inStream2String(is);
			Log.d(TAG, "result:" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String mapToStringParams(Map<String, String> params) {
		if (params == null) return null;
		StringBuffer buf = new StringBuffer();
		for (String key : params.keySet()) {
			buf.append(key + "=" + params.get(key) + "&");
		}
		return buf.toString();
	}
	
	//将输入流转换成字符串  
	private String inStream2String(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		byte[] buf = new byte[1024];  
		int len = -1;  
		while ((len = is.read(buf)) != -1) {  
		    baos.write(buf, 0, len);  
		}  
		return new String(baos.toByteArray());  
	}  
}
