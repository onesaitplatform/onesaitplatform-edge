package com.onesait.edge.engine.zigbee.model;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.ZdoEndDeviceAnnceInd;
import com.onesait.edge.engine.zigbee.ota.OtaManager;
import com.onesait.edge.engine.zigbee.service.ZigbeeService;
import com.onesait.edge.engine.zigbee.types.AlertType;
import com.onesait.edge.engine.zigbee.types.DeviceStatus;
import com.onesait.edge.engine.zigbee.types.DeviceType;
import com.onesait.edge.engine.zigbee.util.Alert;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.State;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;


public class ZclDevice implements Cloneable {
	private static final Logger LOG = LoggerFactory.getLogger(ZclDevice.class);
    private ConcurrentHashMap<Byte, ZclEndpoint> endpoints = new ConcurrentHashMap<>();
	private OctaByte ieeeAddress;
    private DoubleByte shortAddress;
    private String manufacturerName = new String();
    private int lqi = 0;
    private DoubleByte manufacturerCode = null;
    private Byte deviceType;
    private Byte capabilities;
	private DoubleByte[] children = new DoubleByte[0];
	private OtaManager otamanager=new OtaManager();
	private DeviceType generalDeviceType= DeviceType.UNDEFINED;
	private DeviceStatus status=DeviceStatus.OK;
	private boolean pending2Remove=false;
	private ZclDevice childDeviceToRemove= null; //utilizado para identificar quien es el dispositivo borrado cuando llega un mgmt_leave_rsp
	private boolean pendingKnowManCode = false;

	public ZclDevice(OctaByte ieeeAddress, DoubleByte shortAddress) {
		this.ieeeAddress = ieeeAddress;
		this.shortAddress = shortAddress;
	}
	
	public ZclDevice(ZdoEndDeviceAnnceInd endDevAnnce) {
		this.ieeeAddress = endDevAnnce.getIeeeAddress();
		this.shortAddress = endDevAnnce.getSrcAddr();
		this.capabilities = endDevAnnce.getCapabilities();
	}
    
	public void putZclCluster(ZclCluster cluster, Byte endpoint){
		ZclEndpoint ep = this.endpoints.get(endpoint);
		if (ep == null || ep.getCluster(cluster.getId()) != null)
			return;
		
		this.endpoints.get(endpoint).putCluster(cluster); 
	}
	
	public ConcurrentHashMap<Byte, ZclEndpoint> getEndpoints() {
		return endpoints;
	}

	public ZclCluster getZclCluster(DoubleByte clusterId){
		for(ZclEndpoint ep : this.endpoints.values()){
			if (ep.getCluster(clusterId) != null)
				return ep.getCluster(clusterId);
		}
		return null;
	}

	public ZclCluster getZclClusterConfigured(DoubleByte clusterId){
		for(ZclEndpoint ep : this.endpoints.values()){
			if (ep.getCluster(clusterId) != null && ep.getCluster(clusterId).isConfigured())
				return ep.getCluster(clusterId);
		}
		return null;
	}
	
	public ZclCluster getZclCluster(String clusterName){
		for(ZclEndpoint ep : this.endpoints.values()){
			for (ZclCluster zcl : ep.getClusters().values()) {
				if (zcl.getName().equals(clusterName)){
					return zcl;
				}
			}
		}
		return null;
	}
	
	public ZclEndpoint getZclEndpoint(Byte id) {
		return this.endpoints.get(id);
	}

	public void putZclEndpoint(ZclEndpoint ep) {
		if (this.endpoints.get(ep.getId()) != null)
			return;
		this.endpoints.put(ep.getId(), ep);
	}
	
	public void removeZclEndpoint(byte id) {
		this.endpoints.remove(id);
	}
	
	public ZclEndpoint getEndpointByCluster(DoubleByte clusterId) {
		for(ZclEndpoint ep : this.endpoints.values()){
			if (ep.getCluster(clusterId) != null)
				return ep;
		}
		return null;
	}
	
	public OctaByte getIeeeAddress() {
		return ieeeAddress;
	}

	public void setIeeeAddress(OctaByte ieeeAddress) {
		this.ieeeAddress = ieeeAddress;
	}

	public DoubleByte getShortAddress() {
		return shortAddress;
	}

