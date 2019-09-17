package com.onesait.edge.engine.modbus.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Protocol {
	TCP("TCP"),
	RTU("RTU");
	
    private final String value;
    private static final Map<String, Protocol> CONSTANTS = new HashMap<>();

    static {
        for (Protocol c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private Protocol(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static Protocol fromValue(String value) {
        Protocol constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}


