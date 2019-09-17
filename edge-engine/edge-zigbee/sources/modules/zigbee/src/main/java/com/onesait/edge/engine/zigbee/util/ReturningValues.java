package com.onesait.edge.engine.zigbee.util;

import java.util.List;

import com.onesait.edge.engine.zigbee.jsoncontroller.ZbConfigReportJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbGetDataJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbShowNetwork;

public class ReturningValues {

//	private List
	private List<ZbConfigReportJson> configReport;
	private List<ZbGetDataJson> getData;
	private List<ZbShowNetwork> showNetwork;
	public List<ZbConfigReportJson> getConfigReport() {
		return configReport;
	}
	public void setConfigReport(List<ZbConfigReportJson> configReport) {
		this.configReport = configReport;
	}
	public List<ZbGetDataJson> getGetData() {
		return getData;
	}
	public void setGetData(List<ZbGetDataJson> getData) {
		this.getData = getData;
	}
	public List<ZbShowNetwork> getShowNetwork() {
		return showNetwork;
	}
	public void setShowNetwork(List<ZbShowNetwork> showNetwork) {
		this.showNetwork = showNetwork;
	}
	
	
	

		
	
	
	
	}

	
	

