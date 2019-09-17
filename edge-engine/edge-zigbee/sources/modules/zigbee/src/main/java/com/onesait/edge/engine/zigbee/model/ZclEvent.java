package com.onesait.edge.engine.zigbee.model;

import java.util.HashMap;
import java.util.Map;

public class ZclEvent {
	
	public enum Type {
		
		REPORT("REPORT"),
		WRITE("WRITE"),
		CLUSTERCOMMAND("CLUSTERCOMMAND"),
		ALWAYS("ALWAYS");
		
		private String typeStr;

		private static final Map<String, Type> CONSTANTS = new HashMap<>();
		
		private Type(String s) {
			this.typeStr = s.toUpperCase();
		}
		
		public String getTypeStr() {
			return typeStr;
		}

//		private void setTypeStr(String typeStr) {
//			this.typeStr = typeStr;
//		}
		
		static {
			for (Type c: values()) {
	            CONSTANTS.put(c.typeStr, c);
	        }
		}
		
		public static Type fromValue(String value) {
	        Type constant = CONSTANTS.get(value.toUpperCase());
	        if (constant == null) {
	            throw new IllegalArgumentException(value);
	        } else {
	            return constant;
	        }
		}
	}
	
	private String topic = "", signal = "";
	private Long bitmask = null;
	private Type type = Type.ALWAYS;
	
	public ZclEvent(String topic, String signal, String type) {
		super();
		this.setTopic(topic);
		this.setSignal(signal);
		this.setType(type);
	}

	public ZclEvent(String topic, String signal) {
		super();
		this.setTopic(topic);
		this.setSignal(signal);
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSignal() {
		return signal;
	}

	public void setSignal(String signal) {
		this.signal = signal;
	}

	public Long getBitmask() {
		return bitmask;
	}

	public void setBitmask(Long bitmask) {
		this.bitmask = bitmask;
	}

	public Type getType() {
		return type;
	}

	public void setType(String type) {
		this.type = Type.fromValue(type);
	}
}
