package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class SysPing extends ZFrame {

	public SysPing() {
		int[] frameData = {};
		DoubleByte cmdId = new DoubleByte(ZToolCMD.SYS_PING);
		super.buildPacket(cmdId, frameData);
	}
}
