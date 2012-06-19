package com.yapai.guanaitong.struct;

import java.io.Serializable;

public class Status implements Serializable {
	private static final long serialVersionUID = -730941814174312851L;

	private String sysType;
	private String sysPlatform;
	private String sysPoweron;
	private String sysPowerVolumn;
	private String sysStatus;
	private String sysFmDuration;
	private String sysFmFavorite;
	private String sysCallOut;
	private String sysCallIn;
	private String reportTime;
	private String safeRegionOut;
	private String safeRegionIn;

	public String getSafeRegionIn() {
		return safeRegionIn;
	}

	public String getSafeRegionOut() {
		return safeRegionOut;
	}

	public String getSysCallIn() {
		return sysCallIn;
	}

	public String getSysCallOut() {
		return sysCallOut;
	}

	public String getSysFmDuration() {
		return sysFmDuration;
	}

	public String getSysFmFavorite() {
		return sysFmFavorite;
	}

	public String getSysPlatform() {
		return sysPlatform;
	}

	public String getSysPoweron() {
		return sysPoweron;
	}

	public String getSysPowerVolumn() {
		return sysPowerVolumn;
	}

	public String getSysStatus() {
		return sysStatus;
	}

	public String getSysType() {
		return sysType;
	}

	public void setSafeRegionIn(String safeRegionIn) {
		this.safeRegionIn = safeRegionIn;
	}

	public void setSafeRegionOut(String safeRegionOut) {
		this.safeRegionOut = safeRegionOut;
	}

	public void setSysCallIn(String sysCallIn) {
		this.sysCallIn = sysCallIn;
	}

	public void setSysCallOut(String sysCallOut) {
		this.sysCallOut = sysCallOut;
	}

	public void setSysFmDuration(String sysFmDuration) {
		this.sysFmDuration = sysFmDuration;
	}

	public void setSysFmFavorite(String sysFmFavorite) {
		this.sysFmFavorite = sysFmFavorite;
	}

	public void setSysPlatform(String sysPlatform) {
		this.sysPlatform = sysPlatform;
	}

	public void setSysPoweron(String sysPoweron) {
		this.sysPoweron = sysPoweron;
	}

	public void setSysPowerVolumn(String sysPowerVolumn) {
		this.sysPowerVolumn = sysPowerVolumn;
	}

	public void setSysStatus(String sysStatus) {
		this.sysStatus = sysStatus;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getReportTime() {
		return reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

}
