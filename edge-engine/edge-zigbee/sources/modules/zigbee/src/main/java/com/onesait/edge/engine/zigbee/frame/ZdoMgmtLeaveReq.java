package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoMgmtLeaveReq extends ZFrame implements ZdoZFrame, Acknowledgeable{
	
	private DoubleByte nwkAddr;
    private OctaByte ieeeAddress;
    private int removeChildrenRejoin;

    public ZdoMgmtLeaveReq(DoubleByte nwkAddr, OctaByte ieeeAddress) {
    	this(nwkAddr, ieeeAddress, false, false);
    }
    
    public ZdoMgmtLeaveReq(DoubleByte nwkAddr, OctaByte ieeeAddress,
    		boolean removeChildren, boolean rejoin) {
    	this.nwkAddr=nwkAddr;
    	this.ieeeAddress=ieeeAddress;
    	int[] framedata = new int[11];
        framedata[0] = this.nwkAddr.getLsb();
        framedata[1] = this.nwkAddr.getMsb();
        for (int i = 0; i < 8; i++) {
            framedata[2 + i] = this.ieeeAddress.getAddress()[7 - i];
        }
        this.removeChildrenRejoin = removeChildren ? this.removeChildrenRejoin + 2 : this.removeChildrenRejoin;
        this.removeChildrenRejoin = rejoin ? this.removeChildrenRejoin + 1 : this.removeChildrenRejoin;
        framedata[10] = this.removeChildrenRejoin;
        super.buildPacket(new DoubleByte(ZToolCMD.ZDO_MGMT_LEAVE_REQ), framedata);
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

	public int getRemoveChildrenRejoin() {
		return removeChildrenRejoin;
	}

	public void setRemoveChildrenRejoin(int removeChildrenRejoin) {
		this.removeChildrenRejoin = removeChildrenRejoin;
	}

	@Override
	public String toString() {
		return "ZDO_MGMT_LEAVE_REQ [\n nwkAddr=" + nwkAddr + ",\n ieeeAddress="
				+ ieeeAddress + ",\n RemoveChildren_Rejoin="
				+ removeChildrenRejoin + "]";
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return getNwkAddr();
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementLeaveRequest;
	}
}
