package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;

public class ZdoMgmtNwkUpdateNotify extends ZFrame {

	//private DoubleByte srcAddr;
	private Byte status;
	private Byte scannedChannelsListCount;
	private FourByte scannedChannel;
	private DoubleByte totalTransmission;
	private DoubleByte transmissionFailures;
	private int[] chSignalStrengths;
	private DoubleByte srcAddr;
	
	public ZdoMgmtNwkUpdateNotify(int[] frameData, DoubleByte srcAddr) {
		this.srcAddr=srcAddr;
//		this.srcAddr = new DoubleByte(frameData[1], frameData[0]);
		this.status = (byte) frameData[0];
		this.scannedChannel=new FourByte((byte) frameData[4], (byte) frameData[3], (byte) frameData[2], (byte) frameData[1]);
		this.totalTransmission=new DoubleByte(frameData[6], frameData[5]);
		this.transmissionFailures=new DoubleByte(frameData[8], frameData[7]);
		this.scannedChannelsListCount = (byte) frameData[9];
		
		this.chSignalStrengths = new int[frameData[9]];
//		for (int i = 10; (i < chSignalStrengths.length+10) && (i < 12 + ZigbeeConstants.N_CHANNELS); i++) {
		for (int i = 10; i < chSignalStrengths.length+10;i++){
			this.chSignalStrengths[i-10]=frameData[i];
//			this.chSignalStrengths[i - 12] = (byte) frameData[i];
		}
		super.buildPacket(new DoubleByte(0x45, 0xB8), frameData);
	}

	public DoubleByte getSrcAddr() {
		return srcAddr;
	}

	public Byte getStatus() {
		return status;
	}

	public Byte getScannedChannelsListCount() {
		return scannedChannelsListCount;
	}

	public int[] getChSignalStrengths() {
		return chSignalStrengths;
	}
	
	
	public DoubleByte getTotalTransmission() {
		return totalTransmission;
	}

	public FourByte getScannedChannel() {
		return scannedChannel;
	}

	public DoubleByte getTransmissionFailures() {
		return transmissionFailures;
	}

	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementNetworkUpdateNotify;
	}
}
