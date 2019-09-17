package com.onesait.edge.engine.zigbee.types;

public enum ZigbeeDescriptions {
	
	ZIGBEEDEVICE("ZigbeeDevice"),
	ZIGBEE("Zigbee"),
	REPORT("Report"),
	REPORTSUM("Sum of attributes"),
	CHANGESTATUS("Change Status"),
	CHANGEBATTERYSTATUS("Change Battery Status"),
	DEVICELEAVING("Device Leaving"),
	LEAVE("Device Leaving"),
	WRITE_RSP("Write Attribute Rsp"),
	READ("Read Attribute");
	
	
	private final String description;
	
	private ZigbeeDescriptions(String description) {
        this.description = description;
    }
	
	@Override
    public String toString() {
        return this.description;
    }
}
