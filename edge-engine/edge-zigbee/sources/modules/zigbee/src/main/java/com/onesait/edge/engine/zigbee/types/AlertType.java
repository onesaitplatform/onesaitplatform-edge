package com.onesait.edge.engine.zigbee.types;

import java.util.HashMap;
import java.util.Map;

public enum AlertType {

    DEVICE_JOINED("Device joined"),
    DEVICE_LEAVE("Device leaved network"),
    DEVICE_REMOVED("Device removed"),
    DEVICE_ERROR("Device on error"),
    DEVICE_OK("Device ok"),
    CONF_REPORT_SUCCESS("Report conf ok"),
    CONF_REPORT_ERROR("Report conf error"),
    TOGGLE_SUCCESS("Toggle success"),
    TOGGLE_ERROR("Toggle error"),
    SETPOINT_SUCCESS("Setpoint changed"),
    SETPOINT_ERROR("Error changing setpoint"),
	SETMODE_SUCCESS("Setmode changed"),
	DOWNLOADING_OTA_FILE("Downloading ota file"),
	UPGRADE_STARTED("Upgrade staterd"),
	UPGRADE_SUCCES("Upgraded successfully"),
	DEVICE_UP2DATE("Device with last version"),
	SETMODE_ERROR("Error setting mode");
	
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

