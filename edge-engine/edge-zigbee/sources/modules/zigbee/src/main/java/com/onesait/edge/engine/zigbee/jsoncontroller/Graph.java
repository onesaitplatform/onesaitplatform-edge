
package com.onesait.edge.engine.zigbee.jsoncontroller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "labels",
    "series"
})
public class Graph {

    @JsonProperty("labels")
    private List<String> labels = new ArrayList<>();
    @JsonProperty("series")
    private List<List<Series>> series = new ArrayList<>();

    @JsonProperty("labels")
    public List<String> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @JsonProperty("series")
    public List<List<Series>> getSeries() {
        return series;
    }

    @JsonProperty("series")
    public void setSeries(List<List<Series>> series) {
        this.series = series;
    }

	@Override
	public String toString() {
		return "Graph [labels=" + labels + ", series=" + series + "]";
	}
    
    
}
