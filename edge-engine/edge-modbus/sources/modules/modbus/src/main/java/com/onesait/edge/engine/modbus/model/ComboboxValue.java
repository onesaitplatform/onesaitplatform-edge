package com.onesait.edge.engine.modbus.model;

public class ComboboxValue {
	
	private String value;
	private String text;
	
	public ComboboxValue(String value, String text) {
		super();
		this.value = value;
		this.text = text;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	
}
