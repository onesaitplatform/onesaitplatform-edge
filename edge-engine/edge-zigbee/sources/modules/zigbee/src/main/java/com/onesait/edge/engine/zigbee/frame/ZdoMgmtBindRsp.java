package com.onesait.edge.engine.zigbee.frame;

import java.util.ArrayList;

import com.onesait.edge.engine.zigbee.mesh.BindingTableEntry;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;

public class ZdoMgmtBindRsp extends ZFrame implements InputZdoZFrame {

	private DoubleByte srcAddr;
	private int status;
	private int bindingTableEntries;
	private int startIdx;
	private int bindingTableListCount;
	private ArrayList<BindingTableEntry> bindingTableList;
	public static final int BINDING_TABLE_BYTES_OFFSET = 6;
	
	public ZdoMgmtBindRsp(int[] frameData) {
		this.srcAddr = new DoubleByte(frameData[1], frameData[0]);
		this.status = frameData[2];
		if (this.status == 0) {
			this.bindingTableEntries = frameData[3];
			this.startIdx = frameData[4];
			this.bindingTableListCount = frameData[5];
		} else {
			this.bindingTableEntries = this.startIdx = this.bindingTableListCount = 0;
		}
		this.bindingTableList = new ArrayList<>();
		
		for (int i = 0; i < this.bindingTableListCount; i++) {
			int entryOffset = i*BindingTableEntry.BINDING_TABLE_ENTRY_BYTES_SIZE;
			int[] entryData = new int[BindingTableEntry.BINDING_TABLE_ENTRY_BYTES_SIZE];
			
			for (int j = 0; j < entryData.length; j++)
				entryData[j] = frameData[BINDING_TABLE_BYTES_OFFSET + j + entryOffset];			
			BindingTableEntry rte = new BindingTableEntry(entryData);
			bindingTableList.add(rte);
		}
		super.buildPacket(new DoubleByte(0x45, 0xB3), frameData);
	}

	public DoubleByte getSrcAddr() {
		return new DoubleByte(srcAddr.getMsb(), srcAddr.getLsb());
	}

	public int getStatus() {
		return status;
	}

	public int getBindingTableEntries() {
		return bindingTableEntries;
	}

	public int getStartIdx() {
		return startIdx;
	}

	public int getBindingTableListCount() {
		return bindingTableListCount;
	}

	public BindingTableEntry getBindingTableEntry(int idx) {
		BindingTableEntry rte = bindingTableList.get(idx);
		if (rte == null) return null;
		return new BindingTableEntry(rte);
	}
	
	public int getNextPage() {
		int nextPage = this.startIdx + this.bindingTableListCount;
		nextPage = this.bindingTableListCount == 0 ? nextPage + 1 : nextPage;
		if (nextPage >= this.bindingTableEntries)
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
		return ZDOCommand.ManagementBindingResponse;
	}
}
