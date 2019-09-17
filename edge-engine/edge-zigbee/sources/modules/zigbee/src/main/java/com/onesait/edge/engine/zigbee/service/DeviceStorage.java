package com.onesait.edge.engine.zigbee.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.onesait.edge.engine.zigbee.json.Attribute;
import com.onesait.edge.engine.zigbee.json.Cluster;
import com.onesait.edge.engine.zigbee.json.Device;
import com.onesait.edge.engine.zigbee.json.Devices;
import com.onesait.edge.engine.zigbee.json.Endpoint;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.model.ZclEndpoint;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.SystemProperties;

@Service
public class DeviceStorage {

	private static Devices devices = new Devices();
	private static final Logger LOG = LoggerFactory.getLogger(DeviceStorage.class);
	private static final String ZBDEVICESJSON_DOCKER = "/mnt/conf/zbdevices.json";
	@Value("${zigbee.zbdevicesjson.path}")
	private String zbdevicesjson;

	private void getUrlZbDevicesFile() {
		SystemProperties syspro = new SystemProperties();
		if (!syspro.isWindows()) {
			this.zbdevicesjson = ZBDEVICESJSON_DOCKER;
		}
	}

	private void loadDevices() {
		LOG.info("Loading permanent information of zigbee devices.");
		File jsonDevs = new File(this.zbdevicesjson);
		if ((!jsonDevs.exists()) && (!createFile())) {
			return;
		}

		ObjectMapper om = new ObjectMapper();
		Devices jsonDevices;
		try {
			jsonDevices = om.readValue(jsonDevs, Devices.class);
			if (jsonDevices != null) {
				devices = jsonDevices;
			}
		} catch (JsonParseException | JsonMappingException e) {
			LOG.error("Invalid {} content. Creating new file.", this.zbdevicesjson);
			if (jsonDevs.delete()) {
				createFile();
			} else {
				LOG.error("Could not delete {}", this.zbdevicesjson);
			}
		} catch (IOException e) {
			LOG.error("IO error loading devices");
		}
	}

	/**
	 * Initializes the permanent information of zigbee devices.
	 * 
	 * @return Number of devices found.
	 */
	synchronized void init() {
		getUrlZbDevicesFile();
		if (!createFile()) {
			loadDevices();
		} else {
			devices = new Devices();
			LOG.info("No permanent information of zigbee devices found.");
		}
	}

	private boolean createFile() {
		File jsonDevs = new File(this.zbdevicesjson);
		try {
			boolean success = jsonDevs.createNewFile();
			if (success) {
				flushDevices();
			}
			return success;
		} catch (IOException e) {
			LOG.error("Could not create zbdevices.ser. Cause: {}", e);
			return false;
		}
	}

	public synchronized void flushDevices() {
		File file = new File(this.zbdevicesjson);
		if (file.exists() && !file.delete()) {
			LOG.error("The zbdevicesjson could not be removed");
		}
		try {
			if (!file.createNewFile()) {
				LOG.error("The zbdevicesjson could not be created");
			}
			ObjectWriter om = new ObjectMapper().writerWithDefaultPrettyPrinter();
			om.writeValue(file, devices);
		} catch (Exception e) {
			LOG.error("Error flushing devices");
		}
	}

	synchronized void addDevice(Device dev, boolean flush) {
		if (devices.getDevice(dev.getShortaddress()) != null) {
			removeDevice(dev, false);
		}
		Long repeatedShortAddress = searchIeeeAddress(dev.getIeeeaddress());
		if (repeatedShortAddress != null) {
			removeDevice(repeatedShortAddress, false);
		}
		devices.putDevice(dev);
		if (flush) {
			flushDevices();
		}
	}

	synchronized void removeDevice(Device dev, boolean flush) {
		removeDevice(dev.getShortaddress(), flush);
	}

	synchronized void removeDevice(Long shortAddress, boolean flush) {
		devices.removeDevice(shortAddress);
		if (flush) {
			flushDevices();
		}
	}

