package com.onesait.edge.engine.zigbee.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.IasZoneDev;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.monitoring.Ready2SendEnrollRsp;
import com.onesait.edge.engine.zigbee.service.DeviceManager;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.types.CommandValue;
import com.onesait.edge.engine.zigbee.types.DeviceType;
import com.onesait.edge.engine.zigbee.util.BuildMqttMsg;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.MqttMsgDetail;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

public class IASZoneClient extends ClientCluster {

	private static final DoubleByte IAS_ZONE_CLUSTER_ID_DB = new DoubleByte(0x0500);
	public static final String IAS_ZONE_ONOFF="Zone Status";
	public static final String IAS_ZONE_BATT="Battery Status";
	
	// Commands
	public static final byte ZONE_STATUS_CHANGE_NOTIFICATION_CMD_ID = (byte)0x00;
	public static final byte ZONE_ENROLL_REQUEST_CMD_ID = (byte)0x01;
	public static final byte ZONE_ENROLL_RESPONSE_CMD_ID = (byte)0x00;
	
	// Attributes
	public static final DoubleByte ZONE_TYPE_ATTRIBUTE_ID = new DoubleByte(0x0001);
	public static final DoubleByte CIE_ADDRESS_ATTRIBUTE_ID = new DoubleByte(0x0010);
	
	// Zone Status bit masks
	public static final int ZONE_STATUS_ALARM_1				=0x001;
	public static final int ZONE_STATUS_ALARM_2				=0x002;
	public static final int ZONE_STATUS_TAMPER				=0x004;
	public static final int ZONE_STATUS_BATTERY				=0x008;
	public static final int ZONE_STATUS_SUPERVISION_REPORTS	=0x010;
	public static final int ZONE_STATUS_RESTORE_REPORTS		=0x020;
	public static final int ZONE_STATUS_TROUBLE				=0x040;
	public static final int ZONE_STATUS_AC					=0x080;
	public static final int ZONE_STATUS_TEST				=0x100;
	public static final int ZONE_STATUS_BATTERY_DEFECT		=0x200;
	private OctaByte cieIeeeAddress;
	private static final Logger LOG = LoggerFactory.getLogger(IASZoneClient.class);
	private OutputSerialZigbee out;
//	private Map<String, IasZoneDev> iasdevices = new ConcurrentHashMap<String,IasZoneDev>();
	private DeviceManager devManager;

	public IASZoneClient(OctaByte cieIeeeAddress, OutputSerialZigbee out,DeviceManager devManager) {
		this.cieIeeeAddress = cieIeeeAddress;
		this.out=out;
		this.devManager=devManager;
		
	}

	@Override
	protected ZFrame[] manageClusterSpecificFrame(AfIncomingMsg af, ZclDevice dev,MqttConnection mqttConnection) {
		ZFrame[] returnedFrames = new ZFrame[0];
		if (af.getZclCmd() == ZONE_STATUS_CHANGE_NOTIFICATION_CMD_ID) {
			returnedFrames = manageZoneStatusChangeNotification(af, dev,mqttConnection);
		} else if (af.getZclCmd() == ZONE_ENROLL_REQUEST_CMD_ID) {
			returnedFrames = manageZoneEnrollRequest(af, dev);
		}
		return returnedFrames;
	}
	
	private ZFrame[] manageZoneEnrollRequest(AfIncomingMsg zoneEnrollRequest, ZclDevice dev) {
		ZclCluster iasZoneCluster = dev.getZclCluster(IAS_ZONE_CLUSTER_ID_DB);
		if (iasZoneCluster == null) {
			//Timer para chekear el envio del enroll rsp
			Timer sendEnrollRsp = new Timer("TimerEnrollRspCheck", Boolean.TRUE);
			sendEnrollRsp.schedule(new Ready2SendEnrollRsp(dev,out,sendEnrollRsp,zoneEnrollRequest),
					Ready2SendEnrollRsp.DELAY_MS, Ready2SendEnrollRsp.PERIOD_MS);
			return new ZFrame[0];
		}
		long[] defaultParameters = null;
		ZFrame zoneEnrollResponse = iasZoneCluster.buildCmd(zoneEnrollRequest.getSequenceNumber(),
				ZONE_ENROLL_RESPONSE_CMD_ID, defaultParameters);
		return new ZFrame[] {zoneEnrollResponse};
	}

