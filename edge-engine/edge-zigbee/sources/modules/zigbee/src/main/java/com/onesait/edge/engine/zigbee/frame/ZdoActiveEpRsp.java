package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoActiveEpRsp extends ZFrame implements InputZdoZFrame {

	private DoubleByte nwkAddr;
	private DoubleByte srcAddress;
	private int status;
	private int endpointCounter;
	private int[] endpoints;

	public ZdoActiveEpRsp(int[] framedata) {
		this.srcAddress = new DoubleByte(framedata[1], framedata[0]);
		this.status = framedata[2];
		this.nwkAddr = new DoubleByte(framedata[4], framedata[3]);
		this.endpointCounter = framedata[5];
		this.endpoints = new int[this.endpointCounter];
		for (int i = 0; i < this.endpointCounter; i++) {
			endpoints[i] = framedata[6 + i];
		}

		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_ACTIVE_EP_RSP), framedata);
	}

	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	public DoubleByte getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(DoubleByte srcAddress) {
		this.srcAddress = srcAddress;
	}

	public int getEndpointCounter() {
		return endpointCounter;
	}

	public void setEndpointCounter(int endpointCounter) {
		this.endpointCounter = endpointCounter;
	}

	public int[] getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(int[] endpoints) {
		this.endpoints = endpoints;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ZDO_ACTIVE_EP_RSP [\nnwkAddr=" + nwkAddr + "(" + nwkAddr.get16BitValue() + "),\n SrcAddress="
				+ srcAddress + ",\n Status=" + status + ",\n endpointCounter=" + endpointCounter + ",\n endpoints="
				+ endpoints + "]";
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.srcAddress;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ActiveEndpointsResponse;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
}
