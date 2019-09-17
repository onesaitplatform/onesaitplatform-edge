package com.onesait.edge.engine.zigbee.jsoncontroller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		"mac",
		"clusters"
	})
	public class ZbDevicesGraph {
		
		@JsonProperty("mac")
		private String mac;
		@JsonProperty("clusters")
		private List<ZbClusterGraph> clusters=new ArrayList<>();
		
		@JsonProperty("mac")
		public String getMac() {
			return mac;
		}
		@JsonProperty("mac")
		public void setMac(String mac) {
			this.mac = mac;
		}
		@JsonProperty("clusters")
		public List<ZbClusterGraph> getClusters() {
			return clusters;
		}
		@JsonProperty("clusters")
		public void setClusters(List<ZbClusterGraph> clusters) {
			this.clusters = clusters;
		}
}
	