	synchronized Device getDevice(Long shortAddress) {
		return devices.getDevice(shortAddress);
	}

	synchronized Long[] getAllShortAddresses() {
		Long[] permanentAddresses = new Long[devices.getDevices().size()];
		for (int i = 0; i < permanentAddresses.length; i++) {
			permanentAddresses[i] = devices.getDevices().get(i).getShortaddress();
		}
		return permanentAddresses;
	}

	synchronized void copyDevice(ZclDevice devOnMemory) {
		copyDevice(devOnMemory, true);
	}

	synchronized void copyDevice(ZclDevice devOnMemory, boolean flush) {
		if (devOnMemory == null) {
			return;
		}
		if (devOnMemory.getIeeeAddress().equals(new OctaByte(0))) {
			return;
		}
		Device dev = new Device();

		// ZDO
		DoubleByte mc = devOnMemory.getManufacturerCode();
		OctaByte la = devOnMemory.getIeeeAddress();
		DoubleByte sa = devOnMemory.getShortAddress();
		if (mc == null || la == null || sa == null) {
			return;
		}
		dev.setManufacturercode((long) mc.intValue());
		Long macLong = processMacBytes(la);
		dev.setIeeeaddress(macLong);
		dev.setShortaddress((long) sa.intValue());
		Byte capabilities = devOnMemory.getCapabilities();
		if (capabilities != null) {
			dev.setCapabilities(capabilities.longValue());
		}
		Byte deviceType = devOnMemory.getDeviceType();
		if (deviceType != null) {
			dev.setDevicetype(deviceType.longValue());
		}

		// ZCL
		// Endpoints
		for (ZclEndpoint zclep : devOnMemory.getEndpoints().values()) {
			Endpoint ep = new Endpoint();
			ep.setId((long) zclep.getId());
			ep.setDeviceid(zclep.getDeviceId() != null ? (long) zclep.getDeviceId().intValue() : null);
			ep.setProfile(zclep.getProfile() != null ? (long) zclep.getProfile().intValue() : null);
			dev.addEndpoint(ep);
			// Clusters
			for (ZclCluster zclcl : zclep.getClusters().values()) {
				copyClusterNoFlush(dev, zclcl, zclep.getId(), devOnMemory);
			}
		}
		addDevice(dev, flush);
	}

	private static Long processMacBytes(OctaByte la) {
		String mac = la.toString();
		Long macLong;
		int firstChar = Integer.parseInt("" + mac.charAt(0));
		if (firstChar >= 8) {
			int positiveFirstChar = firstChar - 8;
			String unsignedMacStr = positiveFirstChar + mac.substring(1);
			Long unsignedMacLong = Long.decode("0x" + unsignedMacStr);
			macLong = Long.MIN_VALUE + unsignedMacLong;
		} else {
			macLong = Long.decode("0x" + mac);
		}
		return macLong;
	}

	public synchronized void copyAttributeValue(ZclAttribute atOnMemory, DoubleByte dstDevSa, Byte dstEpId,
			DoubleByte dstClId) {
		byte[] value = atOnMemory.getBigEndianValue();
		Device dstDevice = getDevice((long) dstDevSa.intValue());
		if (dstDevice == null) {
			return;
		}
		Endpoint dstEndpoint = dstDevice.getEndpoint(dstEpId);
		if (dstEndpoint == null) {
			return;
		}
		Cluster dstCluster = dstEndpoint.getCluster((long) dstClId.intValue());
		if (dstCluster == null) {
			return;
		}
		Attribute dstAttribute = dstCluster.getAttribute((long) atOnMemory.getId().intValue());
		if (dstAttribute == null) {
			return;
		}
		dstAttribute.setValue(value);
		dstAttribute.setConfigured(atOnMemory.isRspReceived());
		// Mapa para los dispositivos antiguos de Meazon con distinto tipo en el
		// activeenergy
		dstAttribute.setDatatype(atOnMemory.getDatatype().getId().longValue());
		addDevice(dstDevice, true);
	}

