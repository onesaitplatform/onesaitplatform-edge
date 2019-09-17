package com.onesait.edge.engine.zigbee.mesh;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;

public class ZMeshLink {
		
	private DoubleByte localAdd;
	private DoubleByte remoteAdd;
	private int lqi;
	private int depth;
	private OctaByte ieeeAdd;
	private OctaByte extPanId;
	private Byte permitJoin;
	private Byte devInfo;
	
	public ZMeshLink (DoubleByte localAdd, DoubleByte remoteAdd, int lqi, int depth, OctaByte ieeeAdd, OctaByte extPanId, Byte permitJoin, Byte devInfo) {
		this.depth=depth;
		this.extPanId=extPanId;
		this.ieeeAdd=ieeeAdd;
		this.permitJoin=permitJoin;
		this.devInfo=devInfo;
		this.remoteAdd=remoteAdd;
		this.localAdd=localAdd;
		this.lqi=lqi;
	}

	public DoubleByte getLocalAdd() {
		return localAdd;
	}

	public DoubleByte getRemoteAdd() {
		return remoteAdd;
	}

	public int getLqi() {
		return lqi;
	}

	public int getDepth() {
		return depth;
	}

	public OctaByte getIeeeAdd() {
		return ieeeAdd;
	}

	public OctaByte getExtPanId() {
		return extPanId;
	}

	public Byte getPermitJoin() {
		return permitJoin;
	}

	public Byte getDevInfo() {
		return devInfo;
	}

	@Override
	public String toString() {
		return "ZMeshLink [localAdd=" + localAdd + ", remoteAdd=" + remoteAdd + ", lqi=" + lqi + ", depth=" + depth + ", ieeeAdd=" + ieeeAdd + ", extPanId=" + extPanId + ", permitJoin=" + permitJoin
				+ ", devInfo=" + devInfo + "]";
	}
	
	public String toHtmlString() {
		return "ZMeshLink [<br/>localAdd=" + localAdd + ",<br/> remoteAdd=" + remoteAdd + ",<br/> lqi=" + lqi + ",<br/> depth=" + depth +
				",<br/> ieeeAdd=" + ieeeAdd + ",<br/> extPanId=" + extPanId + ",<br/> permitJoin=" + permitJoin + ",<br/> devInfo=" + devInfo + "]<br/>";
	}
}