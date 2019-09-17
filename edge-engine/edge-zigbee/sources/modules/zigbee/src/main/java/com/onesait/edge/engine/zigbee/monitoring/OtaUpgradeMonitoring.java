package com.onesait.edge.engine.zigbee.monitoring;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.ota.ImageNotify;
import com.onesait.edge.engine.zigbee.ota.OtaManager;
import com.onesait.edge.engine.zigbee.service.ZclService;
import com.onesait.edge.engine.zigbee.service.ZigbeeService;
import com.onesait.edge.engine.zigbee.types.AlertType;
import com.onesait.edge.engine.zigbee.util.Alert;
import com.onesait.edge.engine.zigbee.util.FourByte;
import com.onesait.edge.engine.zigbee.util.State;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

public class OtaUpgradeMonitoring extends TimerTask{

	
		public static final long DELAY_MS = 5_000;
		public static final long PERIOD_MS = 1_000 * 15L;
		private static final int PAYLOAD_TYPE=0x00;
		private static final int QUERY_JITTER=100;
		private static final Logger LOG = LoggerFactory.getLogger(ZclService.class);
		private ZclDevice dev;
		private OutputSerialZigbee serial;
		private OtaManager otamng;
		private Timer task;
		
		
		public OtaUpgradeMonitoring(OutputSerialZigbee serial,ZclDevice dev,Timer task){
			this.dev=dev;
			this.serial=serial;
			this.task=task;
			this.otamng=dev.getOtamanager();
		}
		
	public void run() {
		try {
			// haciendolo con image notify
			
			if (otamng.getNumberOfAttempts() < 21) {
				ZclCluster otaCluster = dev.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA);
				if (otaCluster != null) {
					ImageNotify imNotify = new ImageNotify(PAYLOAD_TYPE, QUERY_JITTER, null, null, null);
					int[] frame = buildZclFrame(true, 0, ZClusterLibrary.ZCL_OTA_IMAGE_NOTIFY, imNotify.getFrame());

					ZFrame zframe = new AfDataRequest(dev.getShortAddress(),
							dev.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
							ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0,
							0, frame);
					dev.getOtamanager().setOtaRequest(true);

					this.serial.writeZFrame(zframe);
					waitMs(5000);
					FourByte currentfwVersion = otamng.getCurrentVersion();
					FourByte version2upgrade = otamng.getVersion2upgrade();
					if (version2upgrade.equals(currentfwVersion)) {
						LOG.info("Dev: {} has been upgraded succesfully",dev.getIeeeAddress());
						Alert alert = new Alert(State.INFO, AlertType.UPGRADE_SUCCES.toString(), dev.getIeeeAddress().toString(),dev.getGeneralDeviceType().toString());
						ZigbeeService.getAlertList().add(alert);
						ZigbeeService.checkAlertList();
						otamng.setInstalling(false);
						otamng.setOta(false);
						otamng.setOtaRequest(false);
						task.purge();
						task.cancel();
					} 
					otamng.setNumberOfAttempts(otamng.getNumberOfAttempts() + 1);
				} else {
					LOG.info("Cluster OTA could not be obtained for dev: {}",dev.getIeeeAddress());
					otamng.setNumberOfAttempts(otamng.getNumberOfAttempts() + 1);
				}
			}else{
				LOG.info("Dev: {} could be not upgraded.",dev.getIeeeAddress());
				otamng.setNumberOfAttempts(0);
				otamng.setInstalling(false);
				otamng.setOta(false);
				otamng.setOtaRequest(false);
				task.purge();
				task.cancel();
			}
		} catch (Exception e) {
			LOG.error("Error {} ",e.getMessage());
		} finally {
			Thread.currentThread().interrupt();
		}

	}

	/*private byte[] getReversevalue(byte[] buffer){
		byte[] dataR = new byte[buffer.length];
		int r=buffer.length-1;
		for (int i = 0; i < buffer.length; i++) {
			dataR[r] = buffer[i];
			r--;
		}
		return dataR;
		
		
	}*/
	private void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: {}",e);
			Thread.currentThread().interrupt();
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
	
}
