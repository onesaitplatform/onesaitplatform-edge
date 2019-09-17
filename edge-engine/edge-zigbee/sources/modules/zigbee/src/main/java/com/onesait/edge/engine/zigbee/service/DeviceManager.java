package com.onesait.edge.engine.zigbee.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onesait.edge.engine.zigbee.json.Attribute;
import com.onesait.edge.engine.zigbee.json.Cluster;
import com.onesait.edge.engine.zigbee.json.Device;
import com.onesait.edge.engine.zigbee.json.Endpoint;
import com.onesait.edge.engine.zigbee.jsoncontroller.ClustersInfoJson;
import com.onesait.edge.engine.zigbee.model.IasZoneDev;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.model.ZclEndpoint;
import com.onesait.edge.engine.zigbee.types.DeviceType;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

@Service
public class DeviceManager {

	public static final int IGNORE_TIME_AFTER_DEVICE_LEAVES_MS = 10000;
	private static final int REDISCOVERY_MAX_TIME_MS = 60_000 * 2;
	private static final Logger LOG = LoggerFactory.getLogger(DeviceManager.class);
	public final Map<DoubleByte, ZclDevice> devices = new ConcurrentHashMap<>();
	public final Map<DoubleByte, Date> deletedDevices = new ConcurrentHashMap<>();
	public final Map<OctaByte,ZclDevice> devicesLeaving= new ConcurrentHashMap<>();  //utilizado para mandar por mqtt el evento
	private final Map<DoubleByte, Date> rediscoveryDevices = new ConcurrentHashMap<>(20);
	public final Map<String, IasZoneDev> iasdevices = new ConcurrentHashMap<>();
	public static final String MOTION_SENSOR = "Motion sensor";
	public static final String DOOR_SENSOR = "Contact switch";
	public static final String WATER_SENSOR = "Water sensor";
	public static final String SMOKE_SENSOR = "Fire sensor";
	public static final String ALARM_SENSOR = "Standard Warning Device";
	@Autowired 
	private DeviceStorage deviceStorage;
	@Autowired 
	private ZclService zclservice;

	public ZclDevice getDeviceByMac(String mac) {
		OctaByte ieee = new OctaByte(OctaByte.convertMac(mac));
		if (ieee.equals(ZigbeeConstants.INVALID_IEEE_ADDRESS))
			return null;
		for (ZclDevice zdev : devices.values()) {
			if (zdev.getIeeeAddress().equals(ieee)) {
				return zdev;
			}
		}
		return null;
	}
	
	public ZclDevice getDeviceByMac(OctaByte mac) {
		if (mac.equals(ZigbeeConstants.INVALID_IEEE_ADDRESS))
			return null;
		for (ZclDevice zdev : devices.values()) {
			if (zdev.getIeeeAddress().equals(mac)) {
				return zdev;
			}
		}
		return null;
	}
	
	public ZclDevice getDeviceByNwkAddress(DoubleByte nwkAddress) {

		for (ZclDevice zdev : devices.values()) {
			if (zdev.getShortAddress().equals(nwkAddress)) {
				return zdev;
			}
		}
		return null;
	}
	
	@PostConstruct
	public int loadDevicesFromDisk() {
		LOG.info("Device storage {}" ,deviceStorage);
		deviceStorage.init();
		return loadNewDevicesFromDisk();

	}
	
