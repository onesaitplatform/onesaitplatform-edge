package com.onesait.edge.engine.zigbee.util;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.clients.IASZoneClient;
import com.onesait.edge.engine.zigbee.exception.UnknownCoordinatorMacException;
import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.UtilGetDeviceInfo;
import com.onesait.edge.engine.zigbee.frame.UtilGetDeviceInfoResponse;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.frame.ZbGetDeviceInfoRsp;
import com.onesait.edge.engine.zigbee.frame.ZbStartRequest;
import com.onesait.edge.engine.zigbee.frame.ZdoActiveEpReq;
import com.onesait.edge.engine.zigbee.frame.ZdoActiveEpRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoEndDeviceAnnceInd;
import com.onesait.edge.engine.zigbee.frame.ZdoIeeeAddrReq;
import com.onesait.edge.engine.zigbee.frame.ZdoIeeeAddrRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoLeaveIndRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtLeaveRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtLqiReq;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtLqiRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtNwkUpdateNotify;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtPermitJoinRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtRtgReq;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtRtgRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoNodeDescReq;
import com.onesait.edge.engine.zigbee.frame.ZdoNodeDescRsp;
import com.onesait.edge.engine.zigbee.frame.ZdoSimpleDescReq;
import com.onesait.edge.engine.zigbee.frame.ZdoSimpleDescRsp;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.mesh.NeighborTableEntry;
import com.onesait.edge.engine.zigbee.mesh.RMesh;
import com.onesait.edge.engine.zigbee.mesh.RMeshLink;
import com.onesait.edge.engine.zigbee.mesh.RoutingTableEntry;
import com.onesait.edge.engine.zigbee.mesh.ZMeshLink;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclCoordinator;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.model.ZclDevicetype;
import com.onesait.edge.engine.zigbee.model.ZclEndpoint;
import com.onesait.edge.engine.zigbee.monitoring.DeviceMonitoring;
import com.onesait.edge.engine.zigbee.monitoring.OtaUpgradeMonitoring;
import com.onesait.edge.engine.zigbee.ota.ImageBlockRequest;
import com.onesait.edge.engine.zigbee.ota.ImageBlockResponse;
import com.onesait.edge.engine.zigbee.ota.OtaManager;
import com.onesait.edge.engine.zigbee.ota.QueryNextImageRequest;
import com.onesait.edge.engine.zigbee.ota.QueryNextImageResponse;
import com.onesait.edge.engine.zigbee.ota.UpgradeEndRequest;
import com.onesait.edge.engine.zigbee.ota.UpgradeEndResponse;
import com.onesait.edge.engine.zigbee.service.DeviceManager;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.service.ZclService;
import com.onesait.edge.engine.zigbee.service.ZigbeeService;
import com.onesait.edge.engine.zigbee.types.AlertType;
import com.onesait.edge.engine.zigbee.types.DeviceStatus;

public class ZFrameManagerThread extends Thread{

	
	private static final Logger LOG = LoggerFactory.getLogger(ZFrameManagerThread.class);
	private DeviceMonitoring deviceMonitoring;
	private ZFrame frame;
	private Timer timerDeviceMonitoring = new Timer("TimerDeviceMonitoring", Boolean.TRUE);
	private static final int WAIT_FOR_ATTRIBUTES_TO_SEND_UPDATE_MS = 2000;
	private RMesh rMesh = new RMesh();
//	private Byte receivedChannel = null;
	private OutputSerialZigbee serial;
	private static final int JAMMING_THRESHOLD = 30;
	private ZclCoordinator coordinator;
	private DeviceManager deviceManager;
	private ZclService zclservice;
	private MqttConnection mqttConnection;
//	private ChipMonitoring cm;
	
	public ZFrameManagerThread(ZFrame frame,ZclCoordinator zclCoor, MqttConnection mqttConnection) {
		super();
		this.frame = frame;
		this.coordinator=zclCoor;
		this.deviceManager=this.coordinator.getDeviceManager();
		this.zclservice=zclCoor.getZclService();
		this.serial=this.coordinator.getOutputserial();
		this.mqttConnection=mqttConnection;
	}
	

//
//	public DeviceManager getDeviceManager() {
//		return deviceManager;
//	}
//
//
//
//	public void setInitParameters(ZclCoordinator  coor) {
//		this.coordinator=coor;
//		this.deviceManager=this.coordinator.getDeviceManager();
//		this.zclservice=coor.getZclService();
//	}



	public RMesh getrMesh() {
		return rMesh;
	}

