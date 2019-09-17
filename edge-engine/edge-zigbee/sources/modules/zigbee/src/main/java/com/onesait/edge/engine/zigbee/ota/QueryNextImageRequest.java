package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;

public class QueryNextImageRequest {

	private int fieldControl;
	private DoubleByte manufactureCode;
	private DoubleByte imageType;
	private FourByte currentFileVersion;
	//opcional
	private DoubleByte hardwareVersion;
	private boolean hardVersion=false;
	public QueryNextImageRequest(int fieldControl, DoubleByte manufactureCode, DoubleByte imageType,
			FourByte currentFileVersion, DoubleByte hardwareVersion) {
		this.fieldControl = fieldControl;
		this.manufactureCode = manufactureCode;
		this.imageType = imageType;
		this.currentFileVersion = currentFileVersion;
		this.hardwareVersion = hardwareVersion;
	}
	
	public QueryNextImageRequest(int [] frame){
		if(frame[0]==0x01){
			hardVersion=true;
		}
		buildAttri(frame);
		
		
		
	}
	private void buildAttri(int [] frame){
		fieldControl=frame[0];
//		this.shortAddress=new DoubleByte(framedata[2],framedata[1]);
		this.manufactureCode=new DoubleByte(frame[2], frame[1]);
		this.imageType=new DoubleByte(frame[4], frame[3]);
		this.currentFileVersion=new FourByte((byte) frame[5], (byte) frame[6],(byte) frame[7],(byte) frame[8]);
		if(hardVersion){
			this.hardwareVersion=new DoubleByte(frame[10], frame[9]);
		}
		
		
	}

	public int getFieldControl() {
		return fieldControl;
	}

	public void setFieldControl(int fieldControl) {
		this.fieldControl = fieldControl;
	}

	public DoubleByte getManufacturaCode() {
		return manufactureCode;
	}

	public void setManufacturaCode(DoubleByte manufacturaCode) {
		this.manufactureCode = manufacturaCode;
	}

	public DoubleByte getImageType() {
		return imageType;
	}

	public void setImageType(DoubleByte imageType) {
		this.imageType = imageType;
	}

	public FourByte getCurrentFileVersion() {
		return currentFileVersion;
	}

	public void setCurrentFileVersion(FourByte currentFileVersion) {
		this.currentFileVersion = currentFileVersion;
	}

	public DoubleByte getHardwareVersion() {
		return hardwareVersion;
	}

	public void setHardwareVersion(DoubleByte hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public boolean isHardVersion() {
		return hardVersion;
	}

	public void setHardVersion(boolean hardVersion) {
		this.hardVersion = hardVersion;
	}
	
	
	
}
