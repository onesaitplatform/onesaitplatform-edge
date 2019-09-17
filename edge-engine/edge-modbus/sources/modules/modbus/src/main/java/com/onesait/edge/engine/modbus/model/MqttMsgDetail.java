package com.onesait.edge.engine.modbus.model;

public class MqttMsgDetail {
	
	private String profile;
	private String deviceId;
	private String signalId;
	private String signal;
	private String description;
	private String value;
	private Number number;
	private String unit;
	private long timeStamp;
	private long timeStampInNanos;
	private String deviceType;
	
	public MqttMsgDetail() {
		super();
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
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

	public String getSignal() {
		return signal;
	}

	public void setSignal(String signal) {
		this.signal = signal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Number getNumber() {
		return number;
	}

	public void setNumber(Number number) {
		this.number = number;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
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
		this.deviceType = deviceType;
	}
}
