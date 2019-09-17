package com.onesait.edge.engine.zigbee.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onesait.edge.engine.zigbee.clients.IASZoneClient;
import com.onesait.edge.engine.zigbee.exception.ExceptionCause;
import com.onesait.edge.engine.zigbee.exception.GenericZigbeeException;
import com.onesait.edge.engine.zigbee.exception.UnknownCoordinatorMacException;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtLqiReq;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtPermitJoinRequest;
import com.onesait.edge.engine.zigbee.jsoncontroller.AttributesJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.Child;
import com.onesait.edge.engine.zigbee.jsoncontroller.ClustersInfoJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbClusterGraph;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbConfigReportJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbCoordinatorJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbDeviceJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbDevicesGraph;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbMeshView;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclCoordinator;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.model.ZclEndpoint;
import com.onesait.edge.engine.zigbee.types.AlertType;
import com.onesait.edge.engine.zigbee.types.CommandType;
import com.onesait.edge.engine.zigbee.types.CommandValue;
import com.onesait.edge.engine.zigbee.types.CommandWeb;
import com.onesait.edge.engine.zigbee.types.DeviceStatus;
import com.onesait.edge.engine.zigbee.types.DeviceType;
import com.onesait.edge.engine.zigbee.util.Alert;
import com.onesait.edge.engine.zigbee.util.BuildMqttMsg;
import com.onesait.edge.engine.zigbee.util.DeviceInfo;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.PermitJoin;
import com.onesait.edge.engine.zigbee.util.RequestMaps;
import com.onesait.edge.engine.zigbee.util.ReturningValues;
import com.onesait.edge.engine.zigbee.util.State;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

@Service
public class ZigbeeService {

	@Autowired
	SerialZigbeeConnector serialConnector;

	@Autowired
	DeviceManager deviceManager;

	private static final Logger LOG = LoggerFactory.getLogger(ZigbeeService.class);
	private static final int HIGH_MAX_VALUE = 255;
	private static final int GOOD_MAX_VALUE = 40;
	private static final int MEDIUM_MAX_VALUE = 20;
	private static final int LOW_MAX_VALUE = 10;
	private static final int NO_SIGNAL = 0;
	private static Integer nAttKO = 0;
	private static Integer nAttOK = 0;
	private static Integer nDevices = 0;
	private static Integer nDevicesKO = 0;
	private static Integer nDevicesOK = 0;
	private static List<Alert> alertList = new ArrayList<>();

	protected static Date timeStarted = new Date(Calendar.getInstance().getTime().getTime());

	public static boolean otaLaunched = false;
	public static final long TIME_2H = 2L * 3600 * 60_000;
	public static final long TIME_5MIN = 5L * 60_000;

	public static final Integer TIMEOUT_SET = 15000;

	public int[] buildZclFrame(boolean clusterSpecific, int seqNumber, int cmdId, int[] zclPayload) {
		int pos = 0;
		int[] zclFrame = new int[zclPayload.length + 3];

		if (clusterSpecific) {
			// int [] zclHeader=new int [5];
			zclFrame[pos++] = 0x19; // cluster specific,Direction to client,
									// Disable Default Rsp=true
		} else {
			zclFrame[pos++] = 0x00;
		}
		if (seqNumber >= 256) {
			seqNumber = 0;
		}
		zclFrame[pos++] = seqNumber;
		zclFrame[pos++] = cmdId;
		for (int i = 0; i < zclPayload.length; i++) {
			zclFrame[pos++] = zclPayload[i];
		}
		return zclFrame;
	}

	public List<Child> listChildDevices(DoubleByte[] children) {
		List<Child> childs = new ArrayList<>();
		for (int i = 0; i < children.length; i++) {
			if (children[i] != null) {
				ZclDevice device = deviceManager.getDeviceByNwkAddress(children[i]);
				if (device != null) {
					Child child = new Child();
					child.setChildID(device.getIeeeAddress().toString());
					child.setNwkAddress(device.getShortAddress().toString());
					if (device.getManufacturerName() != null) {
						child.setManufacturer(device.getManufacturerName());
					}
					child.setDeviceType(device.getGeneralDeviceType().toString());
					childs.add(child);
				}
			}
		}
		return childs;
	}

	public void meshview(List<ZbMeshView> mesh) {
		ZclCoordinator coor = this.serialConnector.getZclcoor();
		List<String[]> list = coor.getzMesh().getStringNodeLinks();
		for (String[] str : list) {
			ZclDevice dev = this.deviceManager.getDeviceByMac(str[2]);
			if (dev != null) {
				String father = ("0000".equals(str[0])) ? "Coor" : str[0];
				DeviceInfo deviceInfo = new DeviceInfo(Integer.parseInt(str[7]));
				PermitJoin permitJoin = new PermitJoin(Integer.parseInt(str[6]));
				String pjStr = permitJoin.getPermitJoining();
				String dtStr = deviceInfo.getDeviceType();
				String rowiStr = deviceInfo.getReceiverOnWhenIdle();
				String relStr = deviceInfo.getRelationship();
				String lqiStr = str[4];
				String depthStr = str[5];
				ZbMeshView meshElement = new ZbMeshView();
				// padre
				meshElement.setLocalAddr(father);
				// vecino
				meshElement.setNeighAddr(str[1]);
				// ieee
				meshElement.setIeeeAddress(str[2]);
				// lqi (calidad del enlace)
				meshElement.setLqi(lqiStr);
				// mesh depth
				meshElement.setDepth(depthStr);
				// mesh permit join
				meshElement.setPermitJoin(pjStr);
				// mesh deviceType
				meshElement.setDeviceType(dtStr);
				// mesh rxon
				meshElement.setRxOnIdle(rowiStr);
				// mesh relation
				meshElement.setRelationship(relStr);
				mesh.add(meshElement);
			}
		}
	}

	public static Integer getnAttKO() {
		return nAttKO;
	}

	public static Integer getnAttOK() {
		return nAttOK;
	}

	public static Integer getnDevices() {
		return nDevices;
	}

	public static Integer getnDevicesKO() {
		return nDevicesKO;
	}

	public static Integer getnDevicesOK() {
		return nDevicesOK;
	}

	public void createmesh() throws InterruptedException {

		ZclCoordinator coor = this.serialConnector.getZclcoor();
		coor.cleanMesh();
		DoubleByte db = new DoubleByte(0x00);
		sendLqiReq(db);
		int counter = 1;
		for (ZclDevice dev : this.deviceManager.devices.values()) {
			db = dev.getShortAddress();
			sendLqiReq(db);
			counter++;
			waitMs(1000);
		}
		waitMs(counter * 6000);
		// mesh creada

	}

	public void sendLqiReq(DoubleByte db) {
		ZdoMgmtLqiReq frame = new ZdoMgmtLqiReq(db, 0);
		this.serialConnector.getOutputSerial().writeZFrame(frame);

	}

