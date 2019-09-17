package com.onesait.edge.engine.zigbee.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"devices"
})
public class Devices {
	
	@JsonProperty("devices")
	private List<Device> devices = new ArrayList<>();
	
	@JsonProperty("devices")
	public List<Device> getDevices() {
		return devices;
	}
	
	@JsonProperty("devices")
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	public Device getDevice(Long deviceShortAddress) {
		for (Device device : devices) {
			Long sa = device.getShortaddress();
			if (sa == null) {
				continue;
			}
			if (sa.equals(deviceShortAddress)) {
				return device;
			}
		}
		return null;
	}
	
	public void putDevice(Device dev) {
		this.devices.add(dev);
	}
	
	public void removeDevice(Device dev) {
		this.devices.remove(dev);
	}
	
	public void removeDevice(Long shortAddress) {
		Device dev = getDevice(shortAddress);
		if (dev != null) {
			this.devices.remove(dev);
		}
	}
}