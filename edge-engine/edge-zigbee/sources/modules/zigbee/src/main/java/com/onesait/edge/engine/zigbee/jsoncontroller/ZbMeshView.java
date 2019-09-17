package com.onesait.edge.engine.zigbee.jsoncontroller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"localAddr",
"neighAddr",
"ieeeAddress",
"lqi",
"depth",
"permitJoin",
"deviceType",
"rxOnIdle",
"relationship"
})
public class ZbMeshView {

@JsonProperty("localAddr")
private String localAddr;
@JsonProperty("neighAddr")
private String neighAddr;
@JsonProperty("ieeeAddress")
private String ieeeAddress;
@JsonProperty("lqi")
private String lqi;
@JsonProperty("depth")
private String depth;
@JsonProperty("permitJoin")
private String permitJoin;
@JsonProperty("deviceType")
private String deviceType;
@JsonProperty("rxOnIdle")
private String rxOnIdle;
@JsonProperty("relationship")
private String relationship;

@JsonProperty("localAddr")
public String getLocalAddr() {
return localAddr;
}

@JsonProperty("localAddr")
public void setLocalAddr(String localAddr) {
this.localAddr = localAddr;
}

@JsonProperty("neighAddr")
public String getNeighAddr() {
return neighAddr;
}

@JsonProperty("neighAddr")
public void setNeighAddr(String neighAddr) {
this.neighAddr = neighAddr;
}

@JsonProperty("ieeeAddress")
public String getIeeeAddress() {
return ieeeAddress;
}

@JsonProperty("ieeeAddress")
public void setIeeeAddress(String ieeeAddress) {
this.ieeeAddress = ieeeAddress;
}

@JsonProperty("lqi")
public String getLqi() {
return lqi;
}

@JsonProperty("lqi")
public void setLqi(String lqi) {
this.lqi = lqi;
}

@JsonProperty("depth")
public String getDepth() {
return depth;
}

@JsonProperty("depth")
public void setDepth(String depth) {
this.depth = depth;
}

@JsonProperty("permitJoin")
public String getPermitJoin() {
return permitJoin;
}

@JsonProperty("permitJoin")
public void setPermitJoin(String permitJoin) {
this.permitJoin = permitJoin;
}

@JsonProperty("deviceType")
public String getDeviceType() {
return deviceType;
}

@JsonProperty("deviceType")
public void setDeviceType(String deviceType) {
this.deviceType = deviceType;
}

@JsonProperty("rxOnIdle")
public String getRxOnIdle() {
return rxOnIdle;
}

@JsonProperty("rxOnIdle")
public void setRxOnIdle(String rxOnIdle) {
this.rxOnIdle = rxOnIdle;
}

@JsonProperty("relationship")
public String getRelationship() {
return relationship;
}

@JsonProperty("relationship")
public void setRelationship(String relationship) {
this.relationship = relationship;
}

}

