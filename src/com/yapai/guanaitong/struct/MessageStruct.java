package com.yapai.guanaitong.struct;

import java.io.Serializable;

public class MessageStruct implements Serializable{
	private static final long serialVersionUID = -5372920398604536268L;
	
	private int msgID;
	private int wardID;
	private String wardPhone;
	private int rank;
	private String msg;
	private String time;
	
	public int getMsgID(){
		return msgID;
	}
	
	public int getRank(){
		return rank;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public String getTime(){
		return time;
	}
	
	public void setMsgID(int msgID){
		this.msgID = msgID;
	}
	
	public void setRank(int rank){
		this.rank = rank;
	}
	
	public void setMsg(String msg){
		this.msg = msg;
	}
	
	public void setTime(String time){
		this.time = time;
	}

	public int getWardID() {
		return wardID;
	}

	public void setWardID(int wardID) {
		this.wardID = wardID;
	}

	public String getWardPhone() {
		return wardPhone;
	}

	public void setWardPhone(String wardPhone) {
		this.wardPhone = wardPhone;
	}

}
