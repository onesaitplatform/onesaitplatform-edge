package com.onesait.edge.engine.zigbee.monitoring;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.exception.UnknownCoordinatorMacException;
import com.onesait.edge.engine.zigbee.frame.UtilGetDeviceInfo;
import com.onesait.edge.engine.zigbee.frame.ZdoActiveEpReq;
import com.onesait.edge.engine.zigbee.frame.ZdoBindReq;
import com.onesait.edge.engine.zigbee.frame.ZdoBindReq.ADDRESS_MODE;
import com.onesait.edge.engine.zigbee.frame.ZdoIeeeAddrReq;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtLeaveReq;
import com.onesait.edge.engine.zigbee.frame.ZdoSimpleDescReq;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclCoordinator;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.model.ZclEndpoint;
import com.onesait.edge.engine.zigbee.service.DeviceManager;
import com.onesait.edge.engine.zigbee.service.ZigbeeService;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.MessageRepeater;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.RequestMaps;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

/**
 * Thread that checks periodically devices in memory to make sure that the most
 * relevant information has been received. Also cleans temporal information.
 * 
 * @author dyagues
 */

public class DeviceMonitoring extends TimerTask {

	public static final long DELAY_MS = 60_000;  //60_000
	public static final long PERIOD_MS = 180_000; //180_000
	private static final int TIME_BTW_RETRIES_MS = 100;
	private static final int INVALID_IEEE_ADDRESS_TIMEOUT_MS = 300_000;
	private static final int RETRIES_TO_AGE_OUT = 2;
	private static final int NWK_ADDR_REQ_TIMEOUT_MS = 60_000;
	private static final int TIME_USED_TO_SEND_LEAVE_REJOIN = 60_000;
	private static final int TIME_IAS_ZONE_STATUS_TIMEOUT_MSECONDS=7200_000;
	private ZclCoordinator coor;
	private static final Logger LOG = LoggerFactory.getLogger(DeviceMonitoring.class);
	private Map<DoubleByte, Timestamp> wrongIEEEaddrTimes = new HashMap<>();
	private OutputSerialZigbee serial;
	private Map<DoubleByte, Timestamp> childAgingTimes = new HashMap<>();
	private DeviceManager deviceManager; 

	public DeviceMonitoring(OutputSerialZigbee serial, ZclCoordinator coor, DeviceManager deviceManager) {
		this.serial = serial;
		this.coor = coor;
		this.deviceManager=deviceManager;
	}

	public void run() {

		try {
			cleanLeavingDevicesDates();
			deviceManager.cleanRediscoveryDevices();
			RequestMaps.cleanRequestIds();
			Iterator<ZclDevice> devi = deviceManager.devices.values().iterator();
			while (devi.hasNext()) {
				ZclDevice dev = devi.next();
				if (dev.getIeeeAddress().equals(new OctaByte(0))) {
					checkInvalidIEEEAddress(dev);
					continue;
				}
//				checkDeviceKO()
				int endpointCount = 0;
				for (ZclEndpoint ep : dev.getEndpoints().values()) {
					endpointCount++;
					int clusterCount = 0;
					
					for (ZclCluster cl : ep.getClusters().values()) {
						clusterCount++;
						monitorCluster(cl, ep.getId());
					}
					// Si no hay clusters almacenados y ha pasado el tiempo
					// suficiente, los pedimos
					if (clusterCount == 0 && ep.toDiscover()) {
						ZdoSimpleDescReq zsdr = new ZdoSimpleDescReq(dev.getShortAddress(),
								dev.getShortAddress(), ep.getId());
						this.serial.writeZFrame(zsdr);
						ep.incDiscoverRetries();
					}
				}
				if (endpointCount == 0) {
					ZdoActiveEpReq zaer = new ZdoActiveEpReq(dev.getShortAddress());
					String logMsg = "No endpoints received from device " + dev.getIeeeAddress() +
							". Sending ZDO_ACTIVE_EP_REQ";
					this.serial.sendAndWriteLog(zaer, logMsg, this.getClass().getName());
				}
			}
			if(this.coor.getIeeeAddress()==null){
				serial.sendAndWriteLog(new UtilGetDeviceInfo(),
						"Coordinator IEEE address unknown. Sending IEEE Address request.", this.getClass().getName());
			}
		} catch (Exception e) {
			LOG.error("ERROR: Device Monitoring iteration failed. Cause: {}",e);
		} finally {
			Thread.currentThread().interrupt();
		}
	}

