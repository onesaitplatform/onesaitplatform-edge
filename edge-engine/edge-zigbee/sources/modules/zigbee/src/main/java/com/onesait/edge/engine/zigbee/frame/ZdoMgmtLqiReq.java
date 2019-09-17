package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoMgmtLqiReq extends ZFrame implements ZdoZFrame{

	private DoubleByte nwkAddr;
	
	public ZdoMgmtLqiReq(DoubleByte dstAdd, int offset) {
		int[] framedata = new int[3];
		this.nwkAddr = dstAdd;
		framedata[0] = dstAdd.getLsb();
		framedata[1] = dstAdd.getMsb();
		framedata[2] = offset;
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_MGMT_LQI_REQ), framedata);
	}

	public int getOffset() {
		return this.packet[2];
	}
	
	public DoubleByte getDstAdd() {
		return new DoubleByte(this.packet[1], this.packet[0]);
	}

	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return getNwkAddr();
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementLQIRequest;
	}
}
