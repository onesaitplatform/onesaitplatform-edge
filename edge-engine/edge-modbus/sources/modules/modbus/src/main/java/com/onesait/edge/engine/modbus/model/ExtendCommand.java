package com.onesait.edge.engine.modbus.model;

public class ExtendCommand {

	private String deviceId;
	private String signalId;
	private String commandId;
	private String description;
	private Number value;

	public ExtendCommand(String deviceId, String signalId, String commandId, String description, Number value) {
		this.deviceId = deviceId;
		this.signalId = signalId;
		this.commandId = commandId;
		this.description = description;
		this.value = value;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSignalId() {
		return signalId;
	}

	public void setSignalId(String signalId) {
		this.signalId = signalId;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

}
