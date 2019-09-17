package com.onesait.edge.engine.zigbee.frame;

import java.util.Arrays;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoIeeeAddrRsp extends ZFrame implements InputZdoZFrame {
	/**
	 * Dynamic array, array of 16 bit short addresses - list of network address for
	 * associated devices. This list can be a partial list if the entire list
	 * doesn't fit into a packet. If it is a partial list, the starting index is
	 * StartIndex.
	 */
	private DoubleByte[] assocDevList;
	private OctaByte ieeeAddr;
	private DoubleByte nwkAddr;
	private int numAssocDev;
	/** Source address, size is dependent on SrcAddrMode */
	private OctaByte srcAddress;
	/** indicates that the SrcAddr is either 16 bits or 64 bits */
	private int srcAddrMode;
	/** Starting index into the list of associated devices for this report */
	private int startIndex;
	/** this field indicates either SUCCESS or FAILURE */
	private int status;

	public ZdoIeeeAddrRsp(int[] framedata) {
		this.status = framedata[0];
		byte[] bytes = new byte[8];
		for (int i = 0; i < 8; i++) {
			bytes[i] = (byte) framedata[8 - i];
		}
		this.ieeeAddr = new OctaByte(bytes);
		this.nwkAddr = new DoubleByte(framedata[10], framedata[9]);
		this.startIndex = framedata[11];
		this.numAssocDev = framedata[12];
		this.assocDevList = new DoubleByte[this.numAssocDev];
		for (int i = 0; i < this.assocDevList.length; i++) {
			this.assocDevList[i] = new DoubleByte(framedata[14 + (i * 2)], framedata[13 + (i * 2)]);
		}
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_IEEE_ADDR_RSP), framedata);
	}

	public DoubleByte[] getAssocDevList() {
		return assocDevList;
	}

	public void setAssocDevList(DoubleByte[] assocDevList) {
		this.assocDevList = assocDevList;
	}

	public OctaByte getIeeeAddr() {
		return ieeeAddr;
	}

	public void setIeeeAddr(OctaByte ieeeAddr) {
		this.ieeeAddr = ieeeAddr;
	}

	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	public int getNumAssocDev() {
		return numAssocDev;
	}

	public void setNumAssocDev(int numAssocDev) {
		this.numAssocDev = numAssocDev;
	}

	public OctaByte getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(OctaByte srcAddress) {
		this.srcAddress = srcAddress;
	}

	public int getSrcAddrMode() {
		return srcAddrMode;
	}

	public void setSrcAddrMode(int srcAddrMode) {
		this.srcAddrMode = srcAddrMode;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ZDO_IEEE_ADDR_RSP:[\n AssocDevList=" + Arrays.toString(assocDevList) + ",\n IEEEAddr=" + ieeeAddr
				+ ",\n nwkAddr=" + nwkAddr + "(" + nwkAddr.get16BitValue() + "),\n NumAssocDev=" + numAssocDev
				+ ",\n SrcAddress=" + srcAddress + ",\n SrcAddrMode=" + srcAddrMode + ",\n StartIndex=" + startIndex
				+ ",\n Status=" + status + "]";
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.nwkAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.IEEEAddressResponse;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
}
