package com.yapai.guanaitong.beans;

import java.io.Serializable;

public class LoginWards implements Serializable {
	private static final long serialVersionUID = -7604145385863744528L;
	
	private String phone;
	private int id;
	private int status;
	private String name;
	private String nickName;
	private int isDefault;
	private String head_48;
	private String gender;
	private int newMessage;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}
	public String getHead_48() {
		return head_48;
	}
	public void setHead_48(String head_48) {
		this.head_48 = head_48;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getNewMessage() {
		return newMessage;
	}
	public void setNewMessage(int newMessage) {
		this.newMessage = newMessage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