	private ZFrame[] manageZoneStatusChangeNotification(AfIncomingMsg af, ZclDevice dev, MqttConnection mqttConnection) {
		int zoneStatus = af.getAfIncomingData()[af.getZclPayloadOffset()];
		
		if (dev.getIeeeAddress().equals(ZigbeeConstants.INVALID_IEEE_ADDRESS)) {
			LOG.error("Sensor device with Invalid MAC: {}",dev.getIeeeAddress());
//			
		}else{
		//ahumanes: ver que se hacia antes para hacerlo ahora con mqtt
		//comprobar que no existe el device 
		IasZoneDev iasdev;
		String mac=dev.getIeeeAddress().toString();
		if(!devManager.iasdevices.containsKey(mac)){
			iasdev=new IasZoneDev(mac);
			devManager.iasdevices.put(mac, iasdev);
		}else{
			iasdev=devManager.iasdevices.get(mac);
		}		
		ArrayList<ZFrame> frames = checkAlarmsStatus(iasdev, dev, zoneStatus,mqttConnection);
		if(checkBatteryStatus(iasdev, zoneStatus)){ //si ha cambiado el estado de la bateria
			BuildMqttMsg.buildIASZoneBatteryMqttMsg(iasdev, dev);
		}
			
		//ahumanes: aqui lo que deberemos hacer es llamar al mqtt e indicarle el estado del device
		//Aqui ademas deberemos determinar que tipo de sensor es		
		ZFrame defaultResponse = checkDefaultResponse(af);
		if (defaultResponse != null) frames.add(defaultResponse);

		ZFrame warningFunctionFrame = checkWarningFunction(zoneStatus, dev);
		if (warningFunctionFrame != null) frames.add(warningFunctionFrame);
		
		ZFrame[] returnedFrames = new ZFrame[frames.size()];
		return frames.toArray(returnedFrames);
		}
		return null;
	}

	private ZFrame checkWarningFunction(int zoneStatus, ZclDevice dev) {
		ZclCluster iasWdCluster = dev.getZclCluster(
				ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_WD);
		ZclCluster pollControlCluster = dev.getZclCluster(
				ZClusterLibrary.ZCL_CLUSTER_ID_GEN_POLL_CONTROL);
		if (iasWdCluster == null || pollControlCluster == null) {
			return null;
		}
		if ((zoneStatus & ZONE_STATUS_ALARM_1) > 0 || (zoneStatus & ZONE_STATUS_ALARM_2) > 0) {
			return buildCheckInResponse(dev, true, 4294967290l);
		} else {
			return sendStopFastPolling(dev, 0);
		}
	}

	private ZFrame checkDefaultResponse(AfIncomingMsg af) {
		if (!af.isDisabledDefaultResponse()) {
			int[] msg = new int[] {
					0x10,
					af.getSequenceNumber(),
					ZClusterLibrary.ZCL_CMD_DEFAULT_RSP,
					af.getZclCmd(),
					ZClusterLibrary.ZCL_STATUS_SUCCESS
			};
			return new AfDataRequest(af.getNwkAddr(), af.getSrcEndpoint(),
					ZigbeeConstants.COORDINATOR_ENDPOINT, af.getClusterID(), (byte) af.getTransSeqNumber(),
					0, 0, msg);
		}
		return null;
	}

	private boolean checkBatteryStatus(IasZoneDev iasdev, int zoneStatus) {
		if ((zoneStatus & ZONE_STATUS_BATTERY) != 0) { // Bateria baja
			iasdev.setLowBatery();
			LOG.info("SENSOR BATTERY EVENT: {}. Battery: {}",iasdev.getMac(),iasdev.getBattery());
			return true;	
		}else{//bateria normal
			 return iasdev.setNormalBatery();
		}
	}

