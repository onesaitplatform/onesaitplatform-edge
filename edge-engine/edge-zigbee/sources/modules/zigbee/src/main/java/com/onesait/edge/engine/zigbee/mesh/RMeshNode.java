package com.onesait.edge.engine.zigbee.mesh;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class RMeshNode {

	private DoubleByte shortAddress;
	private HashMap<DoubleByte, RMeshLink> links; // Los enlaces que tengo
	
	public RMeshNode(DoubleByte shortAddress) {
		this.shortAddress = shortAddress;
		this.links = new HashMap<>();
	}
	
	public void addLink(RMeshLink rml) {
		links.put(rml.getRemoteAdd(), rml);
	}
	
	public void removeLink(DoubleByte nwkDst) {
		links.remove(nwkDst);
	}
	
	public int getNumberOfLocalLinks() {
		return links.size();
	}
	
	public List<RMeshLink> getNodeLinks() {
		return new ArrayList<>(this.links.values());
	}

	public DoubleByte getShortAddress() {
		return shortAddress;
	}
	
}
