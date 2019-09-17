package com.onesait.edge.engine.modbus.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Alert {
	private State state;
	private String date;
	private String alertType;
	private String deviceId;
	private String signalId;
	
	public Alert(State state, String alertType, String deviceId, String signalId) {
		super();
		this.date = this.getFormatDate();
		this.state = state;
		this.alertType = alertType;
		this.deviceId = deviceId;
		this.signalId = signalId;
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
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getSignalId() {
		return signalId;
	}
	
	public void setSignalId(String signalId) {
		this.signalId = signalId;
	}

	private String getFormatDate() {
		
		String formattedDate = "";
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		formattedDate = format.format(new Date());
		
		return formattedDate;
	}
}
