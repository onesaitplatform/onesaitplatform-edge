package com.onesait.edge.engine.zigbee.mesh;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;

public class BindingTableEntry {

	private OctaByte srcAddr;
	private int srcEp;
	private DoubleByte clusterId;
	private int dstAddrMode;
	private OctaByte dstAddr;
	private int dstEp;
	public static final int BINDING_TABLE_ENTRY_BYTES_SIZE = 21;
	
	public BindingTableEntry(int[] entrydata) {
		this.srcAddr = new OctaByte((byte)entrydata[7], (byte)entrydata[6],
									  (byte)entrydata[5], (byte)entrydata[4],
									  (byte)entrydata[3], (byte)entrydata[2],
									  (byte)entrydata[1], (byte)entrydata[0]);
		this.srcEp = entrydata[8];
		this.clusterId = new DoubleByte(entrydata[10], entrydata[9]);
		this.dstAddrMode = entrydata[11];
		this.dstAddr = new OctaByte((byte)entrydata[19], (byte)entrydata[18],
									  (byte)entrydata[17], (byte)entrydata[16],
									  (byte)entrydata[15], (byte)entrydata[14],
									  (byte)entrydata[13], (byte)entrydata[12]);
		this.dstEp = (byte)entrydata[20];
	}

	public BindingTableEntry(BindingTableEntry bte) {
		if (bte == null) return;
		this.srcAddr = new OctaByte(bte.getSrcAddr().getAddress());
		this.srcEp = bte.getSrcEp();
		this.clusterId = new DoubleByte(bte.getClusterId().getMsb(), bte.getClusterId().getLsb());
		this.dstAddrMode = bte.getDstAddrMode();
		this.dstAddr = new OctaByte(bte.getDstAddr().getAddress());
		this.dstEp = bte.getDstEp();
	}
	
	@Override
	public String toString() {
		return "BindingTableEntry [srcAddr=" + srcAddr + ", srcEp=" + srcEp + ", clusterId=" + clusterId
				+ ", dstAddrMode=" + dstAddrMode + ", dstAddr=" + dstAddr + ", dstEp=" + dstEp + "]";
	}

	public OctaByte getSrcAddr() {
		return new OctaByte(srcAddr.getAddress());
	}

	public int getSrcEp() {
		return srcEp;
	}

	public DoubleByte getClusterId() {
		return new DoubleByte(clusterId.getMsb(), clusterId.getLsb());
	}

	public int getDstAddrMode() {
		return dstAddrMode;
	}

	public OctaByte getDstAddr() {
		return new OctaByte(dstAddr.getAddress());
	}

	public int getDstEp() {
		return dstEp;
	}
	
}
