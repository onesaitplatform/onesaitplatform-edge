
package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZbGetDeviceInfo extends ZFrame  {

	private int item = 0;

	public int getItem() {
		return item;
	}

	public ZbGetDeviceInfo(int item) {
		this.item = item;
		int []frameData = {item};
		super.buildPacket(new DoubleByte(ZToolCMD.ZB_GET_DEVICE_INFO), frameData);
	}
}
