package com.onesait.edge.engine.zigbee.jsoncontroller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"childID",
"nwk address",
"manufacturer",
"device type"
})
public class Child {

@JsonProperty("child")
private String childID;
@JsonProperty("nwk address")
private String nwkAddress;
@JsonProperty("manufacturer")
private String manufacturer;
@JsonProperty("device type")
private String deviceType;

@JsonProperty("childID")
public String getChildID() {
return childID;
}

@JsonProperty("childID")
public void setChildID(String childID) {
this.childID = childID;
}

@JsonProperty("nwk address")
public String getNwkAddress() {
return nwkAddress;
}

@JsonProperty("nwk address")
public void setNwkAddress(String nwkAddress) {
this.nwkAddress = nwkAddress;
}

@JsonProperty("manufacturer")
public String getManufacturer() {
return manufacturer;
}

@JsonProperty("manufacturer")
public void setManufacturer(String manufacturer) {
this.manufacturer = manufacturer;
}

@JsonProperty("device type")
public String getDeviceType() {
return deviceType;
}

@JsonProperty("device type")
public void setDeviceType(String deviceType) {
this.deviceType = deviceType;
}

}
