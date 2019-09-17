package com.onesait.edge.engine.zigbee.types;

public enum DeviceStatus {
	OK("OK"),
	KO("KO"),
	ON("ON"),
	OFF("OFF"),
	DOWNLOADINGOTAFILE("DOWNLOADING"),
	UPGRADING("UPGRADING");
    private final String status;

    private DeviceStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }
	
}
