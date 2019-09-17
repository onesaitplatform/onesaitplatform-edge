package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class ZdoSimpleDescReq extends ZFrame implements ZdoZFrame{
	
	private DoubleByte dstAddr;
	
    public ZdoSimpleDescReq(DoubleByte dstAddr, DoubleByte nwkAddressOfInterest, int endPoint) {
    	this.dstAddr = dstAddr;
        int[] framedata = new int[5];
        framedata[0] = dstAddr.getLsb();
        framedata[1] = dstAddr.getMsb();
        framedata[2] = nwkAddressOfInterest.getLsb();
        framedata[3] = nwkAddressOfInterest.getMsb();
        framedata[4] = endPoint;
        super.buildPacket(new DoubleByte(ZToolCMD.ZDO_SIMPLE_DESC_REQ), framedata);
    }

	public DoubleByte getDstAddr() {
		return dstAddr;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return dstAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.SimpleDescriptorRequest;
	}
}
