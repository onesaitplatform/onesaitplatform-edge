package com.onesait.edge.engine.zigbee.model;

import java.util.Date;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public abstract class Request {

	private long requestTimeMs;
	private DoubleByte dstAddr;
	
	protected Request() {
		this.requestTimeMs = new Date().getTime();
	}

	public long getRequestTimeMs() {
		return requestTimeMs;
	}
	
	public DoubleByte getDstAddr() {
		return dstAddr;
	}
	
	public void setDstAddr(DoubleByte dstAddr) {
		this.dstAddr = dstAddr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dstAddr == null) ? 0 : dstAddr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (dstAddr == null) {
			if (other.dstAddr != null)
				return false;
		} else if (!dstAddr.equals(other.dstAddr)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Request [requestTimeMs=" + requestTimeMs + ", dstAddr=" + dstAddr + "]";
	}
}
