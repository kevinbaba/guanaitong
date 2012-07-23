package com.yapai.guanaitong.beans;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginStruct implements Serializable {
	private static final long serialVersionUID = 7499872199515631966L;
	
	private String info;
	private int version;
	private int identity;
	private int group_identity;
	private String login_by;
	private JSONObject ward_profile;
	private JSONObject guardian;
	private JSONArray wards;

	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getGroup_identity() {
		return group_identity;
	}
	public void setGroup_identity(int group_identity) {
		this.group_identity = group_identity;
	}
	public String getLogin_by() {
		return login_by;
	}
	public void setLogin_by(String login_by) {
		this.login_by = login_by;
	}
	public JSONObject getWard_profile() {
		return ward_profile;
	}
	public void setWard_profile(JSONObject ward_profile) {
		this.ward_profile = ward_profile;
	}
	public JSONObject getGuardian() {
		return guardian;
	}
	public void setGuardian(JSONObject guardian) {
		this.guardian = guardian;
	}
	public JSONArray getWards() {
		return wards;
	}
	public void setWards(JSONArray wards) {
		this.wards = wards;
	}
	public int getIdentity() {
		return identity;
	}
	public void setIdentity(int identity) {
		this.identity = identity;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
}