	private int loadNewDevicesFromDisk() {
		Long[] permanentShortAddresses = deviceStorage.getAllShortAddresses();
		for (Long saLong : permanentShortAddresses) {
			
			DoubleByte shortAddress = new DoubleByte(saLong);
			if (devices.containsKey(shortAddress)) {
				continue;
			}
			Device dev = deviceStorage.getDevice(saLong);
			if (dev == null) {
				continue;
			}
			Long la = dev.getIeeeaddress();
			if (la == null) {
				continue;
			}
			OctaByte ieeeAddress = new OctaByte(la);
			ZclDevice zcldev = new ZclDevice(ieeeAddress, shortAddress);
			
			Long mc = dev.getManufacturercode();
			if (mc != null) {
				zcldev.setManufacturerCode(new DoubleByte(mc));
			}
			
			reconstructZclDevice(zcldev, dev);
			
			LOG.info("Device {} recovered from permanent data.",ieeeAddress);
			synchronized(devices) {
				devices.put(shortAddress, zcldev);
				saveDeviceTypeByDeviceId(ieeeAddress.toString());
				
				
				
			}
		}
		return devices.size();
	}

	
	private void reconstructZclDevice(ZclDevice zdev, Device dev) {
		if (zdev == null || dev == null) return;
		for (Endpoint ep : dev.getEndpoints()) {
			ZclEndpoint zep = new ZclEndpoint((byte)(ep.getId() & 0xFF));
			if (ep.getDeviceid() != null) {
				zep.setDeviceId(new DoubleByte(ep.getDeviceid()));
			}
			if (ep.getProfile() != null) {
				zep.setProfile(new DoubleByte(ep.getProfile()));
			}
			for (Cluster cl : ep.getClusters()) {
				ZclCluster zcl = (ZclCluster) zclservice.getZcl().getClusters().get(
						new DoubleByte(cl.getId())).clone();
				zcl.setDevice(zdev);
				zcl.setConfigured(cl.isConfigured());
				for (ZclAttribute zat : zcl.getAttributes().values()) {
					//at son los atributos del json
					Attribute at = cl.getAttribute(zat.getId().longValue());
					zat.setUnsupported(at == null);
					if (at != null && at.getValue() != null) {
						if (at.isConfigured()) {
							List<Byte> bytes = at.getValue();
							byte[] primBytes = new byte[bytes.size()];
							for (int i = 0; i < primBytes.length; i++) {
								primBytes[i] = bytes.get(i);
							}
							//ahumanes comprobamos el reporte
							checkTimes2Report(at,zat,zdev);
							//
							zat.updateValue(primBytes,false,false);
							zat.setRspReceived(true);
							
						}
						// Por si ha cambiado el tipo... Meazon...
						zat.setZclDatatype(zclservice.getZcl().getDatatypes().get((byte)(at.getDatatype() & 0xFF)));
					}
				}
				zep.putCluster(zcl);
			}
			zdev.putZclEndpoint(zep);
		}
		zdev.setCapabilities((byte)(dev.getCapabilities() & 0xFF));
		zdev.loadManufacturerName();
	}
	
	private static void checkTimes2Report(Attribute at,ZclAttribute zclat,ZclDevice zcldev) {
		String minTime2Report=null;
		String maxTime2Report=null;
		try{
			minTime2Report=at.getMinReportingTime();
			maxTime2Report=at.getMaxReportingTime();

		if(minTime2Report!=null && !minTime2Report.equals("") && maxTime2Report!=null && !maxTime2Report.equals("")){
			//setMinReportingTime
			zclat.setMinReportingTime(new DoubleByte(minTime2Report));
			zclat.setMaxReportingTime(new DoubleByte(maxTime2Report));
		}
		}catch(Exception e){
		}
	}

	/**
	 * Gets a stored device and changes its short address in device and in hashmap key.
	 * Serialize devices hashmap. WARNING: synchronized with ZclXmlLauncher.devices
	 * @param zdev Device whose SA must be changed
	 * @param newSa DoubleByte of the new SA
	 */
	public void changeDeviceShortAddress(ZclDevice zdev, DoubleByte newSa) {
		DoubleByte oldSa = new DoubleByte(zdev.getShortAddress().intValue());
		if (oldSa.equals(newSa)) return;
		synchronized (devices) {
			devices.remove(zdev.getShortAddress());
			zdev.setShortAddress(newSa);
			devices.put(zdev.getShortAddress(), zdev);
		}
		Device dev = deviceStorage.getDevice(oldSa.longValue());
		if (dev!= null) {
			deviceStorage.changeShortAddress(oldSa, newSa);
		}
	}
	
	public ZclCluster findClusterOnDevice(String mac, DoubleByte clId) {
		ZclDevice dev = getDeviceByMac(new OctaByte(OctaByte.convertMac(mac)));
		if (dev == null) {
			LOG.info("Device not found: {}",mac);
			return null;
		}
		ZclCluster cl = dev.getZclCluster(clId);
		return cl;
	}
	
