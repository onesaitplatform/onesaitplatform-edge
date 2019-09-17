package com.onesait.edge.engine.zigbee.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum CommandWeb {

    CONFIGREPORT("configreport"),
    GETDATA("getdata"),
    SHOWNETWORK("shownetwork"),
    REMOVEDEVICE("removedevice");
    private final String value;
    protected static final Map<String, CommandWeb> CONSTANTS = new HashMap<>();
    protected static final List<String> CONSTANTES = new ArrayList<>();

    static {
        for (CommandWeb c: values()) {
        		CONSTANTES.add(c.value);
        }
    }

    private CommandWeb(String value) {
        this.value = value;
    }
    
    public String getDropdownValue(){
    	return value;
    	
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
    public static CommandWeb fromValue(String value) {
        CommandWeb constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
