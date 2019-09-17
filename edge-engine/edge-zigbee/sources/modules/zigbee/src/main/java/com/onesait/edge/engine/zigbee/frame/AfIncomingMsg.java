package com.onesait.edge.engine.zigbee.frame;


import com.onesait.edge.engine.zigbee.exception.GenericZigbeeException;
import com.onesait.edge.engine.zigbee.util.ByteUtils;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class AfIncomingMsg extends ZFrame {
	
	private DoubleByte cmdId=new DoubleByte(ZToolCMD.AF_INCOMING_MSG);
	
	private int zclFrameControlIdx = 0;
	private int zclTransactionSequenceIdx = 1;
	private int zclCommandIdx = 2;
	private int zclPayloadIdx = 3;
	private static final int OFFSETBYTES=3;
	
    private DoubleByte clusterID;
    private int[] afIncomingData;
    private int dstEndpoint;
    private DoubleByte groupID;
    private int len;
    private int linkQuality;
    private int securityUse;
    private DoubleByte nwkAddr;
    private int srcEndpoint;
    private long timestamp;
    private int transSeqNumber;
    private int wasBroadcast;  

	public AfIncomingMsg(int[] framedata) {
        this.groupID = new DoubleByte(framedata[1],framedata[0]);
        this.clusterID = new DoubleByte(framedata[3],framedata[2]);
        this.nwkAddr = new DoubleByte(framedata[5],framedata[4]);
        this.srcEndpoint = framedata[6];
        this.dstEndpoint = framedata[7];
        this.wasBroadcast = framedata[8];
        this.linkQuality = framedata[9];
        this.securityUse = framedata[10];
        byte[] bytes = new byte[4];
        bytes[3]=(byte) framedata[11];
        bytes[2]=(byte) framedata[12];
        bytes[1]=(byte) framedata[13];
        bytes[0]=(byte) framedata[14];
        try{
        	this.timestamp = ByteUtils.convertMultiByteToLong(bytes);
        }catch (GenericZigbeeException e) {
        	this.timestamp = System.currentTimeMillis();
        }
        this.transSeqNumber = framedata[15];
        this.len = framedata[16];
        
        this.afIncomingData=new int[framedata.length-OFFSETBYTES-17];
        for(int i=0;i<this.afIncomingData.length;i++){
            this.afIncomingData[i]=framedata[17+i];
        }
        
        if (this.isManufacturerSpecific()) {
        	this.setIdxValuesWhenManSpecific();
        }
        super.buildPacket(new DoubleByte(ZToolCMD.AF_INCOMING_MSG), framedata);
    }
    
    public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	private void setIdxValuesWhenManSpecific() {
    	int manufacturerSpecificOffset = 2;
		this.zclTransactionSequenceIdx += manufacturerSpecificOffset;
		this.zclCommandIdx += manufacturerSpecificOffset;
		this.zclPayloadIdx += manufacturerSpecificOffset;
	}
    
	public boolean isReport(){
    	return this.afIncomingData[this.zclCommandIdx]==0x0A;
    }
    
    public boolean isClusterSpecific() {
    	return (this.afIncomingData[0] & ZClusterLibrary.ZCL_FRAME_CONTROL_TYPE) != 0;
    }
    
    public boolean isServerToClient() {
    	return (this.afIncomingData[0] & ZClusterLibrary.ZCL_FRAME_CONTROL_DIRECTION) != 0;
    }
    
    public boolean isDisabledDefaultResponse() {
    	return (this.afIncomingData[0] & ZClusterLibrary.ZCL_FRAME_CONTROL_DISABLE_DEFAULT_RSP) != 0;
    }
    
    public int getZclCmd() {
    	if (this.isManufacturerSpecific())
    		return this.afIncomingData[4];
    	else
    		return this.afIncomingData[2];
    }
    
    public int [] getZclPayload(){
    	int [] zclPayload;
    	if(this.isManufacturerSpecific()){
    		zclPayload=new int[this.afIncomingData.length-5];
    		for(int i=0;i<zclPayload.length;i++){
    			zclPayload[i]=this.afIncomingData[i+5];
    		}
    	}else{
    		zclPayload=new int[this.afIncomingData.length-3];
    		for(int i=0;i<zclPayload.length;i++){
    			zclPayload[i]=this.afIncomingData[i+3];
    		}
    	}
    	return zclPayload;
    }
    
    public int getZclSeqNumber() {
    	return this.afIncomingData[this.zclTransactionSequenceIdx];
    }
       
    public boolean isConfirmationRequest(){
    	return this.afIncomingData[this.zclCommandIdx]==0x01;
    }    
    public boolean isConfirmationConfig(){
    	return this.afIncomingData[this.zclCommandIdx]==0x07;
    }
    public boolean isACK(){
    	return this.afIncomingData[this.zclCommandIdx]==0x0B;
    }
    public boolean isACKReportingConfiguration(){
    	return this.afIncomingData[this.zclCommandIdx]==0x07;
    }
    public String dataToString(){
    	StringBuilder bld = new StringBuilder();
    	for(int i:afIncomingData){
    		bld.append("["+String.format("%02X", (byte)i).toUpperCase()+"]");
    	}
    	return bld.toString();
    }
			
	public byte getLinkQualityAsByte() {
		return (byte)linkQuality;
	}
	public String getStringObject(){
		return "AF_INCOMING_MSG [cmdId=" + cmdId 
				+ "],[ClusterID=" + clusterID
				+ "],[DstEndpoint=" + dstEndpoint 
				+ "],[GroupID=" + groupID
				+ "],[Len=" + len 
				+ "],[LinkQuality=" + linkQuality
				+ "],[SecurityUse=" + securityUse 
				+ "],[SrcAddr=" + nwkAddr
				+ "],[SrcEndpoint=" + srcEndpoint 
				+ "],[Timestamp=" + timestamp
				+ "],[TransSeqNumber=" + transSeqNumber 
				+ "],[WasBroadcast=" + wasBroadcast 
				+ "],[Data[]=" + dataToString()+"]]";
	}
	public int[] getAfIncomingData(){
		return this.afIncomingData;
	}
	public boolean isZDO(){
		return (this.dstEndpoint==0 && this.srcEndpoint==0);
	}
    
	public boolean isManufacturerSpecific() {
		return ((this.afIncomingData[this.zclFrameControlIdx] &
				ZClusterLibrary.ZCL_FRAME_CONTROL_MANU_SPECIFIC) > 0);
	}
	
    public Integer getSequenceNumber() {
    	return this.afIncomingData[this.zclTransactionSequenceIdx];
    }

    public int getZclPayloadOffset() {
    	return this.zclPayloadIdx;
    }
    
    public DoubleByte getCmdId() {
		return cmdId;
	}

	public void setCmdId(DoubleByte cmdId) {
		this.cmdId = cmdId;
	}

	public int getZclFrameControlIdx() {
		return zclFrameControlIdx;
	}

	public void setZclFrameControlIdx(int zclFrameControlIdx) {
		this.zclFrameControlIdx = zclFrameControlIdx;
	}

	public int getZclTransactionSequenceIdx() {
		return zclTransactionSequenceIdx;
	}

	public void setZclTransactionSequenceIdx(int zclTransactionSequenceIdx) {
		this.zclTransactionSequenceIdx = zclTransactionSequenceIdx;
	}

	public int getZclCommandIdx() {
		return zclCommandIdx;
	}

	public void setZclCommandIdx(int zclCommandIdx) {
		this.zclCommandIdx = zclCommandIdx;
	}

	public int getZclPayloadIdx() {
		return zclPayloadIdx;
	}

	public void setZclPayloadIdx(int zclPayloadIdx) {
		this.zclPayloadIdx = zclPayloadIdx;
	}

	public DoubleByte getClusterID() {
		return clusterID;
	}

	public void setClusterID(DoubleByte clusterID) {
		this.clusterID = clusterID;
	}

	public void setData(int[] data) {
		this.afIncomingData = data;
	}

	public int getDstEndpoint() {
		return dstEndpoint;
	}

	public void setDstEndpoint(int dstEndpoint) {
		this.dstEndpoint = dstEndpoint;
	}

	public DoubleByte getGroupID() {
		return groupID;
	}

	public void setGroupID(DoubleByte groupID) {
		this.groupID = groupID;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getLinkQuality() {
		return linkQuality;
	}

	public void setLinkQuality(int linkQuality) {
		this.linkQuality = linkQuality;
	}

	public int getSecurityUse() {
		return securityUse;
	}

	public void setSecurityUse(int securityUse) {
		this.securityUse = securityUse;
	}

	public int getSrcEndpoint() {
		return srcEndpoint;
	}

	public void setSrcEndpoint(int srcEndpoint) {
		this.srcEndpoint = srcEndpoint;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getTransSeqNumber() {
		return transSeqNumber;
	}

	public void setTransSeqNumber(int transSeqNumber) {
		this.transSeqNumber = transSeqNumber;
	}

	public int getWasBroadcast() {
		return wasBroadcast;
	}

	public void setWasBroadcast(int wasBroadcast) {
		this.wasBroadcast = wasBroadcast;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}
}
