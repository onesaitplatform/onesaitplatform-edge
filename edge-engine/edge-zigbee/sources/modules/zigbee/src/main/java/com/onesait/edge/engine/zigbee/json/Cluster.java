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
	"package",
	"manufacturerspecific",
	"input",
	"attributes"
})
public class Cluster {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("package")
	private String _package;
	@JsonProperty("manufacturerspecific")
	private boolean manufacturerspecific;
	@JsonProperty("input")
	private boolean input = true;
	@JsonProperty("attributes")
	private List<Attribute> attributes = new ArrayList<>();
	@JsonProperty("configured")
	private boolean configured;
	
	@JsonProperty("configured")
	public boolean isConfigured() {
		return configured;
	}
	@JsonProperty("configured")
	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

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
	
	@JsonProperty("package")
	public String getPackage() {
		return _package;
	}
	
	@JsonProperty("package")
	public void setPackage(String _package) {
		this._package = _package;
	}
	
	@JsonProperty("manufacturerspecific")
	public boolean isManufacturerspecific() {
		return manufacturerspecific;
	}
	
	@JsonProperty("manufacturerspecific")
	public void setManufacturerspecific(boolean manufacturerspecific) {
		this.manufacturerspecific = manufacturerspecific;
	}
	
	@JsonProperty("input")
	public boolean isInput() {
		return input;
	}
	
	@JsonProperty("input")
	public void setInput(boolean input) {
		this.input = input;
	}
	
	@JsonProperty("attributes")
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	@JsonProperty("attributes")
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	private int findAttributeIdx(long attributeId) {
		for (Attribute attribute : attributes) {
			if (attribute.getId().equals(attributeId)) {
				return attributes.indexOf(attribute);
			}
		}
		return -1;
	}
	
	public Attribute getAttribute(long attributeId) {
		if (findAttributeIdx(attributeId) != -1) {
			return this.attributes.get(findAttributeIdx(attributeId));
		} else {
			return null;
		}
	}

	public void removeAttribute(long attributeId) {
		if (findAttributeIdx(attributeId) != -1) {
			this.attributes.remove(findAttributeIdx(attributeId));
		}
	}
	
	public void addAttribute(Attribute att) {
		this.attributes.add(att);
	}
}