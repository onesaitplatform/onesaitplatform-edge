package com.onesait.edge.engine.zigbee.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Alert {
	private State state;
	private String date;
	private String alertType;
	private String mac;
	private String devType;
	
	public Alert(State state, String alertType, String mac, String devType) {
		super();
		this.date = this.getFormatDate();
		this.state = state;
		this.alertType = alertType;
		this.mac = mac;
		this.setDevType(devType);
	}

	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}

	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getAlertType() {
		return alertType;
	}
	
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	
	public String getMac() {
		return mac;
	}
	
	public void setMac(String mac) {
		this.mac = mac;
	}
	

	private String getFormatDate() {
		
		String formattedDate = "";
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		formattedDate = format.format(new Date());
		
		return formattedDate;
	}

	public String getDevType() {
		return devType;
	}

	public void setDevType(String devType) {
		this.devType = devType;
	}
}
