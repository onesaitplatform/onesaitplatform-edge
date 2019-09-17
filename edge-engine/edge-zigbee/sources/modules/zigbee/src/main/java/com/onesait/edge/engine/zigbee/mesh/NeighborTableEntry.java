package com.onesait.edge.engine.zigbee.mesh;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;

public class NeighborTableEntry {
	public final OctaByte extendedPanID;
	public final OctaByte extendedAddress;
	public final DoubleByte networkAddress;
	public final Byte deviceInfo;
	public final Byte permitJoining;
	public final Byte depth;
	public final Byte lqi;
	
	public NeighborTableEntry(int[] entrydata) {
		this.extendedPanID = new OctaByte((byte)entrydata[7], (byte)entrydata[6],
										  (byte)entrydata[5], (byte)entrydata[4],
										  (byte)entrydata[3], (byte)entrydata[2],
										  (byte)entrydata[1], (byte)entrydata[0]);
		this.extendedAddress = new OctaByte((byte)entrydata[15], (byte)entrydata[14],
										  (byte)entrydata[13], (byte)entrydata[12],
										  (byte)entrydata[11], (byte)entrydata[10],
										  (byte)entrydata[9], (byte)entrydata[8]);
		this.networkAddress = new DoubleByte((byte)entrydata[17], (byte)entrydata[16]);
		this.deviceInfo = (byte)entrydata[18];
		this.permitJoining = (byte)entrydata[19];
		this.depth = (byte)entrydata[20];
		this.lqi = (byte)entrydata[21];
	}

	@Override
	public String toString() {
		return "NeighborTableEntry [extendedPanID=" + extendedPanID + ", extendedAddress=" + extendedAddress + ", networkAddress=" + networkAddress + ", deviceInfo=" + deviceInfo
				+ ", permitJoining=" + permitJoining + ", depth=" + depth + ", lqi=" + lqi + "]";
	}
	
	
}
