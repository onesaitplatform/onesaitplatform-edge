package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;

public class ImageBlockResponse {

	private int status;
	private DoubleByte manufactureCode;
	private DoubleByte imageType;
	private FourByte fileversion;
	private FourByte fileOffset;
	private int dataSize;
	private byte[] imageData;
	private int[] frame;
	private int longitudTrama=14;
	
	public ImageBlockResponse(int status, DoubleByte manufactureCode, DoubleByte imageType, FourByte fileversion,
			FourByte fileOffset, int dataSize,byte[] imageData) {
		
		this.status = status;
		this.manufactureCode = manufactureCode;
		this.imageType = imageType;
		this.fileversion = fileversion;
		this.fileOffset = fileOffset;
		this.dataSize = dataSize;
		this.imageData= imageData;
		this.frame=new int[longitudTrama+imageData.length];
		buildFrame();
	}

public ImageBlockResponse(byte status){
	
	this.status=status & 0xFF;
	buildFrame();	
}
	



private void buildFrame() {
		int pos=0;
		if(status==0x00){
			frame=new int[longitudTrama+imageData.length];
			frame[pos++]=status;
			frame[pos++]=manufactureCode.getLsb();
			frame[pos++]=manufactureCode.getMsb();
			frame[pos++]=imageType.getLsb();
			frame[pos++]=imageType.getMsb();
		
		for(int i=0;i<fileversion.getData().length;i++){
			frame[pos++]=fileversion.getData()[i];
		}
		for(int i=0;i<fileOffset.getReverseData().length;i++){
			frame[pos++]=fileOffset.getReverseData()[i];
		}
		frame[pos++]=dataSize;
		for(int i=0; i<dataSize;i++){
			frame[pos++]=imageData[i] & (0xFF) ;
		}
		}else if(status==(ZClusterLibrary.ZCL_STATUS_OTA_ABORT & 0xFF)){
			frame=new int[1];
			frame[pos++]=status;
		}
		
	}
	public int getSuccessStatus() {
		return status;
	}


	public void setSuccessStatus(int status) {
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


	public FourByte getFileversion() {
		return fileversion;
	}


	public void setFileversion(FourByte fileversion) {
		this.fileversion = fileversion;
	}


	public FourByte getFileOffset() {
		return fileOffset;
	}


	public void setFileOffset(FourByte fileOffset) {
		this.fileOffset = fileOffset;
	}


	public int getDataSize() {
		return dataSize;
	}


	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}


	public byte[] getImageData() {
		return imageData;
	}


	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}


	public int[] getFrame() {
		return frame;
	}


	public void setFrame(int[] frame) {
		this.frame = frame;
	}
	
	
	
}
