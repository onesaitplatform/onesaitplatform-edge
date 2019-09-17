package com.onesait.edge.engine.zigbee.influxdb.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "initDate", "endDate", "argument", "groupBy", "deviceId", "signalId"})
public class QueryForm {

	@JsonProperty("initDate")
	private Long initDate;
	@JsonProperty("endDate")
	private Long endDate;
	@JsonProperty("device")
	private String device;
	@JsonProperty("cluster")
	private String cluster;
	@JsonProperty("attribute")
	private String attribute;
	@JsonProperty("argument")
	private String argument;
	@JsonProperty("groupBy")
	private String groupBy;
	
	@JsonProperty("initDate")
	public Long getInitDate() {
		return initDate;
	}
	@JsonProperty("initDate")
	public void setInitDate(Long initDate) {
		this.initDate = initDate;
	}
	@JsonProperty("endDate")
	public Long getEndDate() {
		return endDate;
	}
	@JsonProperty("endDate")
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}
	@JsonProperty("device")
	public String getDevice() {
		return device;
	}
	@JsonProperty("device")
	public void setDevice(String device) {
		this.device = device;
	}
	@JsonProperty("argument")
	public String getArgument() {
		return argument;
	}
	@JsonProperty("argument")
	public void setArgument(String argument) {
		this.argument = argument;
	}
	@JsonProperty("cluster")
	public String getCluster() {
		return cluster;
	}
	@JsonProperty("cluster")
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getAttribute() {
		return attribute;
	}
	@JsonProperty("attribute")
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	@JsonProperty("groupBy")
	public String getGroupBy() {
		return groupBy;
	}

	@JsonProperty("groupBy")
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	
}