	private void cleanLeavingDevicesDates() {
		try {
			Iterator<DoubleByte> it = deviceManager.deletedDevices.keySet().iterator();
			while (it.hasNext()) {
				DoubleByte deletedDeviceAddr = it.next();
				Date leavingDate = deviceManager.deletedDevices.get(deletedDeviceAddr);
				Long elapsedTimeMs = new Date().getTime() - leavingDate.getTime();
				if (elapsedTimeMs > DeviceManager.IGNORE_TIME_AFTER_DEVICE_LEAVES_MS) {
					deviceManager.deletedDevices.remove(deletedDeviceAddr);
				}
			}
		} catch (Exception e) {
			LOG.error("ERROR: Could not clean deleted devices table. Cause: {}",e);
		}
	}

	private void monitorCluster(ZclCluster cluster, int epId) {
		Boolean bindSent = false;
		for (ZclAttribute at : cluster.getAttributes().values()) {	
			if (!cluster.getDevice().getOtamanager().isOta()) {// si el dispositivo no se encuentra actualizandose
				if (at != null && at.isRetryConfigReport(cluster.getDevice().getZclEndpoint((byte) epId).getDeviceId(),cluster,epId) && (cluster.isConfigured())) {
					if (at.getRetries() >= RETRIES_TO_AGE_OUT) {
						if (isNecessaryToDoRejoin(cluster.getDevice().getShortAddress())) {
							this.coor.leaveDevice(cluster.getDevice().getIeeeAddress().toString(), true);
							break;
						} else {
							boolean deviceAgedOut = checkAndAgeOutDevice(cluster);
							if (deviceAgedOut) {
								break;
							}
						}
					}
					if (!bindSent) {
						createAndSendBindRequest(cluster,epId);
						bindSent = true;
					}
					reconfigureReporting(cluster, at,epId);
				}
				if (at != null && at.isRetryRead(epId) && cluster.isConfigured()) {
					reread(cluster, at);
				}
				if(at != null && cluster.getId().equals(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE) && at.getName().equalsIgnoreCase("Zone Status")){
					checkTimeIasZoneStatus(at);
				}
				waitMs(TIME_BTW_RETRIES_MS);
			}
		}

	}

	private void checkTimeIasZoneStatus(ZclAttribute at) {
		long now = new Date(Calendar.getInstance().getTime().getTime()).getTime();
		if (at.getLastTimeUpdated() != null) {
			long lastTimeReceived = at.getLastTimeUpdated().getTime();
			if (now - lastTimeReceived > TIME_IAS_ZONE_STATUS_TIMEOUT_MSECONDS) {
				at.setReportado(false);
			}
		} else {
			if (now - ZigbeeService.getTimeStarted().getTime() > TIME_IAS_ZONE_STATUS_TIMEOUT_MSECONDS) {
				at.setReportado(false);
			}
		}
	}

	private void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: {}",e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Comprueba si el dispositivo se encuentra en el hashmap de direcciones mac
	 * no validas (0x0000000000000000). Si no esto, lo añade; si si que esta y
	 * han pasado mas de INVALID_IEEE_ADDRESS_TIMEOUT_MS milisegundos desde que
	 * se añadia, se elimina el dispositivo de memoria.
	 * 
	 * @param dev
	 *            El dispositivo a tratar. Debe comprobarse antes que su
	 *            direccion mac es no valida.
	 */
	private void checkInvalidIEEEAddress(ZclDevice dev) {
		DoubleByte devSa = new DoubleByte(dev.getShortAddress().intValue());
		Timestamp devCreatedTime = this.wrongIEEEaddrTimes.get(devSa);
		if (devCreatedTime == null) {
			this.wrongIEEEaddrTimes.put(devSa, new Timestamp(new Date().getTime()));
			ZdoIeeeAddrReq ieeeReq = new ZdoIeeeAddrReq(devSa, false);
			MessageRepeater mr = new MessageRepeater(ieeeReq, 3, 30000, serial);
			mr.start();
		} else if (new Date().getTime() - devCreatedTime.getTime() > INVALID_IEEE_ADDRESS_TIMEOUT_MS) {
			deviceManager.devices.remove(devSa);
			this.wrongIEEEaddrTimes.remove(devSa);
		}
	}

