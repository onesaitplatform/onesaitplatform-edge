package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class ZdoMgmtPermitJoinRequest extends ZFrame implements ZdoZFrame, Acknowledgeable {

	private DoubleByte nwkAddr;
	
	public ZdoMgmtPermitJoinRequest(DoubleByte dstAddr, int duration_sec) {
		int[] framedata = new int[5];
		this.nwkAddr = dstAddr;
		framedata[0] = 0x02;// Broadcast
//		framedata[1] = 0x00;// FF--> ALL; 0x00 --> PERMIT JOIN
//		framedata[2] = 0x00;// FF--> ALL; 0x00 --> PERMIT JOIN
		framedata[1] = dstAddr.getLsb();
		framedata[2] = dstAddr.getMsb();
		framedata[3] = (byte)duration_sec;
		framedata[4] = 1;// 0= security by coordinator
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_MGMT_PERMIT_JOIN_REQ),
				framedata);
	}
	
	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}



	@Override
	public DoubleByte getZdoNwkAddr() {
		return nwkAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementPermitJoinRequest;
	}
}
