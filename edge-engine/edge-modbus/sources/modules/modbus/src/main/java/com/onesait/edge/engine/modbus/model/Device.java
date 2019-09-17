
package com.onesait.edge.engine.modbus.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.onesait.edge.engine.modbus.util.NumberUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "protocolType", "id", "ip", "port", "serial", "bauds", "address", "manufacturer", "model", "deviceType", "monitoringBlocks", "monitoringTimeMs",
		"monitoringDelayMs", "monitoringTimeoutSec", "info", "signals" })
public class Device {

	@JsonProperty("protocolType")
	private String protocolType;
	@JsonProperty("id")
	private String id;
	@JsonProperty("ip")
	private String ip;
	@JsonProperty("port")
	private Integer port;
	@JsonProperty("serial")
	private String serial;
	@JsonProperty("bauds")
	private Integer bauds;
	@JsonProperty("address")
	private Integer address;
	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("model")
	private String model;
	@JsonProperty("deviceType")
	private String deviceType;
	@JsonProperty("monitoringBlocks")
	private Integer monitoringBlocks;
	@JsonProperty("monitoringTimeMs")
	private Long monitoringTimeMs;
	@JsonProperty("monitoringDelayMs")
	private Long monitoringDelayMs;
	@JsonProperty("monitoringTimeoutSec")
	private Integer monitoringTimeoutSec;
	@JsonProperty("info")
	private String info;
	@JsonProperty("signals")
	private List<Signal> signals = new ArrayList<>();

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();
	@JsonIgnore
	private Boolean onError = Boolean.FALSE;
	@JsonIgnore
	private String descError;

	@JsonIgnore
	private Long monitoringTimes = 0L;

	@JsonIgnore
	private Long accumulatedTime = 0L;

	@JsonIgnore
	private Long instantDurationTask = 0L;

