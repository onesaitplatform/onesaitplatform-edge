package com.onesait.edge.engine.zigbee.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.clients.Clients;
import com.onesait.edge.engine.zigbee.clients.IASZoneClient;
import com.onesait.edge.engine.zigbee.clients.PollControlClient;
import com.onesait.edge.engine.zigbee.clients.ThermostatClient;
import com.onesait.edge.engine.zigbee.clients.ThermostatUserInterfaceConfigurationClient;
import com.onesait.edge.engine.zigbee.exception.UnknownCoordinatorMacException;
import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.UtilGetDeviceInfo;
import com.onesait.edge.engine.zigbee.frame.UtilSetPrecfgkey;
import com.onesait.edge.engine.zigbee.frame.ZbGetDeviceInfo;
import com.onesait.edge.engine.zigbee.frame.ZdoActiveEpReq;
import com.onesait.edge.engine.zigbee.frame.ZdoIeeeAddrReq;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtLeaveReq;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtNwkUpdateReq;
import com.onesait.edge.engine.zigbee.frame.ZdoMgmtPermitJoinRequest;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.ConnectDisconnectPort;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.mesh.ZMesh;
import com.onesait.edge.engine.zigbee.monitoring.TimeManager;
import com.onesait.edge.engine.zigbee.security.KeyGenerator;
import com.onesait.edge.engine.zigbee.service.DeviceManager;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.service.ZclService;
import com.onesait.edge.engine.zigbee.types.DeviceType;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ServerManager;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

/**
 * ZclDevice para el coordinador a partir de la ieeeAddress proporcionada como
 * par�metro. Crea los clusters de los que es servidor. Estos clusters deben
 * aparecer en el fichero zcl.xml con el atributo server="true".
 * 
 * @author dyagues
 *
 */
public class ZclCoordinator extends ZclDevice {

	private static final Logger LOG = LoggerFactory.getLogger(ZclCoordinator.class);
	private Timestamp resetTime;
	private static final Byte DEFAULT_ZB_CHANNEL = ZigbeeConstants.LOWEST_CHANNEL;
	private Byte zbChannel = null;
	private Boolean activenet = false;
	private boolean otaEndPoint = false;
	private Integer initAssocDevices = null;
	private Timestamp initTimestamp = new Timestamp(new Date().getTime());
	private static final String MIN_DATE_CODE_FOR_TIME_SERVER = "20160801";
	private Byte channel2Change2 = new Byte((byte) 0);
	private final Object channel2Change2Lock = new Object();
	private HashMap<DoubleByte, ServerManager> serverManagers = new HashMap<>();
	private HashMap<DoubleByte, Timestamp> nwkAddrRequests = new HashMap<>();
	private OutputSerialZigbee outputserial;
	private ZMesh zMesh = new ZMesh();
//	private HashMap<DoubleByte, ZclCluster> serverClusters;
	private Clients clients = new Clients();
	private Byte receivedChannel = null;

	private int[] channelEnergyLevels = new int[ZigbeeConstants.N_CHANNELS];
	private DeviceManager deviceManager;
	private ConnectDisconnectPort cdPort;
	private ZclService zclservice;
	
	

//	@Autowired (required=true)
//	SerialZigbeeConnector connector;
//	@Autowired 
//	ZclService zclService;
//	@Autowired
//	DeviceManager deviceManager;

	// private SerialZigbeeConnector connector;

	
	  public ZclCoordinator(ZclService zclService,OutputSerialZigbee out,DeviceManager deviceManager,ConnectDisconnectPort cdPort) { 
		  super(null, new DoubleByte(0));
		  this.outputserial = out;
		  this.cdPort=cdPort;
		  this.zclservice = zclService;
		  this.deviceManager=deviceManager;
	  
	  }
	

	// public void initCoor(){
	// this.outputserial
	//
	// }
//	public ZclCoordinator() {
//		super(null, new DoubleByte(0));
//		// TODO Auto-generated constructor stub
//	}

	public void initCoor() {
		//this.closeNetwork();
		this.loadChipInfo();
		// Esperamos a recibir la IEEE address del chip Zigbee
		for (int i = 0; i < 4; i++) {
			waitMs(2000);
			if (this.getInitAssocDevices() != null) {
				LOG.info("Chip info received.");
				break;
			}
		}
		setNetworkParameters(true);
		getChannel();
		setGeneralDeviceType(DeviceType.COORDINATOR);
	}

//	private void setZCparameters() {
//		this.setIeeeAddress(null);
//		this.setShortAddress(new DoubleByte(0));
//
//	}

