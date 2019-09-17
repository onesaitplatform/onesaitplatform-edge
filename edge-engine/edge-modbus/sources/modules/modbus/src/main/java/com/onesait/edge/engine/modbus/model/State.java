package com.onesait.edge.engine.modbus.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum State {

    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR");
	
    private final String value;
    private static final Map<String, State> CONSTANTS = new HashMap<>();

    static {
        for (State c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private State(String value) {
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
    public static State fromValue(String value) {
        State constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
