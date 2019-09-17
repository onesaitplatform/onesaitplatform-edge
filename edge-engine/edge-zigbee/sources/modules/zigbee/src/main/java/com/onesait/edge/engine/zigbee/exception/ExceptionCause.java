package com.onesait.edge.engine.zigbee.exception;

public enum ExceptionCause {
	
	PARENT_NOT_FOUND("Parent not found",1), PARENT_FOUND_NORESPONSE("Parent found, but no response",2),
	PARENT_FOUND_RESPONSEOK("Parent found and response received",3); 
	
	private String cause;
	private int errorCode;
	
	private ExceptionCause (String cause, int errorCode){
		this.cause = cause;
		this.errorCode = errorCode;
	}

	public String getCause() {
		return cause;
	}

	public int getErrorCode() {
		return errorCode;
	}	

}
