package com.yapai.guanaitong.struct;

import java.io.Serializable;

public class Status implements Serializable {
	private static final long serialVersionUID = -730941814174312851L;
	
	private int userID;
	private String header;
	
	public int getUserID(){
		return userID;
	}
	
	public String getHeader(){
		return header;
	}
	
	public void setUserID(int userID){
		this.userID = userID;
	}
	
	public void setHeader(String header){
		this.header = header;
	}

}
