package com.onesait.edge.engine.zigbee.types;

public enum CommandType {

	SET("SET"),
	SETMODE("SETMODE"),
	POLLCOMMAND("POLLCOMMAND"),
	READ("READ"),
	WRITE("WRITE");
	
    private final String value;

    private CommandType(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return this.value;
    }


    public static CommandType fromValue(String value) {
        for (CommandType c: CommandType.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