	public List<ZbDeviceJson> buildZbStatusDevices() {
		ArrayList<ZbDeviceJson> zbdevicesStatus = new ArrayList<>();
		ZigbeeService.nAttKO = 0;
		ZigbeeService.nAttOK = 0;
		ZigbeeService.nDevicesKO = 0;
		ZigbeeService.nDevicesOK = 0;
		nDevices = deviceManager.devices.size();
		synchronized (deviceManager.devices) {
			for (Entry<DoubleByte, ZclDevice> dev : deviceManager.devices.entrySet()) {
				ArrayList<ClustersInfoJson> clusterinfo = new ArrayList<>();
				ZclDevice device = dev.getValue();
				// TODO Revisar este metodo para que no pete cuando se este uniendo un device
				if (checkIfDeviceIsOK(device)) {
					ZbDeviceJson zbdevicejson = new ZbDeviceJson();
					zbdevicejson.setMac(device.getIeeeAddress().toString());
					zbdevicejson.setDevType(device.getGeneralDeviceType().toString());
					zbdevicejson.setStatus(device.getStatus().toString());
					for (ZclEndpoint zep : device.getEndpoints().values()) {
						for (ZclCluster zcl : zep.getClusters().values()) {
							ClustersInfoJson cluster = null;
							ArrayList<String> attKO = null;
							ArrayList<String> attOK = null;
							if (existAttrWithInfo(zcl)) {
								attOK = new ArrayList<>();
								attKO = new ArrayList<>();
								cluster = new ClustersInfoJson();
								cluster.setClusterid(zcl.getId().toStr());
								cluster.setName(zcl.getName());
								for (ZclAttribute zatt : zcl.getAttributes().values()) {
									if ((!zatt.isUnsupported() && zatt.isReportado())
											&& (zatt.getMinReportingTime(null) != null
													|| zatt.getName().equalsIgnoreCase("Zone Status"))) {
										attOK.add(zatt.getName());
										ZigbeeService.nAttOK++;
									} else if ((!zatt.isUnsupported() && !zatt.isReportado())
											&& (zatt.getMinReportingTime(null) != null
													|| zatt.getName().equalsIgnoreCase("Zone Status"))) {
										attKO.add(zatt.getName());
										ZigbeeService.nAttKO++;
									}
								}

								if (attKO != null) {
									cluster.setAttributesKO(attKO);
								}
								if (attOK != null) {
									cluster.setAttributesOK(attOK);
								}
								clusterinfo.add(cluster);
							}
						}
					}
					zbdevicejson.setAttributesInfo(clusterinfo);
					zbdevicesStatus.add(zbdevicejson);
				}
			}
		}
		return zbdevicesStatus;
	}