	public void setShortAddress(DoubleByte shortAddress) {
		this.shortAddress = shortAddress;
	}    

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		if (manufacturerName != null) {
			this.manufacturerName = manufacturerName;
		}
	}
	
	public void loadManufacturerName() {
		ZclCluster basic = this.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC);
		if (basic != null) {
			ZclAttribute manName = basic.getAttribute(new DoubleByte(0x0004));
			if (manName != null) {
				this.setManufacturerName((String)manName.getConvertedValue());
			}
		}
	}
	
    @Override
	public String toString() {
		return "ZclDevice [endpoints=" + endpoints + ", ieeeAddress=" + ieeeAddress + ", shortAddress=" + shortAddress
				+ "]";
	}
    
    public String toConsoleString() {
		CharArrayWriter writer = new CharArrayWriter();
		PrintWriter pw = new PrintWriter(writer);

		for (ZclEndpoint zep : this.endpoints.values()) {
			for (ZclCluster zcl : zep.getClusters().values()) {
				pw.println(String.format("Cluster: %s. Endpoint: %s", zcl.getId() + " (" + zcl.getName() + ")", //
						zep.getId()));
			}
		}
    	return writer.toString();
    }

	public int getLqi() {
		return lqi;
	}

	public void setLqi(int lqi) {
		this.lqi = lqi;
	}

	public DoubleByte getManufacturerCode() {
		return manufacturerCode;
	}

	public void setManufacturerCode(DoubleByte manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	public Byte getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Byte deviceType) {
		this.deviceType = deviceType;
	}

	public Byte getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Byte capabilities) {
		this.capabilities = capabilities;
	}
	
	public Object clone() {
		ZclDevice newDev = new ZclDevice((OctaByte) this.ieeeAddress.clone(),
				new DoubleByte(this.shortAddress.intValue()));
		if (this.getCapabilities() != null) {
			newDev.setCapabilities(this.getCapabilities());
		}
		if (this.getDeviceType() != null) {
			newDev.setDeviceType(this.getDeviceType());
		}
		if (this.manufacturerCode != null) {
			newDev.setManufacturerCode(new DoubleByte(this.manufacturerCode.intValue()));
		}
		newDev.setManufacturerName(new String(this.manufacturerName));

		newDev.endpoints = new ConcurrentHashMap<>();
		for (ZclEndpoint zep : this.endpoints.values()) {
			ZclEndpoint newEp = (ZclEndpoint)zep.clone();
			newDev.endpoints.put(newEp.getId(), newEp);
		}
		return newDev;
	}
	
    public Boolean isEndDevice() {
    	return this.capabilities != null ? (this.capabilities & (byte)0x02) == 0 : null;
    }
    
    public Boolean isPowered() {
    	return this.capabilities != null ? (this.capabilities & (byte)0x04) > 0 : null;
    }
    
    public Boolean isCoordinator(){
    	return this.capabilities != null ? (this.capabilities & (byte)0x01) > 0 : null;
    }
    
    public Boolean isRxOnWhenIdle() {
    	return this.capabilities != null ? (this.capabilities & (byte)0x08) > 0 : null;
    }

	public void resetRetries() {
		for (ZclEndpoint zclEp : this.endpoints.values()) {
			for (ZclCluster zclCl : zclEp.getClusters().values()) {
				for (ZclAttribute zclAtt : zclCl.getAttributes().values()) {
					zclAtt.resetRetries();
				}
			}
		}
	}
	
	public DeviceType getGeneralDeviceType() {
		return generalDeviceType;
	}

	public void setGeneralDeviceType(DeviceType generalDeviceType) {
		this.generalDeviceType = generalDeviceType;
	}

	public void setChildren(DoubleByte[] children) {
		if (children != null) {
			this.children  = children;
		}
	}
	public String getChilden(){
		String childs="";
		if (children!=null){
			for (int i = 0; i < children.length; i++) {
				childs="   \n Hijo numero"+i+": "+children[i].toString()+" "+childs;
			}
			return childs;
		}
		else{
			return "Sin hijos";
		}
	}
	
	public DoubleByte[] getChildren(){
		return this.children;
	}
	public boolean hasChildDevice(DoubleByte childNwkAddr) {
		for (int i = 0; i < children.length; i++) {
			if (children[i].equals(childNwkAddr)) {
				return true;
			}
		}
		return false;
	}

	public OtaManager getOtamanager() {
		return otamanager;
	}

	public DeviceStatus getStatus() {
		return status;
	}
	
	public boolean isPending2Remove() {
		return pending2Remove;
	}

	public void setPending2Remove(boolean pending2Remove) {
		this.pending2Remove = pending2Remove;
	}

	public ZclDevice getChildDeviceToRemove() {
		return childDeviceToRemove;
	}

	public void setChildDeviceToRemove(ZclDevice childDeviceToRemove) {
		this.childDeviceToRemove = childDeviceToRemove;
	}
	
	//metodo que sirve simplemente para reflejar las alertas en la web. Aunque hay casos en los que se setean las alertas en otra parte del codigo
	public void setStatus(DeviceStatus status) {
		if(this.status.compareTo(status)!=0) {
			Alert alert=null;
			if((status==DeviceStatus.OK) && (!getOtamanager().isOta())) {
				LOG.info("Device {} cambiando a OK",this.generalDeviceType);
				alert = new Alert(State.INFO, AlertType.DEVICE_OK.toString(), this.ieeeAddress.toString(),this.generalDeviceType.toString());
			}else if((status==DeviceStatus.KO) && (!getOtamanager().isOta())) {
				LOG.info("Device {} cambiando a KO", this.generalDeviceType);
				alert = new Alert(State.ERROR, AlertType.DEVICE_ERROR.toString(), this.ieeeAddress.toString(),this.generalDeviceType.toString());
			} else if (status==DeviceStatus.DOWNLOADINGOTAFILE) {
				LOG.info("Device {} initializing download", this.generalDeviceType);
				alert = new Alert(State.INFO, AlertType.DOWNLOADING_OTA_FILE.toString(), this.ieeeAddress.toString(),this.generalDeviceType.toString());
			} else if (status==DeviceStatus.UPGRADING) {
				LOG.info("Device {} started the upgrade", this.generalDeviceType);
				alert = new Alert(State.INFO, AlertType.UPGRADE_STARTED.toString(), this.ieeeAddress.toString(),this.generalDeviceType.toString());
			}
			ZigbeeService.getAlertList().add(alert);
			ZigbeeService.checkAlertList();
		}
		this.status = status;
	}

	public boolean isPendingKnowManCode() {
		return pendingKnowManCode;
	}

	public void setPendingKnowManCode(boolean pendingKnowManCode) {
		this.pendingKnowManCode = pendingKnowManCode;
	}
	
}
