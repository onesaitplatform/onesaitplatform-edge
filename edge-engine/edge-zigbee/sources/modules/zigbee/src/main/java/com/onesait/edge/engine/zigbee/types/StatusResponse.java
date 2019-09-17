package com.onesait.edge.engine.zigbee.types;

public enum StatusResponse {
    SUCCESS ("Success"),
    ERROR ("Error");
  
    private String status;

	private StatusResponse(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}      
	
}
