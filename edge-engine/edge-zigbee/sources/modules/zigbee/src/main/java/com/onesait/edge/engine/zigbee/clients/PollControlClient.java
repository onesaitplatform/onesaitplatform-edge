package com.onesait.edge.engine.zigbee.clients;

import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class PollControlClient extends ClientCluster {

	// Attributes
	public static final DoubleByte CHECK_IN_ATTRIBUTE_ID_DB = new DoubleByte(0);
	public static final DoubleByte LONG_POLL_INTERVAL_ATTRIBUTE_ID_DB = new DoubleByte(1);
	public static final DoubleByte SHORT_POLL_INTERVAL_ATTRIBUTE_ID_DB = new DoubleByte(2);
	public static final DoubleByte FAST_POLL_TIMEOUT_ATTRIBUTE_ID_DB = new DoubleByte(3);
	
	// Commands
	public static final Byte CHECK_IN_RESPONSE_COMMAND_ID = 0x00;
	public static final Byte FAST_POLL_STOP_COMMAND_ID = 0x01;
	
	public static final DoubleByte POLL_CONTROL_CLUSTER_ID_DB = new DoubleByte(0x20);
	
	private long longPollIntervalToSet = 30; // In quarter seconds
	
	@Override
	protected ZFrame[] manageClusterSpecificFrame(AfIncomingMsg af, ZclDevice dev,MqttConnection connection) {
		return new ZFrame[0];
	}

	@Override
	protected ZFrame[] manageProfileWideFrame(AfIncomingMsg af, ZclDevice dev) {
		return new ZFrame[0];
	}

	@Override
	public ZFrame[] init(ZclDevice dev) {
		if (dev == null) {
			return new ZFrame[0];
		}
		ZclCluster pollControl = dev.getZclCluster(POLL_CONTROL_CLUSTER_ID_DB);
		if (pollControl == null) {
			return new ZFrame[0];
		}
		ZFrame writeLongPollFrame = pollControl.buildWriteAttributes(
				LONG_POLL_INTERVAL_ATTRIBUTE_ID_DB, this.longPollIntervalToSet + "");
		return new ZFrame[] {writeLongPollFrame};
	}

	public long getLongPollIntervalToSet() {
		return longPollIntervalToSet;
	}

	public void setLongPollIntervalToSet(long longPollIntervalToSet) {
		this.longPollIntervalToSet = longPollIntervalToSet;
	}

	@Override
	public DoubleByte getClusterId() {
		return POLL_CONTROL_CLUSTER_ID_DB;
	}
}
