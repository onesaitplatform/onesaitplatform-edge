package com.onesait.edge.engine.zigbee.jsoncontroller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		"name",
		"attributes"
	})
	public class ZbClusterGraph {
		
		@JsonProperty("name")
		private String name;
		@JsonProperty("attributes")
		private List<String> attributes=new ArrayList<>();
		
		@JsonProperty("name")
		public String getName() {
			return name;
		}
		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}
		@JsonProperty("attributes")
		public List<String> getAttributes() {
			return attributes;
		}
		@JsonProperty("attributes")
		public void setAttributes(List<String> attributes) {
			this.attributes = attributes;
		}
	
		
}
