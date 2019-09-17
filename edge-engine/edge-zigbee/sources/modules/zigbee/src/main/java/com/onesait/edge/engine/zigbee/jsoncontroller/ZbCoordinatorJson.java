package com.onesait.edge.engine.zigbee.jsoncontroller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		"mac",
		"manufacturer",
		"channel",
		"fwversion",
		"devType"
	})
	public class ZbCoordinatorJson {
		
		@JsonProperty("mac")
		private String mac;
		@JsonProperty("manufacturer")
		private String manufacturer;
		@JsonProperty("channel")
		private String channel;
		@JsonProperty("fwversion")
		private String fwversion;
		@JsonProperty ("devType")
		private String devType;
		
		
		@JsonProperty("devType")
		public String getDevType() {
			return devType;
		}
		
		@JsonProperty("devType")
		public void setDevType(String devType) {
			this.devType = devType;
		}

		@JsonProperty("mac")
		public String getMac() {
			return mac;
		}
		
		@JsonProperty("mac")
		public void setMac(String mac) {
			this.mac = mac;
		}
		
		@JsonProperty("fwversion")
		public String getFwVersion() {
			return fwversion;
		}
		
		@JsonProperty("fwversion")
		public void setFwVersion(String fwversion) {
			this.fwversion = fwversion;
		}
		@JsonProperty("channel")
		public String getChannel() {
			return channel;
		}
		
		@JsonProperty("shortaddress")
		public void setChannel(String channel) {
			this.channel = channel;
		}
		@JsonProperty("manufacturer")
		public String getManufacturer() {
			return manufacturer;
		}
		
		@JsonProperty("manufacturer")
		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}
	}
	