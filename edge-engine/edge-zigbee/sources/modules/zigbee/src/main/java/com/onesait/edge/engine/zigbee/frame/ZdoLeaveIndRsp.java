package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class ZdoLeaveIndRsp extends ZFrame implements InputZdoZFrame {
	
	private DoubleByte nwkAddr;
    private OctaByte ieeeAddress;
    private int request;
    private int removechildren;
    private int rejoin;

    public ZdoLeaveIndRsp(int[] framedata) {
 
        this.nwkAddr=new DoubleByte(framedata[1],framedata[0]);
        byte[] bytes = new byte[8];
        bytes[7]=(byte)framedata[2];
        bytes[6]=(byte)framedata[3];
        bytes[5]=(byte)framedata[4];
        bytes[4]=(byte)framedata[5];
        bytes[3]=(byte)framedata[6];
        bytes[2]=(byte)framedata[7];
        bytes[1]=(byte)framedata[8];
        bytes[0]=(byte)framedata[9];
        this.ieeeAddress = new OctaByte(bytes);
        this.request = framedata[10];
        this.removechildren = framedata[11];
        this.rejoin = framedata[12];
      
        super.buildPacket(new DoubleByte(ZToolCMD.ZDO_LEAVE_IND), framedata);
    }


	@Override
	public String toString() {
		return "ZDO_LEAVE_IND_RSP [\nnwkAddr=" + nwkAddr + ",\n ieeeAddress="
				+ ieeeAddress + ",\n request=" + request + ",\n removechildren="
				+ removechildren + ",\n rejoin=" + rejoin + "]";
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.nwkAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementLeaveResponse;
	}

	@Override
	public int getStatus() {
		return ZClusterLibrary.ZCL_STATUS_SUCCESS;
	}
	
	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	public OctaByte getIeeeAddress() {
		return ieeeAddress;
	}

	public void setIeeeAddress(OctaByte ieeeAddress) {
		this.ieeeAddress = ieeeAddress;
	}

	public int getRequest() {
		return request;
	}

	public void setRequest(int request) {
		this.request = request;
	}

	public int getRemovechildren() {
		return removechildren;
	}

	public void setRemovechildren(int removechildren) {
		this.removechildren = removechildren;
	}

	public int getRejoin() {
		return rejoin;
	}

	public void setRejoin(int rejoin) {
		this.rejoin = rejoin;
	}

}
