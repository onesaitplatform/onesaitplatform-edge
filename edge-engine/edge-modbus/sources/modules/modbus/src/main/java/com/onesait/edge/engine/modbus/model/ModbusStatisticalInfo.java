package com.onesait.edge.engine.modbus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "totalDevices",
    "totalSignals",
    "totalSignalOnError",
    "totalDeviceOnError",
})
public class ModbusStatisticalInfo {

    @JsonProperty("totalDevices")
    private Integer totalDevices;
    @JsonProperty("totalSignals")
    private Integer totalSignals;
    @JsonProperty("totalDeviceOnError")
    private Integer totalDeviceOnError;
    @JsonProperty("totalSignalOnError")
    private Integer totalSignalOnError;
    
    @JsonProperty("totalDevices")
	public Integer getTotalDevices() {
		return totalDevices;
	}
    @JsonProperty("totalDevices")
	public void setTotalDevides(Integer totalDevices) {
		this.totalDevices = totalDevices;
	}
    @JsonProperty("totalSignals")
	public Integer getTotalSignals() {
		return totalSignals;
	}
    @JsonProperty("totalSignals")
	public void setTotalSignals(Integer totalSignals) {
		this.totalSignals = totalSignals;
	}
    @JsonProperty("totalDeviceOnError")
	public Integer getTotalDeviceOnError() {
		return totalDeviceOnError;
	}
    @JsonProperty("totalDeviceOnError")
	public void setTotalDeviceOnError(Integer totalDeviceOnError) {
		this.totalDeviceOnError = totalDeviceOnError;
	}
    @JsonProperty("totalSignalOnError")
	public Integer getTotalSignalOnError() {
		return totalSignalOnError;
	}
    @JsonProperty("totalSignalOnError")
	public void setTotalSignalOnError(Integer totalSignalOnError) {
		this.totalSignalOnError = totalSignalOnError;
	}
    
    
}