	private void createAndSendBindRequest(ZclCluster cl, int epId) {
		try {
//			Byte epId = cl.getDevice().getEndpointByCluster(cl.getId()).getId();
			ZdoBindReq zbr = new ZdoBindReq(cl.getDevice().getShortAddress(),
					cl.getDevice().getIeeeAddress(), epId, cl.getId(),
					ADDRESS_MODE.ADDRESS_64_BIT, this.coor.getIeeeAddress(),
					ZigbeeConstants.COORDINATOR_ENDPOINT);
			serial.writeZFrame(zbr);
//			LOG.info("Bind request sent to " + cl.getDevice().getIeeeAddress()
//					+ ">> Cluster " + cl.getId() + " endpoint "
//				+ cl.getDevice().getEndpointByCluster(cl.getId()).getId());
//			LOG.info("Bind request sent to " + cl.getDevice().getIeeeAddress()
//					+ ">> Cluster " + cl.getId() + " endpoint "
//					+ epId);
			
		} catch (UnknownCoordinatorMacException zucme) {
			LOG.info("IEEE address of coordinator unknown. Cannot send bind request.");
			serial.sendAndWriteLog(new UtilGetDeviceInfo(),
					"Coordinator IEEE address unknown. Sending IEEE Address request.", this.getClass().getName());
		}
	}

	private boolean checkAndAgeOutDevice(ZclCluster cl) {
		synchronized (this.coor.getNwkAddrRequests()) {
			if (checkNwkAddrReqReceived(cl)) {
				setChildAgingTime(cl.getDevice().getShortAddress());
				LOG.info("Device {} no longer child of coordinator.",cl.getDevice().getIeeeAddress());
				LOG.info("Sending LEAVE REQUEST to {} to delete it from child table.",cl.getDevice().getIeeeAddress());
				ZdoMgmtLeaveReq zmlr = new ZdoMgmtLeaveReq(new DoubleByte(0x0000),
						cl.getDevice().getIeeeAddress(), false, true);
				this.serial.writeZFrame(zmlr);
				return true;
			}
			return false;
		}
	}

	private boolean checkNwkAddrReqReceived(ZclCluster cl) {
		Timestamp t = this.coor.getNwkAddrRequests().get(cl.getDevice().getShortAddress());
		return t != null && new Date().getTime() - t.getTime() < NWK_ADDR_REQ_TIMEOUT_MS;
	}
	
	private boolean isNecessaryToDoRejoin(DoubleByte shortAddress){
		if(!childAgingTimes.containsKey(shortAddress)){
			return false;
		}else if((new Date().getTime()-childAgingTimes.get(shortAddress).getTime())>10*TIME_USED_TO_SEND_LEAVE_REJOIN){ //cambiar a 1 para hacer pruebas
			childAgingTimes.remove(shortAddress);
			return true;
		}
		return false;
	}
	
	private void setChildAgingTime(DoubleByte deviceShortAddress){			
		if(childAgingTimes.get(deviceShortAddress) == null){
			this.childAgingTimes.put(deviceShortAddress, new Timestamp(new Date().getTime()));
		}
	}
	
	private void reconfigureReporting(ZclCluster cl, ZclAttribute at, int epId) {
		ZFrame frame = cl.buildConfigReport(at.getId(),epId);
		serial.writeZFrame(frame);
		//descomentar si queremos ver los logs del reconf report
//		serial.sendAndWriteLog(frame, "Reconf. report of " +
//				cl.getDevice().getIeeeAddress()	+ ">> Attribute: " +
//				at.getName() + ". Cluster: " + cl.getName(),
//				this.getClass().getName()+" EP:"+epId);
		serial.writeZFrame(frame);
		synchronized (deviceManager.devices) {
			at.incRetries();
			at.updateLastTimeReconfigured();
			at.setReportado(false);
		}
	}

	private void reread(ZclCluster cl, ZclAttribute at) {
		ArrayList<DoubleByte> atIds = new ArrayList<>();
		atIds.add(at.getId());
		ZFrame[] zframes = cl.buildReadAttributes(atIds);
		this.serial.writeZFrames(zframes);
		synchronized (deviceManager.devices) {
			at.incRetries();
			at.updateLastTimeReconfigured();
		}
	}
}
