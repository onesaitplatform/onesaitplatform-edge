package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class ZbStartRequest extends ZFrame  {

	public ZbStartRequest() {
		super.buildPacket(new DoubleByte(ZToolCMD.ZB_START_REQUEST), new int[0]);
	}
}
