
package com.onesait.edge.engine.modbus.json;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "label", "type", "data", "backgroundColor", "borderColor", "borderWidth" })
public class Dataset {
	
	//https://www.chartjs.org/docs/latest/configuration/elements.html

	@JsonProperty("label")
	private String label;
	@JsonProperty("type")
	private String type;
	@JsonProperty("data")
	private List<String> data = new ArrayList<>();
	@JsonProperty("backgroundColor")
	private String backgroundColor;
	@JsonProperty("pointRadius")
	private Integer pointRadius;
	@JsonProperty("pointStyle")
	private String pointStyle;
	@JsonProperty("borderColor")
	private String borderColor;
	@JsonProperty("borderWidth")
	private Integer borderWidth;
	
	public Dataset() {
		this.type = "line";
		this.backgroundColor = "rgb(135,190,230,.5)";
		this.borderColor = "#2E6C99";
		this.borderWidth = 1;
		this.pointRadius = 1;
		this.pointStyle = "circle";
	}

	@JsonProperty("label")
	public String getLabel() {
		return label;
	}

	@JsonProperty("label")
	public void setLabel(String label) {
		this.label = label;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("data")
	public List<String> getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(List<String> data) {
		this.data = data;
	}

	@JsonProperty("backgroundColor")
	public String getBackgroundColor() {
		return backgroundColor;
	}

	@JsonProperty("backgroundColor")
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	@JsonProperty("pointRadius")
	public Integer getPointRadius() {
		return pointRadius;
	}

	@JsonProperty("pointRadius")
	public void setPointRadius(Integer pointRadius) {
		this.pointRadius = pointRadius;
	}
	
	@JsonProperty("pointStyle")
	public String getPointStyle() {
		return pointStyle;
	}

	@JsonProperty("pointStyle")
	public void setPointStyle(String pointStyle) {
		this.pointStyle = pointStyle;
	}

	@JsonProperty("borderColor")
	public String getBorderColor() {
		return borderColor;
	}

	@JsonProperty("borderColor")
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	@JsonProperty("borderWidth")
	public Integer getBorderWidth() {
		return borderWidth;
	}

	@JsonProperty("borderWidth")
	public void setBorderWidth(Integer borderWidth) {
		this.borderWidth = borderWidth;
	}

}
