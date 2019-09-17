package com.onesait.edge.engine.zigbee.types;

public enum ZigbeeValue{

	ON("ON"),
	OFF("OFF"),
	HEATING("HEATING"),
	AUTO("AUTO"),
	TOGGLE("TOGGLE"),
	DEFAULT("DEFAULT"),
	HEAT("HEAT"),
	COOL("COOL");
	
	private final String value;
	
	private ZigbeeValue(String value) {
        this.value = value;
    }
	
	@Override
    public String toString() {
        return this.value;
    }

	public static ZigbeeValue fromValue(String value) {
		for (ZigbeeValue c: ZigbeeValue.values()) {
			if (c.value.equals(value)) {
				return c;
			}
		}
		throw new IllegalArgumentException(value);
	}
}
