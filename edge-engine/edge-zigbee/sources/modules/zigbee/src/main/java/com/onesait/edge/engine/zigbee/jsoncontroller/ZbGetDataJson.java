package com.onesait.edge.engine.zigbee.jsoncontroller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"Timestamp",
"Name",
"Attribute",
"nReports",
"Bytes",
"Value",
"MinTime2Report",
"MaxTime2Report",
"Unsupported"
})
public class ZbGetDataJson {

@JsonProperty("Timestamp")
private String timestamp;
@JsonProperty("Name")
private String name;
@JsonProperty("Attribute")
private String attribute;
@JsonProperty("nReports")
private String nReports;
@JsonProperty("Bytes")
private String bytes;
@JsonProperty("Value")
private String value;
@JsonProperty("MinTime2Report")
private String minTime2Report;
@JsonProperty("MaxTime2Report")
private String maxTime2Report;
@JsonProperty("Unsupported")
private String unsupported;

@JsonProperty("Timestamp")
public String getTimestamp() {
return timestamp;
}

@JsonProperty("Timestamp")
public void setTimestamp(String timestamp) {
this.timestamp = timestamp;
}

@JsonProperty("Name")
public String getName() {
return name;
}

@JsonProperty("Name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("Attribute")
public String getAttribute() {
return attribute;
}

@JsonProperty("Attribute")
public void setAttribute(String attribute) {
this.attribute = attribute;
}

@JsonProperty("nReports")
public String getNReports() {
return nReports;
}

@JsonProperty("nReports")
public void setNReports(String nReports) {
this.nReports = nReports;
}

@JsonProperty("Bytes")
public String getBytes() {
return bytes;
}

@JsonProperty("Bytes")
public void setBytes(String bytes) {
this.bytes = bytes;
}

@JsonProperty("Value")
public String getValue() {
return value;
}

@JsonProperty("Value")
public void setValue(String value) {
this.value = value;
}

@JsonProperty("MinTime2Report")
public String getMinTime2Report() {
return minTime2Report;
}

@JsonProperty("MinTime2Report")
public void setMinTime2Report(String minTime2Report) {
this.minTime2Report = minTime2Report;
}

@JsonProperty("MaxTime2Report")
public String getMaxTime2Report() {
return maxTime2Report;
}

@JsonProperty("MaxTime2Report")
public void setMaxTime2Report(String maxTime2Report) {
this.maxTime2Report = maxTime2Report;
}

@JsonProperty("Unsupported")
public String getUnsupported() {
return unsupported;
}

@JsonProperty("Unsupported")
public void setUnsupported(String unsupported) {
this.unsupported = unsupported;
}
}