	public  boolean deviceRecentlyDeleted(DoubleByte devShortAddress) {
		Date dateIncoming = new Date();
		Date mapDate = deletedDevices.get(devShortAddress);
		boolean recentlyDeleted =
				mapDate != null && (dateIncoming.getTime() - mapDate.getTime())
				< IGNORE_TIME_AFTER_DEVICE_LEAVES_MS;
		return recentlyDeleted;
	}

	public void deleteDevice(ZclDevice zdev) {
		synchronized (devices) {
			if(zdev != null && zdev.getShortAddress()!=null){
			devices.remove(zdev.getShortAddress());
			}
		}
		deviceStorage.deleteDevice(zdev);
	}
	
	public void addRediscoveryDevice(DoubleByte devAddr) {
		Date date = new Date();
		rediscoveryDevices.put(devAddr, date);
	}
	
	public boolean isBeingRediscovered(DoubleByte devAddr) {
		Date rediscoveryStartDate = rediscoveryDevices.get(devAddr);
		if (rediscoveryStartDate == null) {
			return false;
		}
		long rediscoveryStartTime = rediscoveryStartDate.getTime();
		long rightNow = new Date().getTime();
		if ((rightNow - rediscoveryStartTime) < REDISCOVERY_MAX_TIME_MS) {
			return true;
		} else {
			removeRediscoveryDevice(devAddr);
			return false;
		}
	}
	
	public void removeRediscoveryDevice(DoubleByte devAddr) {
		rediscoveryDevices.remove(devAddr);
	}
	
	public void cleanRediscoveryDevices() {
		long actualTimeMs = new Date().getTime();
		Iterator<DoubleByte> it = rediscoveryDevices.keySet().iterator();
		while (it.hasNext()) {
			DoubleByte shortAddress = it.next();
			Date rediscoveryDate = rediscoveryDevices.get(shortAddress);
			if ((actualTimeMs - rediscoveryDate.getTime()) > REDISCOVERY_MAX_TIME_MS) {
				rediscoveryDevices.remove(shortAddress);
			}
		}
	}
	
	public void flushDevice(ZclDevice devOnMemory) {
		deviceStorage.copyDevice(devOnMemory);
	}
	public DeviceStorage getDeviceStorage() {
		return deviceStorage;
	}
	
