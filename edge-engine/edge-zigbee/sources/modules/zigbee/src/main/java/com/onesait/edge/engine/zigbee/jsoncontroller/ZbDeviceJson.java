package com.onesait.edge.engine.zigbee.jsoncontroller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"mac",
	"shortaddress",
	"lq",
	"manufacturer",
	"mancode",
	"devType",
	"idEndpoint",
	"idDevice",
	"idCluster",
	"clusterName",
	"mainValues",
	"clusters",
	"attributesInfo",
	"nAttOK",
	"nAttKO",
	"status",
	"thermostatAttributes",
	"percentage"
})
public class ZbDeviceJson {

	@JsonProperty("mac")
	private String mac;
	@JsonProperty("shortaddress")
	private String shortaddress;
	@JsonProperty("lq")
	private int lq;
	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("mancode")
	private String mancode;
	@JsonProperty ("devType")
	private String devType;
	@JsonProperty("idEndpoint")
	private Byte idEndpoint=null;
	@JsonProperty("idDevice")
	private String idDevice;
	@JsonProperty("idCluster")
	private String idCluster;
	@JsonProperty("clusterName")
	private String clusterName;
	@JsonProperty("clusters")
	private List<String> clusters=null;
	@JsonProperty("mainValues")
	private AttributesJson mainValues;
	@JsonProperty("attributesInfo")
	private List<ClustersInfoJson> attributesInfo;
	@JsonProperty("nAttKO")
	private Integer nAttKO;
	@JsonProperty("nAttOK")
	private Integer nAttOK;
	@JsonProperty("status")
	private String status;
	@JsonProperty("thermostatAttributes")
	private AttributesJson thermostatAttributes;	
	@JsonProperty("percentage")
	private Long percentage=null;
	
	@JsonProperty("idCluster")
	public Byte getIdEndpoint() {
		return idEndpoint;
	}
	@JsonProperty("idCluster")
	public void setIdEndpoint(Byte idEndpoint) {
		this.idEndpoint = idEndpoint;
	}
	@JsonProperty("mainValues")
	public AttributesJson getMainValues() {
		return mainValues;
	}
	@JsonProperty("mainValues")
	public void setMainValues(AttributesJson mainValues) {
		this.mainValues = mainValues;
	}

	@JsonProperty("devType")
	public String getDevType() {
		return devType;
	}
	
	@JsonProperty("devType")
	public void setDevType(String devType) {
		this.devType = devType;
	}

	@JsonProperty("mac")
	public String getMac() {
		return mac;
	}
	
	@JsonProperty("mac")
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	@JsonProperty("lq")
	public int getLq() {
		return lq;
	}
	
	@JsonProperty("lq")
	public void setLq(int lq) {
		this.lq = lq;
	}
	@JsonProperty("shortaddress")
	public String getShortaddress() {
		return shortaddress;
	}
	
	@JsonProperty("shortaddress")
	public void setShortaddress(String shortaddress) {
		this.shortaddress = shortaddress;
	}
	@JsonProperty("manufacturer")
	public String getManufacturer() {
		return manufacturer;
	}
	
	@JsonProperty("manufacturer")
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	@JsonProperty("mancode")
	public String getMancode() {
		return mancode;
	}
	
	@JsonProperty("mancode")
	public void setMancode(String mancode) {
		this.mancode = mancode;
	}
	@JsonProperty("idDevice")
	public String getIdDevice() {
	return idDevice;
	}

	@JsonProperty("idDevice")
	public void setIdDevice(String idDevice) {
	this.idDevice = idDevice;
	}

	@JsonProperty("idCluster")
	public String getIdCluster() {
	return idCluster;
	}

	@JsonProperty("idCluster")
	public void setIdCluster(String idCluster) {
	this.idCluster = idCluster;
	}

	@JsonProperty("clusterName")
	public String getClusterName() {
	return clusterName;
	}

	@JsonProperty("clusterName")
	public void setClusterName(String clusterName) {
	this.clusterName = clusterName;
	}
	@JsonProperty("clusters")
	public List<String> getClusters() {
	return this.clusters;
	}

	@JsonProperty("clusters")
	public void setClusters(List<String> clusters) {
	this.clusters = clusters;
	}
	@JsonProperty("attributesInfo")
	public List<ClustersInfoJson> getAttributesInfo() {
	return this.attributesInfo;
	}

	@JsonProperty("attributesInfo")
	public void setAttributesInfo(List<ClustersInfoJson> attributesInfo) {
	this.attributesInfo = attributesInfo;
	}
	@JsonProperty("nAttKO")
	public Integer getnAttKO() {
		return nAttKO;
	}
	@JsonProperty("nAttKO")
	public void setnAttKO(Integer nAttKO) {
		this.nAttKO = nAttKO;
	}
	@JsonProperty("nAttOK")
	public Integer getnAttOK() {
		return nAttOK;
	}
	@JsonProperty("nAttOK")
	public void setnAttOK(Integer nAttOK) {
		this.nAttOK = nAttOK;
	}
	@JsonProperty("status")
	public String getStatus() {
		return status;
	}
	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}
	@JsonProperty("percentage")
	public Long getPercentage() {
		return percentage;
	}
	@JsonProperty("percentage")
	public void setPercentage(long percentage) {
		this.percentage = percentage;
	}
	@JsonProperty("thermostatAttributes")
	public AttributesJson getThermostatAttributes() {
		return thermostatAttributes;
	}
	@JsonProperty("thermostatAttributes")
	public void setThermostatAttributes(AttributesJson thermostatAttributes) {
		this.thermostatAttributes = thermostatAttributes;
	}
	
}