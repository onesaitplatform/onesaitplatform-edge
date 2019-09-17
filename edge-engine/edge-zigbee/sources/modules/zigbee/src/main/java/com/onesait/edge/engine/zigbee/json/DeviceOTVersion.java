package com.onesait.edge.engine.zigbee.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Current File Version", "Stack Build", "Stack Release", "Application Build", "Application Release",

})

public class DeviceOTVersion {

	@JsonProperty("Current File Version")
	private String currentFileVersion;
	@JsonProperty("Stack Build")
	private String stackBuild;
	@JsonProperty("Stack Release")
	private String stackRelease;
	@JsonProperty("Application Build")
	private String applicationBuild;
	@JsonProperty("Application Release")
	private String applicationRelease;

	@JsonProperty("Current File Version")
	public String getCurrentFileVersion() {
		return currentFileVersion;
	}

	@JsonProperty("Current File Version")
	public void setCurrentFileVersion(String currentFileVersion) {
		this.currentFileVersion = currentFileVersion;
	}

	@JsonProperty("Stack Build")
	public String getStackBuild() {
		return stackBuild;
	}

	@JsonProperty("Stack Build")
	public void setStackBuild(String stackBuild) {
		this.stackBuild = stackBuild;
	}

	@JsonProperty("Stack Release")
	public String getStackRelease() {
		return stackRelease;
	}

	@JsonProperty("Stack Release")
	public void setStackRelease(String stackRelease) {
		this.stackRelease = stackRelease;
	}

	@JsonProperty("Application Build")
	public String getApplicationBuild() {
		return applicationBuild;
	}

	@JsonProperty("Application Build")
	public void setApplicationBuild(String applicationBuild) {
		this.applicationBuild = applicationBuild;
	}

	@JsonProperty("Application Release")
	public String getApplicationRelease() {
		return applicationRelease;
	}

	@JsonProperty("Application Release")
	public void setApplicationRelease(String applicationRelease) {
		this.applicationRelease = applicationRelease;
	}

}
