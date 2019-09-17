package com.onesait.edge.engine.zigbee.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class ZclEndpoint implements Cloneable {

	private ConcurrentHashMap<DoubleByte, ZclCluster> clusters = new ConcurrentHashMap<>();
	private ArrayList<DoubleByte> unknownClusters = new ArrayList<>();
	private final byte id;
	private DoubleByte profile = null;
	private DoubleByte deviceId = null;

	private transient Timestamp lastTimeDiscovered = new Timestamp(new Date().getTime());
	private static final int NO_CLUSTERS_MAX_TIME_MS = 60000;
	private transient int discoverRetries = 0;

	public ZclEndpoint(byte id) {
		this.clusters = new ConcurrentHashMap<>();
		this.id = id;
	}

	public DoubleByte getProfile() {
		return profile;
	}

	public ZclCluster getCluster(DoubleByte id) {
		return this.clusters.get(id);
	}

	public void setProfile(DoubleByte profile) {
		if (profile != null) {
			this.profile = new DoubleByte(profile.intValue());
		}
	}

	public byte getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ZclEndpoint [clusters=" + clusters + ", id=" + id + ", profile=" + profile + ", deviceId=" + deviceId
				+ "]";
	}

	public void putCluster(ZclCluster cluster) {
		this.clusters.put(cluster.getId(), cluster);
		updateLastTimeDiscoveredAndResetRetries();
	}

	public ConcurrentHashMap<DoubleByte, ZclCluster> getClusters() {
		return clusters;
	}

	public DoubleByte getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(DoubleByte deviceId) {
		this.deviceId = deviceId;
	}

	public boolean toDiscover() {
		long rightNow = new Date().getTime();
		return this.lastTimeDiscovered == null
				|| (rightNow - this.lastTimeDiscovered.getTime()) > NO_CLUSTERS_MAX_TIME_MS
						* Math.pow(2, this.discoverRetries);
	}

	public void incDiscoverRetries() {
		this.discoverRetries++;
	}
	
	private void updateLastTimeDiscoveredAndResetRetries() {
		this.lastTimeDiscovered = new Timestamp(new Date().getTime());
		this.discoverRetries = 0;
	}

	public void putUnknownCluster(DoubleByte clusterId) {
		this.unknownClusters.add(clusterId);
	}

	public void removeUnknownCluster(DoubleByte clusterId) {
		this.unknownClusters.remove(clusterId);
	}

	public ArrayList<DoubleByte> getUnknownClusters() {
		return unknownClusters;
	}

	public Object clone() {
		ZclEndpoint newEp = new ZclEndpoint(this.id);
		if (this.getDeviceId() != null) {
			newEp.setDeviceId(new DoubleByte(this.getDeviceId().intValue()));
		}
		if (this.getProfile() != null) {
			newEp.setProfile(new DoubleByte(this.getProfile().intValue()));
		}
		newEp.clusters = new ConcurrentHashMap<>();
		for (ZclCluster zcl : this.clusters.values()) {
			ZclCluster newCl = (ZclCluster) zcl.clone();
			newEp.clusters.put(newCl.getId(), newCl);
		}
		return newEp;
	}
}
