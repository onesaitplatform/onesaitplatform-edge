package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.ByteUtils;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoEndDeviceAnnceInd extends ZFrame {

	private DoubleByte nwkAddr;
	private DoubleByte srcAddr;
	private OctaByte ieeeAddress;
	private byte capabilities;

	public ZdoEndDeviceAnnceInd(int[] framedata) {

		this.nwkAddr = new DoubleByte(framedata[1], framedata[0]);
		this.srcAddr = new DoubleByte(framedata[3], framedata[2]);
		byte[] bytes = new byte[8];
		bytes[7] = (byte) framedata[4];
		bytes[6] = (byte) framedata[5];
		bytes[5] = (byte) framedata[6];
		bytes[4] = (byte) framedata[7];
		bytes[3] = (byte) framedata[8];
		bytes[2] = (byte) framedata[9];
		bytes[1] = (byte) framedata[10];
		bytes[0] = (byte) framedata[11];
		this.ieeeAddress = new OctaByte(bytes);
		this.capabilities = (byte) framedata[12];

		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_LEAVE_IND), framedata);
	}

	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	public DoubleByte getSrcAddr() {
		return srcAddr;
	}

	public void setSrcAddr(DoubleByte srcAddr) {
		this.srcAddr = srcAddr;
	}

	public OctaByte getIeeeAddress() {
		return ieeeAddress;
	}

	public void setIeeeAddress(OctaByte ieeeAddress) {
		this.ieeeAddress = ieeeAddress;
	}

	public byte getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(byte capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public String toString() {
		return "ZDO_END_DEVICE_ANNCE_IND [\nnwkAddr=" + nwkAddr + ",\n srcAddr=" + srcAddr + ",\n ieeeAddress="
				+ ieeeAddress + ",\n capabilities=" + ByteUtils.toBase16(capabilities) + "]";
	}
}
