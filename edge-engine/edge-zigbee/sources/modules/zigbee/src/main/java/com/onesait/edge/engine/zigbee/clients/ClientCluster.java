package com.onesait.edge.engine.zigbee.clients;

import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.util.DoubleByte;

public abstract class ClientCluster {

	public final ZFrame[] manageFrame(AfIncomingMsg af, ZclDevice dev, MqttConnection mqttConnection) {
		ZFrame[] framesToSend;
		if (dev == null || af == null) {
			framesToSend = new ZFrame[0];
		} else if (af.isClusterSpecific()) {
			framesToSend = this.manageClusterSpecificFrame(af, dev,mqttConnection);
		} else {
			framesToSend = this.manageProfileWideFrame(af, dev);
		}
		return framesToSend;
	}
	
	protected abstract ZFrame[] manageClusterSpecificFrame(AfIncomingMsg af, ZclDevice dev, MqttConnection mqttConnection);
	
	protected abstract ZFrame[] manageProfileWideFrame(AfIncomingMsg af, ZclDevice dev);
	
	public abstract ZFrame[] init(ZclDevice dev);
	
	public abstract DoubleByte getClusterId();
}
