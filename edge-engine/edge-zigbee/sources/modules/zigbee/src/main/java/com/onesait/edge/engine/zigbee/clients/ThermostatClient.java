package com.onesait.edge.engine.zigbee.clients;

import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class ThermostatClient extends ClientCluster{

	private static final DoubleByte THERMOSTAT_CLUSTER_ID_DB = new DoubleByte(0x0201);
	
	public static final DoubleByte CONTROL_SEQUENCE_OF_OPERATION_ATTRIBUTE_ID = new DoubleByte(0x001b);
	
	@Override
	protected ZFrame[] manageClusterSpecificFrame(AfIncomingMsg af, ZclDevice dev,MqttConnection connection) {
		return null;
	}

	@Override
	protected ZFrame[] manageProfileWideFrame(AfIncomingMsg af, ZclDevice dev) {
		return null;
	}

	@Override
	public ZFrame[] init(ZclDevice dev) {
		if (dev == null) return null;
		ZclCluster thermostatCluster = dev.getZclCluster(THERMOSTAT_CLUSTER_ID_DB);
		if (thermostatCluster == null) return null;
		ZFrame frame = thermostatCluster.buildWriteAttributes(
				CONTROL_SEQUENCE_OF_OPERATION_ATTRIBUTE_ID, "4");
		return new ZFrame[] {frame};
	}

	@Override
	public DoubleByte getClusterId() {
		return new DoubleByte(THERMOSTAT_CLUSTER_ID_DB.intValue());
	}
}
