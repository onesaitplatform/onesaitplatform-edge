package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;

public class UpgradeEndResponse {

	private DoubleByte manufactureCode;
	private DoubleByte imageType;
	private FourByte fileVersion;
	//pendiente de revisar, ya que esto es el timestamp
	private FourByte currentTime;
	private FourByte upgradeTime;
	private int[] frame=new int [16];
	public UpgradeEndResponse(DoubleByte manufactureCode, DoubleByte imageType, FourByte fileVersion,
			FourByte currentTime,FourByte upgradeTime) {
		this.manufactureCode = manufactureCode;
		this.imageType = imageType;
		this.fileVersion = fileVersion;
		this.currentTime = currentTime;
		this.upgradeTime=upgradeTime;
		buildFrame();
	}
	private void buildFrame() {
		int pos=0;
		frame[pos++]=manufactureCode.getLsb();
		frame[pos++]=manufactureCode.getMsb();
		frame[pos++]=imageType.getLsb();
		frame[pos++]=imageType.getMsb();
		
		
		for(int i=0;i<fileVersion.getData().length;i++){
			frame[pos++]=fileVersion.getData()[i];
		}
		for (int i=0; i<currentTime.getReverseData().length;i++){
			frame[pos++]=currentTime.getReverseData()[i];
		}
		for (int i=0; i<upgradeTime.getReverseData().length;i++){
			frame[pos++]=upgradeTime.getReverseData()[i];
		}
		
		
	}
	public FourByte getUpgradeTime() {
		return upgradeTime;
	}
	public void setUpgradeTime(FourByte upgradeTime) {
		this.upgradeTime = upgradeTime;
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
	public FourByte getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(FourByte currentTime) {
		this.currentTime = currentTime;
	}
	public int[] getFrame() {
		return frame;
	}
	public void setFrame(int[] frame) {
		this.frame = frame;
	}
	
	
	
}
