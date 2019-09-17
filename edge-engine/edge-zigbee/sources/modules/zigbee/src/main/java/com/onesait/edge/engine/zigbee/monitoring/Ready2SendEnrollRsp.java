package com.onesait.edge.engine.zigbee.monitoring;

import java.util.Timer;
import java.util.TimerTask;

import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class Ready2SendEnrollRsp extends TimerTask {

	private Timer task;
	private OutputSerialZigbee out;
	private ZclDevice device;
	public static final long PERIOD_MS = 300;
	public static final long DELAY_MS = 0;
	private static final long MAX_ATTEMPTS=200;
	private static final DoubleByte IAS_ZONE_CLUSTER_ID_DB = new DoubleByte(0x0500);
	private AfIncomingMsg zoneEnrollRequest;
	private long attempt=0;

	public Ready2SendEnrollRsp(ZclDevice dev, OutputSerialZigbee out, Timer timer,AfIncomingMsg zoneEnrollRequest) {
		this.device = dev;
		this.out = out;
		this.task = timer;
		this.zoneEnrollRequest=zoneEnrollRequest;

	}

	@Override
	public void run() {
		
		try {
			if (attempt <= MAX_ATTEMPTS) {
				ZclCluster iasZoneCluster = device.getZclCluster(IAS_ZONE_CLUSTER_ID_DB);
				attempt++;
				if (iasZoneCluster != null) {
					ZFrame zoneEnrollResponse = iasZoneCluster.buildCmd(zoneEnrollRequest.getSequenceNumber(),
							(byte) 0x00, null);
					out.writeZFrame(zoneEnrollResponse);
					task.purge();
					task.cancel();
				}
			} else {
				task.purge();
				task.cancel();
			}
		} catch (Exception e) {
			task.purge();
			task.cancel();
		} finally {
			Thread.currentThread().interrupt();
		}

	}
}
