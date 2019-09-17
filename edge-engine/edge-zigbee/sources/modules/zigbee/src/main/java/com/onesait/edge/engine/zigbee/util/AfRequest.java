package com.onesait.edge.engine.zigbee.util;

import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.model.Request;

public class AfRequest extends Request {

	private int cmdId;
	private int sequenceId;
	private DoubleByte clusterId;
	private boolean profileWide = true;
	private DoubleByte optAttId;

	public AfRequest(ZFrame zf) {
		if (zf instanceof AfIncomingMsg) {
			setParameters((AfIncomingMsg) zf);
		} else if (zf instanceof AfDataRequest) {
			setParameters((AfDataRequest) zf);
		}
	}
	
	private void setParameters(AfIncomingMsg afInc) {
		int incCmd = afInc.getZclCmd();
		this.profileWide = true;
		if (incCmd == ZClusterLibrary.ZCL_CMD_READ_RSP) {
			this.cmdId = ZClusterLibrary.ZCL_CMD_READ;
		} else if (incCmd == ZClusterLibrary.ZCL_CMD_WRITE_RSP) {
			this.cmdId = ZClusterLibrary.ZCL_CMD_WRITE;
		} else if (incCmd == ZClusterLibrary.ZCL_CMD_CONFIG_REPORT_RSP) {
			this.cmdId = ZClusterLibrary.ZCL_CMD_CONFIG_REPORT;
		} else if (incCmd == ZClusterLibrary.ZCL_CMD_DEFAULT_RSP) {
			this.cmdId = afInc.getAfIncomingData()[afInc.getZclPayloadOffset()];
			int status = afInc.getAfIncomingData()[afInc.getZclPayloadOffset() + 1];
			// TODO Comprobar para cada dispositivo que responde con uno de estos dos al enviar un cfg
			// reporting especifico de fabricante (Para meters)
			if (!(status == (ZClusterLibrary.ZCL_STATUS_UNSUP_MANU_GENERAL_COMMAND & 0xFF) ||
					status == (ZClusterLibrary.ZCL_STATUS_UNSUP_GENERAL_COMMAND & 0xFF))) {
				this.profileWide = false;
			}
		} else {
			this.cmdId = 0xFF;
		}
		this.setParameters(afInc.getSequenceNumber(), afInc.getNwkAddr(), afInc.getClusterID(), profileWide);
	}
	
	private void setParameters(AfDataRequest afReq) {
		this.cmdId = afReq.getZclCmd();
		this.setParameters(afReq.getSequenceNumber(), afReq.getNwkAddr(), afReq.getClusterID(), !afReq.isClusterSpecific());
		if (afReq.getZclCmd() == (ZClusterLibrary.ZCL_CMD_WRITE & 0xFF) &&
				!afReq.isClusterSpecific()) {
			int lsb = afReq.getData()[afReq.getZclPayloadOffset() + AfDataRequest.ZCL_OFFSET];
			int msb = afReq.getData()[afReq.getZclPayloadOffset() + AfDataRequest.ZCL_OFFSET + 1];
			DoubleByte attId = new DoubleByte(msb, lsb);
			this.setOptAttId(attId);
		}
	}
	
	private void setParameters(int seqNumber, DoubleByte addr, DoubleByte clId, boolean profileWide) {
		this.sequenceId = seqNumber;
		super.setDstAddr(addr);
		this.clusterId = clId;
		this.profileWide = profileWide;
	}
	
	public DoubleByte getOptAttId() {
		return optAttId;
	}

	public void setOptAttId(DoubleByte optAttId) {
		this.optAttId = optAttId;
	}
	
	public int getCmdId() {
		return this.cmdId;
	}
	
	public int getSequenceId() {
		return sequenceId;
	}
	
	public boolean isProfileWide() {
		return profileWide;
	}

	public DoubleByte getClusterId() {
		return clusterId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((clusterId == null) ? 0 : clusterId.hashCode());
		result = prime * result + cmdId;
		result = prime * result + (profileWide ? 1231 : 1237);
		result = prime * result + sequenceId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AfRequest other = (AfRequest) obj;
		if (clusterId == null) {
			if (other.clusterId != null)
				return false;
		} else if (!clusterId.equals(other.clusterId)) {
			return false;
		}
		if (cmdId != other.cmdId)
			return false;
		if (profileWide != other.profileWide)
			return false;
		if (sequenceId != other.sequenceId) {
			return false;
		}
		return true;
	}
}