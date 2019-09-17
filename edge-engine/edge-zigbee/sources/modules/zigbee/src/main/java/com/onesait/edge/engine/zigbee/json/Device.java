package com.onesait.edge.engine.zigbee.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"shortaddress",
	"ieeeaddress",
	"manufacturercode",
	"devicetype",
	"capabilities",
	"endpoints"
})
public class Device {
	
	@JsonProperty("shortaddress")
	private Long shortaddress;
	@JsonProperty("ieeeaddress")
	private Long ieeeaddress;
	@JsonProperty("manufacturercode")
	private Long manufacturercode;
	@JsonProperty("devicetype")
	private Long devicetype;
	@JsonProperty("capabilities")
	private Long capabilities;
	@JsonProperty("endpoints")
	private List<Endpoint> endpoints = new ArrayList<>();
	
	@JsonProperty("shortaddress")
	public Long getShortaddress() {
		return shortaddress;
	}
	
	@JsonProperty("shortaddress")
	public void setShortaddress(Long shortaddress) {
		this.shortaddress = shortaddress;
	}
	
	@JsonProperty("ieeeaddress")
	public Long getIeeeaddress() {
		return ieeeaddress;
	}
	
	@JsonProperty("ieeeaddress")
	public void setIeeeaddress(Long ieeeaddress) {
		this.ieeeaddress = ieeeaddress;
	}
	
	@JsonProperty("manufacturercode")
	public Long getManufacturercode() {
		return manufacturercode;
	}
	
	@JsonProperty("manufacturercode")
	public void setManufacturercode(Long manufacturercode) {
		this.manufacturercode = manufacturercode;
	}
	
	@JsonProperty("devicetype")
	public Long getDevicetype() {
		return devicetype;
	}
	
	@JsonProperty("devicetype")
	public void setDevicetype(Long devicetype) {
		this.devicetype = devicetype;
	}
	
	@JsonProperty("capabilities")
	public Long getCapabilities() {
		return capabilities;
	}
	
	@JsonProperty("capabilities")
	public void setCapabilities(Long capabilities) {
		this.capabilities = capabilities;
	}
	
	@JsonProperty("endpoints")
	public List<Endpoint> getEndpoints() {
		return endpoints;
	}
	
	@JsonProperty("endpoints")
	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}
	
	public Endpoint getEndpoint(long epId) {
		for (Endpoint endpoint : endpoints) {
			if (endpoint.getId() == epId) {
				return endpoint;
			}
		}
		return null;
	}
	
	public void addEndpoint(Endpoint ep) {
		this.endpoints.add(ep);
	}
}