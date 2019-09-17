
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

/**
 * @author asmlopez
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "businessId",
    "signalType",
    "unit",
    "convFactor",
    "description",
    "registerType",
    "register",
    "dataType",
    "bigEndian",
    "isCommandable",
    "commands"
})
public class Signal {

    @JsonProperty("id")
    private String id;
    @JsonProperty("businessId")
    private String businessId;
    @JsonProperty("signalType")
    private String signalType;
    @JsonProperty("unit")
    private String unit;
    @JsonProperty("convFactor")
    private Float convFactor;
    @JsonProperty("description")
    private String description;
    @JsonProperty("registerType")
    private String registerType;
    @JsonProperty("register")
    private Integer register;
    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("bigEndian")
    private Boolean bigEndian;
    @JsonProperty("isCommandable")
    private Boolean isCommandable = Boolean.FALSE;
    @JsonProperty("commands")
    private List<Command> commands = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
    @JsonIgnore
    private Number value; 
    @JsonIgnore
    private Number convertedValue;
    @JsonIgnore
    private Long timestampValueInNanos;
    @JsonIgnore
    private Boolean onError = Boolean.FALSE;
    @JsonIgnore
    private String descError;

    @JsonIgnore
    public Long getTimestampValueInNanos() {
		return timestampValueInNanos;
	}

    @JsonIgnore
	public void setTimestampValueInNanos(Long timestampValue) {
		this.timestampValueInNanos = timestampValue;
	}

	@JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("businessId")
    public String getBusinessId() {
        return businessId;
    }

    @JsonProperty("businessId")
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @JsonProperty("signalType")
    public String getSignalType() {
        return signalType;
    }

    @JsonProperty("signalType")
    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    @JsonProperty("unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("convFactor")
    public Float getConvFactor() {
        return convFactor;
    }

    @JsonProperty("convFactor")
    public void setConvFactor(Float convFactor) {
        this.convFactor = convFactor;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("registerType")
    public String getRegisterType() {
        return registerType;
    }

    @JsonProperty("registerType")
    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    @JsonProperty("register")
    public Integer getRegister() {
        return register;
    }

    @JsonProperty("register")
    public void setRegister(Integer register) {
        this.register = register;
    }

    @JsonProperty("dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty("dataType")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JsonProperty("bigEndian")
    public Boolean getBigEndian() {
        return bigEndian;
    }

    @JsonProperty("bigEndian")
    public void setBigEndian(Boolean bigEndian) {
        this.bigEndian = bigEndian;
    }
    
    @JsonProperty("isCommandable")
    public Boolean getIsCommandable() {
        return isCommandable;
    }

    @JsonProperty("isCommandable")
    public void setIsCommandable(Boolean isCommandable) {
        this.isCommandable = isCommandable;
    }

    @JsonProperty("commands")
    public List<Command> getCommands() {
		return commands;
	}

    @JsonProperty("commands")
	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}
    
	@JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    @JsonIgnore
    public Boolean getOnError() {
		return onError;
	}

    @JsonIgnore
	public void setOnError(Boolean onError) {
		this.onError = onError;
	}

    @JsonIgnore
	public String getDescError() {
		return descError;
	}

    @JsonIgnore
	public void setDescError(String descError) {
		this.descError = descError;
	}

    @JsonIgnore
	public Number getValue() {
		return value;
	}

    @JsonIgnore
	public void setValue(Number value) {
		this.value = value;
	}
	
    @JsonIgnore
	public Number getConvertedValue() {
		return convertedValue;
	}

    @JsonIgnore
	public void setConvertedValue(Number convertedValue) {
		this.convertedValue = convertedValue;
	}
}
