
package com.onesait.edge.engine.modbus.json;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "labels",
    "datasets"
})
public class DataGraph {
	
    @JsonProperty("labels")
    private List<String> labels = new ArrayList<>();
    @JsonProperty("datasets")
    private List<Dataset> datasets = new ArrayList<>();

    @JsonProperty("labels")
    public List<String> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @JsonProperty("datasets")
    public List<Dataset> getDatasets() {
        return datasets;
    }

    @JsonProperty("datasets")
    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }

}
