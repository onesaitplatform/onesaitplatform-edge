package com.onesait.edge.engine.zigbee.util;

import com.onesait.edge.engine.zigbee.frame.ZdoZFrame;

public class ZdoRequest extends Request {

	private DoubleByte cmdId;
	private String requestId;
	private boolean temporarilyFailed = false;

	public ZdoRequest(DoubleByte dstAddr, DoubleByte cmdId, String requestId) {
		super();
		super.setDstAddr(dstAddr);
		this.cmdId = cmdId;
		this.requestId = requestId;
	}
	
	public ZdoRequest(ZdoZFrame zf, String requestId) {
		super.setDstAddr(zf.getZdoNwkAddr());
		if ((zf.getZdoCmdId().intValue() & 0x8000) > 0) {
			this.setCmdId(new DoubleByte(zf.getZdoCmdId().intValue() & ~(0x8000)));
		} else {
			this.setCmdId(zf.getZdoCmdId());
		}
		this.requestId = requestId;
	}
	
	public DoubleByte getCmdId() {
		return cmdId;
	}
	
	public void setCmdId(DoubleByte cmdId) {
		this.cmdId = cmdId;
	}
	
	public String getRequestId() {
		return requestId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cmdId == null) ? 0 : cmdId.hashCode());
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
		ZdoRequest other = (ZdoRequest) obj;
		if (cmdId == null) {
			if (other.cmdId != null)
				return false;
		} else if (!cmdId.equals(other.cmdId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ZdoRequest [cmdId=" + cmdId + ", requestId=" + requestId + ", getRequestTimeMs()=" + getRequestTimeMs()
				+ ", getDstAddr()=" + getDstAddr() + ", toString()=" + super.toString() + ", getClass()=" + getClass()
				+ "]";
	}

	public boolean isTemporarilyFailed() {
		return temporarilyFailed;
	}

	public void setTemporarilyFailed() {
		this.temporarilyFailed = true;
	}
}
