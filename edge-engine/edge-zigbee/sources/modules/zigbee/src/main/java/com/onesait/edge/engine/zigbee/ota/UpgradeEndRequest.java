package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;

public class UpgradeEndRequest {

	private int status;
	private DoubleByte manufactureCode;
	private DoubleByte imageType;
	private FourByte fileVersion;
	/*public UpgradeEndRequest(int status, DoubleByte manufactureCode, DoubleByte imageType, FourByte fileVersion) {
		this.status = status;
		this.manufactureCode = manufactureCode;
		this.imageType = imageType;
		this.fileVersion = fileVersion;
		build
		
		
	}*/
	
	public UpgradeEndRequest (int [] frame){
		buildAttr(frame);
		
	}
	
	private void buildAttr(int[] frame) {
			
		this.status=frame[0];
		this.manufactureCode=new DoubleByte(frame[2], frame[1]);
		this.imageType=new DoubleByte(frame[4], frame[3]);
		this.fileVersion=new FourByte((byte) frame[5],(byte) frame[6],(byte) frame [7],(byte) frame[8]);
		
	}




	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public DoubleByte getManufactureCode() {
		return manufactureCode;
	}
	public void setManufactureCode(DoubleByte manufactureCode) {
		this.manufactureCode = manufactureCode;
	}
	public DoubleByte getImageType() {
		return imageType;
	}
	public void setImageType(DoubleByte imageType) {
		this.imageType = imageType;
	}
	public FourByte getFileVersion() {
		return fileVersion;
	}
	public void setFileVersion(FourByte fileVersion) {
		this.fileVersion = fileVersion;
	}
	
	
	
}
