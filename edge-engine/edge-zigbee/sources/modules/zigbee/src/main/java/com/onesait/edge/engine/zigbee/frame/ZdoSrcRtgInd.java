package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoSrcRtgInd extends ZFrame {
	
	public ZdoSrcRtgInd(DoubleByte dstAdd, DoubleByte[] hops) {
		DoubleByte cmdId = new DoubleByte(ZToolCMD.ZDO_SRC_RTG_IND);
		int frameData[] = new int[hops.length*2 + 3];
		frameData[0] = dstAdd.getMsb();
		frameData[1] = dstAdd.getLsb();
		frameData[2] = hops.length;
		for (int i = 0; i < hops.length; i++) {
			frameData[2*i] = hops[i].getMsb();
			frameData[2*i + 1] = hops[i].getLsb();
		}
		super.buildPacket(cmdId, frameData);
	}
	
	public ZdoSrcRtgInd(int[] frameData) {
		DoubleByte cmdId = new DoubleByte(ZToolCMD.ZDO_SRC_RTG_IND);
		super.buildPacket(cmdId, frameData);
	}
	
	public DoubleByte getDstAdd() {
		return new DoubleByte(this.getData()[0], this.getData()[1]);
	}
	
	public Byte getRelayCount() {
		return new Byte((byte)this.getData()[2]);
	}
	
	public DoubleByte getHopAddress(int n) {
		if (n < (this.getData().length - 3) / 2)
			return new DoubleByte(this.getData()[3 + 2*n], this.getData()[3 + 2*n + 1]);
		else
			return null;
	}
}
