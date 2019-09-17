package com.onesait.edge.engine.zigbee.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class ZigbeeClusterLibrary {

	// TODO: Cambiar de paquete las clases tal que solo ZclXmlScannes pueda añadir nuevos objetos
	
    private HashMap<DoubleByte, ZclCluster> clusters = new HashMap<>();
	private ConcurrentHashMap<Byte, ZclDatatype> datatypes = new ConcurrentHashMap<>();
    private HashMap<DoubleByte, ZclCluster> serverClusters = new HashMap<>();
    private HashMap<DoubleByte, ZclDevicetype> deviceIds = new HashMap<>(); 
    private HashMap<DoubleByte, String> profiles = new HashMap<>();
    
    public void putZclCluster (ZclCluster zclCluster) {
    	this.clusters.put(zclCluster.getId(), zclCluster);
    }
    
    public void putZclDatatype (ZclDatatype zclDatatype) {
    	this.datatypes.put(zclDatatype.getId(), zclDatatype);
    }
    
    public void putServerCluster (ZclCluster zclCluster) {
    	this.serverClusters.put(zclCluster.getId(), zclCluster);
    }
    
    public void putZclDevicetype (ZclDevicetype zclDevicetype) {
    	this.deviceIds.put(zclDevicetype.getId(), zclDevicetype);
    }
    
    public void putProfile (DoubleByte id, String name) {
    	this.profiles.put(id, name);
    }
    
    public HashMap<DoubleByte, ZclCluster> getClusters() {
		return clusters;
	}

	public ConcurrentHashMap<Byte, ZclDatatype> getDatatypes() {
		return datatypes;
	}

	public HashMap<DoubleByte, ZclCluster> getServerClusters() {
		return serverClusters;
	}

	public HashMap<DoubleByte, ZclDevicetype> getDeviceIds() {
		return deviceIds;
	}

	public HashMap<DoubleByte, String> getProfiles() {
		return profiles;
	}
	
	public ZclDatatype getZclDataypeByName(String datatypeName) {
		for (ZclDatatype zcldatatype : this.datatypes.values()) {
			if (zcldatatype.getName().equalsIgnoreCase(datatypeName)) {
				return (ZclDatatype) zcldatatype.clone();
			}
		}
		return null;
	}
}
