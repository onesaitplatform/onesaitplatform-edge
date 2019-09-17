package com.onesait.edge.engine.zigbee.frame;

import java.util.ArrayList;
import java.util.List;

import com.onesait.edge.engine.zigbee.mesh.RoutingTableEntry;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;

public class ZdoMgmtRtgRsp extends ZFrame implements InputZdoZFrame {

	private DoubleByte srcAddr;
	private int status;
	private int rtgTableEntries;
	private int startIdx;
	private int rtgTableListCount;
	private ArrayList<RoutingTableEntry> rtgTableList;
	public List<RoutingTableEntry> getRtgTableList() {
		return rtgTableList;
	}

	public static final int RTG_TABLE_BYTES_OFFSET = 6;
	
	public ZdoMgmtRtgRsp(int[] frameData) {
		this.srcAddr = new DoubleByte(frameData[1], frameData[0]);
		this.status = frameData[2];
		if (this.status == 0) {
			this.rtgTableEntries = frameData[3];
			this.startIdx = frameData[4];
			this.rtgTableListCount = frameData[5];
		} else {
			this.rtgTableEntries = this.startIdx = this.rtgTableListCount = 0;
		}
		this.rtgTableList = new ArrayList<>();
		for (int i = 0; i < this.rtgTableListCount; i++) {
			int entryOffset = i*RoutingTableEntry.RTG_TABLE_ENTRY_BYTES_SIZE;
			DoubleByte dstAddr = new DoubleByte(frameData[RTG_TABLE_BYTES_OFFSET + entryOffset + 1],
												frameData[RTG_TABLE_BYTES_OFFSET + entryOffset]);
			byte status = (byte)frameData[RTG_TABLE_BYTES_OFFSET + entryOffset + 2];
			DoubleByte nextHop = new DoubleByte(frameData[RTG_TABLE_BYTES_OFFSET + entryOffset + 4],
												frameData[RTG_TABLE_BYTES_OFFSET + entryOffset + 3]);
			RoutingTableEntry rte = new RoutingTableEntry(dstAddr, status, nextHop);
			rtgTableList.add(rte);
		}
		super.buildPacket(new DoubleByte(0x45, 0xB2), frameData);
	}

	public DoubleByte getSrcAddr() {
		return new DoubleByte(srcAddr.getMsb(), srcAddr.getLsb());
	}

	public int getStatus() {
		return status;
	}

	public int getRtgTableEntries() {
		return rtgTableEntries;
	}

	public int getStartIdx() {
		return startIdx;
	}

	public int getRtgTableListCount() {
		return rtgTableListCount;
	}

	public RoutingTableEntry getRtgTableEntry(int idx) {
		RoutingTableEntry rte = rtgTableList.get(idx);
		if (rte == null) return null;
		return new RoutingTableEntry(rte.getDstAddr(), rte.getStatus(), rte.getNextHop());
	}
	
	public int getNextPage() {
		int nextPage = this.startIdx + this.rtgTableListCount;
		nextPage = this.rtgTableListCount == 0 ? nextPage + 1 : nextPage;
		if (nextPage >= this.rtgTableEntries)
			return -1;
		else
			return nextPage;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.srcAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementRtgResponse;
	}
}