package com.onesait.edge.engine.zigbee.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;

public class ZMeshNode {

	private DoubleByte shortAddress;
	private HashMap<OctaByte, ZMeshLink> links; // Los enlaces que tengo
	
	public ZMeshNode(DoubleByte shortAddress) {
		this.shortAddress = shortAddress;
		this.links = new HashMap<>();
	}
	
	public void addLink(ZMeshLink zml) {
		links.put(zml.getIeeeAdd(), zml);
	}
	

	public void removeLink(OctaByte nwkDst) {
		links.remove(nwkDst);
	}
	
	public int getNumberOfLocalLinks() {
		return links.size();
	}
	
	public List<ZMeshLink> getNodeLinks() {
		return new ArrayList<>(this.links.values());
	}

	public DoubleByte getShortAddress() {
		return shortAddress;
	}
		
}
