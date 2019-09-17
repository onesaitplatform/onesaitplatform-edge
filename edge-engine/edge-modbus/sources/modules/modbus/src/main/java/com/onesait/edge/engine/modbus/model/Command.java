package com.onesait.edge.engine.modbus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "value",
    "description"
})
public class Command {
	@JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private Number value;
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("value")
    public Number getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Number value) {
        this.value = value;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
}
