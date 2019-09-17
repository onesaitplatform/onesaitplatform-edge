
package com.onesait.edge.engine.modbus.influxdb.json;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "series"
})
public class DbResult {

    @JsonProperty("series")
    private List<DbSeries> series = null;

    @JsonProperty("series")
    public List<DbSeries> getSeries() {
        return series;
    }

    @JsonProperty("series")
    public void setSeries(List<DbSeries> series) {
        this.series = series;
    }

}
