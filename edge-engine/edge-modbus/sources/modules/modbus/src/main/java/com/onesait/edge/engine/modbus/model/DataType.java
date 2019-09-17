package com.onesait.edge.engine.modbus.model;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

    SINT16("SINT16"),
    SINT32("SINT32"),
    SINT64("SINT64"),
    UINT16("UINT16"),
    UINT32("UINT32"),
    UINT64("UINT64"),
    CHART16("CHART16"),
    CHART32("CHART32"),
    BOOLEAN("BOOLEAN"),
    FLOAT32("FLOAT32");
	
    private final String value;
    private static final Map<String, DataType> CONSTANTS = new HashMap<>();

    static {
        for (DataType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private DataType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String value() {
        return this.value;
    }

    public static DataType fromValue(String value) {
        DataType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
    
    public static final int offset(DataType dataType) {
		
    	int result = 0;
    	
    	if (DataType.BOOLEAN.equals(dataType)) {
    		result = 0;
		} else if (DataType.CHART16.equals(dataType)) {
			result = 0;
		} else if (DataType.SINT16.equals(dataType)) {
			result = 0;
		} else if (DataType.UINT16.equals(dataType)) {
			result = 0;
		} else if (DataType.CHART32.equals(dataType)) {
			result = 1;
		} else if (DataType.FLOAT32.equals(dataType)) {
			result = 1;
		} else if(DataType.SINT32.equals(dataType)) {
			result = 1;
		} else if(DataType.UINT32.equals(dataType)) {
			result = 1;
		} else if (DataType.SINT64.equals(dataType)) {
			result = 3;
		} else {
			// UINT_64
			result = 3;
		}
    	
		return result;
	}

    public static String descValue(DataType dataType) {
    	switch (dataType) {
		case SINT16:			
			return "SIGNED INT 16";
		case SINT32:			
			return "SIGNED INT 32";
		case SINT64:	
			return "SIGNED INT 64";
		case UINT16:
			return "UNSIGNED INT 16";
		case UINT32:
			return "UNSIGNED INT 32";
		case UINT64:
			return "UNSIGNED INT 64";
		case CHART16:
			return "CHART 16";
		case CHART32:
			return "CHART 32";
		case BOOLEAN:
			return "BOOLEAN";
		case FLOAT32:
			return "FLOAT 32";
		default:			
			return null;
		}
    }
    
    public static Boolean contains(String dataType) {
    	
    	Boolean result = Boolean.FALSE;
    	
    	if (DataType.BOOLEAN.value.equals(dataType)) {
    		result = Boolean.TRUE;
		} else if (DataType.CHART16.value.equals(dataType)) {
			result = Boolean.TRUE;
		} else if (DataType.SINT16.value.equals(dataType)) {
			result = Boolean.TRUE;
		} else if (DataType.UINT16.value.equals(dataType)) {
			result = Boolean.TRUE;
		} else if (DataType.CHART32.value.equals(dataType)) {
			result = Boolean.TRUE;
		} else if (DataType.FLOAT32.value.equals(dataType)) {
			result = Boolean.TRUE;
		} else if(DataType.SINT32.value.equals(dataType)) {
			result = Boolean.TRUE;
		} else if(DataType.UINT32.value.equals(dataType)) {
			result = Boolean.TRUE;
		} else if (DataType.SINT64.value.equals(dataType)) {
			result = Boolean.TRUE;
		}
    	
		return result;
    }
    
}

