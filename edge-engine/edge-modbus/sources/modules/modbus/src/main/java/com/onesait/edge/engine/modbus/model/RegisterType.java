package com.onesait.edge.engine.modbus.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RegisterType {

    CS("CS"),
    IS("IS"),
    HR("HR"),
    IR("IR");
    private final String value;
    private static final Map<String, RegisterType> CONSTANTS = new HashMap<>();

    static {
        for (RegisterType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private RegisterType(String value) {
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
    public static RegisterType fromValue(String value) {
        RegisterType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
    
    public static Integer intValue(RegisterType registerType){
    	switch (registerType) {
		case CS:			
			return 1;
		case IS:			
			return 2;
		case HR:	
			return 3;
		case IR:
			return 4;
		default:			
			return 0;
		}
    }
    
    public static String descValue(RegisterType registerType) {
    	switch (registerType) {
		case CS:			
			return "COIL STATUS";
		case IS:			
			return "INPUT STATUS";
		case HR:	
			return "HOLDING REGISTER";
		case IR:
			return "INPUT REGISTER";
		default:			
			return null;
		}
    }
}
