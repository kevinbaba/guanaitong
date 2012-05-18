package com.yapai.guanaitong.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;

import android.util.Log;

import com.yapai.guanaitong.MyApplication;

public class MyHttpClient {
	HttpClient mHttpClient;
	final String TAG = "MyHttpClient";
	final String HOST = "http://192.168.0.244/";
//	final String HOST = "http://192.168.1.100/";
	
	public MyHttpClient() {
		mHttpClient = MyApplication.httpClient;    //获取HttpClient实例  
	}
	
	public String CheckAccount(String name, String pwd){
		String result = null;
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("pwd", pwd);
		result = connectGetString(params, null);
		return result;
	}
	
	public HttpEntity connectGetEntity(Map<String, String> params, String uhost){
		try{
			String host = (uhost != null)?uhost:HOST;
			String url = host+ "?" + mapToStringParams(params);
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
	
	public String connectGetString(Map<String, String> params, String uhost){
		try{
			String host = (uhost != null)?uhost:HOST;
			String url = host+ "?" + mapToStringParams(params);
		    Log.d(TAG, "URL:"+url);
		    HttpGet get = new HttpGet(url);
		    HttpResponse response = mHttpClient.execute(get);  
		    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
		        InputStream is = response.getEntity().getContent();  
		        String result = inStream2String(is); 
		        Log.d(TAG, "result:"+result);
		        return result;
		    }
		} catch (Exception e) {  
		    e.printStackTrace();  
		}
		return null;  
	}
	
	private String mapToStringParams(Map<String, String> params) {
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
