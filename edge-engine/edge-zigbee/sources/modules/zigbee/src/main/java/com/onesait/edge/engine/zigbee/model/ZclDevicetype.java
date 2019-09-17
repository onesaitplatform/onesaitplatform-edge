package com.onesait.edge.engine.zigbee.model;

import java.util.HashMap;
import java.util.Map;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class ZclDevicetype {

	private String name;
	private DoubleByte id;
	private Map<DoubleByte, String> requiredServerClusters = new HashMap<>();
	
	public ZclDevicetype(String name, DoubleByte id) {
		this.name = name;
		this.id = new DoubleByte(id.intValue());
	}
	
	public ZclDevicetype(String name, int id) {
		this(name, new DoubleByte(id));
	}

	public String getName() {
		return name;
	}

	public DoubleByte getId() {
		return id;
	}

	public Map<DoubleByte, String> getRequiredServerClusters() {
		return requiredServerClusters;
	}
}
