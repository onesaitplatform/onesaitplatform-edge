package com.onesait.edge.engine.zigbee.types;

public enum CommandValue {

	ON("ON"),
	OFF("OFF"),
	HEATING("HEATING"),
	AUTO("AUTO"),
	TOGGLE("TOGGLE"),
	DEFAULT("DEFAULT"),
	HEAT("HEAT"),
	COOL("COOL");

	private final String value;
	
	private CommandValue(String value) {
        this.value = value;
    }
	
	@Override
    public String toString() {
        return this.value;
    }

	public static CommandValue fromValue(String value) {
		for (CommandValue c: CommandValue.values()) {
			if (c.value.equals(value.trim().toUpperCase())) {
				return c;
			}
		}
		throw new IllegalArgumentException(value);
	}
}
