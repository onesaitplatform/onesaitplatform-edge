package com.onesait.edge.engine.zigbee.frame;

import java.security.InvalidParameterException;
import java.util.Arrays;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

public class UtilSetChannels extends ZFrame  {

	public UtilSetChannels(){
		int[] frameData = new int[]{ // All channels
				0x00,
				0xF8,
				0xFF,
				0x07
		};
		super.buildPacket(new DoubleByte(0x2703), frameData);
	}
	
	public UtilSetChannels(int channel){
		if (channel < ZigbeeConstants.LOWEST_CHANNEL || channel > ZigbeeConstants.HIGHEST_CHANNEL) {
			throw new InvalidParameterException("El canal debe estar en el rango [" + ZigbeeConstants.LOWEST_CHANNEL + "-" + ZigbeeConstants.HIGHEST_CHANNEL + "]");
		}
        int despl = channel - 11;
		int bitmask = 0x00000800;
		bitmask = bitmask << despl;
		
		int[] frameData = new int[]{
				(byte)bitmask,
				(byte)(bitmask >> 8),
				(byte)(bitmask >> 16),
				(byte)(bitmask >> 24)
		};
		super.buildPacket(new DoubleByte(0x2703), frameData);
	}

	@Override
	public String toString() {
		return "UTIL_SET_CHANNELS [packet=" + Arrays.toString(packet) + "]";
	}
}
