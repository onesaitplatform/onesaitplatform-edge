package com.onesait.edge.engine.zigbee.frame;

import java.util.Arrays;
import java.util.List;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class ZdoSimpleDescRsp extends ZFrame implements InputZdoZFrame{
    private DoubleByte devID;
    private int devVer;
    private int inClusterCount;
    private DoubleByte[] inClusterList;
    private int outClusterCount;
    private DoubleByte[] outClusterList;
    private DoubleByte profID;
    private int endpoint;
    private DoubleByte nwkAddr;
    private DoubleByte srcAddress;
    private int status;
    private int len;
	private short[] inputs;
	private short[] outputs;

    public ZdoSimpleDescRsp(int[] framedata) {
        this.srcAddress=new DoubleByte(framedata[1],framedata[0]);
        this.status = framedata[2];
        this.nwkAddr=new DoubleByte(framedata[4],framedata[3]);
        this.len = framedata[5];
        this.endpoint = framedata[6];
        this.profID = new DoubleByte(framedata[8],framedata[7]);
        this.devID = new DoubleByte(framedata[10],framedata[9]);
        this.devVer = framedata[11];
        
        this.inClusterCount = framedata[12];
        this.inClusterList=new DoubleByte[this.inClusterCount];

        for (int i = 0; i < this.inClusterCount; i++) {
            this.inClusterList[i]=new DoubleByte(framedata[(i * 2) + 14],framedata[(i * 2) + 13]);
        }
        this.outClusterCount = framedata[((this.inClusterCount) * 2) + 13];
        this.outClusterList=new DoubleByte[this.outClusterCount];
        for (int i = 0; i < this.outClusterCount; i++) {
            this.outClusterList[i]=new DoubleByte(framedata[(i * 2) + ((this.inClusterCount) * 2) + 15],framedata[(i * 2) + ((this.inClusterCount) * 2) + 14]);
        }
        
        super.buildPacket(new DoubleByte(ZToolCMD.ZDO_SIMPLE_DESC_RSP), framedata);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ZDO_SIMPLE_DESC_RSP [\n DevID=" + devID + ",\n DevVer=" + devVer
				+ ",\n InClusterCount=" + inClusterCount + ",\n InClusterList="
				+ Arrays.toString(inClusterList) + ",\n OutClusterCount="
				+ outClusterCount + ",\n OutClusterList="
				+ Arrays.toString(outClusterList) + ",\n ProfID=" + profID
				+ ",\n Endpoint=" + endpoint + ",\n nwkAddr=" + nwkAddr
				+ ",\n SrcAddress=" + srcAddress + ",\n Status=" + status
				+ ",\n len=" + len + ",\n inputs=" + Arrays.toString(inputs)
				+ ",\n outputs=" + Arrays.toString(outputs) + "]";
	}

	
	public DoubleByte getDevID() {
		return devID;
	}

	public void setDevID(DoubleByte devID) {
		this.devID = devID;
	}

	public int getDevVer() {
		return devVer;
	}

	public void setDevVer(int devVer) {
		this.devVer = devVer;
	}

	public int getInClusterCount() {
		return inClusterCount;
	}

	public void setInClusterCount(int inClusterCount) {
		this.inClusterCount = inClusterCount;
	}

	public int getOutClusterCount() {
		return outClusterCount;
	}

	public void setOutClusterCount(int outClusterCount) {
		this.outClusterCount = outClusterCount;
	}

	public DoubleByte[] getOutClusterList() {
		return outClusterList;
	}

	public void setOutClusterList(DoubleByte[] outClusterList) {
		this.outClusterList = outClusterList;
	}

	public DoubleByte getProfID() {
		return profID;
	}

	public void setProfID(DoubleByte profID) {
		this.profID = profID;
	}

	public int getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(int endpoint) {
		this.endpoint = endpoint;
	}

	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	public DoubleByte getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(DoubleByte srcAddress) {
		this.srcAddress = srcAddress;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public short[] getInputs() {
		return inputs;
	}

	public void setInputs(short[] inputs) {
		this.inputs = inputs;
	}

	public short[] getOutputs() {
		return outputs;
	}

	public void setOutputs(short[] outputs) {
		this.outputs = outputs;
	}

	public void setInClusterList(DoubleByte[] inClusterList) {
		this.inClusterList = inClusterList;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public DoubleByte[] getInClusterList() {
		return inClusterList;
	}

	/**
	 * @return the inClusterList
	 */
	public List<DoubleByte> getInClusterListAsList() {
		return Arrays.asList(inClusterList);
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.srcAddress;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.SimpleDescriptorResponse;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
}
