package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;

public class ZdoMgmtBindReq extends ZFrame implements ZdoZFrame {

	private int startIdx = 0;
	private DoubleByte dstAddr;
	
	public ZdoMgmtBindReq(DoubleByte dstAddr, int startIdx) {
		this.dstAddr = new DoubleByte(dstAddr.getMsb(), dstAddr.getLsb());
		this.startIdx = startIdx;
		int[] frameData = new int[] {
				this.dstAddr.getLsb(),
				this.dstAddr.getMsb(),
				this.startIdx
		};
		super.buildPacket(new DoubleByte(0x25, 0x33), frameData);
	}

	public int getStartIdx () {
		return this.startIdx;
	}
	
	public DoubleByte getDstAddr() {
		return new DoubleByte(dstAddr.getLsb(), dstAddr.getMsb());
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return dstAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementBindingRequest;
	}
}
