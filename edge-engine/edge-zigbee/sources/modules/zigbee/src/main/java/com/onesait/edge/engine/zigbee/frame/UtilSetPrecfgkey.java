package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

public class UtilSetPrecfgkey extends ZFrame  {
	
	public UtilSetPrecfgkey(){
		int[] frameData = new int[] { // Indra default
				0x69,
				0x6e,
				0x47,
				0x72,
				0x69,
				0x64,
				0x31,
				0x34,
				0x37,
				0x38,
				0x36,
				0x32,
				0x32,
				0x36,
				0x37,
				0x31
		};
		super.buildPacket(new DoubleByte(0x2705), frameData);
	}

	public UtilSetPrecfgkey(int[] keyBytes) {
		int[] trunkedArray = keyBytes;
		if (keyBytes.length > ZigbeeConstants.NWK_KEY_LENGTH_BYTES) {
			trunkedArray = new int[ZigbeeConstants.NWK_KEY_LENGTH_BYTES];
			for (int i = 0; i < trunkedArray.length; i++) {
				trunkedArray[i] = keyBytes[i];
			}
		}
		super.buildPacket(new DoubleByte(0x2705), trunkedArray);
	}
	
	public UtilSetPrecfgkey(byte[] keyBytes) {
		int[] trunkedArray = new int[ZigbeeConstants.NWK_KEY_LENGTH_BYTES];
		for (int i = 0; i < ZigbeeConstants.NWK_KEY_LENGTH_BYTES; i++) {
			if (i < keyBytes.length) {
				trunkedArray[i] = keyBytes[i];
			} else {
				trunkedArray[i] = 0;
			}
		}
		super.buildPacket(new DoubleByte(0x2705), trunkedArray);
	}
}
