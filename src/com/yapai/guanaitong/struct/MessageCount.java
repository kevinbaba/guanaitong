package com.yapai.guanaitong.struct;

import java.io.Serializable;

public class MessageCount implements Serializable {
	private static final long serialVersionUID = -4386818327352488510L;
	
	private int wardId;
	private int count;

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getWardId() {
		return wardId;
	}
	public void setWardId(int wardId) {
		this.wardId = wardId;
	}

}
