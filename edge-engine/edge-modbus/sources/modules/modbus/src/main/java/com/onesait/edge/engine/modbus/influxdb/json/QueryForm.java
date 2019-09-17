package com.onesait.edge.engine.modbus.influxdb.json;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "initDate", "endDate", "argument", "groupBy", "deviceId", "signalId" })
public class QueryForm {

	@JsonProperty("initDate")
	private Long initDate;
	@JsonProperty("endDate")
	private Long endDate;
	@JsonProperty("argument")
	private String argument;
	@JsonProperty("groupBy")
	private String groupBy;
	@JsonProperty("deviceId")
	private String deviceId;
	@JsonProperty("signalId")
	private String signalId;

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
	
	@JsonProperty("argument")
	public String getArgument() {
		return argument;
	}

	@JsonProperty("argument")
	public void setArgument(String argument) {
		this.argument = argument;
	}

	@JsonProperty("groupBy")
	public String getGroupBy() {
		return groupBy;
	}

	@JsonProperty("groupBy")
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	@JsonProperty("deviceId")
	public String getDeviceId() {
		return deviceId;
	}

	@JsonProperty("deviceId")
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@JsonProperty("signalId")
	public String getSignalId() {
		return signalId;
	}

	@JsonProperty("signalId")
	public void setSignalId(String signalId) {
		this.signalId = signalId;
	}
}
