package com.onesait.edge.engine.zigbee.types;

import com.onesait.edge.engine.zigbee.service.DeviceManager;

public enum DeviceType {
		
		METER("METER"),
		PLUG("PLUG"), 
		CLAMP("CLAMP"),
		CLAMP3("CLAMP3"),
		THERMOSTAT("THERMOSTAT"), 
		SENSOR("SENSOR"),
		MOTION_SENSOR("MOTION_SENSOR"),
		DOOR_SENSOR("DOOR_SENSOR"),
		WATER_SENSOR("WATER_SENSOR"),
		SMOKE_SENSOR("SMOKE_SENSOR"),
		PANIC_SENSOR("PANIC_SENSOR"),
		UNDEFINED("UNDEFINED"), 
		BULB("BULB"),
		BULB_TEMP("BULB_TEMP"),
		BULB_COLOR("BULB_COLOR"),
		ALARM("ALARM_SENSOR"),
		SWITCH_BUTTON("SWITCH_BUTTON"),
		DIMMER("DIMMER"),
		COORDINATOR("COORDINATOR");
		
	    private final String value;

	    private DeviceType(String value) {
	        this.value = value;
	    }

	    @Override
	    public String toString() {
	        return this.value;
	    }


	    public static DeviceType fromValue(String value) {
	        for (DeviceType c: DeviceType.values()) {
	            if (c.value.equals(value)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(value);
	    }
	    
	    public static String sensorType(DeviceType type){
	    	switch (type){
	    	case DOOR_SENSOR:
	    		return DeviceManager.DOOR_SENSOR;
	    	case MOTION_SENSOR:
	    		return DeviceManager.MOTION_SENSOR;
	    	case WATER_SENSOR:
	    		return DeviceManager.WATER_SENSOR;
	    	case SMOKE_SENSOR:
	    		return DeviceManager.SMOKE_SENSOR;
	    	case ALARM:
	    		return DeviceManager.ALARM_SENSOR;
	    	case SENSOR:
	    		return SENSOR.toString();
			default:
				return SENSOR.toString();
	    	}
	    	}

	    
	    public static Boolean isSensor(DeviceType type) {

			if (type.equals(DeviceType.SENSOR) || type.equals(DeviceType.DOOR_SENSOR) || type.equals(DeviceType.MOTION_SENSOR) || type.equals(DeviceType.WATER_SENSOR) || type.equals(DeviceType.SMOKE_SENSOR) || type.equals(DeviceType.PANIC_SENSOR) || type.equals(DeviceType.ALARM)) {
				return true;
			} else {
				return false;
			}
		}
	    
}
