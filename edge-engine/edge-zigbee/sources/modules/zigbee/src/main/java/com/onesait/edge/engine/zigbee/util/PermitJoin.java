package com.onesait.edge.engine.zigbee.util;

public class PermitJoin {

	private int data = 0;
	
	public PermitJoin( int data){
		this.data=data;
	}

	public int getData() {
		return data;
	}
	
	public String getPermitJoining(){
		int result = this.data & 0x3;
		switch (result) {
			case 0:
				return "NOT ACCEPT";
			case 1:
				return "ACCEPT";
			default:
				return "UNKNOWN";
		}
	}

}
