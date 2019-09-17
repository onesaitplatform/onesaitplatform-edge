package com.onesait.edge.engine.zigbee.frame;


import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class SysReset extends ZFrame {
	
	private int serialBootloaderReset = 0;
	
	public SysReset() {
		this.serialBootloaderReset = 0;
		int[] frameData = {this.serialBootloaderReset};
		DoubleByte cmdId = new DoubleByte(ZToolCMD.SYS_RESET);
		super.buildPacket(cmdId, frameData);
	}

	public SysReset(boolean serialBootloaderReset) {
		this.serialBootloaderReset = serialBootloaderReset? 1 : 0;
		int[] frameData = {this.serialBootloaderReset};
		DoubleByte cmdId = new DoubleByte(ZToolCMD.SYS_RESET);
		super.buildPacket(cmdId, frameData);
	}
	
	public int getSerialBootloaderReset () {
		return this.serialBootloaderReset;
	}
	
	public void setSerialBootloaderReset (int sbr) {
		this.serialBootloaderReset = sbr;
	}	
}
