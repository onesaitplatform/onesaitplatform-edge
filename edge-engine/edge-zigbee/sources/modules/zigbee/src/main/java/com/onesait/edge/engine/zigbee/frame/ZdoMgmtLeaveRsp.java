package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoMgmtLeaveRsp extends ZFrame {
	
	private DoubleByte srcAddr;
    private int status;

    public ZdoMgmtLeaveRsp(int [] frame) {
    	this.srcAddr=new DoubleByte(frame[1], frame[0]);
    	this.status=frame[2];
    	super.buildPacket(new DoubleByte(ZToolCMD.ZDO_MGMT_LEAVE_REQ), frame);
    }

	public DoubleByte getSrcAddr() {
		return srcAddr;
	}

	public void setSrcAddr(DoubleByte srcAddr) {
		this.srcAddr = srcAddr;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    
    
	

}