	@JsonIgnore
	public Long getInstantDurationTask() {
		return instantDurationTask;
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
	public Boolean getOnError() {
		return onError;
	}

	@JsonIgnore
	public void setOnError(Boolean onError) {
		this.onError = onError;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("protocolType")
	public String getProtocolType() {
		return protocolType;
	}

	@JsonProperty("protocolType")
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("ip")
	public String getIp() {
		return ip;
	}

	@JsonProperty("ip")
	public void setIp(String ip) {
		this.ip = ip;
	}

	@JsonProperty("port")
	public Integer getPort() {
		return port;
	}

	@JsonProperty("port")
	public void setPort(Integer port) {
		this.port = port;
	}

	@JsonProperty("serial")
	public String getSerial() {
		return serial;
	}

	@JsonProperty("serial")
	public void setSerial(String serial) {
		this.serial = serial;
	}

	@JsonProperty("bauds")
	public Integer getBauds() {
		return bauds;
	}

	@JsonProperty("bauds")
	public void setBauds(Integer bauds) {
		this.bauds = bauds;
	}

	@JsonProperty("address")
	public Integer getAddress() {
		return address;
	}

	@JsonProperty("address")
	public void setAddress(Integer address) {
		this.address = address;
	}

	@JsonProperty("manufacturer")
	public String getManufacturer() {
		return manufacturer;
	}

	@JsonProperty("manufacturer")
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@JsonProperty("model")
	public String getModel() {
		return model;
	}

	@JsonProperty("model")
	public void setModel(String model) {
		this.model = model;
	}

	@JsonProperty("deviceType")
	public String getDeviceType() {
		return deviceType;
	}

	@JsonProperty("deviceType")
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@JsonProperty("monitoringBlocks")
	public Integer getMonitoringBlocks() {
		return monitoringBlocks;
	}

	@JsonProperty("monitoringBlocks")
	public void setMonitoringBlocks(Integer monitoringBlocks) {
		this.monitoringBlocks = monitoringBlocks;
	}

	@JsonProperty("monitoringTimeMs")
	public Long getMonitoringTimeMs() {
		return monitoringTimeMs;
	}

	@JsonProperty("monitoringTimeMs")
	public void setMonitoringTimeMs(Long monitoringTimeMs) {
		this.monitoringTimeMs = monitoringTimeMs;
	}

	@JsonProperty("monitoringDelayMs")
	public Long getMonitoringDelayMs() {
		return monitoringDelayMs;
	}

	@JsonProperty("monitoringDelayMs")
	public void setMonitoringDelayMs(Long monitoringDelayMs) {
		this.monitoringDelayMs = monitoringDelayMs;
	}

	@JsonProperty("monitoringTimeoutSec")
	public Integer getMonitoringTimeoutSec() {
		return monitoringTimeoutSec;
	}

	@JsonProperty("monitoringTimeoutSec")
	public void setMonitoringTimeoutSec(Integer monitoringTimeoutSec) {
		this.monitoringTimeoutSec = monitoringTimeoutSec;
	}

	@JsonProperty("info")
	public String getInfo() {
		return info;
	}

	@JsonProperty("info")
	public void setInfo(String info) {
		this.info = info;
	}

	@JsonProperty("signals")
	public List<Signal> getSignals() {
		return signals;
	}

	@JsonProperty("signals")
	public void setSignals(List<Signal> signals) {
		this.signals = signals;
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
	public Integer getSignalsCounter() {
		return signals.size();
	}

	@JsonIgnore
	public Long getMonitoringTimes() {
		return monitoringTimes;
	}

	@JsonIgnore
	public Long getAccumulatedTime() {
		return accumulatedTime;
	}

	@JsonIgnore
	public void addMonitoringTaskTime(Long time2add) {
		this.instantDurationTask = time2add;
		this.monitoringTimes++;
		this.accumulatedTime += time2add;
	}

	@JsonIgnore
	public Long getMonitoringtimeAverage() {
		if (this.monitoringTimes <= 0)
			this.monitoringTimes = 1L;
		return (this.accumulatedTime / this.monitoringTimes);
	}

	/**
	 * Calculate the min Registry count by registerType. It will by the begin for
	 * block for monitoring
	 * 
	 * @param type
	 *            - RegisterType
	 * @return Integer
	 */
	@JsonIgnore
	public Integer getMinModbusDeviceRegistryByType(RegisterType type) {

		Integer min = null;

		for (Signal signal : signals) {

			Integer register = signal.getRegister();

			if (signal.getRegisterType().equals(type.toString()))
				min = (min == null || register < min) ? register : min;
		}

		return min;
	}

	/**
	 * Calculate the max Registry count by registerType. It will by the end for
	 * block for monitoring. We will need the offset data type register.
	 * 
	 * @param type
	 *            - RegisterType
	 * @return
	 */
	@JsonIgnore
	public Integer getMaxModbusDeviceRegistryByType(RegisterType type) {

		Integer max = null;

		for (Signal signal : signals) {

			Integer register = signal.getRegister();
			DataType dataType = DataType.fromValue(signal.getDataType());

			if (signal.getRegisterType().equals(type.toString()))
				max = (max == null || register + DataType.offset(dataType) > max) ? register + DataType.offset(dataType) : max;
		}

		return max;
	}

	/**
	 * check if device have signals from the Register Type specificated
	 * 
	 * @param type
	 *            RegisterType
	 * @return True if the device contains that register type y some signal, false
	 *         in opposite case
	 */
	@JsonIgnore
	public boolean containsRegistries(RegisterType type) {

		boolean contains = false;

		for (Signal signal : signals) {

			if (signal.getRegisterType().equals(type.toString()))
				contains = true;
		}
		return contains;
	}

	@JsonIgnore
	public Integer minRegisterinBlock(int blocks, int blockNumber, RegisterType type) {
		Integer min = null;
		for (Signal signal : signals) {
			if (type.toString().equals(signal.getRegisterType())) {
				int block = signal.getRegister() / blocks;
				if (block == blockNumber && (min == null || min > signal.getRegister())) {
					min = signal.getRegister();
				}
			}
		}
		return min;
	}

	@JsonIgnore
	public Integer maxRegisterinBlock(int blocks, int blockNumber, RegisterType type) {
		Integer max = null;
		for (Signal signal : signals) {
			if (type.toString().equals(signal.getRegisterType())) {
				int block = signal.getRegister() / blocks;
				if (block == blockNumber && (max == null || max < signal.getRegister())) {
					DataType dataType = DataType.fromValue(signal.getDataType());
					max = signal.getRegister() + DataType.offset(dataType);
				}
			}
		}
		return max;
	}

	/**
	 * for each signal, classified by register signal type, the method set the
	 * concrete registry signal value extracted from the array block signal,
	 * converted to a readable human language (Number).
	 * 
	 * @param values
	 *            - array of block signals, only of one register signal type (HR,CS,
	 *            IS, IR)
	 * @param registerType
	 *            - the concrete register signal type of the block signal.
	 * @param initCountReg
	 *            - number of the first register of the block
	 */
	@JsonIgnore
	public void setValuesInSignalsByRegisterType(@NotNull short[] values, RegisterType registerType, int initCountReg) {

		int indexRegisterPosition = 0;
		long timestamp = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());

		for (Signal signal : signals) {

			// if the signal has the same type that the argument registerType && is in range
			// in block (in block you have array singnas from initCountReg to initCountReg +
			// size
			if (signal.getRegisterType().equals(registerType.toString()) && signal.getRegister().intValue() >= initCountReg
					&& signal.getRegister().intValue() < initCountReg + values.length) {

				// searching the register in the block array values
				DataType dataType = DataType.fromValue(signal.getDataType());
				indexRegisterPosition = signal.getRegister().intValue() - initCountReg;
				short[] register = Arrays.copyOfRange(values, indexRegisterPosition, indexRegisterPosition + DataType.offset(dataType) + 1);

				// transform to bytes
				byte[] resultTrans = NumberUtils.getBytes(register, signal.getBigEndian(), dataType);
				signal.setValue(NumberUtils.getDataFormattedByDataType(dataType, resultTrans));
				signal.setConvertedValue(NumberUtils.getConvertedDataFormattedByDataType(dataType, resultTrans, signal.getConvFactor()));
				signal.setTimestampValueInNanos(timestamp++);

				// System.out.println(Thread.currentThread().getName()+"- signal:
				// "+signal.getId()+" value: "+signal.getValue()+" position:offset
				// "+indexRegisterPosition+":"+DataType.offset(dataType)+" "+
				// signal.getTimestampValue());
			}
		}
	}

	public Boolean existSignalId(String signalId) {

		Boolean exist = Boolean.FALSE;
		
		for (Signal signal : signals) {
			if(signal.getId().equalsIgnoreCase(signalId)) {
				exist = Boolean.TRUE;
				break;
			}
		}

		return exist;
	}

	public Boolean existBusinessId(String businessId) {

		Boolean exist = Boolean.FALSE;
		
		for (Signal signal : signals) {
			if(signal.getBusinessId().equalsIgnoreCase(businessId)) {
				exist = Boolean.TRUE;
				break;
			}
		}

		return exist;
	}

	public void checkDevice() {
		this.monitoringTimeMs = (this.monitoringTimeMs == null) ? (long) 60_000 : this.monitoringTimeMs;
		this.monitoringBlocks = (this.monitoringBlocks == null) ? 10 : this.monitoringBlocks;
		this.monitoringTimeoutSec = (this.monitoringTimeoutSec == null) ? 1 : this.monitoringTimeoutSec;
		this.monitoringDelayMs = (this.monitoringDelayMs == null) ? 1 : this.monitoringDelayMs;
		this.port = (this.port == null) ? 502 : this.port;
		this.manufacturer = (this.manufacturer == null) ? "UNDEFINED" : this.manufacturer;
		this.model = (this.model == null) ? "UNDEFINED" : this.model;
		this.deviceType = (this.deviceType == null) ? "OTHERS" : this.deviceType;
		this.info = (this.info == null) ? new String() : this.info;
	}
}
