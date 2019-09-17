package com.onesait.edge.engine.zigbee.jsoncontroller;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"shortaddress",
"mac",
"manufacturer",
"device type",
"children"
})
public class ZbShowNetwork {

@JsonProperty("shortaddress")
private String shortaddress;
@JsonProperty("mac")
private String mac;
@JsonProperty("manufacturer")
private String manufacturer;
@JsonProperty("device type")
private String deviceType;
@JsonProperty("children")
private List<Child> children = null;

@JsonProperty("shortaddress")
public String getShortaddress() {
return shortaddress;
}

@JsonProperty("shortaddress")
public void setShortaddress(String shortaddress) {
this.shortaddress = shortaddress;
}

@JsonProperty("mac")
public String getMac() {
return mac;
}

@JsonProperty("mac")
public void setMac(String mac) {
this.mac = mac;
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

@JsonProperty("children")
public List<Child> getChildren() {
return children;
}

@JsonProperty("children")
public void setChildren(List<Child> children) {
this.children = children;
}

}

