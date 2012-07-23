package com.yapai.guanaitong.beans;

import java.io.Serializable;

public class LoginWardProfile implements Serializable {
	private static final long serialVersionUID = -8166505939429422143L;
	
	private String phone;
	private String name;
	private String head_48;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHead_48() {
		return head_48;
	}
	public void setHead_48(String head_48) {
		this.head_48 = head_48;
	}
}
