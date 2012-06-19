package com.yapai.guanaitong.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yapai.guanaitong.struct.*;

public class JSONUtil {
	//Login
	public final static String STATUS = "status";
	public final static String INFO = "info";
	public final static String VERSION = "version";
	public final static String IDENTITY = "identity";
	public final static String GROUP_IDENTITY = "group_identity";
	public final static String LOGINBY = "login_by";
	public final static String WARD_PROFILE = "ward_profile";
	public final static String GUARDIAN = "guardian";
	public final static String WARDS = "wards";
	public final static String PHONE = "phone";
	public final static String NAME = "name";
	public final static String HEAD_48 = "head_48";
	public final static String LOGIN_ID = "id";
	public final static String NICKNAME = "nickname";
	public final static String DEFAULT = "default";
	public final static String GENDER = "gender";
	

	//Status
	private final static String SYSTYPE = "sys_type";
	private final static String SYSPLATFORM = "sys_platform";
	private final static String SYSPOWERON = "sys_poweron";
	private final static String SYSPOWVERVOLUMN = "sys_powervolumn";
	private final static String SYSSTATUS = "sys_status";
	private final static String SYSFMDURATION = "sys_fmduration";
	private final static String SYSFMFAVORITE = "sys_fmfavorite";
	private final static String SYSCALLOUT = "sys_callout";
	private final static String SYSCALLIN = "sys_callin";
	private final static String REPOTTIME = "report_time";
	private final static String SAFEREGIONOUT = "safe_region_out";
	private final static String SAFEREGIONIN = "safe_region_in";

	//Message
	private final static String MSGID = "id";
	private final static String RANK = "level";
	private final static String MSG = "detail";
	private final static String TIME = "report_time";
	private final static String MSGWARDID = "id";
	private final static String MSGCOUNT = "count";
	
	public static LoginStruct json2Login(String json) throws JSONException{
		LoginStruct login = new LoginStruct();
		JSONObject jsonObject=new JSONObject(json);
		login.setInfo(jsonObject.getString(INFO));
		if(! "SUCCESS".equals(login.getInfo())){		//如果登陆不成功
			return login;
		}
		login.setVersion(jsonObject.getInt(VERSION));
		login.setIdentity(jsonObject.getInt(IDENTITY));
		login.setGroup_identity(jsonObject.getInt(GROUP_IDENTITY));
		login.setLogin_by(jsonObject.getString(LOGINBY));
		if(jsonObject.getString(WARD_PROFILE).length() != 0)
			login.setWard_profile(jsonObject.getJSONObject(WARD_PROFILE));
		if(jsonObject.getString(GUARDIAN).length() != 0)
			login.setGuardian(jsonObject.getJSONObject(GUARDIAN));
		if(jsonObject.getString(WARDS).length() != 0)
			login.setWards(jsonObject.getJSONArray(WARDS));
		return login;
	}
	
	public static LoginWardProfile json2LoginWardProfile(JSONObject obj) throws JSONException{
		if(obj == null) return null;
		LoginWardProfile lwp = new LoginWardProfile();
		lwp.setPhone(obj.getString(PHONE));
		lwp.setName(obj.getString(NAME));
		lwp.setHead_48(obj.getString(HEAD_48));
		return lwp;
	}
	
	public static LoginGuardian json2LoginGuardian(JSONObject obj) throws JSONException{
		if(obj == null) return null;
		LoginGuardian lg = new LoginGuardian();
		lg.setPhone(obj.getString(PHONE));
		lg.setName(obj.getString(NAME));
		return lg;
	}
	
	public static List <LoginWards> json2LoginWardsList(JSONArray jsonArray) throws JSONException{
		if(jsonArray == null) return null;
		List <LoginWards> list = new ArrayList <LoginWards>();
		for(int i=0; i< jsonArray.length(); i++){
			JSONObject obj=jsonArray.getJSONObject(i);
			LoginWards lw = new LoginWards();
			lw.setPhone(obj.getString(PHONE));
			lw.setId(obj.getInt(LOGIN_ID));
			lw.setNickName(obj.getString(NICKNAME));
			lw.setHead_48(obj.getString(HEAD_48));
			lw.setGender(obj.getString(GENDER));
			list.add(lw);
		}
		return list;
	}
	
	public static Status json2Status(String json) throws JSONException{
		Status st = new Status();
		JSONObject jsonObject=new JSONObject(json);
		st.setSysType(jsonObject.getString(SYSTYPE));
		st.setSysPlatform(jsonObject.getString(SYSPLATFORM));
		st.setSysPoweron(jsonObject.getString(SYSPOWERON));
		st.setSysPowerVolumn(jsonObject.getString(SYSPOWVERVOLUMN));
		st.setSysStatus(jsonObject.getString(SYSSTATUS));
		st.setSysFmDuration(jsonObject.getString(SYSFMDURATION));
		st.setSysFmFavorite(jsonObject.getString(SYSFMFAVORITE));
		st.setSysCallOut(jsonObject.getString(SYSCALLOUT));
		st.setSysCallIn(jsonObject.getString(SYSCALLIN));
		st.setReportTime(jsonObject.getString(REPOTTIME));
		st.setSafeRegionOut(jsonObject.getString(SAFEREGIONOUT));
		st.setSafeRegionIn(jsonObject.getString(SAFEREGIONIN));

		return st;
	}
	
	public static List<MessageStruct> json2MessageContextList(String json) throws JSONException{
		if(json == null) return null;
		List<MessageStruct> msgList = new ArrayList<MessageStruct>();
		JSONArray jsonArray = new JSONArray(json);
		for(int i=0; i< jsonArray.length(); i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			MessageStruct msg = new MessageStruct();
			msg.setMsgID(jsonObject.getInt(MSGID));
			msg.setRank(jsonObject.getInt(RANK));
			msg.setMsg(jsonObject.getString(MSG));
			msg.setTime(jsonObject.getString(TIME));
			msgList.add(msg);
		}
		return msgList;
	}
	
	public static List<MessageCount> json2MessageCount(String json) throws JSONException{
		if(json == null) return null;
		List<MessageCount> msgCountList = new ArrayList<MessageCount>();
		JSONArray jsonArray = new JSONArray(json);
		for(int i=0; i< jsonArray.length(); i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			MessageCount msgCount = new MessageCount();
			msgCount.setWardId(jsonObject.getInt(MSGWARDID));
			msgCount.setCount(jsonObject.getInt(MSGCOUNT));
		}
		return msgCountList;
	}
	
}
