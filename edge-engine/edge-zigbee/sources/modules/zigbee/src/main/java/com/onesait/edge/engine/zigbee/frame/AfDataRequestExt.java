package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class AfDataRequestExt extends ZFrame {

	private DoubleByte clusterID;
	private int destEndpoint;
	private OctaByte dstAddr;
	private int len;
	private int options;
	private int radius;
	private int srcEndpoint;
	private int transID;
	private byte dstAddressMode;
	private DoubleByte dstPanId;

	public AfDataRequestExt(byte dstAddressMode, OctaByte dstAddress, DoubleByte dstPanId, int dstEndPoint,
			int srcEndPoint, DoubleByte clusterId, byte transId, int bitmapOpt, int radius, int[] msg) {

		if (msg.length > 128) {
			throw new IllegalArgumentException("Payload is too big, maximum is 128");
		}
		if (dstEndPoint > 255 || srcEndPoint > 255 || transId > 255 || bitmapOpt > 255 || radius > 255) {
			throw new IllegalArgumentException("parameters not valid: dstEndPoint > 255 "
					+ "||srcEndPoint> 255  ||transId> 255 ||bitmapOpt> 255 ||radiu0s> 255");
		}

		this.dstPanId = new DoubleByte(dstPanId.intValue());
		this.dstAddressMode = dstAddressMode;
		this.clusterID = clusterId;
		this.dstAddr = dstAddress;
		this.destEndpoint = dstEndPoint;
		this.srcEndpoint = srcEndPoint;
		this.transID = transId;
		this.radius = radius;
		this.len = msg.length;
		this.options = bitmapOpt;

		int[] framedata = new int[msg.length + 19];
		framedata[0] = this.dstAddressMode;
		byte[] dstAddrByteArr = this.dstAddr.getAddressReverse();
		for (int i = 0; i < 8; i++) {
			framedata[1 + i] = dstAddrByteArr[i];
		}
		framedata[9] = dstEndPoint & 0xFF;
		framedata[10] = this.dstPanId.getLsb();
		framedata[11] = this.dstPanId.getMsb();
		framedata[12] = srcEndPoint & 0xFF;
		framedata[13] = clusterId.getLsb();
		framedata[14] = clusterId.getMsb();
		framedata[15] = transId & 0xFF;
		framedata[16] = bitmapOpt & 0xFF;
		framedata[17] = radius & 0xFF;
		framedata[18] = msg.length;
		for (int i = 0; i < msg.length; i++) {
			framedata[19 + i] = msg[i];
		}
		super.buildPacket(new DoubleByte(ZToolCMD.AF_DATA_REQUEST_EXT), framedata);
	}

	public AfDataRequestExt(byte dstAddressMode, OctaByte dstAddress, DoubleByte dstPanId, int dstEndPoint,
			int srcEndPoint, DoubleByte clusterId, byte transId, int bitmapOpt, int radius, byte[] msg) {

		if (msg.length > 128) {
			throw new IllegalArgumentException("Payload is too big, maxium is 128");
		}
		if (dstEndPoint > 255 || srcEndPoint > 255 || transId > 255 || bitmapOpt > 255 || radius > 255) {
			throw new IllegalArgumentException("parameters not valid: dstEndPoint > 255 "
					+ "||srcEndPoint> 255  ||transId> 255 ||bitmapOpt> 255 ||radiu0s> 255");
		}

		this.dstPanId = new DoubleByte(dstPanId.intValue());
		this.dstAddressMode = dstAddressMode;
		this.clusterID = clusterId;
		this.dstAddr = dstAddress;
		this.destEndpoint = dstEndPoint;
		this.srcEndpoint = srcEndPoint;
		this.transID = transId;
		this.radius = radius;
		this.len = msg.length;
		this.options = bitmapOpt;

		int[] framedata = new int[msg.length + 19];
		framedata[0] = this.dstAddressMode;
		byte[] dstAddrByteArr = this.dstAddr.getAddressReverse();
		for (int i = 0; i < 8; i++) {
			framedata[1 + i] = dstAddrByteArr[i];
		}
		framedata[9] = dstEndPoint & 0xFF;
		framedata[10] = this.dstPanId.getLsb();
		framedata[11] = this.dstPanId.getMsb();
		framedata[12] = srcEndPoint & 0xFF;
		framedata[13] = clusterId.getLsb();
		framedata[14] = clusterId.getMsb();
		framedata[15] = transId & 0xFF;
		framedata[16] = bitmapOpt & 0xFF;
		framedata[17] = radius & 0xFF;
		framedata[18] = msg.length;
		for (int i = 0; i < msg.length; i++) {
			framedata[19 + i] = msg[i];
		}
		super.buildPacket(new DoubleByte(ZToolCMD.AF_DATA_REQUEST_EXT), framedata);
	}

	public DoubleByte getClusterID() {
		return clusterID;
	}

	public void setClusterID(DoubleByte clusterID) {
		this.clusterID = clusterID;
	}

	public int getDestEndpoint() {
		return destEndpoint;
	}

	public void setDestEndpoint(int destEndpoint) {
		this.destEndpoint = destEndpoint;
	}

	public OctaByte getDstAddr() {
		return dstAddr;
	}

	public void setDstAddr(OctaByte dstAddr) {
		this.dstAddr = dstAddr;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getOptions() {
		return options;
	}

	public void setOptions(int options) {
		this.options = options;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getSrcEndpoint() {
		return srcEndpoint;
	}

	public void setSrcEndpoint(int srcEndpoint) {
		this.srcEndpoint = srcEndpoint;
	}

	public int getTransID() {
		return transID;
	}

	public void setTransID(int transID) {
		this.transID = transID;
	}

	public byte getDstAddressMode() {
		return dstAddressMode;
	}

	public void setDstAddressMode(byte dstAddressMode) {
		this.dstAddressMode = dstAddressMode;
	}

	public DoubleByte getDstPanId() {
		return dstPanId;
	}

	public void setDstPanId(DoubleByte dstPanId) {
		this.dstPanId = dstPanId;
	}

}
