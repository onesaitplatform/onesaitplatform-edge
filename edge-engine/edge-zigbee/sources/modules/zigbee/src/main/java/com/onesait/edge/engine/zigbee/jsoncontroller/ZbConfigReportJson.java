package com.onesait.edge.engine.zigbee.jsoncontroller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"cluster"
})
public class ZbConfigReportJson {

@JsonProperty("cluster")
private String cluster;

@JsonProperty("cluster")
public String getCluster() {
return cluster;
}

@JsonProperty("cluster")
public void setCluster(String cluster) {
this.cluster = cluster;
}

}