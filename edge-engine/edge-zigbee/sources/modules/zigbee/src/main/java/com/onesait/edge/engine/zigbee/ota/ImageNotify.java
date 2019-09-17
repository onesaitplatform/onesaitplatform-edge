package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;

public class ImageNotify {

	private int payloadType;
	private int queryJitter;
	private DoubleByte manufactureCode;
	private DoubleByte imageType;
	private FourByte newFileVersion;
	private int [] frame;
	boolean isManuCode=false;
	boolean isImageType=false;
	boolean isNewFileVersion=false;
	private int longitudTrama=2;
	public ImageNotify(int payloadType, int queryJitter, DoubleByte manufactureCode, DoubleByte imageType,
			FourByte newFileVersion) {
		this.payloadType = payloadType;
		this.queryJitter = queryJitter;
		this.manufactureCode = manufactureCode;
		this.imageType = imageType;
		this.newFileVersion = newFileVersion;
		buildFrame();
	}
	private void buildFrame() {
		int pos=0;
		if(payloadType==0x01){
			isManuCode=true;
			longitudTrama=longitudTrama+2;
		}else if(payloadType==0x02){
			longitudTrama=longitudTrama+4;
			isManuCode=true;
			isImageType=true;
		}else if(payloadType==0x03){
			longitudTrama=longitudTrama+8;
			isManuCode=true;
			isImageType=true;
			isNewFileVersion=true;
			
		}
		frame=new int [longitudTrama];
		frame[pos++]=payloadType;
		frame[pos++]=queryJitter;
		if(isNewFileVersion){
			frame[pos++]=manufactureCode.getLsb();
			frame[pos++]=manufactureCode.getMsb();
			frame[pos++]=imageType.getLsb();
			frame[pos++]=imageType.getMsb();
			for (int i=0; i<newFileVersion.getData().length;i++){
				frame[pos++]=newFileVersion.getData()[i];
			}
		}else if(isImageType){
			frame[pos++]=manufactureCode.getLsb();
			frame[pos++]=manufactureCode.getMsb();
			frame[pos++]=imageType.getLsb();
			frame[pos]=imageType.getMsb();
			
		}else if(isManuCode){
			frame[pos++]=manufactureCode.getLsb();
			frame[pos]=manufactureCode.getMsb();
		}		
	}
	public int getPayloadType() {
		return payloadType;
	}
	public void setPayloadType(int payloadType) {
		this.payloadType = payloadType;
	}
	public int getQueryJitter() {
		return queryJitter;
	}
	public void setQueryJitter(int queryJitter) {
		this.queryJitter = queryJitter;
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
	public FourByte getNewFileVersion() {
		return newFileVersion;
	}
	public void setNewFileVersion(FourByte newFileVersion) {
		this.newFileVersion = newFileVersion;
	}
	public int[] getFrame() {
		return frame;
	}
	public void setFrame(int[] frame) {
		this.frame = frame;
	}
	public boolean isManuCode() {
		return isManuCode;
	}
	public void setManuCode(boolean isManuCode) {
		this.isManuCode = isManuCode;
	}
	public boolean isImageType() {
		return isImageType;
	}
	public void setImageType(boolean isImageType) {
		this.isImageType = isImageType;
	}
	public boolean isNewFileVersion() {
		return isNewFileVersion;
	}
	public void setNewFileVersion(boolean isNewFileVersion) {
		this.isNewFileVersion = isNewFileVersion;
	}
	
	
	
	
}
