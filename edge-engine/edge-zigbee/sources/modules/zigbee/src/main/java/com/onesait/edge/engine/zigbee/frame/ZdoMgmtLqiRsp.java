package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.mesh.NeighborTableEntry;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoMgmtLqiRsp extends ZFrame implements InputZdoZFrame{

	private int status;
	private DoubleByte srcAdd;
	/* Numero total de enlaces del dispositivo*/
	private int nTableEntries;
	/* Numero de enlaces del dispositivo que vienen en el mensaje */
	private int nTableCount;
	private int startIndex;
	private static final int NEIGHBOR_TABLE_ENTRY_BYTES_SIZE = 22;
	private static final int NEIGHBOR_TABLE_BYTES_OFFSET = 6;	
	private NeighborTableEntry[] neighbors;
	
	public ZdoMgmtLqiRsp(int[] framedata) {
		this.srcAdd = new DoubleByte(framedata[1], framedata[0]);
		this.status = (byte)framedata[2];
		if(this.status==0){
			this.nTableEntries = framedata[3];
			this.startIndex = framedata[4];
			this.nTableCount = framedata[5];
			this.neighbors = new NeighborTableEntry[this.nTableCount];
			for (int i = 0; i < this.nTableCount; i++) {
				int neighbordata[] = new int[NEIGHBOR_TABLE_ENTRY_BYTES_SIZE];
				for (int j = 0; j < neighbordata.length; j++){
					neighbordata[j] = framedata[i*NEIGHBOR_TABLE_ENTRY_BYTES_SIZE + NEIGHBOR_TABLE_BYTES_OFFSET + j];
				}
				this.neighbors[i] = new NeighborTableEntry(neighbordata);;
			}
		} else {
			this.nTableEntries = this.startIndex = this.nTableCount = 0;
		}
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_MGMT_LQI_RSP), framedata);
	}
	
	public Byte getLQI(int tableEntryIndex){
		if (tableEntryIndex >= this.nTableCount) {
			return null;
		}
		return this.neighbors[tableEntryIndex].lqi;
	}
	
	public Byte getLQI(DoubleByte neighborAddress) {
		for (int i = 0; i < this.nTableCount; i++) {
			if (this.neighbors[i].networkAddress.equals(neighborAddress))
				return this.neighbors[i].lqi;
		}
		return null;
	}

	public DoubleByte[] getNeighborsNwkAddresses() {
		DoubleByte[] nwkAdds = new DoubleByte[this.nTableCount];
		for (int i = 0; i < this.nTableCount; i++) {
			nwkAdds[i] = new DoubleByte(this.neighbors[i].networkAddress.getMsb(),
										this.neighbors[i].networkAddress.getLsb());
		}
		return nwkAdds;
	}
	
	public NeighborTableEntry[] getNeighbors() {
		return neighbors;
	}

	private boolean isOK() {
		return this.status==0;
	}
	
	public int nextPage(){
		int npage = this.startIndex+this.nTableCount;
		if (this.nTableCount == 0)
			npage += 1; 			//Para no pedir siempre la misma
		return (npage<this.nTableEntries)?npage:-1;
	}

	public DoubleByte getSrcAdd() {
		return srcAdd;
	}

	public int getStartIndex() {
		return startIndex;
	}

	@Override
	public String toString() {
		String str = "<br/>ZDO_MGMT_LQI_RSP [<br/>"+
				"src  Addr:"+this.srcAdd+", <br/>"+
				"status OK:"+this.isOK()+", <br/>"+
				"n total entries:"+this.nTableEntries+", <br/>"+
				"init page  idx:"+this.startIndex+", <br/>"+
				"n page entries:"+this.nTableCount+": <br/>";
		if (this.neighbors!=null){		
		for(NeighborTableEntry ntb:this.neighbors){
			str+="<br/>NEIGHBOR_"+ntb;
		}
		}
		return str+="]";
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.srcAdd;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementLQIResponse;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
}
