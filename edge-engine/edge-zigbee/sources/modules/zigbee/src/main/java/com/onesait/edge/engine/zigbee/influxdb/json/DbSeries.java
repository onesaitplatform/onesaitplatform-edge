
package com.onesait.edge.engine.zigbee.influxdb.json;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "columns",
    "values"
})
public class DbSeries {

    @JsonProperty("name")
    private String name;
    @JsonProperty("columns")
    private List<String> columns = null;
    @JsonProperty("values")
    private List<List<String>> values = null;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("columns")
    public List<String> getColumns() {
        return columns;
    }

    @JsonProperty("columns")
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    @JsonProperty("values")
    public List<List<String>> getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(List<List<String>> values) {
        this.values = values;
    }

}
