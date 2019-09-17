package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;

public class ImageBlockRequest {
	private int fieldControl;
	private DoubleByte manufactureCode;
	private DoubleByte imageType;
	private FourByte fileversion;
	private FourByte fileoffset;
	private int maximumDataSize;
	private DoubleByte minimumBlockPeriod;
	private OctaByte requestNodeAddress;
	// numero de bytes minimos presentes en la trama;
	private int longitudTrama = 14;
	private int[] frame;
	boolean nodeAddress = false;
	boolean blockPeriod = false;

	public ImageBlockRequest(int fieldControl, DoubleByte manufactureCode, DoubleByte imageType, FourByte fileversion,
			FourByte fileoffset, OctaByte requestNodeAddress, DoubleByte minimumBlockPeriod) {

		this.fieldControl = fieldControl;
		this.manufactureCode = manufactureCode;
		this.imageType = imageType;
		this.fileversion = fileversion;
		this.fileoffset = fileoffset;
		if (minimumBlockPeriod != null) {
			this.minimumBlockPeriod = minimumBlockPeriod;
			longitudTrama = longitudTrama + 8;
			blockPeriod = true;
		}
		if (requestNodeAddress != null) {
			this.requestNodeAddress = requestNodeAddress;
			longitudTrama = longitudTrama + 2;
			nodeAddress = true;
		}
		this.frame = new int[longitudTrama];
		buildFrame();
	}

	public ImageBlockRequest(int[] frame) {
		//int pos = 0;
		this.fieldControl = frame[0];
		if ((frame[0] & 0x01) == 0x01) {
			nodeAddress = true;
			longitudTrama = longitudTrama + 8;
		}
		if ((frame[0] & 0x02) == 0x02) {
			blockPeriod = true;
			longitudTrama = longitudTrama + 2;
		}
		this.frame = new int[longitudTrama];
		//this.manufactureCode=new DoubleByte(frame[2], frame[1]);
		//this.imageType=new DoubleByte(frame[4], frame[3]);
		//this.currentFileVersion=new FourByte((byte) frame[5], (byte) frame[6],(byte) frame[7],(byte) frame[8]);
		this.manufactureCode=new DoubleByte(frame[2],frame[1]);
		this.imageType=new DoubleByte(frame[4], frame[3]);
		// comprobar que esto esta bien
		this.fileversion = new FourByte((byte) frame[5], (byte) frame[6], (byte) frame[7],
				(byte) frame[8]);
		this.fileoffset = new FourByte((byte) frame[12], (byte) frame[11], (byte) frame[10],
				(byte) frame[9]);
		this.maximumDataSize = frame[13];
		int pos=13;
		if (nodeAddress) {
			this.requestNodeAddress = new OctaByte((byte) frame[pos + 8], (byte) frame[pos + 7], (byte) frame[pos + 6],
					(byte) frame[pos + 5], (byte) frame[pos + 4], (byte) frame[pos + 3], (byte) frame[pos + 2],
					(byte) frame[pos+1]);
			pos=pos+8;

		}
		if (blockPeriod) {
			this.minimumBlockPeriod=new DoubleByte(frame[pos+2],frame[pos+1]);
			
		}

	}

	public void setFrame(int[] frame) {

		this.frame = new int[frame.length];
		this.frame = frame;
	}

	public int[] getFrame() {
		return frame;

	}
	

	public FourByte getFileoffset() {
		return fileoffset;
	}

	public void setFileoffset(FourByte fileoffset) {
		this.fileoffset = fileoffset;
	}

	public DoubleByte getMinimumBlockPeriod() {
		return minimumBlockPeriod;
	}

	public void setMinimumBlockPeriod(DoubleByte minimumBlockPeriod) {
		this.minimumBlockPeriod = minimumBlockPeriod;
	}
	
	public int getMaximumDataSize() {
		return maximumDataSize;
	}
	
	public DoubleByte getManufactureCode() {
		return manufactureCode;
	}
	

	public FourByte getFileversion() {
		return fileversion;
	}

	public DoubleByte getImageType() {
		return imageType;
	}

	private void buildFrame() {
		int pos = 0;
		frame[pos++] = fieldControl;
		frame[pos++] = manufactureCode.getLsb();
		frame[pos++] = manufactureCode.getMsb();
		frame[pos++] = imageType.getLsb();
		frame[pos++] = imageType.getMsb();
		// el file version va en orden "normal"
		for (int i = 0; i < fileversion.getData().length; i++) {
			frame[pos++] = fileversion.getData()[i];

		}
		for (int i = 0; i < fileoffset.getReverseData().length; i++) {
			frame[pos++] = fileoffset.getReverseData()[i];
		}
		frame[pos++] = maximumDataSize;
		if (nodeAddress) {
			for (int i = 0; i < requestNodeAddress.getAddressReverse().length; i++) {
				frame[pos++] = requestNodeAddress.getAddressReverse()[i];
			}
		}
		if (blockPeriod) {
			frame[pos++] = minimumBlockPeriod.getLsb();
			frame[pos++] = minimumBlockPeriod.getMsb();

		}

	}

}
