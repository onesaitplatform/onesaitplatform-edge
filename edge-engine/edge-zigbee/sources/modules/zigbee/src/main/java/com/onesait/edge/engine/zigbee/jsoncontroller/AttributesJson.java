package com.onesait.edge.engine.zigbee.jsoncontroller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"attributeid",
"name",
"manufacturer",
"minTime",
"maxTime",
"localTemperature",
"thermostatMode",
"OHeatSetpoint",
"OCoolSetpoint"
})
public class AttributesJson {

/** Esto es para leer del XML */	
@JsonProperty("attributeid")
private String attributeid;
@JsonProperty("name")
private String name;
@JsonProperty("manufacturer")
private String manufacturer;
@JsonProperty("minTime")
private String minTime;
@JsonProperty("maxTime")
private String maxTime;



@JsonProperty("activeEnergy")
private String activeEnergy;
@JsonProperty("status")
private String status;
@JsonProperty("localTemperature")
private String localTemperature;
@JsonProperty("thermostatMode")
private String thermostatMode;
@JsonProperty("OHeatSetpoint")
private String oHeatSetpoint;
@JsonProperty("OCoolSetpoint")
private String oCoolSetpoint;
@JsonProperty("activePower")
private String activePower;
@JsonProperty("mainValue")
private String mainValue;


@JsonProperty("activePower")
public String getActivePower() {
	return activePower;
}
@JsonProperty("activePower")
public void setActivePower(String activePower) {
	this.activePower = activePower;
}

@JsonProperty("mainValue")
public String getMainValue() {
	return this.mainValue;
}
@JsonProperty("mainValue")
public void setMainValue(String mainValue) {
	this.mainValue = mainValue;
}

@JsonProperty("activeEnergy")
public String getActiveEnergy() {
	return activeEnergy;
}
@JsonProperty("activeEnergy")
public void setActiveEnergy(String activeEnergy) {
	this.activeEnergy = activeEnergy;
}
@JsonProperty("status")
public String getStatus() {
	return status;
}
@JsonProperty("status")
public void setStatus(String status) {
	this.status = status;
}
@JsonProperty("localTemperature")
public String getLocalTemperature() {
	return localTemperature;
}
@JsonProperty("localTemperature")
public void setLocalTemperature(String localTemperature) {
	this.localTemperature = localTemperature;
}

public String getOHeatSetpoint() {
	return this.oHeatSetpoint;
}
public void setOHeatSetpoint(String oHeatSetpoint) {
	this.oHeatSetpoint = oHeatSetpoint;
}
public String getOCoolSetpoint() {
	return this.oCoolSetpoint;
}
public void setOCoolSetpoint(String oCoolSetpoint) {
	this.oCoolSetpoint = oCoolSetpoint;
}
@JsonProperty("thermostatMode")
public String getThermostatMode() {
	return thermostatMode;
}
@JsonProperty("thermostatMode")
public void setThermostatMode(String thermostatMode) {
	this.thermostatMode = thermostatMode;
}
@JsonProperty("attributeid")
public String getAttributeid() {
return attributeid;
}

@JsonProperty("attributeid")
public void setAttributeid(String attributeid) {
this.attributeid = attributeid;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("manufacturer")
public String getManufacturer() {
return manufacturer;
}

@JsonProperty("manufacturer")
public void setManufacturer(String manufacturer) {
this.manufacturer = manufacturer;
}

@JsonProperty("minTime")
public String getMinTime() {
return minTime;
}

@JsonProperty("minTime")
public void setMinTime(String minTime) {
this.minTime = minTime;
}

@JsonProperty("maxTime")
public String getMaxTime() {
return maxTime;
}

@JsonProperty("maxTime")
public void setMaxTime(String maxTime) {
this.maxTime = maxTime;
}

}