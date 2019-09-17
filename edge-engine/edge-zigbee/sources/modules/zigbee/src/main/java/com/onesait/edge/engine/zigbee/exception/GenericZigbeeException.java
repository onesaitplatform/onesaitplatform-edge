package com.onesait.edge.engine.zigbee.exception;

public class GenericZigbeeException extends Exception{

	
	private static final long serialVersionUID = 1L;
	private ExceptionCause exceptionCause;
	public GenericZigbeeException(String msg){
		super(msg);
	}
	public GenericZigbeeException(ExceptionCause cause){
		super(cause.getCause());
		this.setExceptionCause(cause);
	}
	public ExceptionCause getExceptionCause() {
		return exceptionCause;
	}
	public void setExceptionCause(ExceptionCause exceptionCause) {
		this.exceptionCause = exceptionCause;
	}
	
	
}
