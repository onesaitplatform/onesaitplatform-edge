package com.onesait.edge.engine.zigbee.model;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;

public class ZclAttribute implements Cloneable {

	private static final long MIN_REPORTING_TIMEOUT_MS = 60_000;
	private static final long READ_RSP_TIMEOUT_MS = 90_000;
	private DoubleByte id;
	private String name;
	private String alias;

	private Long defaultValue;
	private byte[] value = new byte[0];
	private Boolean mandatory = Boolean.TRUE;
	private List<DoubleByte> attrs2sum = new ArrayList<>();
	/**
	 * attribute can be reader if letter r is present attribute can be written if
	 * letter w is present
	 */
	private String access = "rw";
	private Boolean reportable = Boolean.TRUE;
	private Long rangeFrom = Long.MIN_VALUE;
	private Long rangeTo = Long.MAX_VALUE;
	/**
	 * Datatype object defined by type
	 */
	private ZclDatatype datatype;
	/**
	 * If true, attribute we want to read
	 */
	private Boolean read = Boolean.FALSE;
	private DoubleByte minReportingTime = null;
	private DoubleByte maxReportingTime = null;
	private Date lastTimeUpdated = null;
	/**
	 * If different than null, code is required on commands
	 */
	private DoubleByte code = null;
	private int nReports = 0;
	private boolean unsupported = false;
	/**
	 * Number of retries for report config messages when device stops reporting
	 */
	private int retries = 0;
	private Timestamp lastTimeReconfigured = new Timestamp(new Date().getTime());
	/**
	 * Values and names stored if attribute datatype is enum8 / enum16 type
	 */
	private HashMap<DoubleByte, String> enums = new HashMap<>();

	private Double defaultConversion = null;
	private HashMap<DoubleByte, Double> conversionFactor = new HashMap<>();

	private boolean rspReceived = false;
	private ArrayList<ZclEvent> events = new ArrayList<>();
	private String manufacturerName = "";
	private Map<DoubleByte, ReportingFactor> reportingFactors = new ConcurrentHashMap<>();
	private Object attributeLock = new Object();
	private int sequenceNumber = -1; // inicializamos a -1 que es el ID invalido
	private Boolean reportado = false; // utilizado para comprobar el estado del atributo para el dashboard de la web
										// (ejem: cuando no reporta)

	public ZclAttribute(DoubleByte id, String name, ZclDatatype type, Long defaultValue, Boolean mandatory,
			String access, Boolean reportable, Boolean read, DoubleByte code, String manufacturer) {
		super();
		this.id = id;
		this.name = name;
		this.datatype = (ZclDatatype) type.clone();
		this.defaultValue = defaultValue;
		this.mandatory = mandatory;
		this.access = access;
		this.reportable = reportable;
		this.read = read;
		this.setCode(code);
		this.setManufacturerName(manufacturer);
		this.value = this.long2ByteArray(defaultValue);
		this.reportado = false;
	}

	public DoubleByte getId() {
		return id;
	}

