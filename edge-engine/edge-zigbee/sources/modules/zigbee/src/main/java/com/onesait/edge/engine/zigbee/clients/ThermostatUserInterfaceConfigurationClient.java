package com.onesait.edge.engine.zigbee.clients;

import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class ThermostatUserInterfaceConfigurationClient extends ClientCluster {

	private static final DoubleByte THERMOSTAT_USER_INTERFACE_CONFIGURATION_ID_DB = new DoubleByte(0x0204);
	
	public static final DoubleByte KEYPAD_LOOKOUT_ATTRIBUTE_ID = new DoubleByte(0x0001);
	
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
		ZclCluster tuicCluster = dev.getZclCluster(THERMOSTAT_USER_INTERFACE_CONFIGURATION_ID_DB);
		if (tuicCluster == null) {
			return new ZFrame[0];
		}
		ZFrame frame = tuicCluster.buildWriteAttributes(
				KEYPAD_LOOKOUT_ATTRIBUTE_ID, "0");
		return new ZFrame[] {frame};
	}

	@Override
	public DoubleByte getClusterId() {
		return new DoubleByte(THERMOSTAT_USER_INTERFACE_CONFIGURATION_ID_DB.intValue());
	}
}
