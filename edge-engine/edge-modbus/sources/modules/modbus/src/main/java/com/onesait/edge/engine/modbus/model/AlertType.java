package com.onesait.edge.engine.modbus.model;

import java.util.HashMap;
import java.util.Map;

public enum AlertType {

    DEVICE_CREATED("Device created"),
    DEVICE_UPDATED("Device updated"),
    DEVICE_DELETED("Device deleted"),
    DEVICE_ERROR("Device error"),
    SIGNAL_CREATED("Signal created"),
    SIGNAL_UPDATED("Signal updated"),
    SIGNAL_DELETED("Signal deleted"),
    SIGNAL_ERROR("Signal error"),
    COMMAND_CREATED("Command created"),
    COMMAND_UPDATED("Command updated"),
    COMMAND_DELETED("Command deleted"),
    COMMAND_EXECUTED("Command executed"),
    COMMAND_ERROR("Command error");
	
    private final String value;
    private static final Map<String, AlertType> CONSTANTS = new HashMap<>();

    static {
        for (AlertType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private AlertType(String value) {
        this.value = value;
    }
    
	public String getValue() {
		return value;
	}


	@Override
    public String toString() {
        return this.value;
    }

    public String value() {
        return this.value;
    }

    public static AlertType fromValue(String value) {
        AlertType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}

