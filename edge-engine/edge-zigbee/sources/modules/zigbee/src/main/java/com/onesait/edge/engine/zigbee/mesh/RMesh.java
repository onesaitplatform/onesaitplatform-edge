package com.onesait.edge.engine.zigbee.mesh;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class RMesh {

	
	private HashMap<DoubleByte, RMeshNode> nodes = new HashMap<>();
		

	public void addRMeshNode(RMeshNode node){
		this.nodes.put(node.getShortAddress(),node);
	}
	
	public HashMap<DoubleByte, RMeshNode> getNodes() {
		return nodes;
	}
	
	public List<String[]> getStringNodeLinks(){
		List<String[]> nodeLinks = new ArrayList<>();
		for(RMeshNode node: nodes.values()){					
			for(RMeshLink link:node.getNodeLinks()){
				String[] str= new String[]{
						link.getLocalAdd().toStr(),
						link.getRemoteAdd().toStr(),
						link.getNextHop().toStr(),
						link.getStatus().toString()
						};	
				nodeLinks.add(str);
			}			
		}
		return nodeLinks;
	}
	
	public void addLink(RMeshLink link){
		RMeshNode node =this.nodes.get(link.getLocalAdd());
		if(node==null){
			RMeshNode newNode = new RMeshNode(link.getLocalAdd());
			node = newNode;
		}
		node.addLink(link);
		this.nodes.put(node.getShortAddress(), node);
	}
	
}
