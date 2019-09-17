package com.onesait.edge.engine.zigbee.util;

import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.ZclCluster;

public abstract class ServerManager {

	private ZclCluster cluster;
	private OutputSerialZigbee serial;

	protected void setCluster(ZclCluster cluster) {
		this.cluster = cluster;
	}

	protected ZclCluster getCluster() {
		return cluster;
	}
	
	protected OutputSerialZigbee getSerial() {
		return serial;
	}

	protected void setSerial(OutputSerialZigbee serial) {
		this.serial = serial;
	}
	
	public abstract void init();
}