	private ArrayList<ZFrame> checkAlarmsStatus(IasZoneDev iasdev,ZclDevice dev, int zoneStatus, MqttConnection mqttConnection) {
		//ahumanes: antes la funcion era asi: private ArrayList<ZFrame> checkAlarmsStatus(PropertyEventZigbee pez, ZclDevice dev, int zoneStatus)
		List<MqttMsgDetail> msgs = new ArrayList<>();
		if ((zoneStatus & ZONE_STATUS_ALARM_1) > 0 || (zoneStatus & ZONE_STATUS_ALARM_2) > 0) {
			iasdev.setStatus(CommandValue.ON.toString());
		} else {
			iasdev.setStatus(CommandValue.OFF.toString());
		}
		ArrayList<ZFrame> frames = new ArrayList<>();
		ZclCluster iasZoneCluster = dev.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE);
		if (iasZoneCluster != null) {
			ZclAttribute zoneType = iasZoneCluster.getAttribute(ZONE_TYPE_ATTRIBUTE_ID);
			if (zoneType != null && !zoneType.isRspReceived()) {
				// Pedimos el tipo de sensor
				ArrayList<DoubleByte> attIds = new ArrayList<>();
				attIds.add(ZONE_TYPE_ATTRIBUTE_ID);
				ZFrame[] zframes = iasZoneCluster.buildReadAttributes(attIds);
				for (int i = 0; i < zframes.length; i++) frames.add(zframes[i]);
			} else if (zoneType != null) {
				iasdev.setSensorType(((String)zoneType.getConvertedValue()).toUpperCase());
			}
		}
		LOG.info("SENSOR EVENT {}: {} {}",iasdev.getMac(), iasdev.getSensorType(),iasdev.getStatus());
		if (iasdev.getSensorType()==null){
			iasdev.setSensorType(DeviceType.SENSOR.toString());
		}
		if(!iasdev.getSensorType().equalsIgnoreCase(DeviceType.sensorType(dev.getGeneralDeviceType()))){
			this.devManager.saveDeviceTypeByDeviceId(dev.getIeeeAddress().toString());
		}
		msgs.add(BuildMqttMsg.buildIASZoneOnOff(iasdev,dev));
		mqttConnection.sendThroughMqtt(msgs);
		return frames;
	}

	@Override
	protected ZFrame[] manageProfileWideFrame(AfIncomingMsg af, ZclDevice dev) {
		return new ZFrame[0];
	}
	
	@Override
	public ZFrame[] init(ZclDevice zdev) {
		LOG.info(">>>SENSOR EVENT: FIRST ASSOCIATION MESSAGE FOR EVENTS DEVICES");
		ZclCluster iasZoneCluster = zdev.getZclCluster(
				ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE);
		if (iasZoneCluster == null) {
			return new ZFrame[0];
		}
		ZFrame writeCieAddressFrame = iasZoneCluster.buildWriteAttributes(
				CIE_ADDRESS_ATTRIBUTE_ID, "0x" + cieIeeeAddress.toString());
		return new ZFrame[] {writeCieAddressFrame};
	}

	@Override
	public DoubleByte getClusterId() {
		return IAS_ZONE_CLUSTER_ID_DB;
	}
	
	private ZFrame buildCheckInResponse(ZclDevice dev, boolean startFastPolling, long fastPollTimeout) {
		ZclCluster pollControlCluster = dev.getZclCluster(
				ZClusterLibrary.ZCL_CLUSTER_ID_GEN_POLL_CONTROL);
		long[] parameters = new long[]{startFastPolling ? 1 : 0, fastPollTimeout};
		return pollControlCluster.buildCmd(
				PollControlClient.CHECK_IN_RESPONSE_COMMAND_ID, parameters);
	}
	
	private ZFrame sendStopFastPolling(ZclDevice dev, int quarterSecondsToNextCheckIn) {
		ZclCluster pollControlCluster = dev.getZclCluster(
				ZClusterLibrary.ZCL_CLUSTER_ID_GEN_POLL_CONTROL);
		long[] parameters = new long[]{quarterSecondsToNextCheckIn};
		return pollControlCluster.buildCmd(
				PollControlClient.FAST_POLL_STOP_COMMAND_ID, parameters);
	}
//	public Map<String, IasZoneDev> getIasdevices() {
//		return iasdevices;
//	}
}
