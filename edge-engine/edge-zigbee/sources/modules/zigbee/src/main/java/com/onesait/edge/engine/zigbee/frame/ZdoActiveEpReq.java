package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class ZdoActiveEpReq extends ZFrame implements ZdoZFrame {
	
	private DoubleByte dstAddr;
	
    public ZdoActiveEpReq(DoubleByte dstAddr, DoubleByte nwkAddressOfInterest) {
    	this.dstAddr = dstAddr;
        int[] framedata = new int[4];
        framedata[0] = dstAddr.getLsb();
        framedata[1] = dstAddr.getMsb();
        framedata[2] = nwkAddressOfInterest.getLsb();
        framedata[3] = nwkAddressOfInterest.getMsb();
        super.buildPacket(new DoubleByte(ZToolCMD.ZDO_ACTIVE_EP_REQ), framedata);
    }
    
    public ZdoActiveEpReq(DoubleByte dstAddr) {
    	this.dstAddr = dstAddr;
        int[] framedata = new int[4];
        framedata[0] = dstAddr.getLsb();
        framedata[1] = dstAddr.getMsb();
        framedata[2] = dstAddr.getLsb();
        framedata[3] = dstAddr.getMsb();
        super.buildPacket(new DoubleByte(ZToolCMD.ZDO_ACTIVE_EP_REQ), framedata);
    }
    
	public DoubleByte getDstAddr() {
		return dstAddr;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return getDstAddr();
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ActiveEndpointsRequest;
	}
}
