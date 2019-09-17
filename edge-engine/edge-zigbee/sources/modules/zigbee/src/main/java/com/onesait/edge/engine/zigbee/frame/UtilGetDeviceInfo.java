package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class UtilGetDeviceInfo extends ZFrame  {

	public UtilGetDeviceInfo(){
		super.buildPacket(new DoubleByte(ZToolCMD.UTIL_GET_DEVICE_INFO), new int[0]);
	}
}
