package com.yapai.guanaitong.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yapai.guanaitong.struct.*;

public class JSONUtil {
	//Status
	private final static String USERID = "";
	private final static String HEADER = "";

	//Message
	private final static String MSGID = "";
	private final static String RANK = "";
	private final static String MSG = "";
	private final static String TIME = "";
	
	public static Status json2Status(String json) throws JSONException{
		Status st = new Status();
		JSONObject jsonObject=new JSONObject(json);
		st.setUserID(jsonObject.getInt(USERID));
		st.setHeader(jsonObject.getString(HEADER));
		return st;
	}
	
	public static List<Message> json2MessageList(String json) throws JSONException{
		List<Message> msgList = new ArrayList<Message>();
		Message msg = new Message();
		JSONArray jsonArray = new JSONArray(json);
		for(int i=0; i< jsonArray.length(); i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			msg.setMsgID(jsonObject.getInt(MSGID));
			msg.setRank(jsonObject.getInt(RANK));
			msg.setMsg(jsonObject.getString(MSG));
			msg.setTime(jsonObject.getString(TIME));
			msgList.add(msg);
		}
		return msgList;
	}
	
}
