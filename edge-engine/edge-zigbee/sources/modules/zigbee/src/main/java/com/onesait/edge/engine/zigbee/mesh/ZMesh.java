package com.onesait.edge.engine.zigbee.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class ZMesh {

	
	private HashMap<DoubleByte, ZMeshNode> nodes = new HashMap<>();
		

	public void addZMeshNode(ZMeshNode node){
		this.nodes.put(node.getShortAddress(),node);
	}

	
	public HashMap<DoubleByte, ZMeshNode> getNodes() {
		return nodes;
	}
	

	public List<String[]> getStringNodeLinks(){
		List<String[]> nodeLinks = new ArrayList<>();
		for(ZMeshNode node: nodes.values()){					
			for(ZMeshLink link:node.getNodeLinks()){
				String[] str= new String[]{
						link.getLocalAdd().toStr(),
						link.getRemoteAdd().toStr(),
						link.getIeeeAdd().toString(),
						link.getExtPanId().toString(),
						link.getLqi()+"",
						link.getDepth()+"",
						link.getPermitJoin().toString(),
						link.getDevInfo().toString()
						};	
				nodeLinks.add(str);
			}			
		}
		return nodeLinks;
	}
	
	public void addLink(ZMeshLink link){
		ZMeshNode node =this.nodes.get(link.getLocalAdd());
		if(node==null){
			ZMeshNode newNode = new ZMeshNode(link.getLocalAdd());
			node = newNode;
		}
		node.addLink(link);
		this.nodes.put(node.getShortAddress(), node);
	}
	
}