	public synchronized void saveDeviceTypeByDeviceId(String mac) {

		DeviceType deviceType = DeviceType.UNDEFINED;

		ZclDevice device = getDeviceByMac(mac);

		if (device != null) {
			ConcurrentHashMap<Byte, ZclEndpoint> endpoints = device.getEndpoints();
			if (endpoints != null) {
				for (ZclEndpoint endpoint : endpoints.values()) {
					if (endpoint != null && endpoint.getDeviceId() != null) {
						if (endpoint.getDeviceId().equals(new DoubleByte(0x0301))) {
							deviceType = DeviceType.THERMOSTAT;
						} else if (endpoint.getDeviceId().equals(new DoubleByte(0x0051))) { //SmartPlug
							deviceType = DeviceType.METER;
						} else if (endpoint.getDeviceId().equals(new DoubleByte(0x0100)) || endpoint.getDeviceId().equals(new DoubleByte(0x0101)) || endpoint.getDeviceId().equals(new DoubleByte(0x0102)) || endpoint.getDeviceId().equals(new DoubleByte(0x010d))) {
							//Esperamos para que los atributos no reportables se configuren y asi poder diferenciar el tipo de bombilla.

							ZclCluster clusterColor = getDeviceByMac(mac).getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_LIGHTING_COLOR_CONTROL);
							if (clusterColor != null) {
								ZclAttribute attrhue = clusterColor.getAttribute(new DoubleByte(0x0000));
								ZclAttribute attrtmp = clusterColor.getAttribute(new DoubleByte(0x0007));

								if (!attrhue.isUnsupported() && !attrtmp.isUnsupported()) {
									deviceType = DeviceType.BULB_COLOR;
								} else if (!attrtmp.isUnsupported()) {
									deviceType = DeviceType.BULB_TEMP;
								}
							} else {
								deviceType = DeviceType.BULB;
							}
						} else if (endpoint.getDeviceId().equals(new DoubleByte(0x0403))) {
							deviceType = DeviceType.ALARM;
						} else if (endpoint.getDeviceId().equals(new DoubleByte(0x0009))) {
							for (ZclCluster cluster : endpoint.getClusters().values()) {
								if (cluster.getId().equals(ZClusterLibrary.ZCL_CLUSTER_ID_SE_SIMPLE_METERING)) {
									deviceType = DeviceType.METER;
									break;
								} else {
									deviceType = DeviceType.SWITCH_BUTTON;
								}
							}
						} else if (endpoint.getDeviceId().equals(new DoubleByte(0x0402)) ||
								endpoint.getDeviceId().equals(new DoubleByte(0x000C)) ||
								endpoint.getDeviceId().equals(new DoubleByte(0x0107))) {
							Map<DoubleByte, ZclCluster> clusters = endpoint.getClusters();
							for (ZclCluster cluster : clusters.values()) {
								if (cluster.getId().equals(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE)) {
									if (cluster.getAttribute(new DoubleByte(0x01)) != null && cluster.getAttribute(new DoubleByte(0x01)).getConvertedValue() != null) {
										String zoneType = cluster.getAttribute(new DoubleByte(0x01)).getConvertedValue().toString();
										LOG.info("Atributte ZoneType value: {}",zoneType);
										if (zoneType.equalsIgnoreCase(DOOR_SENSOR)) {
											deviceType = DeviceType.DOOR_SENSOR;
										} else if (zoneType.equalsIgnoreCase(MOTION_SENSOR)) {
											deviceType = DeviceType.MOTION_SENSOR;
										} else if (zoneType.equalsIgnoreCase(WATER_SENSOR)) {
											deviceType = DeviceType.WATER_SENSOR;
										} else if (zoneType.equalsIgnoreCase(SMOKE_SENSOR)) {
											deviceType = DeviceType.SMOKE_SENSOR;
										} else if (zoneType.equalsIgnoreCase(ALARM_SENSOR)) {
											deviceType = DeviceType.ALARM;
										} else {
											deviceType = DeviceType.SENSOR;
										}
									}else {
										deviceType = DeviceType.SENSOR;
									}
								}
							}
						} else if (endpoint.getDeviceId().equals(new DoubleByte(0x0401))) {
							deviceType = DeviceType.PANIC_SENSOR;
						} else if (endpoint.getDeviceId().equals(new DoubleByte(0x0000)) && (deviceType.equals(DeviceType.METER))) { //Buscamos el cluster OTA
								deviceType = this.getSpecificMeterType(device);
						}

					}
				}
			}
			flushDevice(device);
		}
//		this.setDeviceType(deviceType); ahumanes: setear aqui 
		device.setGeneralDeviceType(deviceType);
		LOG.info("Device: {} changed DeviceType to {}",mac ,deviceType);
	}
	
	private DeviceType getSpecificMeterType(ZclDevice device) {
		ZclCluster clusterOta = device.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA);
		DeviceType deviceType = DeviceType.METER;

		if (clusterOta != null) {
			ZclAttribute attrType = clusterOta.getAttribute(new DoubleByte(0x0008));
			if (attrType != null) {
				String value = attrType.getConvertedValue().toString();
				if (value.equalsIgnoreCase("53") || value.equalsIgnoreCase("5") || value.equalsIgnoreCase("6")) {
					deviceType = DeviceType.CLAMP3;
				} else if (value.equalsIgnoreCase("37")) {
					deviceType = DeviceType.CLAMP;
				} else if (value.equalsIgnoreCase("20")) {
					deviceType = DeviceType.PLUG;
				} else {
					deviceType = DeviceType.METER;
				}
			}
		}

		return deviceType;
	}
	public List<ClustersInfoJson> getTableClusters(){
		return this.zclservice.getClustersinfo();
		
	}
}