	public void sendIEEE_ADDRESS_REQ(DoubleByte nwkAddr) {
		this.outputserial.getIeeeAddrReqSent().put(
				nwkAddr, new Timestamp(new Date().getTime()));
		ZdoIeeeAddrReq ziar = new ZdoIeeeAddrReq(nwkAddr, true);
		this.outputserial.writeZFrame(ziar);
	}
	
	public Timestamp getResetTime() {
		return resetTime;
	}

	public void setResetTime(Timestamp resetTime) {
		this.resetTime = resetTime;
	}

	public Byte getZbchannel() {
		return zbChannel;
	}

	public void setZbchannel(Byte zbchannel) {
		this.zbChannel = zbchannel;
	}

	public Boolean getActivenet() {
		return activenet;
	}

	public void setActivenet(Boolean activenet) {
		this.activenet = activenet;
	}

	public Integer getInitAssocDevices() {
		return initAssocDevices;
	}

	public void setInitAssocDevices(Integer initAssocDevices) {
		this.initAssocDevices = initAssocDevices;
	}

	public Timestamp getInitTimestamp() {
		return initTimestamp;
	}

	public void setInitTimestamp(Timestamp initTimestamp) {
		this.initTimestamp = initTimestamp;
	}

	public Byte getChannel2Change2() {
		return channel2Change2;
	}

	public boolean hasOtaEndPoint() {
		return otaEndPoint;
	}

	public void setHasOtaEndPoint(boolean otaEndPoint) {
		this.otaEndPoint = otaEndPoint;
	}

	public boolean setChannel2Change2(Byte channel2Change2) {
		boolean success = false;
		if (channel2Change2 != null) {
			if (!channelOverlapsWifi(channel2Change2)) {
				LOG.info("Channel {} does not overlap with wifi channels.",channel2Change2);
				synchronized (this.channel2Change2Lock) {
					this.channel2Change2 = channel2Change2;
				}
				success = true;
			} else {
				LOG.info("Channel {} overlaps with wifi channels.",channel2Change2);
			}
		}
		return success;
	}

	public boolean channelOverlapsWifi(Byte zbChannel) {
		int channelMask = getChannelMask(zbChannel);
		return (channelMask & ZigbeeConstants.WIFI_SHARED_CHANNELS_MASK) > 0;
	}

	public HashMap<DoubleByte, ServerManager> getServerManagers() {
		return serverManagers;
	}

	public void setServerManagers(HashMap<DoubleByte, ServerManager> serverManagers) {
		this.serverManagers = serverManagers;
	}

	public HashMap<DoubleByte, Timestamp> getNwkAddrRequests() {
		return nwkAddrRequests;
	}

	public void setNwkAddrRequests(HashMap<DoubleByte, Timestamp> nwkAddrRequests) {
		this.nwkAddrRequests = nwkAddrRequests;
	}

	public static Byte getDefaultZbChannel() {
		return DEFAULT_ZB_CHANNEL;
	}


