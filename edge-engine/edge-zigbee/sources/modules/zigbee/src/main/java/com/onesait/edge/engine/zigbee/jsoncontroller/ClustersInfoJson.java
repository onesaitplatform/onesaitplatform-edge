package com.onesait.edge.engine.zigbee.jsoncontroller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"clusterid",
"name",
"attributes",
"attributesOK",
"attributtesKO"
})
public class ClustersInfoJson {

@JsonProperty("clusterid")
private String clusterid;
@JsonProperty("name")
private String name;
@JsonProperty("attributes")
private List<AttributesJson> attributes;
@JsonProperty("attributesOK")
private List<String> attributesOK;
@JsonProperty("attributesKO")
private List<String> attributesKO;


@JsonProperty("clusterid")
public String getClusterid() {
return clusterid;
}

@JsonProperty("clusterid")
public void setClusterid(String clusterid) {
this.clusterid = clusterid;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}



@JsonProperty("attributes")
public List<AttributesJson> getAttributes() {
return attributes;
}

@JsonProperty("attributes")
public void setAttributes(List<AttributesJson> attributes) {
this.attributes = attributes;
}
@JsonProperty("attributesOK")
public List<String> getAttributesOK() {
	return attributesOK;
}
@JsonProperty("attributesOK")
public void setAttributesOK(List<String> attributesOK) {
	this.attributesOK = attributesOK;
}
@JsonProperty("attributesKO")
public List<String> getAttributesKO() {
	return attributesKO;
}
@JsonProperty("attributesKO")
public void setAttributesKO(List<String> attributesKO) {
	this.attributesKO = attributesKO;
}

public Integer getNumberAttributes(){
	return attributes.size();
}

}