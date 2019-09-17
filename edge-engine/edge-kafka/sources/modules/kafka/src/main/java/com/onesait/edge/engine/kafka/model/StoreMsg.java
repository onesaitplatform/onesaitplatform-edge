package com.onesait.edge.engine.kafka.model;

import java.util.Date;

public class StoreMsg {
	
	private String message;
	private Date date;
	
	public StoreMsg(String message, Date date) {
		super();
		this.message = message;
		this.date = date;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {	
		this.message = message;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
}
