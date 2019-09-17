package com.onesait.edge.engine.zigbee.util;

public class DeviceInfo {
	private static final String UNKNOWN="UNKNOWN";

	private int data = 0;
	
	public DeviceInfo( int data){
		this.data=data;
	}

	public int getData() {
		return data;
	}
	
	public String getDeviceType(){
		int result=this.data & 0x3;
		switch (result) {
			case 0x00:
				return "COORD";
			case 0x01:
				return "ROUTER";
			case 0x02:
				return "END DEV";
			default:
				return UNKNOWN;
		}
	}
	
	public String getReceiverOnWhenIdle(){
		int result=this.data & 0xC;
		switch (result) { 
			case 0x00:
				return "OFF";
			case 0x04:
				return "ON";
			default:
				return UNKNOWN;
		}
	}
	
	public String getRelationship(){
		int result=this.data & 0x70;
		switch (result) {
			case 0x00:
				return "PARENT";
			case 0x10:
				return "CHILD";
			case 0x20:
				return "SIBLING";
			case 0x30:
				return "NONE";
			case 0x40:
				return "PREVIOUS CHILD";
			default:
				return UNKNOWN;
		}
	}
}
