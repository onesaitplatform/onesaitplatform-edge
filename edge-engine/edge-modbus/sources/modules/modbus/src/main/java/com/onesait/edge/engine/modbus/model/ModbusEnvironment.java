
package com.onesait.edge.engine.modbus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "process",
    "update",
    "coreThreadPoolSize",
    "bulkFormat",
    "devices"
})
public class ModbusEnvironment {

    @JsonProperty("process")
    private String process;
    @JsonProperty("update")
    private String update;
    @JsonProperty("coreThreadPoolSize")
    private Integer coreThreadPoolSize;
    @JsonProperty("bulkFormat")
    private Boolean bulkFormat;
    @JsonProperty("devices")
    private List<Device> devices = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("process")
    public String getProcess() {
        return process;
    }

    @JsonProperty("process")
    public void setProcess(String process) {
        this.process = process;
    }

    @JsonProperty("update")
    public String getUpdate() {
        return update;
    }

    @JsonProperty("update")
    public void setUpdate(String update) {
        this.update = update;
    }

    @JsonProperty("coreThreadPoolSize")
    public Integer getCoreThreadPoolSize() {
        return coreThreadPoolSize;
    }

    @JsonProperty("coreThreadPoolSize")
    public void setCoreThreadPoolSize(Integer coreThreadPoolSize) {
        this.coreThreadPoolSize = coreThreadPoolSize;
    }
    
    @JsonProperty("bulkFormat")
    public Boolean getBulkFormat() {
        return bulkFormat;
    }

    @JsonProperty("bulkFormat")
    public void setBulkFormat(Boolean bulkFormat) {
        this.bulkFormat = bulkFormat;
    }
    
    @JsonProperty("devices")
    public List<Device> getDevices() {
        return devices;
    }

    @JsonProperty("devices")
    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
