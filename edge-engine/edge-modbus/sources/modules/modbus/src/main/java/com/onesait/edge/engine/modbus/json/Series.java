
package com.onesait.edge.engine.modbus.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "meta",
    "value"
})
public class Series {

    @JsonProperty("meta")
    private String meta;
    @JsonProperty("value")
    private String value;
    

    public Series(String meta, String value) {
		super();
		this.meta = meta;
		this.value = value;
	}

	@JsonProperty("meta")
    public String getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(String meta) {
        this.meta = meta;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

	@Override
	public String toString() {
		return "Series [meta=" + meta + ", value=" + value + "]";
	}
    
}