	public void initCoordinator(OctaByte mac) {
		this.setIeeeAddress(mac);
		LOG.info("Coordinator IEEEAddress: {}",this.getIeeeAddress());
		this.createClients();
		this.putZclEndpoint(new ZclEndpoint(ZigbeeConstants.COORDINATOR_ENDPOINT));

		createAndConfigureServerClusters();
		requestFirmwareVersionWithRetries();

		if (timeServerAllowed()) {
			ZclCluster coorTimeCl = this.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_TIME);
			if (coorTimeCl == null) {
				return;
			}
			TimeManager tm = new TimeManager(coorTimeCl, outputserial);
			tm.init();
			LOG.info("Time server cluster activated");

			// TODO Hay que calcular el timezone y enviarselo al chip
			this.serverManagers.put(coorTimeCl.getId(), tm);
		}
	}

	private void createAndConfigureServerClusters() {
		for (ZclCluster srvCl : this.zclservice.getZcl().getServerClusters().values()) {
			this.putZclCluster(srvCl, ZigbeeConstants.COORDINATOR_ENDPOINT);
			srvCl.setDevice(this);
			configureServerCluster(srvCl.getId());
		}
		waitMs(1000);
	}

	private void requestFirmwareVersionWithRetries() {
		for (int i = 0; i < 6; i++) {
			// String fwVersionStr = getFirmwareVersion();
			String fwVersionStr = getZigbeeChipVersion(true);
			if (fwVersionStr == null || fwVersionStr.equals("")) {
				if (i == 5)
					LOG.info("Could not receive firmware information from coordinator.");
				else {
					LOG.info("Waiting for coordinator parameters...");
					waitMs(4000);
					configureServerCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC);
				}
			} else {
				LOG.info("Coordinator parameters received.");
				break;
			}
		}
	}

	private boolean timeServerAllowed() {
		ZclCluster coorBasicCl = this.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC);
		if (coorBasicCl == null) {
			return false;
		}
		String dateCode = (String) coorBasicCl.getAttribute(new DoubleByte(0x0006)).getConvertedValue(); // Date
																											// code
		if (dateCode == null) {
			return false;
		}
		LOG.info("Zigbee firmware date: {}",dateCode);
		if (dateCode.compareToIgnoreCase(MIN_DATE_CODE_FOR_TIME_SERVER) < 0) {
			return false;
		}
		return true;
	}

	private void configureServerCluster(DoubleByte clusterId) {
		ZclCluster srvCl = this.getZclCluster(clusterId);
		if (srvCl == null)
			return;
		try {
			ZFrame[] frames = srvCl.configureAttributes(getIeeeAddress(), null);
			outputserial.writeZFrames(frames);

		} catch (UnknownCoordinatorMacException e) {
			outputserial.manageZclUnknownCoordinatorMacException();
		}
	}

	/*
	 * private String getFirmwareVersion() { ZclCluster coorBasicCl =
	 * this.getZclCluster( ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC); if
	 * (coorBasicCl != null) { ZclAttribute fwVersionAt =
	 * coorBasicCl.getAttribute(new DoubleByte(5)); if (fwVersionAt != null) {
	 * return (String) fwVersionAt.getConvertedValue(); } } return ""; }
	 */

	public void scanAndChangeChannel() {

		String logMsg = "Scanning Zigbee channels";
		ZdoMgmtNwkUpdateReq chScanReq = new ZdoMgmtNwkUpdateReq(new DoubleByte(0), (byte) 0x2,
				ZigbeeConstants.ALL_CHANNELS_MASK, (byte) 4, (byte) 1, new DoubleByte(0));
		outputserial.sendAndWriteLog(chScanReq, logMsg, this.getClass().getName());
		waitMs(6000);

		changeChannel();
	}

	public boolean changeChannel() {
		synchronized (this.channel2Change2Lock) {
			boolean result = this.changeChannel(this.channel2Change2);
			this.channel2Change2 = (byte) 0;
			return result;
		}
	}

	public boolean changeChannel(byte channel) {
		if (this.channelOverlapsWifi(channel)) {
			return false;
		}
		if (channel >= ZigbeeConstants.LOWEST_CHANNEL && channel <= ZigbeeConstants.HIGHEST_CHANNEL) {
			communicateChannelChangeToNetwork(channel);
			return true;
		} else {
			LOG.info("No valid channel selected to change.");
			return false;
		}
	}

	private void communicateChannelChangeToNetwork(byte channel) {
		int mask = getChannelMask(channel);
		ZdoMgmtNwkUpdateReq chChangeReq = new ZdoMgmtNwkUpdateReq(new DoubleByte(0xFFFF), (byte) 0xF, mask,
				(byte) 0xFE, (byte) 1, new DoubleByte(0));
		String logMsg = "New zigbee channel set: " + channel;
		outputserial.sendAndWriteLog(chChangeReq, logMsg, this.getClass().getName());
	}

	public int getChannelMask(Byte channel) {
		return ZigbeeConstants.CHANNEL_B_MASK << (channel.intValue() - ZigbeeConstants.LOWEST_CHANNEL);
	}

	public void setNwkKeyAndRestart() {
		synchronized (outputserial) {

			if (this.getIeeeAddress() == null) {
				LOG.error("Coordinator IEEE address unknown. Cannot configure security.");
				return;
			}
			KeyGenerator keyGen = new KeyGenerator(this.getIeeeAddress().toString());
			UtilSetPrecfgkey usp = new UtilSetPrecfgkey(keyGen.getNwkKey());
			outputserial.writeZFrame(usp);

			ZdoMgmtLeaveReq leaveReq = new ZdoMgmtLeaveReq(new DoubleByte(0), this.getIeeeAddress(), false, true);
			outputserial.writeZFrame(leaveReq);
			LOG.info("Configuring security parameters. Resetting...");
			this.cdPort.disconnect();
			waitMs(22000);
			this.cdPort.connectSystem();
//			LOG.info("Sending start request");
//			ZB_START_REQUEST startFrame = new ZB_START_REQUEST();
//			outputserial.writeZFrame(startFrame);
//			waitMs(1000);			
		}
	}

	private void createClients() {
		PollControlClient pcc = new PollControlClient();
		IASZoneClient izc = new IASZoneClient(this.getIeeeAddress(), outputserial,deviceManager);
		ThermostatClient tc = new ThermostatClient();
		ThermostatUserInterfaceConfigurationClient tuicc = new ThermostatUserInterfaceConfigurationClient();
		this.clients.addClients(pcc, izc, tc, tuicc);
	}

	public ZFrame[] initClient(DoubleByte clusterId, ZclDevice dev) {
		return this.clients.init(clusterId, dev);
	}

	public ZFrame[] sendFrameToClient(AfIncomingMsg af, ZclDevice dev, MqttConnection mqttConnection) {
		return this.clients.manageFrame(af, dev,mqttConnection);
	}

	public boolean leaveDevice(String macToDelete, boolean rejoin) {
		boolean result = false;
		LOG.info("Finding parent of {} ...",macToDelete);
		ZclDevice parentDevice = findParentOf(macToDelete);
		ZclDevice devToDelete = deviceManager.getDeviceByMac(macToDelete);
		if (devToDelete != null) {
			if (parentDevice != null) {
				parentDevice.setChildDeviceToRemove(devToDelete);
				LOG.debug("Padre encontrado: {}",parentDevice.getIeeeAddress());
				sendRemoveMessage(devToDelete.getIeeeAddress(), parentDevice.getShortAddress(), rejoin);
				result = true;
			} else {
				LOG.debug("Padre no encontrado: mandamos el leave al dispositivo");
				// Si no encontramos al padre, enviamos el mensaje directamente
				// al dispositivo, por si acaso, y porque en ZB3.0 no conocemos
				// aun el procedimiento.
				sendRemoveMessage(new OctaByte(0), devToDelete.getShortAddress(), rejoin);
			}
		}
		return result;
	}

	private ZclDevice findParentOf(String mac) {
		ZclDevice parentDevice = null;
		ZclDevice child = deviceManager.getDeviceByMac(mac);
		if (child == null) {
			return null;
		}
		DoubleByte childSrcAddr = child.getShortAddress();
		askForChildren(this);
		if (this.hasChildDevice(childSrcAddr)) {
			parentDevice = this;
			return parentDevice;
		}
		for (ZclDevice dev : deviceManager.devices.values()) {
			// Si es null en la siguiente linea no sabemos si es endDevice o no.
			// Mandamos el mensaje por si acaso
			if (dev.isEndDevice() == null || !dev.isEndDevice()) {
				askForChildren(dev);
				if (dev.hasChildDevice(childSrcAddr)) {
					parentDevice = dev;
					break;
				}
			}
		}
		return parentDevice;
	}

	private void sendRemoveMessage(OctaByte childMac, DoubleByte shortAddress, boolean rejoin) {
		ZdoMgmtLeaveReq leaveReq = new ZdoMgmtLeaveReq(shortAddress, childMac, false, rejoin);
		outputserial.writeZFrame(leaveReq);

	}

	private void askForChildren(ZclDevice dev) {
		ZdoIeeeAddrReq childrenReq = new ZdoIeeeAddrReq(dev.getShortAddress(), true);
		outputserial.writeZFrame(childrenReq);
		waitMs(2000);
	}

	public void reset() {
		this.setResetTime(new Timestamp(new Date().getTime()));
		this.resetServers();
	}

	private void resetServers() {
		for (ServerManager server : this.serverManagers.values()) {
			server.init();
		}
	}

	public void updateChild(DoubleByte nwkAddr) {
		DoubleByte[] prevChildren = super.getChildren();
		int numChildren = prevChildren.length;
		DoubleByte[] childUpdated = new DoubleByte[numChildren + 1];
		for (int i = 0; i < numChildren; i++) {
			childUpdated[i] = prevChildren[i];
		}
		childUpdated[numChildren] = nwkAddr;
		super.setChildren(childUpdated);
	}

	public void compareChildrenAndUpdate(DoubleByte[] assocList) {
		for (int j = 0; j < assocList.length; j++) {
			DoubleByte[] children = super.getChildren();
			int numChildren = children.length;
			for (int i = 0; i < numChildren; i++) {
				if (assocList[j].equals(children[i])) {
					removeChild(i);
					break;
				}
			}

		}
	}

	private void removeChild(int position) {
		DoubleByte[] previouschild = super.getChildren();
		ArrayList<DoubleByte> Childaux = new ArrayList<DoubleByte>();
		for (int i = 0; i < previouschild.length; i++) {
			Childaux.add(previouschild[i]);
		}
		Childaux.remove(position);
		DoubleByte[] finalChild = Childaux.toArray(new DoubleByte[Childaux.size()]);
		super.setChildren(finalChild);
	}

	public int[] getChannelEnergyLevels() {
		return this.channelEnergyLevels;
	}

	public void setChannelEnergyLevels(int[] energyLevels) {
		if (energyLevels.length == ZigbeeConstants.N_CHANNELS) {
			this.channelEnergyLevels = energyLevels;
		}
	}

	public String getZigbeeChipVersion(boolean sendReadAndSleeep) {
		try {
			if(sendReadAndSleeep){
			ZclCluster basic = this.getZclCluster(new DoubleByte(0));
			ArrayList<DoubleByte> attributes = new ArrayList<>();
			attributes.add(new DoubleByte(5));
			attributes.add(new DoubleByte(6));
			ZFrame[] frames = basic.buildReadAttributes(attributes);
			//comprobar aqui primero si esta en estado de hold
			this.outputserial.writeZFrames(frames);					
			Thread.sleep(3000);
			}
			return (String)this.getZclCluster(
				new DoubleByte(0)).getAttribute(new DoubleByte(5)).getConvertedValue();
		} catch (Exception e) {
			return "";
		}
	}

	public void initZigbeeChip() {
		this.closeNetwork();
		this.loadChipInfo();
		// Esperamos a recibir la IEEE address del chip Zigbee
		for (int i = 0; i < 4; i++) {
			waitMs(2000);
			if (this.getInitAssocDevices() != null) {
				LOG.info("Chip info received.");
				break;
			}
		}
	}

	public void loadChipInfo() {
		String logMsg = "SEARCHING FOR DEVICES STORED IN ZB CHIP";
		outputserial.sendAndWriteLog(new UtilGetDeviceInfo(), logMsg, this.getClass().getName());
		outputserial.writeZFrame(new ZdoActiveEpReq(new DoubleByte(0x0000), new DoubleByte(0x0000)));
	}

	private void closeNetwork() {
		outputserial.sendAndWriteLog(new ZdoMgmtPermitJoinRequest(new DoubleByte(0), 0),
				"Closing zigbee network", this.getClass().getName());
	}

	public void setNetworkParameters(boolean onlyIfNoDevices) {

		boolean noDevices = deviceManager.devices.size() == 0 && this.getInitAssocDevices() != null
				&& this.getInitAssocDevices().intValue() == 0;
		if (!onlyIfNoDevices || noDevices) {
			// String comPeripheralNeeded = SystemProperties.isDocker() ? "USB"
			// : "UART";
			// ahumanes: comentado para pruebas, inicio
			// if (SystemProperties.isDocker()) {
			this.setNwkKeyAndRestart();
			LOG.info("Seeting network parameters and restarting the chip");
			// }
			// fin
			this.getChannel();
			this.scanAndChangeChannel();
		}
	}

	public void getChannel() {
		outputserial.writeZFrame(new ZbGetDeviceInfo(0x05));
	}
	
	public OutputSerialZigbee getOutputserial() {
		return outputserial;
	}

	public DeviceManager getDeviceManager() {
		return deviceManager;
	}

	public ConnectDisconnectPort getCdPort() {
		return cdPort;
	}
	public ZclService getZclService(){
		return this.zclservice;
	}
	public Clients getClients() {
		return clients;
	}
	
	public Byte getAndResetZbchannel() {
		Byte lastChannelReceived = getReceivedChannel();
		resetReceivedChannel();
		return lastChannelReceived;
	}

	
	public synchronized Byte getReceivedChannel() {
		if (receivedChannel != null) {
			return  receivedChannel;
		} else {
			return null;
		}
	}
	public ZMesh getzMesh() {
		return zMesh;
	}

	public void cleanMesh() {
		this.zMesh = new ZMesh();
	}

	public synchronized void resetReceivedChannel() {
		this.receivedChannel = null;
	}
	
	
	private void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: " + e);
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void setReceivedChannel(Byte receivedChannel) {
		this.receivedChannel = receivedChannel;
	}
}
