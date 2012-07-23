package com.yapai.guanaitong.beans;

import java.io.Serializable;

public class LoginGuardian implements Serializable {
	private static final long serialVersionUID = -3222912887478435763L;

	private String phone;
	private String name;
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
}
