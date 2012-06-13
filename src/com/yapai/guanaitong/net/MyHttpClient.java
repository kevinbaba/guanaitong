package com.yapai.guanaitong.net;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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
import com.yapai.guanaitong.util.Config;

public class MyHttpClient { 
	public DefaultHttpClient mHttpClient;
	final String TAG = "MyHttpClient";
	
	final String HOST = Config.HOST;
	final String LOGIN_URL = Config.CLIENT_LOGIN_URL;
	final String GET_PASSWORD_TOKEN_URL = Config.CLIENT_GET_PASSWORD_TOKEN_URL;
	final String STATUS_URL = Config.CLIENT_STATUS_URL;
	public String WARD_CHANGE_LOGIN = Config.WARD_CHANGE_LOGIN;
	
	public static Cookie cookie = null; 
	Context mContext;
	
	public MyHttpClient(Context context) {
		mContext = context;
		mHttpClient = MyApplication.httpClient;    //获取HttpClient实例  
	}

	//Login----------------------------------------------------
	public String CheckAccount(String name, String pwd){
		Map<String, String> params = new HashMap<String, String>();
		params.put("account", name);
		params.put("password", pwd);
		return connectGetString(params, LOGIN_URL);
	}
	
	public String GetPasswordToken(){
		return connectGetString(null, GET_PASSWORD_TOKEN_URL);
	}
	//Login end----------------------------------------------------
	
	//Status ----------------------------------------------------
	public String getUserStatus(){
		return connectGetString(null, STATUS_URL);
	}
	//Status end----------------------------------------------------
	
	//MainBoard ----------------------------------------------------
	public String switchward(int id){
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(id));
		return connectGetString(params, WARD_CHANGE_LOGIN);
	}
	//MainBoard end----------------------------------------------------
	
	//common ---------------------------------------------------------
	public boolean downLoadFile(String url, String name){
		try {
			HttpEntity entiry = connectGetEntity(null, url);
			if (entiry == null)
				return false;
			InputStream is = entiry.getContent();
			FileOutputStream fos=mContext.openFileOutput(name, Context.MODE_PRIVATE);
			byte[] buf = new byte[1024];  
			int len = -1;  
			while ((len = is.read(buf)) != -1) {  
			    fos.write(buf, 0, len);  
			}  
			is.close();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	//common end ---------------------------------------------------------

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
			is.close();
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
		baos.close();
		return new String(baos.toByteArray());  
	}  
}