	public void setId(DoubleByte id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Long defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public Boolean getReportable() {
		return reportable;
	}

	public void setReportable(Boolean reportable) {
		this.reportable = reportable;
	}

	public Long getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(Long rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public Long getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(Long rangeTo) {
		this.rangeTo = rangeTo;
	}

	public ZclDatatype getDatatype() {
		return datatype;
	}

	public void setZclDatatype(ZclDatatype zcldatatype) {
		this.datatype = (ZclDatatype) zcldatatype.clone();
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Boolean isReportado() {
		return reportado;
	}

	public void setReportado(Boolean reportado) {
		this.reportado = reportado;
	}

	public DoubleByte getMinReportingTime(DoubleByte devId) {
		try {
			if (this.reportingFactors.get(devId) != null) {
				int minTime = this.reportingFactors.get(devId).minReportingTime;
				return new DoubleByte(minTime);
			} else {
				return this.minReportingTime;
			}
		} catch (Exception e) {
			return this.minReportingTime;
		}
	}

	public void setMinReportingTime(DoubleByte minReportingTime) {
		this.minReportingTime = minReportingTime;
	}

	public DoubleByte getMaxReportingTime(DoubleByte devId) {
		try {
			if (this.reportingFactors.get(devId) != null) {
				int maxTime = this.reportingFactors.get(devId).maxReportingTime;
				return new DoubleByte(maxTime);
			} else {
				return this.maxReportingTime;
			}
		} catch (Exception e) {
			return this.maxReportingTime;
		}
	}

	public void setMaxReportingTime(DoubleByte maxReportingTime) {
		this.maxReportingTime = maxReportingTime;
	}

	public Boolean getReporting() {
		return (minReportingTime != null && maxReportingTime != null)
				&& (!maxReportingTime.lowerThan(minReportingTime));
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	/**
	 * Sets a new value and the current date, and resets the retries counter. This
	 * method shall not be used when receiving an automatic report of the attribute,
	 * nor a read attributes response neither a cluster command with attributes.
	 * 
	 * @param value Value to set
	 */
	public synchronized void updateValue(byte[] value, boolean logs, Boolean reportado) {
		this.lastTimeUpdated = new Date(Calendar.getInstance().getTime().getTime());
		if (reportado != null) {
			this.reportado = reportado;
		}
		if (logs) {
			// LOG.info("Update value. Fecha: "+lastTimeUpdated.toGMTString());
		}
		this.setBigEndianValue(value);
		this.resetRetries();
	}

	/**
	 * Sets a new value and the current date, resets the retries counter and
	 * increase the number of automatic reports received. This method shall be used
	 * when receiving an automatic report.
	 * 
	 * @param value Value to set
	 */
	public synchronized void updateValueAndIncreaseReports(byte[] value) {
		this.updateValue(value, false, true);
		this.increaseReports();
	}

	public synchronized void setBigEndianValue(byte[] value) {
		if (this.isFixedLength() && value.length > this.getDatatype().getLength()) {
			for (int i = 0; i < this.value.length; i++) {
				this.value[i] = value[i + (value.length - this.value.length)];
			}
		} else if (this.isFixedLength() && value.length == this.getDatatype().getLength()) {
			this.value = value.clone();
		} else if (!this.isFixedLength()) {
			this.value = value.clone();
		}
	}

	private byte[] long2ByteArray(Long value) {
		if (value != null) {
			byte[] longValue8Bytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(value).array();

			if (this.datatype != null) {
				return longBytes2AttributeBytes(longValue8Bytes);
			} else {
				return new byte[0];
			}
		} else {
			if (this.datatype != null && this.datatype.getLength() != null) {
				byte[] bytes = new byte[this.datatype.getLength()];
				for (int i = 0; i < bytes.length; i++) {
					bytes[i] = 0;
				}
				return bytes;
			} else {
				return new byte[0];
			}
		}
	}

	private byte[] longBytes2AttributeBytes(byte[] longValue8Bytes) {
		if (this.datatype.getLength() != 0) {
			byte[] attributeBytes = new byte[this.datatype.getLength()];
			for (int i = 0; i < attributeBytes.length; i++) {
				attributeBytes[i] = longValue8Bytes[longValue8Bytes.length - attributeBytes.length + i];
			}
			return attributeBytes;
		} else if (this.datatype.getName().contains("string")) {
			return longValue8Bytes;
		} else {
			return new byte[0];
		}
	}

	public synchronized byte[] getBigEndianValue() {
		return this.value.clone();
	}

	/**
	 * Get value stored as Long converted to bytes. Byte positions are the opposite
	 * as in the attributes.
	 * 
	 * @return byte[] whose size is the same as attribute datatype length
	 */
	public byte[] getLittleEndianValue() {

		byte[] reverseBytes = new byte[getBigEndianValue().length];
		for (int i = 0; i < reverseBytes.length; i++) {
			reverseBytes[i] = getBigEndianValue()[getBigEndianValue().length - 1 - i];
		}
		return reverseBytes;
	}

	public ArrayList<ZclEvent> getEvents() {
		return events;
	}

	@Override
	public String toString() {
		return "ZclAttribute [id=" + id + ", name=" + name + ", type=" + datatype.getName() + ", unsupported="
				+ unsupported + ", rspReceived=" + rspReceived + "]";
	}

	public Date getLastTimeUpdated() {
		return lastTimeUpdated;
	}

	/**
	 * Returns attribute value converted from byte array to its java Object type.
	 * Once returned, cast must be made.
	 * 
	 * @return attribute value Object
	 */
	public Object getConvertedValue() {
		Object obj = null;
		if (this.datatype.getName().equals(ZclDatatype.FLOAT_STR)) {
			obj = ZclDatatype.bytes2float(this.getBigEndianValue());

		} else if (this.datatype.getName().equals(ZclDatatype.OSTRING_STR)
				|| this.datatype.getName().equals(ZclDatatype.CSTRING_STR)) {
			obj = ZclDatatype.bytes2string(this.getBigEndianValue());

		} else if (this.datatype.getName().equals(ZclDatatype.BOOLEAN_STR)) {
			obj = ZclDatatype.bytes2boolean(this.getBigEndianValue());

		} else if (this.datatype.getName().equals(ZclDatatype.INT8_STR)
				|| this.datatype.getName().equals(ZclDatatype.INT16_STR)
				|| this.datatype.getName().equals(ZclDatatype.INT24_STR)
				|| this.datatype.getName().equals(ZclDatatype.INT32_STR)
				|| this.datatype.getName().equals(ZclDatatype.INT40_STR)
				|| this.datatype.getName().equals(ZclDatatype.INT48_STR)
				|| this.datatype.getName().equals(ZclDatatype.INT56_STR)
				|| this.datatype.getName().equals(ZclDatatype.INT64_STR)) {
			obj = ZclDatatype.bytes2int(this.getBigEndianValue());

		} else if (this.datatype.getName().equals(ZclDatatype.UINT8_STR)
				|| this.datatype.getName().equals(ZclDatatype.UINT16_STR)
				|| this.datatype.getName().equals(ZclDatatype.UINT24_STR)
				|| this.datatype.getName().equals(ZclDatatype.UINT32_STR)
				|| this.datatype.getName().equals(ZclDatatype.UINT40_STR)
				|| this.datatype.getName().equals(ZclDatatype.UINT48_STR)
				|| this.datatype.getName().equals(ZclDatatype.UINT56_STR)
				|| this.datatype.getName().equals(ZclDatatype.UINT64_STR)) {
			obj = ZclDatatype.bytes2uint(this.getBigEndianValue());

		} else if (this.datatype.getName().equals(ZclDatatype.ENUM16_STR)
				|| this.datatype.getName().equals(ZclDatatype.ENUM8_STR)) {
			DoubleByte atValue;
			if (this.getBigEndianValue().length == 1) {
				atValue = new DoubleByte(this.getBigEndianValue()[0]);
			} else {
				atValue = new DoubleByte(this.getBigEndianValue()[0], this.getBigEndianValue()[1]);
			}
			String enumStr = this.enums.get(atValue);
			if (enumStr != null)
				obj = enumStr;
			else
				obj = "";

		} else if (this.datatype.getName().equals(ZclDatatype.BITMAP8_STR)
				|| this.datatype.getName().equals(ZclDatatype.BITMAP16_STR)
				|| this.datatype.getName().equals(ZclDatatype.BITMAP24_STR)
				|| this.datatype.getName().equals(ZclDatatype.BITMAP32_STR)
				|| this.datatype.getName().equals(ZclDatatype.BITMAP40_STR)
				|| this.datatype.getName().equals(ZclDatatype.BITMAP48_STR)
				|| this.datatype.getName().equals(ZclDatatype.BITMAP56_STR)
				|| this.datatype.getName().equals(ZclDatatype.BITMAP64_STR)) {
			obj = Long.valueOf(new BigInteger(this.getBigEndianValue()).longValue());

		} else if (this.datatype.getName().equals(ZclDatatype.IEEE_STR)) {
			obj = new OctaByte(this.getBigEndianValue());
		} else {
			obj = this.getBigEndianValue();
		}

		return obj;
	}

	public int getnReports() {
		return nReports;
	}

	public void setnReports(int nReports) {
		this.nReports = nReports;
	}

	public int increaseReports() {
		this.nReports++;
		return this.nReports;
	}

	public boolean isUnsupported() {
		return unsupported;
	}

	public void setUnsupported(boolean unsupported) {
		this.unsupported = unsupported;
	}

	public DoubleByte getCode() {
		return code;
	}

	public void setCode(DoubleByte code) {
		this.code = code;
	}

	public int getRetries() {
		return retries;
	}

	public void incRetries() {
		this.retries++;
	}

	public void resetRetries() {
		this.retries = 0;
	}

	public boolean isRetryConfigReport(DoubleByte devId, ZclCluster cluster, int epId) {
		if (this.getReporting() && !this.isUnsupported()) {
			long rightNow = new Date().getTime();
			if (this.lastTimeReconfigured == null) {
				return true;
			} else if (this.lastTimeUpdated == null
					|| rightNow - this.lastTimeUpdated.getTime() > getReportingTimeoutMs(devId)) {
				return (rightNow - this.lastTimeReconfigured.getTime()) > getReportingTimeoutMs(devId)
						* Math.pow(2, this.retries);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private long getReportingTimeoutMs(DoubleByte devId) {
		try {
			long maxReportingTimeSec = this.getMaxReportingTime(devId).intValue();
			long reportingTimeoutMs = maxReportingTimeSec * 1500; // cambiar para hacer pruebas a 500
			// Si el timeout es muy bajo, un pequeño retraso por parte del dispositivo
			// puede hacer que se reconfigure el reporte, asi que establecemos uno m�nimo.
			reportingTimeoutMs = reportingTimeoutMs < MIN_REPORTING_TIMEOUT_MS ? MIN_REPORTING_TIMEOUT_MS
					: reportingTimeoutMs;
			return reportingTimeoutMs;
		} catch (Exception e) {
			return 1;
		}
	}

	public boolean isRetryRead(int epId) {
		if (this.getRead() && !this.isUnsupported()) {
			if (this.isRspReceived()) {
				return false;
			}
			long rightNow = new Date().getTime();
			if (this.lastTimeReconfigured == null) {
				return true;
			} else if (this.lastTimeUpdated == null
					|| rightNow - this.lastTimeUpdated.getTime() > READ_RSP_TIMEOUT_MS) {
				return (rightNow - this.lastTimeReconfigured.getTime()) > READ_RSP_TIMEOUT_MS
						* Math.pow(2, this.retries);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void updateLastTimeReconfigured() {
		this.lastTimeReconfigured = new Timestamp(new Date().getTime());
	}

	public HashMap<DoubleByte, String> getEnums() {
		return enums;
	}

	public void setEnums(HashMap<DoubleByte, String> enums) {
		this.enums = enums;
	}

	public HashMap<DoubleByte, Double> getConversionFactor() {
		return conversionFactor;
	}

	public void setConversionFactor(HashMap<DoubleByte, Double> conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	public synchronized boolean isRspReceived() {
		return rspReceived;
	}

	public synchronized void setRspReceived(boolean rspReceived) {
		this.rspReceived = rspReceived;
	}

	public boolean isManufacturerSpecific() {
		return this.code != null;
	}

	public int getLength() {
		if (!this.datatype.getName().toLowerCase().contains("string")) {
			return this.datatype.getLength();
		}
		Object value = this.getConvertedValue();
		if (value instanceof String) {
			String valueString = (String) value;
			return valueString.length();
		}
		return 0;
	}

	public Object clone() {
		ZclAttribute clonedAtt = new ZclAttribute(id, name, this.datatype, defaultValue, mandatory, access, reportable,
				read, code, manufacturerName);
		clonedAtt.setReportado(this.reportado);
		clonedAtt.setMaxReportingTime(this.maxReportingTime);
		clonedAtt.setMinReportingTime(this.minReportingTime);
		clonedAtt.setnReports(getnReports());
		clonedAtt.setEnums(getEnums());
		clonedAtt.getEvents().addAll(this.getEvents());
		clonedAtt.setUnsupported(this.isUnsupported());
		clonedAtt.setBigEndianValue(this.getBigEndianValue());
		clonedAtt.setRspReceived(this.isRspReceived());
		clonedAtt.setDefaultConversion(this.defaultConversion);
		clonedAtt.setConversionFactor(this.conversionFactor);
		clonedAtt.setAlias(this.alias);
		clonedAtt.setAttrs2sum(this.attrs2sum);
		for (ReportingFactor rf : this.reportingFactors.values()) {
			clonedAtt.addReportingFactor(rf.deviceId, rf.minReportingTime, rf.maxReportingTime);
		}
		return clonedAtt;
	}

	private boolean isFixedLength() {
		return this.getDatatype() != null && this.getDatatype().getLength() != null
				&& this.getDatatype().getLength() != 0;
	}

	public void addReportingFactor(DoubleByte factorDeviceId, Integer factorMinReportingTime,
			Integer factorMaxReportingTime) {
		ReportingFactor rf = new ReportingFactor(factorDeviceId, factorMaxReportingTime, factorMinReportingTime);
		this.reportingFactors.put(factorDeviceId, rf);
	}

	private class ReportingFactor {
		public final DoubleByte deviceId;
		public final Integer maxReportingTime;
		public final Integer minReportingTime;

		public ReportingFactor(DoubleByte deviceId, Integer maxReportingTime, Integer minReportingTime) {
			super();
			this.deviceId = deviceId;
			this.maxReportingTime = maxReportingTime;
			this.minReportingTime = minReportingTime;
		}
	}

	public synchronized int getSequenceNumber() {
		return this.sequenceNumber;
	}

	public synchronized void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Double getDefaultConversion() {
		return defaultConversion;
	}

	public void setDefaultConversion(Double defaultConversion) {
		this.defaultConversion = defaultConversion;
	}

	public Object getAttributeLock() {
		return attributeLock;
	}

	public void setAttributeLock(Object attributeLock) {
		this.attributeLock = attributeLock;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<DoubleByte> getAttrs2sum() {
		return attrs2sum;
	}

	public void setAttrs2sum(List<DoubleByte> attrs2sum) {
		this.attrs2sum = attrs2sum;
	}
	
	

}
