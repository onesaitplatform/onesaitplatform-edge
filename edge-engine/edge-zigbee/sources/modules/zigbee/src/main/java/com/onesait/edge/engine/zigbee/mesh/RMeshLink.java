package com.onesait.edge.engine.zigbee.mesh;


import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class RMeshLink {
		
	private DoubleByte localAdd;
	private DoubleByte remoteAdd;
	private DoubleByte nextHop;
	private Byte status;
	
	public RMeshLink (DoubleByte localAdd, DoubleByte remoteAdd, DoubleByte nextHop, Byte status) {
		this.nextHop=nextHop;
		this.remoteAdd=remoteAdd;
		this.localAdd=localAdd;
		this.status=status;
	}
	
	public DoubleByte getLocalAdd() {
		return localAdd;
	}

	public void setLocalAdd(DoubleByte localAdd) {
		this.localAdd = localAdd;
	}

	public DoubleByte getRemoteAdd() {
		return remoteAdd;
	}

	public void setRemoteAdd(DoubleByte remoteAdd) {
		this.remoteAdd = remoteAdd;
	}

	public DoubleByte getNextHop() {
		return nextHop;
	}

	public void setNextHop(DoubleByte nextHop) {
		this.nextHop = nextHop;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "RMeshLink [localAdd=" + localAdd + ", remoteAdd=" + remoteAdd + ", nextHop=" + nextHop + ", status="
				+ status + "]";
	}

	public String toHtmlString() {
		return "RMeshLink [<br/>localAdd=" + localAdd + ",<br/> remoteAdd=" + remoteAdd + ",<br/> nextHop=" + nextHop + ",<br/> status=" + status + "]<br/>";
	}
}