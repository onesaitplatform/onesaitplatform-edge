package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;

public class QueryNextImageResponse {
	private int status;
	private DoubleByte manufactureCode;
	private DoubleByte imageType;
	private FourByte fileVersion;
	private FourByte imageSize;
//	private int[] frame=new int[13];
	private int[] frame;

	public QueryNextImageResponse(int status, DoubleByte manufactureCode, DoubleByte imageType, FourByte fileVersion,
			FourByte imageSize) {
		this.status = status;
		this.manufactureCode = manufactureCode;
		this.imageType = imageType;
		this.fileVersion = fileVersion;
		this.imageSize = imageSize;
		buildFrame();
		
	}
	public QueryNextImageResponse(byte status){
		this.status=status & 0xFF;
		buildFrame();
	}

	private void buildFrame() {
		int pos = 0;
		if (status == 0x00) {
			frame = new int[13];
			frame[pos++] = status;
			frame[pos++] = manufactureCode.getLsb();
			frame[pos++] = manufactureCode.getMsb();
			frame[pos++] = imageType.getLsb();
			frame[pos++] = imageType.getMsb();

			for (int i = 0; i < fileVersion.getData().length; i++) {
				frame[pos++] = fileVersion.getData()[i];
			}
			for (int i = 0; i < imageSize.getReverseData().length; i++) {
				frame[pos++] = imageSize.getReverseData()[i];
			}
		} else {
			frame = new int[1];
			frame[pos++] = status;
		}

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

	public FourByte getImageSize() {
		return imageSize;
	}

	public void setImageSize(FourByte imageSize) {
		this.imageSize = imageSize;
	}

	public int[] getFrame() {
		return frame;
	}

	public void setFrame(int[] frame) {
		this.frame = frame;
	}

}
