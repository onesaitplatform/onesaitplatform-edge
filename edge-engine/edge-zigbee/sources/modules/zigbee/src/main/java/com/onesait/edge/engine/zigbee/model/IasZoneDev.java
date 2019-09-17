package com.onesait.edge.engine.zigbee.model;

import com.onesait.edge.engine.zigbee.types.CommandValue;

public class IasZoneDev {
	private String status; //ON OFF
	private String booleanStatus; //true o false dependiendo de si es on o off
	private String mac;
	private String sensorType;
	private String battery; //LOW OK
	private boolean lowBattery=false; 
	private double valueStatus=0; // 0 off, 1on
	private double valueLowBatery=0; //0 bateria normal, 1 bateria baja
	

	public IasZoneDev( String mac) {
//		this.value = value;
		this.mac = mac;
		this.lowBattery=false;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
		if(status.equalsIgnoreCase(CommandValue.ON.toString())){
			this.valueStatus=1;
			this.setBooleanStatus("true");
		}else if(status.equalsIgnoreCase(CommandValue.OFF.toString())){
			this.valueStatus=0;
			this.setBooleanStatus("false");
		}
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	public double getValueStatus(boolean batteryEvent) {
		if(batteryEvent) {
			return this.valueLowBatery;
		}else {
			return this.valueStatus;
		}
	}
//	public double getValueLowBatery() {
//		return valueLowBatery;
//	}
	public boolean setLowBatery(){
		this.battery="LOW";
		this.valueLowBatery=1;
		this.lowBattery=true;
		return true;
		
//		boolean batteryStatusChanged=false;
//		if(!lowBatery){
//		this.battery="LOW";
//		this.valueLowBatery=1;
//		this.lowBatery=true;
//		batteryStatusChanged=true;
//		}
//		return batteryStatusChanged;
	}

	public boolean setNormalBatery(){
		this.lowBattery=false;
		this.battery="OK";
		this.valueLowBatery=0;
		return true;
		
//		boolean batteryStatusChanged=false;
//		if(lowBattery){
//		this.lowBattery=false;
//		this.battery="OK";
//		batteryStatusChanged=true;
//		this.valueLowBatery=0;
//		}
//		return batteryStatusChanged;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public String getBattery() {
		return battery;
	}

	public void setBattery(String battery) {
		this.battery = battery;
	}

	public boolean isLowBattery() {
		return lowBattery;
	}

	public void setLowBattery(boolean lowBattery) {
		this.lowBattery = lowBattery;
	}

	public String getBooleanStatus() {
		return booleanStatus;
	}

	public void setBooleanStatus(String booleanStatus) {
		this.booleanStatus = booleanStatus;
	}
	
	
}
