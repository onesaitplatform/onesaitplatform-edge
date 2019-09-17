package com.onesait.edge.engine.zigbee.util;

public class MqttMsgDetail {
	
	private String profile="";
	private String deviceId="";
	private String signalId="";
	private String signal="";
	private String description="";
	private String value="";
	private long timeStamp;
	private long timeStampInNanos;
	private String deviceType="";
	private double number; 

	public MqttMsgDetail() {
		super();
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		if(profile!=null){
		this.profile = profile;
		}
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		if(deviceId!=null){
		this.deviceId = deviceId;
		}
	}

	public String getSignalId() {
		return signalId;
	}

	public void setSignalId(String signalId) {
		if(signalId!=null){
		this.signalId = signalId;
		}
	}

	public String getSignal() {
		return signal;
	}

	public void setSignal(String signal) {
		if(signal!=null){
		this.signal = signal;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if(description!=null){
		this.description = description;
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if(value!=null){
		this.value = value;
		}
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getTimeStampInNanos() {
		return timeStampInNanos;
	}

	public void setTimeStampInNanos(long timeStampInNanos) {
		this.timeStampInNanos = timeStampInNanos;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		if(deviceType!=null){
		this.deviceType = deviceType;
		}
	}
	
	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}
}
