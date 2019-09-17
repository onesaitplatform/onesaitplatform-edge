package com.onesait.edge.engine.zigbee.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"id",
	"profile",
	"deviceid",
	"clusters"
})
public class Endpoint {
	
	@JsonProperty("id")
	private Long id;
	@JsonProperty("profile")
	private Long profile;
	@JsonProperty("deviceid")
	private Long deviceid;
	@JsonProperty("clusters")
	private List<Cluster> clusters = new ArrayList<>();
	
	@JsonProperty("id")
	public Long getId() {
		return id;
	}
	
	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonProperty("profile")
	public Long getProfile() {
		return profile;
	}
	
	@JsonProperty("profile")
	public void setProfile(Long profile) {
		this.profile = profile;
	}
	
	@JsonProperty("deviceid")
	public Long getDeviceid() {
		return deviceid;
	}
	
	@JsonProperty("deviceid")
	public void setDeviceid(Long deviceid) {
		this.deviceid = deviceid;
	}
	
	@JsonProperty("clusters")
	public List<Cluster> getClusters() {
		return clusters;
	}
	
	@JsonProperty("clusters")
	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	
	public Cluster getCluster(Long clusterId, boolean input) {
		for (Cluster cluster : clusters) {
			if (cluster.getId().equals(clusterId) && cluster.isInput() == input) {
				return cluster;
			}
		}
		return null;
	}
	
	public Cluster getCluster(Long clusterId) {
		return getCluster(clusterId, true);
	}
	
	public void addCluster(Cluster cl) {
		this.clusters.add(cl);
	}
}