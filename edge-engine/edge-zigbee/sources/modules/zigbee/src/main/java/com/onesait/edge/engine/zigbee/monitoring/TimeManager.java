package com.onesait.edge.engine.zigbee.monitoring;

import java.security.InvalidParameterException;
import java.util.Timer;

import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.util.ServerManager;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;


public class TimeManager extends ServerManager {

	private Timer timerSetChipClock = new Timer("timerSetChipClok", Boolean.TRUE);
	private ZclCluster cluster;
	private OutputSerialZigbee serial;
	public TimeManager(ZclCluster cluster, OutputSerialZigbee serial) {
		if (!cluster.getId().equals(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_TIME)) {
			throw new InvalidParameterException();
		}
		this.cluster=cluster;
		this.serial=serial;
	}

	@Override
	public void init() {
		this.timerSetChipClock.schedule(new SetChipClockTask(serial,cluster),SetChipClockTask.DELAY_MS,SetChipClockTask.PERIOD_MS);
		
	}
}