	private boolean checkIfDeviceIsOK(ZclDevice device) {
		try {
			DeviceType deviceType = device.getGeneralDeviceType();
			if (deviceType.toString() != null) {
				if (deviceType == DeviceType.THERMOSTAT) {
					ZclCluster thermostat = device.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_HAVC_THERMOSTAT);
					ZclAttribute localtemp = thermostat.getAttribute(ZClusterLibrary.ZCL_ATT_LOCAL_TEMPERATURE);
					if (isDeviceKOorOK(localtemp, device, ZigbeeService.TIME_5MIN)) {
						device.setStatus(DeviceStatus.OK);
						ZigbeeService.nDevicesOK++;
					} else {
						device.setStatus(DeviceStatus.KO);
						ZigbeeService.nDevicesKO++;
					}
				} else if (deviceType == DeviceType.METER || deviceType == DeviceType.PLUG
						|| deviceType == DeviceType.CLAMP || deviceType == DeviceType.CLAMP3) {
					ZclCluster zcl = device.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_SE_SIMPLE_METERING);
					if (this.isMeazonDevice(device)) {
						if (deviceType == DeviceType.CLAMP3) {
							ZclAttribute powerPh1 = zcl.getAttribute(ZClusterLibrary.ZCL_ATT_MEAZON_ACTIVE_POWERL1);
							ZclAttribute powerPh2 = zcl.getAttribute(ZClusterLibrary.ZCL_ATT_MEAZON_ACTIVE_POWERL2);
							ZclAttribute powerPh3 = zcl.getAttribute(ZClusterLibrary.ZCL_ATT_MEAZON_ACTIVE_POWERL3);
							if ((powerPh3 != null) && (powerPh2 != null) && (powerPh1 != null)) {
								if (isDeviceKOorOK(powerPh1, device, ZigbeeService.TIME_5MIN)
										&& isDeviceKOorOK(powerPh2, device, ZigbeeService.TIME_5MIN)
										&& isDeviceKOorOK(powerPh3, device, ZigbeeService.TIME_5MIN)) {
									device.setStatus(DeviceStatus.OK);
									ZigbeeService.nDevicesOK++;
								} else {
									device.setStatus(DeviceStatus.KO);
									ZigbeeService.nDevicesKO++;
								}
							} else {
								device.setStatus(DeviceStatus.KO);
								ZigbeeService.nDevicesKO++;
							}
						} else { // es un plug de meazon o una pinza monofasica de
									// meazon
							ZclAttribute powerPh1 = zcl.getAttribute(ZClusterLibrary.ZCL_ATT_MEAZON_ACTIVE_POWERL1);
							if (isDeviceKOorOK(powerPh1, device, ZigbeeService.TIME_5MIN)) {
								device.setStatus(DeviceStatus.OK);
								ZigbeeService.nDevicesOK++;
							} else {
								device.setStatus(DeviceStatus.KO);
								ZigbeeService.nDevicesKO++;
							}
						}
					} else {
						ZclAttribute powerPh1 = zcl.getAttribute(ZClusterLibrary.ZCL_ATT_INSTANTANEOUS_DEMAND);
						if (isDeviceKOorOK(powerPh1, device, ZigbeeService.TIME_5MIN)) {
							device.setStatus(DeviceStatus.OK);
							ZigbeeService.nDevicesOK++;
						} else {
							device.setStatus(DeviceStatus.KO);
							ZigbeeService.nDevicesKO++;
						}
					}
				} else if (deviceType.toString().contains("SENSOR")) {
					ZclCluster zcl = device.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE);
					ZclAttribute status = zcl.getAttribute(new DoubleByte(2));
					if (isDeviceKOorOK(status, device, ZigbeeService.TIME_2H)) {
						device.setStatus(DeviceStatus.OK);
						ZigbeeService.nDevicesOK++;
					} else {
						device.setStatus(DeviceStatus.KO);
						ZigbeeService.nDevicesKO++;
					}
				} else if (deviceType.toString().contains("BULB")) {
					ZclCluster onoff = device.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_ON_OFF);
					ZclAttribute onff = onoff.getAttribute(new DoubleByte(0));
					if (isDeviceKOorOK(onff, device, ZigbeeService.TIME_5MIN)) {
						device.setStatus(DeviceStatus.OK);
						ZigbeeService.nDevicesOK++;
					} else {
						device.setStatus(DeviceStatus.KO);
						ZigbeeService.nDevicesKO++;
					}
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isDeviceKOorOK(ZclAttribute att, ZclDevice device, long timeinMS) {
		Date timenow = new Date(Calendar.getInstance().getTime().getTime());
		long now = timenow.getTime();
		if (att != null) {
			if (att.getLastTimeUpdated() != null) {
				if (now - att.getLastTimeUpdated().getTime() > timeinMS) {
					return false; // KO
				} else {
					return true; // OK
				}
			} else {
				if (now - ZigbeeService.timeStarted.getTime() > timeinMS) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private boolean existAttrWithInfo(ZclCluster cluster) {
		for (ZclAttribute zatt : cluster.getAttributes().values()) {
			if (!zatt.isUnsupported()
					&& ((zatt.getMinReportingTime(null) != null) || (zatt.getName().equalsIgnoreCase("Zone Status")))) {
				return true;
			}
		}
		return false;
	}

	public List<ZbDeviceJson> buildZbdevicesJson() {
		ArrayList<ZbDeviceJson> zbdevices = new ArrayList<>();
		synchronized (deviceManager.devices) {
			for (Entry<DoubleByte, ZclDevice> dev : deviceManager.devices.entrySet()) {
				try {
					DoubleByte manufacturerCode;
					ZclDevice device = dev.getValue();
					ZclCluster basic = device.getZclCluster(new DoubleByte(0));
					ZclAttribute manName = null;
					if (basic != null) {
						manName = basic.getAttribute(new DoubleByte(4));
						String manNameStr = (String) manName.getConvertedValue();
						device.setManufacturerName((String) manName.getConvertedValue());
						if ((device.getManufacturerName() == null || device.getManufacturerName().equals(""))
								&& basic != null) {
							if (manName != null && manName.isRspReceived()) {
								device.setManufacturerName(manNameStr);
							} else if (manName != null) {
								ArrayList<DoubleByte> manNameAr = new ArrayList<DoubleByte>();
								manNameAr.add(new DoubleByte(4));
								ZFrame[] frames = basic.buildReadAttributes(manNameAr);
								this.serialConnector.getOutputSerial().writeZFrame(frames[0]);
							}
						}
					}
					ZbDeviceJson zbdevicejson = new ZbDeviceJson();
					checkIfDeviceIsOK(device);
					checkDeviceStatus(device,zbdevicejson);
					zbdevicejson.setMac(device.getIeeeAddress().toString());
					zbdevicejson.setShortaddress(device.getShortAddress().toString());
					zbdevicejson.setLq((getSignalQuality(device.getLqi())));
					if (manName != null) {
						zbdevicejson.setManufacturer((String) manName.getConvertedValue());
					}
					zbdevicejson.setClusters(this.buildZbClustersJson(device.getIeeeAddress().toString(), false));
					try {
						zbdevicejson.setMainValues(this.buildMainValue(device));
					} catch (Exception frames) {

					}
					if (device.getGeneralDeviceType() != null) {
						zbdevicejson.setDevType(device.getGeneralDeviceType().toString());
					}
					if ((manufacturerCode = device.getManufacturerCode()) == null) {
						zbdevicejson.setMancode("");
					} else {
						zbdevicejson.setMancode(manufacturerCode.toString());
					}
					zbdevices.add(zbdevicejson);
				} catch (Exception e) {
					LOG.error("Error reading the info of: {} ", (dev.getValue()).getIeeeAddress());
					if (dev.getValue().getIeeeAddress().equals(ZigbeeConstants.INVALID_IEEE_ADDRESS)) {
						try {
							deviceManager.devices.remove(dev.getKey());
							LOG.info("Removing invalid mac: {}", (dev.getValue()).getIeeeAddress());
						} catch (Exception j) {

						}
					}

				}
			}
			return zbdevices;
		}
	}

	private void checkDeviceStatus(ZclDevice device, ZbDeviceJson zbdevicejson) {
		if (device.getOtamanager().isOta()) {
			zbdevicejson.setStatus(DeviceStatus.DOWNLOADINGOTAFILE.toString());
			zbdevicejson.setPercentage(device.getOtamanager().getPorcentaje());
		}else if (device.getOtamanager().isInstalling()) {
			zbdevicejson.setStatus(DeviceStatus.UPGRADING.toString());
		} else {
			zbdevicejson.setStatus(device.getStatus().toString());
		}
		
	}

	private int getSignalQuality(int lqi) {
		if ((lqi <= HIGH_MAX_VALUE) && (lqi > GOOD_MAX_VALUE)) {
			return 4;
		} else if ((lqi <= GOOD_MAX_VALUE) && (lqi > MEDIUM_MAX_VALUE)) {
			return 3;
		} else if ((lqi <= MEDIUM_MAX_VALUE) && (lqi > LOW_MAX_VALUE)) {
			return 2;
		} else if ((lqi <= LOW_MAX_VALUE) && (lqi > NO_SIGNAL)) {
			return 1;
		} else {
			return 0;
		}
	}

	private AttributesJson buildMainValue(ZclDevice dev) {
		DeviceType generalDeviceType = dev.getGeneralDeviceType();
		AttributesJson mainValues = new AttributesJson();
		try {
			if (generalDeviceType == DeviceType.THERMOSTAT) {
				ZclCluster zcl = dev.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_HAVC_THERMOSTAT);
				ZclAttribute localtemp = zcl.getAttribute(new DoubleByte(0));
				double temp = BuildMqttMsg.getConversionFactorYRoundNumber(localtemp, 2, dev);
				mainValues.setMainValue(String.format("%.2f", temp) + "ºC");
			} else if (generalDeviceType == DeviceType.METER || generalDeviceType == DeviceType.PLUG
					|| generalDeviceType == DeviceType.CLAMP || generalDeviceType == DeviceType.CLAMP3) {
				ZclCluster zcl = dev.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_SE_SIMPLE_METERING);
				if (this.isMeazonDevice(dev)) {
					if (generalDeviceType == DeviceType.CLAMP3) {
						double aPowerPh1, aPowerPh2, aPowerPh3;
						ZclAttribute powerPh1 = zcl.getAttribute(new DoubleByte(0x2001));
						ZclAttribute powerPh2 = zcl.getAttribute(new DoubleByte(0x2002));
						ZclAttribute powerPh3 = zcl.getAttribute(new DoubleByte(0x2003));
						try {
							aPowerPh1 = BuildMqttMsg.getConversionFactorYRoundNumber(powerPh1, 2, dev);
						} catch (NumberFormatException e) {
							aPowerPh1 = 0;
						}
						try {
							aPowerPh2 = BuildMqttMsg.getConversionFactorYRoundNumber(powerPh2, 2, dev);
						} catch (NumberFormatException e) {
							aPowerPh2 = 0;
						}
						try {
							aPowerPh3 = BuildMqttMsg.getConversionFactorYRoundNumber(powerPh3, 2, dev);
						} catch (NumberFormatException e) {
							aPowerPh3 = 0;
						}
						double sum = aPowerPh3 + aPowerPh2 + aPowerPh1;
						mainValues.setMainValue(String.format("%.2f", sum) + "W");
					} else {
						ZclAttribute power = zcl.getAttribute(new DoubleByte(0x2001));
						double aPowerPh1;
						try {
							aPowerPh1 = BuildMqttMsg.getConversionFactorYRoundNumber(power, 2, dev);
						} catch (NumberFormatException e) {
							aPowerPh1 = 0;
						}
						mainValues.setMainValue(String.format("%.2f", aPowerPh1) + "W");
					}
				} else {
					ZclAttribute powerPh1 = zcl.getAttribute(new DoubleByte(0x0400));
					double aPowerPh1 = BuildMqttMsg.getConversionFactorYRoundNumber(powerPh1, 2, dev);
					mainValues.setMainValue(String.format("%.2f", aPowerPh1) + "W");
				}
			} else if (generalDeviceType.toString().contains("SENSOR")) {
				ZclCluster zcl = dev.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE);
				ZclAttribute status = zcl.getAttribute(new DoubleByte(2));
				int zoneStatus = Integer.parseInt(status.getConvertedValue().toString());
				if ((zoneStatus & 1) > 0 || (zoneStatus & 2) > 0) {
					mainValues.setMainValue(CommandValue.ON.toString());
				} else {
					mainValues.setMainValue(CommandValue.OFF.toString());
				}
			} else if (generalDeviceType.toString().contains("BULB")) {
				ZclCluster zcl = dev.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_ON_OFF);
				ZclAttribute status = zcl.getAttribute(new DoubleByte(0));
				if (status.getConvertedValue().toString().equalsIgnoreCase("false")) {
					mainValues.setMainValue(DeviceStatus.OFF.toString());
				} else {
					mainValues.setMainValue(DeviceStatus.ON.toString());
				}

			}
		} catch (Exception e) {
			mainValues.setMainValue("");
		}
		return mainValues;
	}

	private boolean isMeazonDevice(ZclDevice devOnMemory) {
		if (devOnMemory == null) {
			return false;
		}
		ZclCluster basicCluster = devOnMemory.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC);
		if (basicCluster == null) {
			return false;
		}
		ZclAttribute manufacturerNameAt = basicCluster.getAttribute(new DoubleByte(0x0004));
		if (manufacturerNameAt == null) {
			return false;
		}
		String manufacturerName = (String) manufacturerNameAt.getConvertedValue();
		if (manufacturerName.toLowerCase().startsWith("meazon")) {
			return true;
		}
		return false;
	}

	public ZbCoordinatorJson buildZbCoordinatorJson() {
		ZbCoordinatorJson coord = new ZbCoordinatorJson();
		coord.setDevType(DeviceType.COORDINATOR.toString());
		coord.setManufacturer("WATERGYLAB");
		String channel = getChannel(false);
		String fwversion = getFwVersion(false);
		String mac = getZbMac(false);
		if (channel == null) {
			channel = getChannel(true);
			LOG.info("Canal: {}", channel);
		}
		if (channel != null) {
			coord.setChannel(channel);
		}
		if (fwversion != null) {
			coord.setFwVersion(fwversion);
		}
		if (mac != null) {
			coord.setMac(mac);
		}
		return coord;

	}

	public String getZbMac(boolean sleep) {
		try {
			OctaByte ieeeAddr = this.serialConnector.getZclcoor().getIeeeAddress();
			if (ieeeAddr == null) {
				this.serialConnector.getZclcoor().sendIEEE_ADDRESS_REQ(new DoubleByte(0));
				if (sleep) {
					Thread.sleep(3000);
				}
			}
			byte[] address = this.serialConnector.getZclcoor().getIeeeAddress().getAddress();
			String ieeeAddressStr = "";
			for (byte value : address) {
				ieeeAddressStr += String.format("%02X", value).toUpperCase();
			}
			return ieeeAddressStr;
		} catch (Exception e) {
			return null;
		}

	}

	public String getChannel(boolean sleep) {
		this.serialConnector.getZclcoor().getChannel();
		if (sleep) {
			waitMs(4000);
		}
		Byte channel = this.serialConnector.getZclcoor().getAndResetZbchannel();
		return "0x" + String.format("%02X", channel);
	}

	public String getFwVersion(boolean sleepandRead) {
		try {
			String fwVersionStr = "";
			ZclCoordinator coordinator = serialConnector.getZclcoor();
			ZclCluster basic = coordinator.getZclCluster(new DoubleByte(0));
			if (basic != null) {
				fwVersionStr = coordinator.getZigbeeChipVersion(sleepandRead);
				if (fwVersionStr == null || fwVersionStr.equals("")) {
					return "";
				}
				return fwVersionStr;
			}
			return fwVersionStr;
		} catch (Exception e) {
			return "";

		}

	}

	public ZclCluster findClusterOnDevice(String mac, DoubleByte clusterId) {
		OctaByte macOb = new OctaByte(OctaByte.convertMac(mac));
		ZclDevice zdev = deviceManager.getDeviceByMac(macOb);
		if (zdev == null) {
			return null;
		}

		ZclCluster cluster = zdev.getZclCluster(clusterId);
		if (cluster == null) {
		}
		return cluster;
	}

	public String serializer(byte[] data) {
		String str = "";
		boolean white = true;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0 && white && i < data.length - 1) {
				str += "  ";
			} else {
				str += String.format("%02X", data[i]).toUpperCase();
				white = false;
			}
			if (i < data.length - 1) {
				str += " ";
			}
		}
		return str;
	}

	// public boolean deleteDevice(String mac) {
	// ZclDevice zdev = deviceManager.getDeviceByMac(mac);
	// ZclCoordinator coor = serialConnector.getZclcoor();
	// boolean result = false;
	// if (zdev != null && !zdev.getShortAddress().equals(
	// ZigbeeConstants.COORDINATOR_SHORT_ADDRESS)) {
	// coor.leaveDevice(zdev.getIeeeAddress().toString(), false);
	// deviceManager.deleteDevice(zdev);
	// result=true;
	// }
	//
	// return result;
	// }
	//
	public boolean deleteDevice(String mac) throws GenericZigbeeException {
		boolean result = false;
		ZclDevice zdev = deviceManager.getDeviceByMac(mac);
		if (zdev == null) {
			return false;
		}
		zdev.setPending2Remove(true);
		ZclCoordinator coor = serialConnector.getZclcoor();
		if (zdev != null && !zdev.getShortAddress().equals(ZigbeeConstants.COORDINATOR_SHORT_ADDRESS)) {
			if (coor.leaveDevice(zdev.getIeeeAddress().toString(), false)) {
				if (wait_leave_process(8000, mac)) {
					LOG.info("Parent device found and deleting child.");
					// Padre encontrado, leave enviado y el dispositivo se entera.
					return true;
				} else {
					LOG.warn("Parent device found but no response received.");
					// Padre encontrado, leave enviado y no hay respuesta
					Alert alert;
					alert = new Alert(State.WARNING, AlertType.DEVICE_REMOVED.toString(), mac,
							zdev.getGeneralDeviceType().toString());
					ZigbeeService.getAlertList().add(alert);
					ZigbeeService.checkAlertList();
					deviceManager.deleteDevice(zdev);
					throw new GenericZigbeeException(ExceptionCause.PARENT_FOUND_NORESPONSE);
				}
			} else {
				if (wait_leave_process(8000, mac)) {
					LOG.info("Parent device found. Leave sent to device and It responded");
					// padre no encontrado. Leave enviado al device y el device se enteró y se
					// eliminó
					return true;

				} else {
					// padre no encontrado. Leave enviado al device y el device no se eliminó
					LOG.warn("Parent device found. Leave sent to device and no response received");
					Alert alert;
					alert = new Alert(State.WARNING, AlertType.DEVICE_REMOVED.toString(), mac,
							zdev.getGeneralDeviceType().toString());
					ZigbeeService.getAlertList().add(alert);
					ZigbeeService.checkAlertList();
					deviceManager.deleteDevice(zdev);
					throw new GenericZigbeeException(ExceptionCause.PARENT_NOT_FOUND);

				}
				// LOG.info("Parent device couldn't be found. Deleting from memory.");
				// return false;
			}

			// ZigbeeService.getAlertList().add(alert);
			// ZigbeeService.checkAlertList();
			// deviceManager.devicesLeaving.put(zdev.getIeeeAddress(), zdev);
			// deviceManager.deleteDevice(zdev);
		}
		return result;
	}

	public void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: " + e);
			Thread.currentThread().interrupt();
		}
	}

	public void wait_zb_process(int timeout, ZclCoordinator coor) throws InterruptedException {
		int counter = 0;
		while (counter < timeout) {
			waitMs(100);
			counter += 100;
			if (coor.getActivenet()) {
				counter = timeout;
			}
		}
	}

	private void wait_conf_process(int timeout, ZFrame frame) {
		int counter = 0;
		while (counter < timeout) {
			waitMs(100);
			counter += 100;
			String reqmade = RequestMaps.getAfRequestId(frame);
			if (reqmade == null) {
				counter = timeout;
			}
		}

	}

	private boolean wait_leave_process(int timeout, String mac) {
		int counter = 0;
		while (counter < timeout) {
			waitMs(200);
			counter += 200;
			ZclDevice dev = deviceManager.getDeviceByMac(mac);
			if (dev == null) {
				return true;
			}
		}
		return false;

	}
	
	public boolean checkLaunchOTA(int timeout) {
		int counter=0;
		while (counter < timeout) {
			waitMs(100);
			counter += 100;
			if (ZigbeeService.otaLaunched) {
				counter = timeout;
				ZigbeeService.otaLaunched=false;
				return true;
			}
		}
		return false;
	}

	public void toggleDevice(String devmac) {
		ZclCluster onOff = findClusterOnDevice(devmac, ZClusterLibrary.ZCL_CLUSTER_ID_GEN_ON_OFF);
		if (onOff != null) {
			Alert alert = new Alert(State.INFO, AlertType.TOGGLE_SUCCESS.toString(), devmac,
					deviceManager.getDeviceByMac(devmac).getGeneralDeviceType().toString());
			ZigbeeService.getAlertList().add(alert);
			ZigbeeService.checkAlertList();
			// TODO hacer el caso en el que el dispositivo no responda al toggle
			ZFrame zf = onOff.buildCmd((byte) 0x2, null);
			this.serialConnector.getOutputSerial().writeZFrame(zf);
		}

	}

	public boolean setMode(String devmac, String mode) throws GenericZigbeeException {
		ZclDevice zdev = deviceManager.getDeviceByMac(devmac);
		boolean success = false;
		if (zdev == null) {
			throw new GenericZigbeeException("Device not found");
		}
		ZclCluster thermostat = zdev.getZclCluster(new DoubleByte(0x201));
		ZFrame frame = null;
		ZclAttribute att = thermostat.getAttribute(ZClusterLibrary.ZCL_ATT_SYSTEM_MODE);
		try {
			CommandValue commandMode;
			commandMode = readThermostatMode(att, thermostat, devmac);
			if ((commandMode != null) && (commandMode.equals(CommandValue.fromValue(mode)))) {
				success = true;
				LOG.info("El dispositivo ya se encuentra en el modo correcto");
			} else {
				switch (CommandValue.fromValue(mode)) {
				case COOL:
					frame = thermostat.buildWriteAttributes(att.getId(), "3");
					break;
				case HEAT:
					frame = thermostat.buildWriteAttributes(att.getId(), "4");
					break;
				case OFF:
					frame = thermostat.buildWriteAttributes(att.getId(), "0");
					break;
				default:
					throw new GenericZigbeeException("Incorrect option selected");
				}
				if (frame != null) {
					RequestMaps.addAfRequestId((ZFrame) frame, devmac + String.valueOf(att.getSequenceNumber()));
					serialConnector.getOutputSerial().writeZFrame(frame);
					wait_conf_process(TIMEOUT_SET, frame);
					String reqmade = RequestMaps.getAfRequestId(frame);
					// comprobamos que se haya escrito bien
					if (reqmade == null) {
						LOG.info("Escritura con exito de modo: {}", mode);
						success = true;
					} else {
						att = thermostat.getAttribute(ZClusterLibrary.ZCL_ATT_SYSTEM_MODE);
						String moderead = (String) att.getConvertedValue();
						if (moderead.equalsIgnoreCase(mode)) {
							LOG.info("The set and reported mode are the same (success)");
							success = true;
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw e;
		}
		Alert alert = null;
		if (success) {
			alert = new Alert(State.INFO, AlertType.SETMODE_SUCCESS.toString(), devmac,
					zdev.getGeneralDeviceType().toString());
		} else {
			alert = new Alert(State.ERROR, AlertType.SETMODE_ERROR.toString(), devmac,
					zdev.getGeneralDeviceType().toString());
		}
		ZigbeeService.getAlertList().add(alert);
		ZigbeeService.checkAlertList();
		return success;
	}

	public boolean activenet() throws InterruptedException {
		ZclCoordinator coor = this.serialConnector.getZclcoor();
		coor.setActivenet(false);
		ZdoMgmtPermitJoinRequest permitJoinCo = new ZdoMgmtPermitJoinRequest(new DoubleByte(0),
				(byte) ZigbeeConstants.ZIGBEE_ACTIVENET_SEC);
		ZdoMgmtPermitJoinRequest permitJoinBc = new ZdoMgmtPermitJoinRequest(new DoubleByte(0xFFFC),
				(byte) ZigbeeConstants.ZIGBEE_ACTIVENET_SEC);
		this.serialConnector.getOutputSerial().writeZFrame(permitJoinCo);
		try {
			waitMs(1000);
			this.serialConnector.getOutputSerial().writeZFrames(new ZFrame[] { permitJoinCo, permitJoinBc });
			wait_zb_process(5000, coor);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: {} ", e);
			Thread.currentThread().interrupt();
		}
		if (coor.getActivenet()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean setSetPoint(String devmac, String temp) {
		ZFrame frame;
		try {
			ZclCluster thermostat = deviceManager.findClusterOnDevice(devmac,
					ZClusterLibrary.ZCL_CLUSTER_ID_HAVC_THERMOSTAT);
			ZclAttribute att = thermostat.getAttribute(new DoubleByte(0x001c));

			if (thermostat != null) {
				ZclDevice zdev = deviceManager.getDeviceByMac(devmac);
				CommandValue mode;
				mode = readThermostatMode(att, thermostat, devmac);
				if (mode != null && !mode.equals(CommandValue.OFF)) {
					switch (mode) {
					case HEAT:
						frame = thermostat.buildWriteAttributes(new DoubleByte(0x12), temp);
						RequestMaps.addAfRequestId((ZFrame) frame, String.valueOf(att.getSequenceNumber()));
						// RequestMaps.addAfRequestId((ZFrame) frame, devmac +
						// temp);
						serialConnector.getOutputSerial().writeZFrame(frame);
						LOG.info("Cambiando el setpoint de heat");
						return readSetpoint(new DoubleByte(0x12), thermostat, temp, zdev, frame);
					case COOL:
						frame = thermostat.buildWriteAttributes(new DoubleByte(0x11), temp);
						RequestMaps.addAfRequestId((ZFrame) frame, String.valueOf(att.getSequenceNumber()));
						// RequestMaps.addAfRequestId((ZFrame) frame, devmac +
						// temp);
						serialConnector.getOutputSerial().writeZFrame(frame);
						LOG.info("Cambiando setpoint de cool");
						return readSetpoint(new DoubleByte(0x11), thermostat, temp, zdev, frame);
					default:
						return false;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean readSetpoint(DoubleByte attid, ZclCluster thermostat, String temp, ZclDevice zdev, ZFrame frame) {
		boolean success = false;
		Integer temperature = Integer.valueOf(temp);
		ZclAttribute att = thermostat.getAttribute(attid);
		// ArrayList<DoubleByte> attributes = new ArrayList<>();
		// attributes.add(att.getId());
		// ZFrame[] frames = thermostat.buildReadAttributes(attributes);
		// serialConnector.getOutputSerial().writeZFrame(frames[0]);
		// RequestMaps.addAfRequestId((ZFrame) frames[0],
		// String.valueOf(att.getSequenceNumber()));
		wait_conf_process(TIMEOUT_SET, frame);
		String reqmade = RequestMaps.getAfRequestId(frame);
		if (reqmade == null) {
			LOG.info("The device replied with write att rsp (success)");
			success = true;
		} else {
			long setpointread = (long) att.getConvertedValue();
			if (setpointread == temperature) {
				LOG.info("The set and reported temperature are the same (success)");
				success = true;
			}
		}
		Alert alert = null;
		if (success) {
			alert = new Alert(State.INFO, AlertType.SETMODE_SUCCESS.toString(), zdev.getIeeeAddress().toString(),
					zdev.getGeneralDeviceType().toString());
		} else {
			alert = new Alert(State.ERROR, AlertType.SETPOINT_ERROR.toString(), zdev.getIeeeAddress().toString(),
					zdev.getGeneralDeviceType().toString());
		}
		ZigbeeService.getAlertList().add(alert);
		ZigbeeService.checkAlertList();
		return success;

	}

	private CommandValue readThermostatMode(ZclAttribute att, ZclCluster thermostat, String devmac) {
		// ArrayList<DoubleByte> attributes = new ArrayList<>();
		// attributes.add(att.getId());
		// ZFrame[] frames = thermostat.buildReadAttributes(attributes);
		// RequestMaps.addAfRequestId((ZFrame) frames[0], devmac +
		// String.valueOf(att.getSequenceNumber()));
		// serialConnector.getOutputSerial().writeZFrame(frames[0]);
		// wait_conf_process(6000, frames[0]);
		// String reqmade = RequestMaps.getAfRequestId(frames[0]);
		// if (reqmade == null) {
		try {
			String mode = (String) att.getConvertedValue();
			if (mode.equalsIgnoreCase(CommandValue.COOL.toString())) {
				return CommandValue.COOL;

			} else if (mode.equalsIgnoreCase(CommandValue.HEAT.toString())) {
				return CommandValue.HEAT;

			} else if (mode.equalsIgnoreCase(CommandValue.OFF.toString())) {
				return CommandValue.OFF;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		// }
		// return null;

	}

	public ReturningValues sendCommand(CommandWeb command, String... args) throws GenericZigbeeException {
		ReturningValues response = null;

		switch (command) {
		case CONFIGREPORT:
			String mac = args[0];
			response = configReport(mac, args);
			LOG.info("Enviado conf report");
			break;
		case GETDATA:

			break;
		case SHOWNETWORK:

			break;

		default:
			break;
		}
		return response;

	}

	public ReturningValues configReport(String mac, String... optionalParameters) throws GenericZigbeeException {
		ZclDevice zdev = deviceManager.getDeviceByMac(new OctaByte(OctaByte.convertMac(mac)));
		if (zdev == null) {
			throw new GenericZigbeeException("The device could not be found");
		}
		if (optionalParameters.length == 5 && optionalParameters[0] != null && optionalParameters[1] != null
				&& optionalParameters[2] != null && optionalParameters[3] != null) {
			int minreport = Integer.parseInt(optionalParameters[3]);
			int maxreport = Integer.parseInt(optionalParameters[4]);

			String cluster = optionalParameters[1];
			ZclCluster zcluster = null;
			if (!BuildMqttMsg.isNumeric(cluster)) {
				zcluster = zdev.getZclCluster(cluster);
			} else {
				if (!cluster.startsWith("0x")) {
					cluster = "0x" + cluster;
				}
				DoubleByte clusterid = new DoubleByte(Integer.decode(cluster));
				zcluster = zdev.getZclClusterConfigured(clusterid);
			}
			if (zcluster == null) {
				throw new GenericZigbeeException("Cluster not found in device");
			} else {
				String att = optionalParameters[2];
				ZclAttribute zatt = null;
				if (!BuildMqttMsg.isNumeric(att)) {
					zatt = zcluster.getAttribute(att);
				} else {
					if (!att.startsWith("0x")) {
						att = "0x" + att;
					}
					zatt = zcluster.getAttribute(new DoubleByte(Integer.decode(att.trim())));
				}
				if (zatt == null) {
					throw new GenericZigbeeException("Attribute not found on device");
				}
				if (zatt.isUnsupported()) {
					throw new GenericZigbeeException("Attribute not supported in the device");
				} else if (minreport > maxreport) {
					throw new GenericZigbeeException("The maxTime must be greater than minTime");
				} else {
					zatt.setMaxReportingTime(new DoubleByte(maxreport));
					zatt.setMinReportingTime(new DoubleByte(minreport));
					deviceManager.flushDevice(zdev);
					ZFrame frame = zcluster.buildConfigReport(zatt.getId(), null);

					// cambios nuevos
					RequestMaps.addAfRequestId((ZFrame) frame, zdev.getIeeeAddress().toString());
					serialConnector.getOutputSerial().writeZFrame(frame);
					wait_conf_process(3000, frame);
					String reqmade = RequestMaps.getAfRequestId(frame);

					if (reqmade != null) {
						throw new GenericZigbeeException("Device did not respone to the zbconfigreport");
					}
					return null;
				}
			}

		} else {
			try {
				ZclDevice device = deviceManager.getDeviceByMac(new OctaByte(OctaByte.convertMac(mac)));
				ArrayList<String> clNames = new ArrayList<>();
				for (ZclEndpoint ep : device.getEndpoints().values()) {
					for (ZclCluster cl : ep.getClusters().values()) {
						if (cl.isConfigured()) {
							if (cl.isBindable()) {
								clNames.add(cl.getName());
								ZFrame[] frames = cl
										.configureAttributes(this.serialConnector.getZclcoor().getIeeeAddress(), null);
								for (int i = 0; i < frames.length; i++) {
									this.serialConnector.getOutputSerial().writeZFrame(frames[i]);
								}
							}
						}
					}
				}
				List<ZbConfigReportJson> clusters = new ArrayList<>();
				for (String cl : clNames) {
					ZbConfigReportJson clusterconfigured = new ZbConfigReportJson();
					clusterconfigured.setCluster(cl);
					clusters.add(clusterconfigured);
				}
				ReturningValues clustersjson = new ReturningValues();
				clustersjson.setConfigReport(clusters);
				return clustersjson;

			} catch (UnknownCoordinatorMacException e) {
				throw new GenericZigbeeException("UnknownCoordinatorMac");
			}

		}

	}

	public boolean meterOnOff(String mac, String status) throws GenericZigbeeException {
		ZclDevice zdev = deviceManager.getDeviceByMac(mac);
		try {
			CommandValue.fromValue(status);
			ZclCluster onOff = findClusterOnDevice(mac, ZClusterLibrary.ZCL_CLUSTER_ID_GEN_ON_OFF);
			if (zdev == null) {
				throw new GenericZigbeeException("Device not found");
			}
			if (onOff == null) {
				throw new GenericZigbeeException("Cluster onoff not found on device");
			}
			return checkIfOnOffSuccess(zdev, onOff, CommandValue.fromValue(status));
		} catch (IllegalArgumentException e) {
			throw new GenericZigbeeException("Incorrect option");
		}

	}

	private boolean checkIfOnOffSuccess(ZclDevice zdev, ZclCluster onOff, CommandValue status) {

		Byte command = null;
		Boolean attValue = null;
		switch (status) {
		case ON:
			command = (byte) 0x01;
			attValue = true;
			break;
		case OFF:
			command = (byte) 0x00;
			attValue = false;
			break;
		case TOGGLE:
			command = (byte) 0x02;
			break;
		default:
			break;
		}
		if (command != null) {
			ZFrame zf = onOff.buildCmd(command, null);
			RequestMaps.addAfRequestId((ZFrame) zf, zdev.getIeeeAddress().toString() + status.toString());
			serialConnector.getOutputSerial().writeZFrame(zf);
			wait_conf_process(3000, zf);
			String reqmade = RequestMaps.getAfRequestId(zf);

			if (reqmade == null) {
				LOG.info("El dispositivo ha respondido a un {} (escritura)", status);
				// comprobamos si status es realmente un on
				ZclAttribute att = onOff.getAttribute(new DoubleByte(0x0000));
				ArrayList<DoubleByte> attributes = new ArrayList<>();
				attributes.add(att.getId());
				ZFrame[] frames = onOff.buildReadAttributes(attributes);
				RequestMaps.addAfRequestId((ZFrame) frames[0], zdev.getIeeeAddress().toString() + status.toString());
				serialConnector.getOutputSerial().writeZFrame(frames[0]);
				wait_conf_process(3000, frames[0]);
				reqmade = RequestMaps.getAfRequestId(frames[0]);
				if (reqmade == null) {
					Boolean value = (Boolean) att.getConvertedValue();
					if (attValue != null && attValue.equals(value)) {
						LOG.info("El dispositivo ha cambiado correctamente(lectura)");
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;

	}

	public void identifyDevice(String mac, String time) throws GenericZigbeeException {
		ZclDevice zcldevice = deviceManager.getDeviceByMac(mac);

		if (zcldevice == null) {
			throw new GenericZigbeeException("Device not found");
		}
		ZclCluster identify = findClusterOnDevice(mac, ZClusterLibrary.ZCL_CLUSTER_ID_GEN_IDENTIFY);

		if (identify == null) {
			throw new GenericZigbeeException("Cluster not found in device");

		}
		Integer seconds = Integer.valueOf(time);
		if (seconds > 15) {
			throw new GenericZigbeeException("15 seconds is the maximun value");
		}
		long[] params = { seconds };
		ZFrame zf = identify.buildCmd((byte) 0x0, params);
		this.serialConnector.getOutputSerial().writeZFrame(zf);

	}

	public List<String> buildZbClustersJson(String mac, boolean onlyreportables) {
		ArrayList<String> clusters = new ArrayList<>();
		OctaByte macOb = new OctaByte(OctaByte.convertMac((String) mac));
		ZclDevice zdev = this.deviceManager.getDeviceByMac(macOb);
		if (zdev != null) {
			for (ZclEndpoint zep : this.deviceManager.getDeviceByMac(macOb).getEndpoints().values()) {
				for (ZclCluster zcl : zep.getClusters().values()) {
					this.checkIfClusterExist(zcl.getName(), clusters, onlyreportables, zcl);
				}
			}
			return clusters;
		}
		return null;
	}

	public AttributesJson buildThermostatValues(String mac) {
		AttributesJson thermostatValue = new AttributesJson();
		OctaByte macOb = new OctaByte(OctaByte.convertMac((String) mac));
		ZclDevice zdev = this.deviceManager.getDeviceByMac(macOb);
		if (zdev != null) {
			ZclCluster zclthermostat = zdev.getZclClusterConfigured(ZClusterLibrary.ZCL_CLUSTER_ID_HAVC_THERMOSTAT);
			if (zclthermostat != null) {
				ZclAttribute mode = zclthermostat.getAttribute(new DoubleByte(0x001c));
				ZclAttribute localtemperature = zclthermostat.getAttribute(new DoubleByte(0x0000));
				ZclAttribute oheatsetpoint = zclthermostat.getAttribute(new DoubleByte(0x0012));
				ZclAttribute ocoolsetpoint = zclthermostat.getAttribute(new DoubleByte(0x0011));
				double temp = BuildMqttMsg.getConversionFactorYRoundNumber(localtemperature, 2, zdev);
				double heatsetpoint = BuildMqttMsg.getConversionFactorYRoundNumber(oheatsetpoint, 2, zdev);
				double coolsetpoint = BuildMqttMsg.getConversionFactorYRoundNumber(ocoolsetpoint, 2, zdev);
				// double temp =
				// Double.parseDouble(localtemperature.getConvertedValue().toString());
				// double heatsetpoint =
				// Double.parseDouble(oheatsetpoint.getConvertedValue().toString());
				// double coolsetpoint =
				// Double.parseDouble(ocoolsetpoint.getConvertedValue().toString());
				// ZclAttribute localtemperature=
				if (mode != null) {
					String modestr = (String) mode.getConvertedValue();
					thermostatValue.setThermostatMode(modestr);

					if (ocoolsetpoint != null && oheatsetpoint != null) {
						if (modestr.equalsIgnoreCase(CommandValue.COOL.toString())) {
							String ocoolsetpointstr = String.format("%.2f", coolsetpoint) + "ºC";
							// String ocoolsetpointstr = ocoolsetpoint.getConvertedValue().toString();
							thermostatValue.setOCoolSetpoint(ocoolsetpointstr);
						} else if (modestr.equalsIgnoreCase(CommandValue.HEAT.toString())) {
							String oheatsetpointstr = String.format("%.2f", heatsetpoint) + "ºC";
							// String oheatsetpointstr = oheatsetpoint.getConvertedValue().toString();
							thermostatValue.setOHeatSetpoint(oheatsetpointstr);
						}

					}
				}
				if (localtemperature != null) {
					thermostatValue.setLocalTemperature(String.format("%.2f", temp) + "ºC");
					// thermostatValue.setLocalTemperature(localtemperature.getConvertedValue().toString());
				}
				return thermostatValue;
			}
		}
		return null;

	}

	private void checkIfClusterExist(String name, List<String> clusters, boolean onlyreportables, ZclCluster zcl) {
		for (String nombre : clusters) {
			if (!nombre.equalsIgnoreCase(name.toLowerCase()))
				continue;
			return;
		}
		if (onlyreportables) {
			if (checkIfHasAttReportables(zcl)) {
				clusters.add(name);
			}
		} else {
			clusters.add(name);
		}
	}

	private boolean checkIfClusterExist(String name, ZbDevicesGraph devicegraph) {
		List<ZbClusterGraph> clustersgraph = devicegraph.getClusters();
		for (ZbClusterGraph clustergraph : clustersgraph) {
			if (clustergraph.getName().equalsIgnoreCase(name)) {
				return true;
			}

		}
		return false;

	}

	private boolean checkIfHasAttReportables(ZclCluster zcl) {
		for (ZclAttribute zatt : zcl.getAttributes().values()) {
			if (!zatt.isUnsupported() && zatt.getMinReportingTime(null) != null) {
				return true;
			}
		}
		return false;
	}

	public List<String> buildAttributesJson(String mac, String cluster) {
		ZclDevice zdev = this.deviceManager.getDeviceByMac(mac);
		List<String> atts = new ArrayList<String>();
		if (zdev != null) {
			ZclCluster zcl = zdev.getZclCluster(cluster);
			if (zcl != null) {
				for (ZclAttribute zatt : zcl.getAttributes().values()) {
					if (!zatt.isUnsupported() && zatt.getMinReportingTime(null) != null) {
						atts.add(zatt.getName());
					}
				}
				return atts;
			}
		}
		return null;
	}

	public List<ZbDevicesGraph> buildZbdevicesGraph() {
		ArrayList<ZbDevicesGraph> devicesGraphinfo = new ArrayList<ZbDevicesGraph>();
		synchronized (deviceManager.devices) {
			for (Entry<DoubleByte, ZclDevice> dev : deviceManager.devices.entrySet()) {
				ZbDevicesGraph zbDeviceGraph = new ZbDevicesGraph();
				ZclDevice device = dev.getValue();
				zbDeviceGraph.setMac(device.getIeeeAddress().toString());

				for (ZclEndpoint zep : device.getEndpoints().values()) {
					for (ZclCluster zcl : zep.getClusters().values()) {
						if (!checkIfClusterExist(zcl.getName(), zbDeviceGraph)) {
							ZbClusterGraph clustersGraph = new ZbClusterGraph();
							clustersGraph.setName(zcl.getName());
							for (ZclAttribute zatt : zcl.getAttributes().values()) {
								if (!zatt.isUnsupported()) {
									clustersGraph.getAttributes().add(zatt.getName());
								}
							}
							if (zcl.getName().equalsIgnoreCase("IAS Zone")) {
								clustersGraph.getAttributes().add(IASZoneClient.IAS_ZONE_BATT);
							}
							zbDeviceGraph.getClusters().add(clustersGraph);
						}
					}
				}
				devicesGraphinfo.add(zbDeviceGraph);
			}
		}
		return devicesGraphinfo;
	}

	public final String getTimeSystemTimeZone(String fecha) {
		String resp = "";
		// 2018-11-08T00:00:00Z
		try {
			Date d = null;
			// DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			DateFormat formaters = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			DateFormat formatterms = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			formatterms.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				d = formatterms.parse(fecha);
			} catch (ParseException e) {
				try {
					d = formaters.parse(fecha);
				} catch (ParseException j) {
					LOG.error("Error parsin date");
				}
			}
			DateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			formatter2.setTimeZone(TimeZone.getDefault());
			if (d != null) {
				resp = formatter2.format(d.getTime());
			}
		} catch (Exception e) {
			LOG.error("The date could not be formated");
		}
		return resp;
	}

	public static List<Alert> getAlertList() {
		return alertList;
	}

	public static void checkAlertList() {
		if (alertList.size() > 10) {
			for (int i = 10; i < alertList.size(); i++) {
				alertList.remove(i);
			}
		}
	}

	public static Date getTimeStarted() {
		return timeStarted;
	}

	public void pollControlManagement(String mac, CommandType command, DoubleByte id, String interval,long [] params) {
		ZclDevice zdev = this.deviceManager.getDeviceByMac(mac);

		if (zdev != null) {
			ZclCluster pollcontrol = zdev.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_POLL_CONTROL);
			if (pollcontrol != null) {
				if (command == CommandType.READ) {
					ArrayList<DoubleByte> attributes = new ArrayList<>();
					attributes.add(id);
					ZFrame[] frames = pollcontrol.buildReadAttributes(attributes);
					this.serialConnector.getOutputSerial().writeZFrames(frames);
				} else if (command == CommandType.WRITE) {
					ZFrame frame = pollcontrol.buildWriteAttributes(id, interval);// 60s
					this.serialConnector.getOutputSerial().writeZFrame(frame);
					
				} else if(command == CommandType.POLLCOMMAND) {
					ZFrame zf = pollcontrol.buildCmd(Byte.decode(id.toString()), params);
					this.serialConnector.getOutputSerial().writeZFrame(zf);
					
				}

			}
		}
	}

}