	public synchronized void deleteAttribute(long atId, long devSa, long epId, long clId) {
		Device dev = devices.getDevice(devSa);
		if (dev == null) {
			return;
		}
		Endpoint ep = dev.getEndpoint(epId);
		if (ep == null) {
			return;
		}
		Cluster cl = ep.getCluster(clId);
		if (cl == null) {
			return;
		}
		cl.removeAttribute(atId);
		addDevice(dev, true);
	}

	public synchronized void deleteAttribute(DoubleByte atId, DoubleByte devSa, Byte epId, DoubleByte clId) {
		deleteAttribute(atId.longValue(), devSa.longValue(), epId.longValue(), clId.longValue());
	}

	synchronized void copyClusterNoFlush(Device dev, ZclCluster clToCopy, Byte dstEpId, ZclDevice zcldev) {
		if (dev == null || clToCopy == null || dstEpId == null || zcldev == null) {
			return;
		}
		Cluster cl = new Cluster();
		cl.setPackage("");
		cl.setId(clToCopy.getId().longValue());
		cl.setName(clToCopy.getName());
		cl.setManufacturerspecific(clToCopy.isManSpec());
		cl.setInput(true);
		cl.setConfigured(clToCopy.isConfigured());
		// Atributos
		for (ZclAttribute zclatt : clToCopy.getAttributes().values()) {
			if (!zclatt.isUnsupported()) {
				Attribute att = new Attribute();
				att.setAccess(zclatt.getAccess());
				att.setDatatype(zclatt.getDatatype() != null ? zclatt.getDatatype().getId().longValue() : null);
				att.setId(zclatt.getId() != null ? zclatt.getId().longValue() : null);
				att.setMandatory(zclatt.getMandatory() != null ? zclatt.getMandatory() : true);
				att.setName(zclatt.getName() != null ? zclatt.getName() : "");
				att.setReportable(true);
				att.setManufacturerspecific(zclatt.getCode() != null);

				byte[] valueByteArr = zclatt.getBigEndianValue();
				List<Byte> valueList = new ArrayList<>();
				for (int i = 0; i < valueByteArr.length; i++) {
					valueList.add(valueByteArr[i]);
				}
				att.setValue(valueList);

				// ahumanes
//				cluster.getDevice().getZclEndpoint((byte) epId).getDeviceId()
				DoubleByte maxReportTime = zclatt
						.getMaxReportingTime(zcldev.getZclEndpoint((byte) dstEpId).getDeviceId());
				DoubleByte minReportTime = zclatt
						.getMinReportingTime(zcldev.getZclEndpoint((byte) dstEpId).getDeviceId());
				if (minReportTime != null && maxReportTime != null) {
					att.setMinReportingTime(Integer.toString(minReportTime.intValue()));
					att.setMaxReportingTime(Integer.toString(maxReportTime.intValue()));
				}
				att.setConfigured(zclatt.isRspReceived());
				cl.addAttribute(att);
			}
		}
		Endpoint dstEndpoint = dev.getEndpoint(dstEpId);
		if (dstEndpoint == null) {
			return;
		}
		dstEndpoint.addCluster(cl);
		addDevice(dev, false);
	}

	public synchronized void deleteDevice(ZclDevice devOnMemory) {
		if (devOnMemory != null) {
			removeDevice(devOnMemory.getShortAddress().longValue(), true);
		}
	}

	synchronized void changeShortAddress(DoubleByte oldSa, DoubleByte newSa) {
		Device dev = getDevice(oldSa.longValue());
		if (dev == null) {
			return;
		}
		removeDevice(dev, false);
		dev.setShortaddress((long) newSa.intValue());
		addDevice(dev, true);
	}

	private static synchronized Long searchIeeeAddress(Long ieeeAddress) {
		for (Device dev : devices.getDevices()) {
			if (dev.getIeeeaddress().equals(ieeeAddress)) {
				return dev.getShortaddress();
			}
		}
		return null;
	}

	public Devices getDevices() {
		return DeviceStorage.devices;
	}
}
