package com.onesait.edge.engine.zigbee.mesh;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class RoutingTableEntry {

	private DoubleByte dstAddr;
	private byte status;
	private DoubleByte nextHop;
	public static final int RTG_TABLE_ENTRY_BYTES_SIZE = 5;
	
	public RoutingTableEntry(DoubleByte dstAddr, byte status, DoubleByte nextHop) {
		this.dstAddr = new DoubleByte(dstAddr.getMsb(), dstAddr.getLsb());
		this.status = status;
		this.nextHop = new DoubleByte(nextHop.getMsb(), nextHop.getLsb());
	}

	public DoubleByte getDstAddr() {
		return new DoubleByte(dstAddr.getMsb(), dstAddr.getLsb());
	}

	public byte getStatus() {
		return status;
	}

	public DoubleByte getNextHop() {
		return new DoubleByte(nextHop.getMsb(), nextHop.getLsb());
	}

	@Override
	public String toString() {
		return "RoutingTableEntry [dstAddr=" + dstAddr.toString() + ", status=" +
				status + ", nextHop=" + nextHop.toString() + "]";
	}
	
}
