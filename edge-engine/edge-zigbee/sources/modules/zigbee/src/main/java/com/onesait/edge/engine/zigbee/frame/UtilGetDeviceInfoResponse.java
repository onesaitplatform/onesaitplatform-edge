package com.onesait.edge.engine.zigbee.frame;

import java.util.Arrays;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class UtilGetDeviceInfoResponse extends ZFrame {
	
	public static final int DEV_HOLD_VALUE = 0;
	
    /**Dynamic array; Assoc Devices List*/
    private DoubleByte[] assocDevicesList;
    /**Device Type*/
    private int deviceState;
    /**Bitmap byte field indicating device type; where bits 0 to 2 indicate the capability for the device to operate as a 
     * coordinator; router; or end device; respectively
     */
    private int deviceType;
    /**IEEE Address*/    
    private OctaByte ieeeAddr;
    /**Number Assoc Devices*/
    private int numAssocDevices;
    /**Short Address*/
    private DoubleByte shortAddress;
    /**The fail status is returned if the address value in the command message was not within the valid range.*/
    private int status;


    public UtilGetDeviceInfoResponse(int cmd_status1, OctaByte num1, DoubleByte num2, int device_type1, int device_state1, int num3, DoubleByte[] numArray1) {
        this.status = cmd_status1;
        this.ieeeAddr = num1;
        this.shortAddress = num2;
        this.deviceType = device_type1;
        this.deviceState = device_state1;
        this.numAssocDevices = num3;
        this.assocDevicesList = numArray1;
        int[] framedata = new int[14 + (numArray1.length * 2)];
        framedata[0] = this.status;
        for (int i = 0; i < 8; i++) {
            framedata[i + 1] = this.ieeeAddr.getAddress()[7-i];
        }
        framedata[9] = this.shortAddress.getLsb();
        framedata[10] = this.shortAddress.getMsb();
        framedata[11] = this.deviceType;
        framedata[12] = this.deviceState;
        framedata[13] = this.numAssocDevices;
        for (int i = 0; i < numArray1.length; i++) {
            framedata[14 + (i * 2)] = numArray1[i].getMsb();
            framedata[14 + (i * 2) + 1] = numArray1[i].getLsb();
        }

        super.buildPacket(new DoubleByte(ZToolCMD.UTIL_GET_DEVICE_INFO_RESPONSE), framedata);
    }
    
    public UtilGetDeviceInfoResponse(int[] framedata) {

        this.status = framedata[0];
        byte[] bytes=new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[7-i] = (byte)framedata[i + 1];
        }
        this.ieeeAddr=new OctaByte(bytes);
        this.shortAddress=new DoubleByte(framedata[9],framedata[10]);
        this.deviceType = framedata[11];
        this.deviceState = framedata[12];
        this.numAssocDevices = framedata[13];
        //AssocDevicesList=new DoubleByte[(framedata.length-14)/2];//Actually more than NumAssocDevices
        assocDevicesList=new DoubleByte[this.numAssocDevices];
        for (int i = 0; i < this.assocDevicesList.length; i++) {
            assocDevicesList[i]=new DoubleByte(framedata[15 + (i*2)],framedata[14 + (i*2)]);
        }
        super.buildPacket(new DoubleByte(ZToolCMD.UTIL_GET_DEVICE_INFO_RESPONSE), framedata);
    }
    @Override
	public String toString() {
		return "UTIL_GET_DEVICE_INFO_RESPONSE [\n AssocDevicesList="
				+ Arrays.toString(assocDevicesList) + ",\n DeviceState="
				+ deviceState + ",\n DeviceType=" + deviceType + ",\n IEEEAddr="
				+ ieeeAddr + ",\n NumAssocDevices=" + numAssocDevices
				+ ",\n ShortAddress=" + shortAddress + ",\n Status=" + status + "]";
	}

	public DoubleByte[] getAssocDevicesList() {
		return assocDevicesList;
	}

	public void setAssocDevicesList(DoubleByte[] assocDevicesList) {
		this.assocDevicesList = assocDevicesList;
	}

	public int getDeviceState() {
		return deviceState;
	}

	public void setDeviceState(int deviceState) {
		this.deviceState = deviceState;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public OctaByte getIeeeAddr() {
		return ieeeAddr;
	}

	public void setIeeeAddr(OctaByte ieeeAddr) {
		this.ieeeAddr = ieeeAddr;
	}

	public int getNumAssocDevices() {
		return numAssocDevices;
	}

	public void setNumAssocDevices(int numAssocDevices) {
		this.numAssocDevices = numAssocDevices;
	}

	public DoubleByte getShortAddress() {
		return shortAddress;
	}

	public void setShortAddress(DoubleByte shortAddress) {
		this.shortAddress = shortAddress;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}        
}
