package com.onesait.edge.engine.zigbee.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"id",
	"name",
	"datatype",
	"access",
	"mandatory",
	"value",
	"reportable",
	"manufacturerspecific",
	"minReportingTime",
	"maxReportingTime",
	"configured",
	"reportado"
})
public class Attribute {
	
	@JsonProperty("id")
	private Long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("datatype")
	private Long datatype;
	@JsonProperty("access")
	private String access;
	@JsonProperty("mandatory")
	private boolean mandatory;
	@JsonProperty("value")
	private List<Byte> value = new ArrayList<>();
	@JsonProperty("reportable")
	private boolean reportable;
	@JsonProperty("manufacturerspecific")
	private boolean manufacturerspecific;
	@JsonProperty("minReportingTime")
	private String minReportingTime;
	@JsonProperty("maxReportingTime")
	private String maxReportingTime;
	@JsonProperty("configured")
	private boolean configured = false;

	@JsonProperty("id")
	public Long getId() {
		return id;
	}
	
	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}
	
	
	@JsonProperty("datatype")
	public Long getDatatype() {
		return datatype;
	}
	
	@JsonProperty("datatype")
	public void setDatatype(Long datatype) {
		this.datatype = datatype;
	}
	
	@JsonProperty("access")
	public String getAccess() {
		return access;
	}
	
	@JsonProperty("access")
	public void setAccess(String access) {
		this.access = access;
	}
	
	@JsonProperty("mandatory")
	public boolean isMandatory() {
		return mandatory;
	}
	
	@JsonProperty("mandatory")
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	@JsonProperty("value")
	public List<Byte> getValue() {
		return value;
	}
	
	@JsonProperty("value")
	public void setValue(List<Byte> value) {
		this.value = value;
	}
	
	public void setValue(byte[] value) {
		if (value != null) {
			List<Byte> valueList = new ArrayList<>();
			for (int i = 0; i < value.length; i++) {
				valueList.add(i, value[i]);
			}
			this.value = valueList;
		}
	}
	
	@JsonProperty("reportable")
	public boolean isReportable() {
		return reportable;
	}
	
	@JsonProperty("reportable")
	public void setReportable(boolean reportable) {
		this.reportable = reportable;
	}
	
	@JsonProperty("manufacturerspecific")
	public boolean isManufacturerspecific() {
		return manufacturerspecific;
	}
	
	@JsonProperty("manufacturerspecific")
	public void setManufacturerspecific(boolean manufacturerspecific) {
		this.manufacturerspecific = manufacturerspecific;
	}
	
	@JsonProperty("minReportingTime")
	public String getMinReportingTime() {
		return minReportingTime;
	}
	@JsonProperty("minReportingTime")
	public void setMinReportingTime(String minReportingTime) {
		this.minReportingTime = minReportingTime;
	}
	@JsonProperty("maxReportingTime")
	public String getMaxReportingTime() {
		return maxReportingTime;
	}
	@JsonProperty("maxReportingTime")
	public void setMaxReportingTime(String maxReportingTime) {
		this.maxReportingTime = maxReportingTime;
	}

	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}
}