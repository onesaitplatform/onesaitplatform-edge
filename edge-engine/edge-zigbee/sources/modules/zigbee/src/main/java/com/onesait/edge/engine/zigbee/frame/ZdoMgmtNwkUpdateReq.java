package com.onesait.edge.engine.zigbee.frame;

import java.math.BigInteger;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;

public class ZdoMgmtNwkUpdateReq extends ZFrame implements ZdoZFrame {

	private DoubleByte dstAddr;
	private Byte dstAddrMode;
	private Integer channelMask;
	private Byte scanDuration;
	private Byte scanCount;
	private DoubleByte nwkManagerAddr;

	public ZdoMgmtNwkUpdateReq(DoubleByte dstAddr, Byte dstAddrMode, Integer channelMask, Byte scanDuration,
			Byte scanCount, DoubleByte nwkManagerAddr) {

		this.dstAddr = dstAddr;
		this.dstAddrMode = dstAddrMode;
		this.channelMask = channelMask;
		if (this.channelMask > 0x7FFF800) {
			this.channelMask = 0x7FFF800;
		}
		this.scanDuration = scanDuration;
		this.scanCount = scanCount;
		this.nwkManagerAddr = nwkManagerAddr;

		byte[] chMaskByteAr = new BigInteger(this.channelMask.toString()).toByteArray();
		if (chMaskByteAr.length < 4) {
			byte[] auxAr = new byte[4];
			for (int i = 0; i < chMaskByteAr.length; i++) {
				auxAr[i + (auxAr.length - chMaskByteAr.length)] = chMaskByteAr[i];
			}
			for (int i = 0; i < auxAr.length - chMaskByteAr.length; i++) {
				auxAr[i] = 0;
			}
			chMaskByteAr = auxAr;
		}

		int[] frameData = new int[] { this.dstAddr.getLsb(), this.dstAddr.getMsb(), this.dstAddrMode, chMaskByteAr[3],
				chMaskByteAr[2], chMaskByteAr[1], chMaskByteAr[0], this.scanDuration, this.scanCount,
				this.nwkManagerAddr.getLsb(), this.nwkManagerAddr.getMsb() };
		super.buildPacket(new DoubleByte(0x25, 0x37), frameData);
	}

	public DoubleByte getDstAddr() {
		return new DoubleByte(dstAddr.getLsb(), dstAddr.getMsb());
	}

	public Byte getDstAddrMode() {
		return dstAddrMode;
	}

	public void setDstAddrMode(Byte dstAddrMode) {
		this.dstAddrMode = dstAddrMode;
	}

	public Integer getChannelMask() {
		return channelMask;
	}

	public void setChannelMask(Integer channelMask) {
		this.channelMask = channelMask;
	}

	public Byte getScanDuration() {
		return scanDuration;
	}

	public void setScanDuration(Byte scanDuration) {
		this.scanDuration = scanDuration;
	}

	public Byte getScanCount() {
		return scanCount;
	}

	public void setScanCount(Byte scanCount) {
		this.scanCount = scanCount;
	}

	public DoubleByte getNwkManagerAddr() {
		return nwkManagerAddr;
	}

	public void setNwkManagerAddr(DoubleByte nwkManagerAddr) {
		this.nwkManagerAddr = nwkManagerAddr;
	}

	public void setDstAddr(DoubleByte dstAddr) {
		this.dstAddr = dstAddr;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return dstAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementNetworkUpdateRequest;
	}
}
