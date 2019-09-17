package com.onesait.edge.engine.zigbee.util;

import com.google.gson.JsonElement;
import com.onesait.edge.engine.zigbee.types.StatusResponse;

public class StandardResponse {

	private StatusResponse status;
	private String message;
	private JsonElement data;
	private Integer ndevices;
	private Integer nSignalsReportables;
	private Integer nAttOK;
	private Integer nAttKO;
	private JsonElement gauge;
	private Integer nDevicesKO;
	private Integer leaveCode;
	private String leaveDetails;

	public StandardResponse(StatusResponse status) {
		super();
		this.status = status;
	}

	public StandardResponse(StatusResponse status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public StandardResponse(StatusResponse status, JsonElement data) {
		super();
		this.status = status;
		this.data = data;
	}

	public StandardResponse(StatusResponse status, Integer leaveCode, String leaveDetails) {
		super();
		this.status = status;
		this.leaveCode = leaveCode;
		this.leaveDetails = leaveDetails;
	}

	public StandardResponse(StatusResponse status, Integer ndevices, Integer nAttKO, Integer nAttOK, JsonElement data,
			JsonElement gauge, Integer nDevicesKO) {
		super();
		this.status = status;
		this.data = data;
		this.ndevices = ndevices;
		this.nDevicesKO = nDevicesKO;
		this.nAttKO = nAttKO;
		this.nAttOK = nAttOK;
		this.nSignalsReportables = nAttKO + nAttOK;
		this.gauge = gauge;
	}

	public Integer getnSignalsReportables() {
		return nSignalsReportables;
	}

	public void setnSignalsReportables(Integer nSignalsReportables) {
		this.nSignalsReportables = nSignalsReportables;
	}

	public Integer getnAttOK() {
		return nAttOK;
	}

	public void setnAttOK(Integer nAttOK) {
		this.nAttOK = nAttOK;
	}

	public Integer getnAttKO() {
		return nAttKO;
	}

	public void setnAttKO(Integer nAttKO) {
		this.nAttKO = nAttKO;
	}

	public JsonElement getGauge() {
		return gauge;
	}

	public void setGauge(JsonElement gauge) {
		this.gauge = gauge;
	}

	public Integer getnDevicesKO() {
		return nDevicesKO;
	}

	public void setnDevicesKO(Integer nDevicesKO) {
		this.nDevicesKO = nDevicesKO;
	}

	public int getLeaveCode() {
		return leaveCode;
	}

	public void setLeaveCode(Integer leaveCode) {
		this.leaveCode = leaveCode;
	}

	public String getLeaveDetails() {
		return leaveDetails;
	}

	public void setLeaveDetails(String leaveDetails) {
		this.leaveDetails = leaveDetails;
	}

	public Integer getNdevices() {
		return ndevices;
	}

	public void setNdevices(Integer ndevices) {
		this.ndevices = ndevices;
	}

	public StatusResponse getStatus() {
		return status;
	}

	public void setStatus(StatusResponse status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}