	public void cleanrMesh() {
		this.rMesh = new RMesh();
	}

//	public void setChipMonitorig(ChipMonitoring cm) {
//		this.cm = cm;
//	}

	
	@Override
	public void  run() {
//ahumanes: descomentar esto
//		if (this.cm != null) {
//			this.cm.markActivity();
//		}
//		if (connector==null){
//			waitMs(20000);
//		}
		DoubleByte db = frame.getMtCmdId();
		if (db.equals(new DoubleByte(ZToolCMD.ZDO_END_DEVICE_ANNCE_IND))) {
			this.manageZDO_END_DEVICE_ANNCE_IND(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_IEEE_ADDR_RSP))) {
			this.manageZDO_IEEE_ADDR_RSP(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_ACTIVE_EP_RSP))) {
			this.manageZDO_ACTIVE_EP_RSP(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_LEAVE_IND))) {
			this.manageZDO_LEAVE_IND(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_SIMPLE_DESC_RSP))) {
			this.manageZDO_SIMPLE_DESC_RSP(frame);
		} else if(db.equals(new DoubleByte(ZToolCMD.ZDO_MGMT_LEAVE_RSP))) {
			this.manageZDO_LEAVE_RESPONSE(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.AF_INCOMING_MSG))) {
			this.manageAF_INCOMING_MSG(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.UTIL_GET_DEVICE_INFO_RESPONSE))) {
			this.manageUTIL_GET_DEVICE_INFO_RESPONSE(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.ZB_GET_DEVICE_INFO_RSP))) {
			this.manageZB_GET_DEVICE_INFO_RSP(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.SYS_RESET_RESPONSE))) {
			this.coordinator.reset();
			UtilGetDeviceInfo infoRequest = new UtilGetDeviceInfo();
			this.serial.writeZFrame(infoRequest);
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_MGMT_LQI_RSP))) {
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_MGMT_RTG_RSP))) {
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_NODE_DESC_RSP))) {
			this.manageZDO_NODE_DESC_RSP(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.ZDO_MGMT_PERMIT_JOIN_RSP))) {
			this.manageZDO_MGMT_PERMIT_JOIN_RSP(frame);
		} else if (db.equals(new DoubleByte(ZToolCMD.NLME_PERMITJOINING_RESPONSE))){
			this.manageNLME_PERMITJOIN_RESPONSE(frame);
		}
		
		}
	
	private void manageZDO_LEAVE_RESPONSE(ZFrame frame) {
		ZdoMgmtLeaveRsp leaversp = new ZdoMgmtLeaveRsp(frame.getData());
		ZclDevice parentDevice = deviceManager.getDeviceByNwkAddress(leaversp.getSrcAddr());
		if (parentDevice != null) {
			ZclDevice childToRemove = parentDevice.getChildDeviceToRemove();
			LOG.info("Confirmation receiced from parent: {}",parentDevice.getIeeeAddress());
			if (childToRemove != null) {
				LOG.info("Device to remove: {}",childToRemove.getIeeeAddress());
				if (leaversp.getStatus() == 0) {
					LOG.info("Src add: {}",leaversp.getSrcAddr());
					Alert alert = null;
					if (childToRemove.isPending2Remove()) {
						alert = new Alert(com.onesait.edge.engine.zigbee.util.State.INFO, AlertType.DEVICE_REMOVED.toString(),
								childToRemove.getIeeeAddress().toString(),
								childToRemove.getGeneralDeviceType().toString());
						childToRemove.setPending2Remove(false);
					} else {
						alert = new Alert(com.onesait.edge.engine.zigbee.util.State.INFO, AlertType.DEVICE_LEAVE.toString(),
								childToRemove.getIeeeAddress().toString(),
								childToRemove.getGeneralDeviceType().toString());
					}
					ZigbeeService.getAlertList().add(alert);
					ZigbeeService.checkAlertList();
					LOG.info("ZIGBEE DEVICE LEAVING: {}",childToRemove.getIeeeAddress());
					deviceManager.deleteDevice(childToRemove);
					synchronized (deviceManager.devices) {
						deviceManager.devices.remove(childToRemove.getShortAddress());
					}
					synchronized (deviceManager.deletedDevices) {
						if (childToRemove != null) {
							deviceManager.deletedDevices.put(childToRemove.getShortAddress(), new Date());
						}
					}
				}
				childToRemove = null;
			}
		}

	}


	private void manageNLME_PERMITJOIN_RESPONSE(ZFrame frame) {
		int status=frame.getData()[0];
		if (status==0){
			this.coordinator.setActivenet(true);
		}		
	}

	/**
	 * Dar de alta un dispositivo y pedir los endpoints
	 */
	private void manageZDO_END_DEVICE_ANNCE_IND(ZFrame frame) {
		ZdoEndDeviceAnnceInd zf = new ZdoEndDeviceAnnceInd(frame.getData());

		if (deviceManager.getDeviceByMac(zf.getIeeeAddress()) == null) {
			constructNewDevice(zf);
			LOG.info("NEW JOIN OF ZIGBEE DEVICE: {} {} ({})",zf.getIeeeAddress(),zf.getSrcAddr(),zf.getSrcAddr().intValue());
			waitMs(3000);
			sendNodeDescReq(zf.getSrcAddr());
		} else {
			LOG.info("MEMORY UPDATE OF ZIGBEE DEVICE: {} {} ({})",zf.getIeeeAddress(), zf.getSrcAddr(),zf.getSrcAddr().intValue());
			ZclDevice oldZDev = deviceManager.getDeviceByMac(zf.getIeeeAddress());
			deviceManager.changeDeviceShortAddress(oldZDev, zf.getSrcAddr());
			ZclDevice newDev = deviceManager.getDeviceByMac(zf.getIeeeAddress());
			newDev.resetRetries();
		}
	}

	private void manageZDO_IEEE_ADDR_RSP(ZFrame frame) {
		ZdoIeeeAddrRsp ieeeAddrRsp = new ZdoIeeeAddrRsp(frame.getData());
		ZclDevice dev = deviceManager.getDeviceByMac(ieeeAddrRsp.getIeeeAddr());
		if (ieeeAddrRsp.getNwkAddr().equals(ZigbeeConstants.COORDINATOR_SHORT_ADDRESS)) {
			this.coordinator.setChildren(ieeeAddrRsp.getAssocDevList());
		} else if (dev != null) {
			LOG.debug("\n Source: " + ieeeAddrRsp.getNwkAddr() + " children: " + Arrays.toString(ieeeAddrRsp.getAssocDevList()));
			if (dev.isEndDevice() == null || !dev.isEndDevice()) {
				if (!hasNotRouterParents(ieeeAddrRsp.getNwkAddr()) && !isNotCoordinatorChild(ieeeAddrRsp.getNwkAddr())) {
					coordinator.updateChild(ieeeAddrRsp.getNwkAddr());
				}
				if ((ieeeAddrRsp.getNumAssocDev() != 0)) {
					coordinator.compareChildrenAndUpdate(ieeeAddrRsp.getAssocDevList());
				}
			}
			dev.setChildren(ieeeAddrRsp.getAssocDevList());
		}

		if ((ieeeAddrRsp.getNumAssocDev() == 0) && (this.serial.isExpectedIeeeAddressResponse(ieeeAddrRsp))) {
			updateDeviceWithIeeeAddrRspData(ieeeAddrRsp);
		}
	}

	private boolean hasNotRouterParents(DoubleByte nwkAddr) {
		for (ZclDevice dev : deviceManager.devices.values()) {
			if (dev.isEndDevice() == null || !dev.isEndDevice()) {
				DoubleByte[] hijos = dev.getChildren();
				for (int i = 0; i < dev.getChildren().length; i++) {
					if (hijos[i].equals(nwkAddr)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isNotCoordinatorChild(DoubleByte nwkAddr) {
		this.coordinator.getChildren();
		DoubleByte[] hijos = this.coordinator.getChildren();
		for (int i = 0; i < hijos.length; i++) {
			if (hijos[i].equals(nwkAddr)) {
				return true;
			}
		}
		return false;
	}

	private void updateDeviceWithIeeeAddrRspData(ZdoIeeeAddrRsp ieeeAddrRsp) {
		DoubleByte srcAddr = ieeeAddrRsp.getNwkAddr();
		OctaByte mac = ieeeAddrRsp.getIeeeAddr();
		if (srcAddr.equals(new DoubleByte(0))) {
			this.coordinator.initCoordinator(mac);
		} else {
			updateDeviceIeeeAddrAndSendNodeDescReq(srcAddr, mac);
		}
	}

	private void updateDeviceIeeeAddrAndSendNodeDescReq(DoubleByte srcAddr, OctaByte mac) {
		ZclDevice zdev = deviceManager.devices.get(srcAddr);
		if (zdev == null) {
			// Por peticion ante una s.a. que me ha devuelto el chip y no tengo.
			zdev = deviceManager.getDeviceByMac(mac);
			if (zdev != null) {
				// Unreachable code? Si recibo este mensaje es porque
				// he enviado una request a la short address actual.
				deviceManager.changeDeviceShortAddress(zdev, srcAddr);
			} else {
				constructNewDevice(srcAddr, mac);
				sendNodeDescReq(srcAddr);
			}
		} else {
			checkTemporalDevice(zdev, srcAddr, mac);
		}
	}

	/**
	 * Saves endpoints on source device if exists
	 */
	private void manageZDO_ACTIVE_EP_RSP(ZFrame frame) {

		ZdoActiveEpRsp zf = new ZdoActiveEpRsp(frame.getData());
		DoubleByte srcAddr = zf.getNwkAddr();
		if (zf.getNwkAddr().equals(new DoubleByte(0x0000)) && zf.getSrcAddress().equals(new DoubleByte(0x0000))) {
			for (int i = 0; i < zf.getEndpointCounter(); i++) {
				if (zf.getEndpoints()[i] == ZigbeeConstants.COORDINATOR_OTA_ENDPOINT) {
					this.coordinator.setHasOtaEndPoint(true);
					return;
				}
				if(i==zf.getEndpointCounter()-1){
					this.coordinator.setHasOtaEndPoint(false);
					return;
				}
			}
		} else {
			ZclDevice zdev = deviceManager.devices.get(srcAddr);

			if (zdev == null)
				return;

			String log = "Detected endpoints on device " + zdev.getIeeeAddress() + ":";

			for (int i = 0; i < zf.getEndpoints().length; i++) {
				ZclEndpoint ep = new ZclEndpoint((byte) zf.getEndpoints()[i]);
				synchronized (deviceManager.devices) {
					zdev.putZclEndpoint(ep);
				}
				log += " " + Byte.toString(ep.getId());
			}
			for (int i = 0; i < zf.getEndpoints().length; i++) {
				ZclEndpoint ep = zdev.getEndpoints().get((byte) zf.getEndpoints()[i]);
				ZdoSimpleDescReq zsdr = new ZdoSimpleDescReq(srcAddr, srcAddr, ep.getId());
				this.serial.writeZFrame(zsdr);
			}
			LOG.debug(log);
		}
	}

	private void manageZDO_LEAVE_IND(ZFrame frame) {
		ZdoLeaveIndRsp zlir = new ZdoLeaveIndRsp(frame.getData());
		ZclDevice devOnMemory = deviceManager.devices.get(zlir.getNwkAddr());
		if (devOnMemory != null) {
//		ZclDevice devLeaving = deviceManager.devicesLeaving.get(zlir.ieeeAddress);
			if ((zlir.getRequest() == 0 && zlir.getRejoin() == 0) ||
			// Debido a que en las placas de Telecon no es posible cambiar el
			// firmware,
			// el valor de estos campos en el mensaje, cuando el que avisa de que un
			// dispositivo
			// ha abandonado la red es su padre, es 255 y no puede modificarse.
			// Si llega a modificarse el firmware, modificar o eliminar tambien la
			// siguiente sentencia:
			// (zlir.request == 255 && zlir.rejoin == 255)
					(zlir.getRequest() == 255 && zlir.getRejoin() == 255) ||
					// Los meters de Meazon envian el flag de rejoin puesto a 1
					// siempre...
					(zlir.getRequest() == 0 && zlir.getRejoin() == 1 && isMeazonDevice(devOnMemory))) {
				// TODO ahumanes a revisar esto
				/*
				 * ConcurrentHashMap<DoubleByte, ClientCluster>
				 * clients=coordinator.getClients().getClients(); IASZoneClient
				 * iaszoneclient=null; for (ClientCluster client : clients.values()) { if
				 * (client.getClusterId().equals(new DoubleByte(0x0500))) { if(client instanceof
				 * IASZoneClient) iaszoneclient= (IASZoneClient) client; continue; } }
				 * if(iaszoneclient!=null){
				 * iaszoneclient.getIasdevices().remove(devOnMemory.getIeeeAddress().toString())
				 * ; }
				 */
				Alert alert = null;

//			deviceManager.deleteDevice(zdev);
				if (devOnMemory.isPending2Remove()) {
					alert = new Alert(com.onesait.edge.engine.zigbee.util.State.INFO, AlertType.DEVICE_REMOVED.toString(),
							devOnMemory.getIeeeAddress().toString(), devOnMemory.getGeneralDeviceType().toString());
					devOnMemory.setPending2Remove(false);
				} else {
					alert = new Alert(com.onesait.edge.engine.zigbee.util.State.INFO, AlertType.DEVICE_LEAVE.toString(),
							devOnMemory.getIeeeAddress().toString(), devOnMemory.getGeneralDeviceType().toString());
				}
				ZigbeeService.getAlertList().add(alert);
				ZigbeeService.checkAlertList();

				deviceManager.deleteDevice(devOnMemory);
				synchronized (deviceManager.devices) {
					deviceManager.devices.remove(zlir.getNwkAddr());
				}
				synchronized (deviceManager.deletedDevices) {
					if (devOnMemory != null) {
						deviceManager.deletedDevices.put(devOnMemory.getShortAddress(), new Date());
					}
				}

				LOG.info("ZIGBEE DEVICE LEAVING: {}",zlir.getIeeeAddress());
				// ahumanes: cambiarlo para un evento de mqtt
				List<MqttMsgDetail> msgs = new ArrayList<>();
				synchronized (deviceManager.devicesLeaving) {
//			msgs.add(BuildMqttMsg.buildLeavingDeviceMqttMsg(devLeaving));
					deviceManager.devicesLeaving.remove(zlir.getIeeeAddress());
				}
				mqttConnection.sendThroughMqtt(msgs);

			} else if (zlir.getRequest() == 0 && zlir.getRejoin() == 1) {
				LOG.info("ZIGBEE DEVICE REJOINING: {} . WAITING FOR UPDATE.",zlir.getIeeeAddress());
			}
		}
	}

	private boolean isMeazonDevice(ZclDevice devOnMemory) {
		if (devOnMemory == null) {
			return false;
		}
		ZclCluster basicCluster = devOnMemory.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC);

		if (basicCluster == null) {
			return false;
		}
		ZclAttribute manufacturerNameAt = basicCluster.getAttribute(new DoubleByte(0x4));
		if (manufacturerNameAt == null) {
			return false;
		}
		String manufacturerName = (String) manufacturerNameAt.getConvertedValue();
		if (manufacturerName.toLowerCase().startsWith("meazon")) {
			return true;
		}
		return false;
	}

	private synchronized void  manageZDO_SIMPLE_DESC_RSP(ZFrame frame) {

		ZdoSimpleDescRsp zf = new ZdoSimpleDescRsp(frame.getData());
		ZclDevice zdev = deviceManager.devices.get(zf.getNwkAddr());
		if (zdev == null || zdev.getZclEndpoint((byte) zf.getEndpoint()) == null)
			return;
		ZclEndpoint ep = zdev.getZclEndpoint((byte) zf.getEndpoint());
		boolean areNewClusters = false;

		if (this.zclservice.getZcl().getProfiles().containsKey(zf.getProfID())) {

			areNewClusters = addNewClustersToEndpoint(zf);
		} else {
			removeEndpointFromDevice(zf);
		}
		updateToIoIfClustersReceived(zdev, ep);

		if (areNewClusters) {
			flushDevice(zdev);
		}
	}

	private boolean  addNewClustersToEndpoint(ZdoSimpleDescRsp zf) {
		ZclDevice zdev = deviceManager.devices.get(zf.getNwkAddr());
		if (zdev == null || zdev.getZclEndpoint((byte) zf.getEndpoint()) == null)
			return false;
		ZclEndpoint ep = zdev.getZclEndpoint((byte) zf.getEndpoint());
		boolean areNewClusters = false;

		synchronized (deviceManager.devices) {
			ep.setProfile(zf.getProfID());
			ep.setDeviceId(zf.getDevID());
		}
		// ahumanes
		/* CLUSTER DE ENTRADA */
		for (int i = 0; i < zf.getInClusterList().length; i++) {
			ZclCluster cluster = zclservice.getZcl().getClusters().get(zf.getInClusterList()[i]);
			if (cluster == null) {
				// Añadimos a la lista de clusters desconocidos por si son
				// incluidos en un futuro en el fichero zcl.xml
				synchronized (deviceManager.devices) {
					if (!ep.getUnknownClusters().contains(zf.getInClusterList()[i])) {
						ep.putUnknownCluster(zf.getInClusterList()[i]);
					}
				}
			}

			// Si el dispositivo ya tiene el cluster en el mismo endpoint, no
			// hacemos nada
			synchronized (deviceManager.devices) {
				if (cluster != null && !ep.getClusters().containsKey(cluster.getId())) {
					areNewClusters = true;
					ZclCluster devCluster = (ZclCluster) cluster.clone();
					devCluster.setDevice(zdev);
					ep.putCluster(devCluster);
					LOG.info("Created cluster {} {} on device {} ep 0x{}",cluster.getId(), cluster.getName(),zdev.getIeeeAddress(),Integer.toHexString(ep.getId()));					
					if (!deviceManager.isBeingRediscovered(zdev.getShortAddress())) {
						//ahumanes: si el mismo cluster esta en otro end point no se deberia configurar ese reporte
						if(!checkClusterConfiguratedInOtherEndPoint(devCluster,zdev,ep)){
							sendRequiredClusterFramesToDevice(devCluster, ep.getId());
						}
					}
				}
			}
		}
		// CLUSTER DE SALIDA

		for (int i = 0; i < zf.getOutClusterList().length; i++) {
			ZclCluster cluster = zclservice.getZcl().getClusters().get(zf.getOutClusterList()[i]);
			if (cluster == null) {
				// Añadimos a la lista de clusters desconocidos por si son
				// incluidos en un futuro en el fichero zcl.xml
				synchronized (deviceManager.devices) {
					if (!ep.getUnknownClusters().contains(zf.getOutClusterList()[i])) {
						ep.putUnknownCluster(zf.getOutClusterList()[i]);
					}
				}
			}

			// Si el dispositivo ya tiene el cluster en el mismo endpoint, no
			// hacemos nada
			synchronized (deviceManager.devices) {
				if (cluster != null && !ep.getClusters().containsKey(cluster.getId())) {
					areNewClusters = true;
					ZclCluster devCluster = (ZclCluster) cluster.clone();
					devCluster.setDevice(zdev);
					ep.putCluster(devCluster);
					LOG.info("Created cluster {} {} on device {} ep 0x{}",cluster.getId(),cluster.getName()
							,zdev.getIeeeAddress(),Integer.toHexString(ep.getId()));
					if (!deviceManager.isBeingRediscovered(zdev.getShortAddress())) {
						//ahumanes: si el mismo cluster esta en otro end point no se deberia configurar ese reporte
						if(!checkClusterConfiguratedInOtherEndPoint(devCluster,zdev,ep)){
							sendRequiredClusterFramesToDevice(devCluster,ep.getId());
						}
					}
				}
			}
		}

		return areNewClusters;
	}

	private boolean checkClusterConfiguratedInOtherEndPoint(ZclCluster devCluster, ZclDevice zdev, ZclEndpoint ep) {
		// devCluster.
		// boolean configuratedInOtherEP=false;
		for (ZclEndpoint zep : zdev.getEndpoints().values()) { // recorremos todos los enpoints
			ZclCluster zcl = zep.getCluster(devCluster.getId());
			if (ep.getId() != zep.getId()) { // comprobamos que no miremos en el mismo end point
				if (zcl != null) { // esta el cluster en ese endpoint
					if (!zcl.isConfigured()) {
						if (allEndpointsAreFalse(zcl, zdev)) {
							zcl.setConfigured(true);
							return false;
						}
					}
				}
			} else {
				if (allEndpointsAreFalse(devCluster, zdev)) {
					zcl.setConfigured(true);
					return false;
				}
			}

		}
		return true;

	}

	private boolean allEndpointsAreFalse(ZclCluster devCluster,ZclDevice zdev) {
		for (ZclEndpoint zep : zdev.getEndpoints().values()){
			ZclCluster zcl= zep.getCluster(devCluster.getId());
			if(zcl!=null){
				if(zcl.isConfigured()){
					return false;
				}
			}
		}
		
		return true;
	}

	private void sendRequiredClusterFramesToDevice(ZclCluster devCluster, byte enpoint) {
		this.serial.writeZFrames(this.coordinator.initClient(devCluster.getId(), devCluster.getDevice()));
		waitMs(100);
		ZFrame[] configAttributesFrames = null;
		try {
			configAttributesFrames = devCluster.configureAttributes(this.coordinator.getIeeeAddress(),(int) enpoint);
		} catch (UnknownCoordinatorMacException e1) {
			this.serial.manageZclUnknownCoordinatorMacException();
		}
		ZFrame[] configCommandsFrames = devCluster.configureCommands();
		this.serial.writeZFrames(configAttributesFrames);
		waitMs(500);
		this.serial.writeZFrames(configCommandsFrames);
	}

	private void manageZDO_NODE_DESC_RSP(ZFrame incZFrame) {
		ZdoNodeDescRsp incNodeDescRsp = new ZdoNodeDescRsp(incZFrame.getData());
		extractAndSaveDataFromNodeDescRsp(incNodeDescRsp);
		ZclDevice zdev = deviceManager.devices.get(incNodeDescRsp.getSrcAddress());
		if (zdev != null) {
			if (zdev.isPendingKnowManCode()) {
				LOG.info("ManCode {} received from {}",incNodeDescRsp.getManufacturerCode(),zdev.getIeeeAddress());
				zdev.setPendingKnowManCode(false);
			}else {
				sendActiveEpReq(incNodeDescRsp.getNwkAddr());
			}
		}
	}

	private void manageUTIL_GET_DEVICE_INFO_RESPONSE(ZFrame frame) {
		UtilGetDeviceInfoResponse zf = new UtilGetDeviceInfoResponse(frame.getData());
		startChipIfHoldState(zf);
		this.coordinator.initCoordinator(zf.getIeeeAddr());
		LOG.info("Llega el manage util rsp");
		this.coordinator.setInitAssocDevices(zf.getNumAssocDevices());

		for (int i = 0; i < zf.getNumAssocDevices(); i++) {
			DoubleByte shortAddr = zf.getAssocDevicesList()[i];
			LOG.debug("Found new device with short address: {}",shortAddr);
			ZclDevice dev = deviceManager.devices.get(shortAddr);
			if (dev == null) {
				LOG.debug("Rebuilding device in memory: {}",shortAddr);
				this.serial.getIeeeAddrReqSent().put(shortAddr, new Timestamp(new Date().getTime()));
				ZdoIeeeAddrReq ziar = new ZdoIeeeAddrReq(shortAddr, false);
				this.serial.writeZFrame(ziar);
			}
		}
		if (this.deviceMonitoring==null){
			this.deviceMonitoring=new DeviceMonitoring(this.serial, coordinator,this.deviceManager);
			this.timerDeviceMonitoring.schedule(deviceMonitoring,DeviceMonitoring.DELAY_MS, DeviceMonitoring.PERIOD_MS);
		}
	}

	private void startChipIfHoldState(UtilGetDeviceInfoResponse zf) {
		if (zf.getDeviceState() == UtilGetDeviceInfoResponse.DEV_HOLD_VALUE) {
			LOG.info("Sending start request");
			ZbStartRequest startFrame = new ZbStartRequest();
			this.serial.writeZFrame(startFrame);
			waitMs(1000);
		}
	}

	private void manageZB_GET_DEVICE_INFO_RSP(ZFrame frame) {

		ZbGetDeviceInfoRsp zf = new ZbGetDeviceInfoRsp(frame.getData());
		StringBuilder log = new StringBuilder();
		log.append("GET INFO RSP received. " + ZbGetDeviceInfoRsp.getItemIds()[zf.getItemId()] + ":");
		if (ZbGetDeviceInfoRsp.getItemIds()[zf.getItemId()].equals("CHANNEL")) {
			this.coordinator.setReceivedChannel((byte) zf.getItemValue()[0]);
			this.coordinator.setZbchannel((byte) zf.getItemValue()[0]);
		}
		
		for (int i = 0; i < zf.getItemValue().length; i++) {
			log.append(" 0x" + Integer.toHexString(zf.getItemValue()[i]));
		}
		if(LOG.isDebugEnabled()) {
		  LOG.debug(log.toString());
		}
	}

	/**
	 * Manage an AF_INCOMING_MSG for which no device or no cluster in a device
	 * has been found
	 */
	private void manageUnknownAF(AfIncomingMsg af) {
		if (af.getNwkAddr().equals(new DoubleByte(0))
				|| (!af.isClusterSpecific() && !(af.getZclCmd() == ZClusterLibrary.ZCL_CMD_REPORT))) {
			return;
		}
		ZclDevice zdev = deviceManager.devices.get(af.getNwkAddr());
		OctaByte devMac = null;
		if (zdev == null) {
			zdev = new ZclDevice(ZigbeeConstants.INVALID_IEEE_ADDRESS, af.getNwkAddr());
			deviceManager.addRediscoveryDevice(af.getNwkAddr());
			synchronized (deviceManager.devices) {
				deviceManager.devices.put(zdev.getShortAddress(), zdev);
			}
		} else {
			devMac = zdev.getIeeeAddress();
		}

		if (af.getClusterID().equals(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE) && af.isServerToClient()
				&& af.isClusterSpecific() && af.getZclCmd() == IASZoneClient.ZONE_STATUS_CHANGE_NOTIFICATION_CMD_ID) {
			this.serial.writeZFrames(this.coordinator.sendFrameToClient(af, zdev,mqttConnection));
		}
		if (af.getClusterID().equals(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE) && af.isServerToClient()
				&& af.isClusterSpecific() && af.getZclCmd() == IASZoneClient.ZONE_ENROLL_REQUEST_CMD_ID) {
			this.serial.writeZFrames(this.coordinator.sendFrameToClient(af, zdev,mqttConnection));
		}

		ZclCluster unkCluster = zdev.getZclCluster(af.getClusterID());
		if (unkCluster != null) {
			// TODO Unreachable code?
			List<MqttMsgDetail> mqttmsgs=unkCluster.manageAFIncomingMSG(af);
			this.mqttConnection.sendThroughMqtt(mqttmsgs);
			
		} else {
			ZclCluster receivedCluster = zclservice.getZcl().getClusters().get(af.getClusterID());
			if (receivedCluster == null) {
				return;
			}
			unkCluster = (ZclCluster) receivedCluster.clone();
			ZclEndpoint zep = new ZclEndpoint((byte) af.getSrcEndpoint());
			synchronized (deviceManager.devices) {
				zdev.putZclEndpoint(zep);
				unkCluster.setDevice(zdev);
				zdev.putZclCluster(unkCluster, (byte) af.getSrcEndpoint());
				sendRequiredClusterFramesToDevice(unkCluster,zep.getId());
			}
			LOG.info("Created cluster {} {} on unknown device ({})",unkCluster.getId(),unkCluster.getName(),zdev.getShortAddress());
		}
		if (devMac != null && !devMac.equals(ZigbeeConstants.INVALID_IEEE_ADDRESS)) {
			LOG.info("CLUSTER UNKNOWN OF ZIGBEE DEVICE: {} {}",devMac,af.getNwkAddr());
		} else {
			// Si no tenemos la IEEE address, cuando llegue enviaremos el UPDATE
			this.serial.getIeeeAddrReqSent().put(af.getNwkAddr(), new Timestamp(new Date().getTime()));
			LOG.info("UNKNOWN DEVICE ({}). SENDING IEEE_ADDRESS_REQUEST.",af.getNwkAddr());
			this.serial.getIeeeAddrReqSent().put(af.getNwkAddr(), new Timestamp(new Date().getTime()));
			ZdoIeeeAddrReq req = new ZdoIeeeAddrReq(af.getNwkAddr(), false);
			this.serial.writeZFrame(req);
		}
		ZdoNodeDescReq zndr = new ZdoNodeDescReq(af.getNwkAddr());
		this.serial.writeZFrame(zndr);
	}

	private void manageCoordinatorAF(AfIncomingMsg af) {
		List<MqttMsgDetail> mqttmsgs;
		if (af.getZclCmd() == ZClusterLibrary.ZCL_CMD_READ_RSP) {
			ZclCluster cluster = this.coordinator.getZclCluster(af.getClusterID());
			if (cluster != null) {
				mqttmsgs=cluster.manageAFIncomingMSG(af);
				this.mqttConnection.sendThroughMqtt(mqttmsgs);
			}
		}
	}

	private void manageAF_INCOMING_MSG(ZFrame frame) {
		AfIncomingMsg incomingAf = new AfIncomingMsg(frame.getData());

		if (!deviceManager.deviceRecentlyDeleted(incomingAf.getNwkAddr())) {

			if (incomingAf.isZDO()) {
				manageAFZDO(incomingAf);
			} else {
				if (incomingAf.getNwkAddr().equals(new DoubleByte(0))) {
					manageCoordinatorAF(incomingAf);
					return;
				}
				// Que provenga del cluster OTA y ademas que sea de cluster
				// especific la respuesta
				if (incomingAf.getClusterID().equals(ZClusterLibrary.ZCL_CLUSTER_ID_OTA) && incomingAf.isClusterSpecific()) {
					manageOTAmessage(incomingAf);

					// incomingAf.getZclCmd()
				} else {
					ZclDevice zdev = deviceManager.devices.get(incomingAf.getNwkAddr());
					if (zdev == null || zdev.getIeeeAddress().equals(ZigbeeConstants.INVALID_IEEE_ADDRESS)) {
						manageUnknownAF(incomingAf);
						return;
					}
					ZclCluster cluster = zdev.getZclCluster(incomingAf.getClusterID());
					if (cluster == null) {
						manageUnknownAF(incomingAf);
						return;
					}
						this.serial.writeZFrames(this.coordinator.sendFrameToClient(incomingAf, zdev,this.mqttConnection));
					if (incomingAf.isClusterSpecific()) {
						synchronized (deviceManager.devices) {

							ZFrame[] frames = cluster.manageAFClusterSpecific(incomingAf);
							serial.writeZFrames(frames);
						}

						// Deber�a ser solo la derecha del OR. Parche para EDP
					} else if (incomingAf.getZclCmd() == ZClusterLibrary.ZCL_CMD_REPORT
							|| incomingAf.isServerToClient()) {
						this.serial.stopRetries(incomingAf);
						synchronized (deviceManager.devices) {
							//ahumanes: esto estaba antes decomentado:
							
							DoubleByte manuCode=zdev.getManufacturerCode();
							if (manuCode==null) {
								if(!zdev.isPendingKnowManCode()) {
									ZdoNodeDescReq zaer = new ZdoNodeDescReq(zdev.getShortAddress());
									zdev.setPendingKnowManCode(true);
									serial.writeZFrame(zaer);
									LOG.info("Unknown manCode on dev: {}",zdev.getIeeeAddress());
									LOG.info("Sending node desc");
								}
							}
							List<MqttMsgDetail> mqttmsgs=cluster.manageAFIncomingMSG(incomingAf);
							this.mqttConnection.sendThroughMqtt(mqttmsgs);
							
						}
					//parche para emter de SE que mandaba default rsp como respuestas a comandos como
					//conf_report o read_req
					}else if(incomingAf.getZclCmd() == ZClusterLibrary.ZCL_CMD_DEFAULT_RSP){
						synchronized (deviceManager.devices) {
						cluster.manageDefaultRspToServer(incomingAf);
						}
					}
				}
			}

		}
	}
	


	private synchronized void  manageOTAmessage(AfIncomingMsg incomingAf) {
		//mirar de que tipo de mensaje se trata
		int cmd=incomingAf.getZclCmd();
		DoubleByte nwkAddress=incomingAf.getNwkAddr();
		ZclDevice dev=deviceManager.getDeviceByNwkAddress(nwkAddress);
		//este wait es de prueba
//		waitMs(100);
		waitMs(200);
		if(dev.getOtamanager().isAbort()){
			
			ImageBlockResponse imBlockRsp = new ImageBlockResponse(ZClusterLibrary.ZCL_STATUS_OTA_ABORT);
			int[] zclFrame = buildZclFrame(true, incomingAf.getZclSeqNumber() + 1,
					ZClusterLibrary.ZCL_OTA_IMAGE_BLOCK_RSP, imBlockRsp.getFrame());
			
			ZFrame zframe = new AfDataRequest(dev.getShortAddress(),
					dev.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
					ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0,
					zclFrame);
			dev.getOtamanager().setAbort(false);
			dev.getOtamanager().setInstalling(false);
			dev.getOtamanager().setOta(false);
			this.serial.writeZFrame(zframe);
			
		}else if(cmd==ZClusterLibrary.ZCL_OTA_QUERY_NEXT_IMAGE_REQ){
			manageQueryNextImageReq(dev,incomingAf);
			
			
		}else if(cmd==ZClusterLibrary.ZCL_OTA_IMAGE_BLOCK_REQ){
			manageImageBlockReq(dev,incomingAf);
			
		}
		else if(cmd==ZClusterLibrary.ZCL_OTA_UPGRADE_END_REQ){
			manageUpgradeEndReq(dev,incomingAf);
		}
		
		
		
		
	}

	private void manageUpgradeEndReq(ZclDevice dev, AfIncomingMsg incomingAf) {
		int [] zclPayload =incomingAf.getZclPayload();
		UpgradeEndRequest upgradeEndReq=new UpgradeEndRequest(zclPayload);
		OtaManager otamanager =dev.getOtamanager();
//		if(otamanager.isOta()){
//		otamanager.setInstalling(true);
//		}
		if (upgradeEndReq.getStatus()==0x00 && otamanager.isOta()){
		FourByte[] time=getCurrentTime();
		
		
		UpgradeEndResponse upgradeEndRsp = new UpgradeEndResponse(upgradeEndReq.getManufactureCode(),
				upgradeEndReq.getImageType(), upgradeEndReq.getFileVersion(), time[0], time[1]);
		int[] zclFrame = buildZclFrame(true, incomingAf.getZclSeqNumber() + 1,
				ZClusterLibrary.ZCL_OTA_UPGRADE_END_RSP, upgradeEndRsp.getFrame());
		
		ZFrame zframe = new AfDataRequest(dev.getShortAddress(),
				dev.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
				ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0,
				zclFrame);
		
		this.serial.writeZFrame(zframe);
		LOG.info("Ota proccess: confirmation received from: {} .Download completed",dev.getIeeeAddress());
		LOG.info("Dev: {} installing the new image ",dev.getIeeeAddress());
		LOG.info("TimerTask created for check the installation");
		dev.setStatus(DeviceStatus.UPGRADING);
		Timer upgradeOtaCheck = new Timer("TimerUpgradeOtaCheck", Boolean.TRUE);
		upgradeOtaCheck.schedule(new OtaUpgradeMonitoring(this.serial,dev,upgradeOtaCheck),
				OtaUpgradeMonitoring.DELAY_MS, OtaUpgradeMonitoring.PERIOD_MS);
		otamanager.setInstalling(true);
		}else if((upgradeEndReq.getStatus()==ZClusterLibrary.ZCL_STATUS_OTA_ABORT) || (upgradeEndReq.getStatus()==ZClusterLibrary.ZCL_STATUS_OTA_INVALID_IMAGE)){
			LOG.info("Dev: {} has aborted the download",dev.getIeeeAddress());
			otamanager.setOta(false);
			otamanager.setInstalling(false);
			otamanager.setOtaRequest(false);
		}
		
		
	}


	private FourByte[] getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		long secsSinceEpoch = calendar.getTime().getTime() / 1000;
		long secsSince20000101 = secsSinceEpoch - ZigbeeConstants.TIME_STANDARD_ORIGIN_EPOCH_SEC;
		//FourByte upgradeTime=getUpgradeTime(secsSince20000101);
		FourByte currentTime=new FourByte(secsSince20000101);
		FourByte upgradeTime=currentTime;
		FourByte[] array=new FourByte[2];
		array[0]=upgradeTime;
		array[1]=currentTime;
		return array;
		
	}
	/*private FourByte getUpgradeTime(long time){

		long days=time / (86400);
		long hours=(time % 86400) / 3600;
		long min=(((time % 86400) % 3600) / 60);
        long seconds = ((time % 86400) % 3600) % 60;
        
        FourByte upgradeTime= new FourByte(time);
        
		return upgradeTime;
	}*/

	private void manageImageBlockReq(ZclDevice dev, AfIncomingMsg incomingAf) {
		int [] zclPayload =incomingAf.getZclPayload();
		ImageBlockRequest imBlockReq=new ImageBlockRequest(zclPayload);
		byte[] buffer=readOtaFile(dev,imBlockReq);
		if(buffer!=null){
			ZFrame zframe=null;
			//mirar a ver si hay que cancelar o no
			if(dev.getOtamanager().isAbort()){
				
				ImageBlockResponse imBlockRsp = new ImageBlockResponse(ZClusterLibrary.ZCL_STATUS_OTA_ABORT);
				int[] zclFrame = buildZclFrame(true, incomingAf.getZclSeqNumber() + 1,
						ZClusterLibrary.ZCL_OTA_IMAGE_BLOCK_RSP, imBlockRsp.getFrame());
				
				zframe = new AfDataRequest(dev.getShortAddress(),
						dev.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
						ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0,
						zclFrame);
				dev.getOtamanager().setAbort(false);
				dev.getOtamanager().setInstalling(false);
				dev.getOtamanager().setOta(false);
				
				
			}else{
			//QueryNextImageResponse nextImRsp = new QueryNextImageResponse(0, nextImReq.getManufacturaCode(),
				//	nextImReq.getImageType(), version2upgrade,dev.getOtamanager().getNewImageSize());
			ImageBlockResponse imBlockRsp = new ImageBlockResponse(0, imBlockReq.getManufactureCode(),
					imBlockReq.getImageType(), imBlockReq.getFileversion(), imBlockReq.getFileoffset(), buffer.length,
					buffer);
			int[] zclFrame = buildZclFrame(true, incomingAf.getZclSeqNumber() + 1,
					ZClusterLibrary.ZCL_OTA_IMAGE_BLOCK_RSP, imBlockRsp.getFrame());
			
			zframe = new AfDataRequest(dev.getShortAddress(),
					dev.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
					ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0,
					zclFrame);
			}
			
			this.serial.writeZFrame(zframe);
			
		}	
	}

	private byte[] readOtaFile(ZclDevice dev, ImageBlockRequest imBlockReq) {
		OtaManager otamanager = dev.getOtamanager();
		String filepath = otamanager.getFilePath();
		File fichero = new File(filepath);

		if (fichero.exists()) {
			Integer data2read = new Integer(imBlockReq.getMaximumDataSize() - (imBlockReq.getMaximumDataSize() / 8));
//			Integer data2read=new Integer (imBlockReq.getMaximumDataSize());
			byte[] buffer = new byte[data2read];
			long fileOffset = imBlockReq.getFileoffset().longValue();
			long imageSize = otamanager.getNewImageSize().longValue();
			try(BufferedInputStream bufferlectura = new BufferedInputStream(new FileInputStream(filepath))) {
			
			// LOG.info("File offset: "+String.valueOf(fileOffset));
			if (imageSize - fileOffset < data2read.longValue()) {
				data2read = (int) (long) (imageSize - fileOffset);
				LOG.info("Finishing the download");
			}
			buffer = new byte[data2read];
			if(bufferlectura.skip(fileOffset)>=0) {
				bufferlectura.read(buffer, 0, buffer.length);
				bufferlectura.close();
			}
			}catch (Exception e) {
				LOG.error("Error creating the bufferInputStream");
			}
			long aux = (fileOffset + data2read) * 100;
			long porcentaje = aux / imageSize;
			otamanager.setPorcentaje(porcentaje);
			// LOG.info("Porcentaje :"+String.valueOf(porcentaje))
//			printLog(porcentaje,dev);
//			System.out.println("\nDatos enviados: "+String.valueOf((fileOffset+data2read)));
//			System.out.println("Porcentaje "+porcentaje);

			if (imageSize == (fileOffset + data2read)) {
				LOG.info("File OTA transfer completed in dev: {}. Waiting for confirmation from device", dev.getIeeeAddress());
			}
			return buffer;

		} else {
			return null;
			// revisar lo que hacer aqui
		}

	}

//	private void printLog(long porcentaje, ZclDevice dev) {
//		if (porcentaje%10==0){
//			LOG.info("Ota download: "+porcentaje+" % in dev: "+dev.getIeeeAddress().toString());
//		}
//		
//	}

	private void manageQueryNextImageReq(ZclDevice dev, AfIncomingMsg incomingAf) {
		// sacamos la version del mensaje

		int[] zclPayload = incomingAf.getZclPayload();
		QueryNextImageRequest nextImReq = new QueryNextImageRequest(zclPayload);
		OtaManager otamanager = dev.getOtamanager();
		FourByte version2upgrade = otamanager.getVersion2upgrade();
		if (otamanager.isOtaRequest()) {
			if (otamanager.isInstalling()) {
				//leer la version del fichero y setear la current version
				otamanager.setCurrentVersion(nextImReq.getCurrentFileVersion());
//				if (version2upgrade.equals(nextImReq.getCurrentFileVersion())){
//					
//				}
				
			} else {
				// comparar versiones de fichero
			
				if (checkVersion(version2upgrade, nextImReq.getCurrentFileVersion())) {
					// empezar ota
					// generar nextImageResponse
					otamanager.setCurrentVersion(nextImReq.getCurrentFileVersion());
					QueryNextImageResponse nextImRsp = new QueryNextImageResponse(0, nextImReq.getManufacturaCode(),
							nextImReq.getImageType(), version2upgrade, dev.getOtamanager().getNewImageSize());
					int[] zclFrame = buildZclFrame(true, incomingAf.getZclSeqNumber() + 1,
							ZClusterLibrary.ZCL_OTA_QUERY_NEXT_IMAGE_RSP, nextImRsp.getFrame());
					ZFrame zframe = new AfDataRequest(dev.getShortAddress(),
							dev.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
							ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0,
							0, zclFrame);
					// paramos la reconfiguracion de reporte
					otamanager.setOta(true);
					// guardamos la version antigua del dispositivo
					otamanager.setCurrentVersion(nextImReq.getCurrentFileVersion());
					// mandamos el mensaje
					this.serial.writeZFrame(zframe);
					ZigbeeService.otaLaunched=true;
					LOG.info("Iniciando la actualizacion del dispositivo");
				} else {
					Alert alert = new Alert(com.onesait.edge.engine.zigbee.util.State.INFO, AlertType.DEVICE_UP2DATE.toString(), dev.getIeeeAddress().toString(),dev.getGeneralDeviceType().toString());
					ZigbeeService.otaLaunched=false;
					ZigbeeService.getAlertList().add(alert);
					ZigbeeService.checkAlertList();
					LOG.info("Dispositivo con la ultima version");
					otamanager.setInstalling(false);
					otamanager.setOta(false);
					otamanager.setOtaRequest(false);
				}
			}
		} else {
			// mandar una respuesta con status=NO_IMAGE_available
			QueryNextImageResponse nextImRsp = new QueryNextImageResponse(
					ZClusterLibrary.ZCL_STATUS_OTA_NO_IMAGE_AVAILABLE);
			int[] zclFrame = buildZclFrame(true, incomingAf.getZclSeqNumber() + 1,
					ZClusterLibrary.ZCL_OTA_QUERY_NEXT_IMAGE_RSP, nextImRsp.getFrame());
			
			//almacenar la informacion que viene en el mensaje
			if(otamanager.isInfoRequested()){
				otamanager.setCurrentVersion(nextImReq.getCurrentFileVersion());
				otamanager.setInfoRequested(false);
			}
			
			
			ZFrame zframe = new AfDataRequest(dev.getShortAddress(),
					dev.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
					ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0,
					zclFrame);
			this.serial.writeZFrame(zframe);
		}

	}

	private int[] buildZclFrame(boolean clusterSpecific, int seqNumber, int cmdId,int[] zclPayload) {
		int pos = 0;
		int [] zclFrame=new int[zclPayload.length+3];
		
		if (clusterSpecific) {
			// int [] zclHeader=new int [5];
			zclFrame[pos++] = 0x19; // cluster specific,Direction to client,
										// Disable Default Rsp=true
		} else {
			zclFrame[pos++] = 0x00;
		}
		if(seqNumber>=256){
			seqNumber=0;
		}
		zclFrame[pos++] = seqNumber;
		zclFrame[pos++] = cmdId;
		for(int i=0;i<zclPayload.length;i++){
			zclFrame[pos++]=zclPayload[i];
		}
		return zclFrame;
	
	}

	private boolean checkVersion(FourByte version2upgrade, FourByte versionInDevice) {
		return version2upgrade.greaterThan(versionInDevice);

	}

	private void manageZDO_MGMT_LQI_RSP(ZFrame frame) {

		ZdoMgmtLqiRsp fr = new ZdoMgmtLqiRsp(frame.getData());

		for (NeighborTableEntry nte : fr.getNeighbors()) {
			ZMeshLink link = new ZMeshLink(fr.getSrcAdd(), nte.networkAddress, nte.lqi, nte.depth, nte.extendedAddress,
					nte.extendedPanID, nte.permitJoining, nte.deviceInfo);
			if (link.getDepth() > 0)
				this.coordinator.getzMesh().addLink(link);
		}
	}

	private void manageZDO_MGMT_RTG_RSP(ZFrame frame) {

		ZdoMgmtRtgRsp fr = new ZdoMgmtRtgRsp(frame.getData());

		for (RoutingTableEntry rte : fr.getRtgTableList()) {
			RMeshLink link = new RMeshLink(fr.getSrcAddr(), rte.getDstAddr(), rte.getNextHop(), rte.getStatus());
			this.rMesh.addLink(link);
		}
	}

	private void manageZDO_MGMT_PERMIT_JOIN_RSP(ZFrame frame) {
		LOG.info("LLLEGA TRAMA DE ABRIR LA RED");
		ZdoMgmtPermitJoinRsp pjresp = new ZdoMgmtPermitJoinRsp(frame.getData());
		if (pjresp.getStatus() == 0 && !this.coordinator.getActivenet()) {
			// TODO Mal, poner synchronized en alguna parte
			this.coordinator.setActivenet(true);
//			RequestMaps.manageAssociatedZdoRequest(pjresp, SignalType.ACTIVENET);
		}
	}

	private void manageAFZDO(AfIncomingMsg af) {

		int statusIdx = 1;
		if (af.getClusterID().equals(ZDOCommand.ManagementLQIResponse)) {
			LOG.debug("[ZDO INCOMING MSG, LQI RSP:" + af.toString() + "]");
			if (af.getAfIncomingData()[statusIdx] == ZClusterLibrary.ZCL_STATUS_SUCCESS) {
				int framedata[] = new int[af.getAfIncomingData().length + 1];
				framedata[0] = af.getNwkAddr().getLsb();
				framedata[1] = af.getNwkAddr().getMsb();
				for (int i = 2; i < af.getAfIncomingData().length + 1; i++){
					framedata[i] = af.getAfIncomingData()[i - 1];
				}
				ZdoMgmtLqiRsp zmlr = new ZdoMgmtLqiRsp(framedata);
				LOG.debug("MENSAJE LQI RECIBIDO:" + zmlr);
				this.manageZDO_MGMT_LQI_RSP(zmlr);
				if (zmlr.nextPage() != -1) {
					this.serial.writeZFrame(new ZdoMgmtLqiReq(zmlr.getSrcAdd(), zmlr.nextPage()));
				}
			}
		} else if (af.getClusterID().equals(ZDOCommand.ManagementRtgResponse)) {
			LOG.info("[ZDO INCOMING MSG, RTG RSP: {} ]", af);
			if (af.getAfIncomingData()[statusIdx] == ZClusterLibrary.ZCL_STATUS_SUCCESS) {
				int framedata[] = new int[af.getAfIncomingData().length - 1];
				framedata[0] = af.getNwkAddr().getLsb();
				framedata[1] = af.getNwkAddr().getMsb();
				for (int i = 2; i < af.getAfIncomingData().length - 1; i++)
					framedata[i] = af.getAfIncomingData()[i - 1];
				ZdoMgmtRtgRsp zmrr = new ZdoMgmtRtgRsp(framedata);
				LOG.debug("RTG MSG RECEIVED: {}",zmrr);

				DoubleByte srcAddr = zmrr.getSrcAddr();
				LOG.debug("Route Response from  {}",srcAddr);
				String routeEntriesStr = "Paths: ";
				for (int i = 0; i < zmrr.getRtgTableListCount(); i++) {
					RoutingTableEntry rte = zmrr.getRtgTableEntry(i);
					routeEntriesStr += rte.toString() + " ";
				}
				LOG.debug(routeEntriesStr);

				this.manageZDO_MGMT_RTG_RSP(zmrr);
				if (zmrr.getNextPage() != -1) {
					this.serial.writeZFrame(new ZdoMgmtRtgReq(zmrr.getSrcAddr(), zmrr.getNextPage()));
				}
			}
		} else if (af.getClusterID().equals(ZDOCommand.NetworkAddressRequest)) {
			DoubleByte srcAddrDb = af.getNwkAddr();
			synchronized (this.coordinator.getNwkAddrRequests()) {
				this.coordinator.getNwkAddrRequests().put(srcAddrDb, new Timestamp(new Date().getTime()));
			}
		} else if (af.getClusterID().equals(ZDOCommand.ManagementNetworkUpdateNotify)) {
			// TODO: Añadir scannedChannels a la clase NWK UPDATE NOTIFY y
			// comprobar si el canal actual esta en la lista
//			synchronized (this.coordinator.getChannel2Change2()) {
				if (af.getAfIncomingData()[statusIdx] == ZClusterLibrary.ZCL_STATUS_SUCCESS) {

					int framedata[] = new int[af.getAfIncomingData().length - 1];
					framedata[0] = af.getNwkAddr().getLsb();
					framedata[1] = af.getNwkAddr().getMsb();
					for (int i = 2; i < af.getAfIncomingData().length-1; i++){
					framedata[i] = af.getAfIncomingData()[i + 1];
					}
					ZdoMgmtNwkUpdateNotify zmnun = new ZdoMgmtNwkUpdateNotify(framedata,new DoubleByte(0x0000));
					selectBestChannelAndShowJamming(zmnun);
				}
//			}
		}
	}

	private void selectBestChannelAndShowJamming(ZdoMgmtNwkUpdateNotify zmnun) {
		if (zmnun.getSrcAddr().equals(ZigbeeConstants.COORDINATOR_SHORT_ADDRESS)) {
			int[] noiseLevels = zmnun.getChSignalStrengths(); // Obtiene los
																// niveles de
																// ruido de
																// todos los
																// canales
			this.coordinator.setChannelEnergyLevels(noiseLevels);
			Byte currentChannel = getValidCurrentChannel();
			int actualChannelStrength = noiseLevels[currentChannel - ZigbeeConstants.LOWEST_CHANNEL];
			int minNoiseLevel = this.coordinator.channelOverlapsWifi(currentChannel) ? noiseLevels[0] : actualChannelStrength;

			for (int i = 0; i < noiseLevels.length; i++) {
				LOG.debug("Energy: " + noiseLevels[i] + " Channel: 0x"
						+ Integer.toHexString(i + ZigbeeConstants.LOWEST_CHANNEL));
				if (noiseLevels[i] < minNoiseLevel) {
					boolean channelSet = this.coordinator.setChannel2Change2((byte) (i + ZigbeeConstants.LOWEST_CHANNEL));
					minNoiseLevel = channelSet ? noiseLevels[i] : minNoiseLevel;
				}
			}
			if (minNoiseLevel > JAMMING_THRESHOLD) {
				LOG.info("Warning: Possible Jamming detected");
			}
			if (this.coordinator.getChannel2Change2().intValue() != 0
					&& this.coordinator.getChannel2Change2().intValue() != currentChannel.intValue()) {
				LOG.info("New best zigbee channel found: {}",this.coordinator.getChannel2Change2());
			} else {
				LOG.debug("Current channel has the best quality.");
			}
		}
	}

	private Byte getValidCurrentChannel() {
		Byte currentChannel = this.coordinator.getZbchannel();
		if (currentChannel == null || currentChannel.byteValue() < ZigbeeConstants.LOWEST_CHANNEL
				|| currentChannel.byteValue() > ZigbeeConstants.HIGHEST_CHANNEL) {
			currentChannel = ZclCoordinator.getDefaultZbChannel();
			this.coordinator.setChannel2Change2(currentChannel);
		}
		return currentChannel;
	}

	private void extractAndSaveDataFromNodeDescRsp(ZdoNodeDescRsp nodeDescRsp) {
		ZclDevice zdev = deviceManager.devices.get(nodeDescRsp.getSrcAddress());
		if (zdev != null) {
			synchronized (deviceManager.devices) {
				zdev.setManufacturerCode(nodeDescRsp.getManufacturerCode());
				zdev.setDeviceType((byte) nodeDescRsp.getNodeType());
				zdev.setCapabilities((byte) nodeDescRsp.getCapabilities());
			}
		}
	}

	private void sendActiveEpReq(DoubleByte dstAddress) {
		ZdoActiveEpReq zaer = new ZdoActiveEpReq(dstAddress, dstAddress);
		this.serial.writeZFrame(zaer);
	}

	private void sendNodeDescReq(DoubleByte dstAddress) {
		ZdoNodeDescReq zaer = new ZdoNodeDescReq(dstAddress);
		this.serial.writeZFrame(zaer);
	}

	private void updateToIoIfClustersReceived(ZclDevice zdev, ZclEndpoint ep) {
		ZclDevicetype devId = zclservice.getZcl().getDeviceIds().get(ep.getDeviceId());
		if (devId != null) {
			waitMs(WAIT_FOR_ATTRIBUTES_TO_SEND_UPDATE_MS);
			if (devId.getRequiredServerClusters().size() > 0) {
				boolean allClustersReceived = true;
				for (DoubleByte reqClusterId : devId.getRequiredServerClusters().keySet()) {
					if (ep.getCluster(reqClusterId) == null) {
						allClustersReceived = false;
						break;
					}
				}
				if (allClustersReceived) {
					this.deviceManager.saveDeviceTypeByDeviceId(zdev.getIeeeAddress().toString());
					Alert alert = new Alert(com.onesait.edge.engine.zigbee.util.State.INFO, AlertType.DEVICE_JOINED.toString(), zdev.getIeeeAddress().toString(),zdev.getGeneralDeviceType().toString());
					ZigbeeService.getAlertList().add(alert);
					ZigbeeService.checkAlertList();
					LOG.info("All clusters required for device {} with device id {} received. UPDATE sent.",zdev.getIeeeAddress(),devId.getName());
					LOG.info("General deviceType: {}",zdev.getGeneralDeviceType());
					deviceManager.removeRediscoveryDevice(zdev.getShortAddress());
				}
			}
		}
	}

	private void removeEndpointFromDevice(ZdoSimpleDescRsp zf) {
		ZclDevice zdev = deviceManager.devices.get(zf.getNwkAddr());
		if (zdev == null)
			return;
		synchronized (deviceManager.devices) {
			zdev.removeZclEndpoint((byte) zf.getEndpoint());
		}
	}

	private void flushDevice(ZclDevice zdev) {
		ZclDevice newDev;
		synchronized (deviceManager.devices) {
			newDev = (ZclDevice) zdev.clone();
		}
		deviceManager.flushDevice(newDev);
	}

	private boolean updateShortAddress(DoubleByte srcAddr, OctaByte mac) {
		ZclDevice oldShortAddressDev = deviceManager.getDeviceByMac(mac);
		if (oldShortAddressDev != null) {
			deviceManager.changeDeviceShortAddress(oldShortAddressDev, srcAddr);
			return true;
		}
		return false;
	}

	private void constructNewDevice(ZdoEndDeviceAnnceInd endDevAnnce) {
		ZclDevice zdev = new ZclDevice(endDevAnnce);
		synchronized (deviceManager.devices) {
			deviceManager.devices.put(zdev.getShortAddress(), zdev);
		}
//		EventManagerZigbee.updateToIo(zdev);
	}

	private void constructNewDevice(DoubleByte srcAddress, OctaByte mac) {
		ZclDevice zdev = new ZclDevice(mac, srcAddress);
		synchronized (deviceManager.devices) {
			deviceManager.devices.put(zdev.getShortAddress(), zdev);
		}
	}

	private void checkTemporalDevice(ZclDevice zdev, DoubleByte shortAddress, OctaByte mac) {
		boolean deviceShortAddressChanged = updateShortAddress(shortAddress, mac);
		if (zdev.getIeeeAddress().equals(ZigbeeConstants.INVALID_IEEE_ADDRESS) && !deviceShortAddressChanged) {
			synchronized (deviceManager.devices) {
				zdev.setIeeeAddress(mac);
				deviceManager.devices.put(zdev.getShortAddress(), zdev);
			}
			LOG.info("UNKNOWN DEVICE UPDATED: {} {}", mac,shortAddress);
//			EventManagerZigbee.updateToIo(zdev);
			sendNodeDescReq(zdev.getShortAddress());
//			EventManagerZigbee.sendPendingEvents(shortAddress, mac);
		}
	}
	private void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: " + e);
			Thread.currentThread().interrupt();
		}
	}
}

