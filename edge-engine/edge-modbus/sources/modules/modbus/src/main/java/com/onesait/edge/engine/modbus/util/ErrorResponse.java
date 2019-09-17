package com.onesait.edge.engine.modbus.util;


public class ErrorResponse {
	
	private String cause = "Error message";

	public ErrorResponse(String cause) {
		this.cause = cause;
	}

	public String getCause() {
		return cause;
	}
}
