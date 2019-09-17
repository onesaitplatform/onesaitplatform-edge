package com.onesait.edge.engine.zigbee.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class OctaByte implements Serializable, Cloneable {
		
	// broadcast address 0x000000ff
	public static final OctaByte ADDRESS_BROADCAST = new OctaByte(new byte[] {0, 0, 0, 0, 0, 0, (byte) 0xff, (byte) 0xff});
	public static final OctaByte ADDRESS_ZNET_COORDINATOR = new OctaByte(new byte[] {0, 0, 0, 0, 0, 0, 0, 0});
	
	private byte[] data;

	/**
	 * Parses an 64-bit XBee address from a string representation
	 * Must be in the format "## ## ## ## ## ## ## ##" (i.e. don't use 0x prefix)
	 * 
	 * @param addressStr
	 */
	public OctaByte(String addressStr) {
		constructFromString(addressStr);
	}

	/**
	 * Parses an 64-bit XBee address from a string representation
	 * Must be in the format "## ## ## ## ## ## ## ##" (i.e. don't use 0x prefix)
	 * 
	 * @param addressStr
	 */
	public OctaByte(String addressStr, boolean token) {
		if(token){
			constructFromString(addressStr);
		}else{
			String str="";
			for (int i = 0; i < addressStr.length(); i++) {
				str+=addressStr.charAt(i);
				if(i%2==1){
					str+=" ";
				}				
			}
			str=str.trim();
			constructFromString(str);
		}
		
	}
	
	private void constructFromString(String str) {
		StringTokenizer st = new StringTokenizer(str, " ");
		
		data = new byte[8];
		
		for (int i = 0; i < data.length; i++) {
			String byteStr = st.nextToken();
			data[i] = (byte) ( Integer.parseInt(byteStr, 16) & 0xFF );
			//log.debug("byte is " + ByteUtils.toBase16(address[i]) + " at pos " + i);
		}
	}
	
	public OctaByte(long ieee){
		data = new byte[8];
		for (int i = data.length - 1 ; i >= 0; i--) {
			data[i] = (byte) ieee ;
			ieee = ieee >> 8;
		}
	}
	
	/**
	 * Creates a 64-bit address
	 *  
	 * @param b1 MSB
	 * @param b2
	 * @param b3
	 * @param b4
	 * @param b5
	 * @param b6
	 * @param b7
	 * @param b8 LSB
	 */
	public OctaByte(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
		data = new byte[8];
		
		data[0] = b1;
		data[1] = b2;
		data[2] = b3;
		data[3] = b4;
		data[4] = b5;
		data[5] = b6;
		data[6] = b7;
		data[7] = b8;
	}

	public OctaByte(byte[] address) {
		this.data = Arrays.copyOf( address, address.length );
	}
	
	public OctaByte() {
		data = new byte[8];
	}

	public void setAddress(byte[] address) {
	    this.data = Arrays.copyOf( address, address.length );
	}
	

	public byte[] getAddress() {
		return Arrays.copyOf( data, data.length );
	}
	public byte[] getAddressReverse() {
		byte[] address = this.getAddress();
		byte[] addressR= new byte[address.length];
		for (int i = 0; i < address.length; i++) {
			addressR[address.length-i-1]=address[i];
		}
		return addressR;
	}	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	// TODO: Cambiar toString y crear toHexString
	@Override
	public String toString() {
		return ByteUtils.showFrame(data);
	}
	/**
	 * Convert MAC STRING "################" in format "## ## ## ## ## ## ## ##" 
	 * @param mac
	 * @return
	 */
	public static String convertMac(String mac){
		String output="";
		try{
			for (int i = 0; i < 16; i=2+i) {
				output+=mac.substring(i, i+2)+" ";
			}
		}catch(Exception e){
			
		}
		return output.trim();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OctaByte other = (OctaByte) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		return true;
	}

	public Object clone() {
		OctaByte clonedOb = new OctaByte();
		byte[] data = new byte[this.data.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = this.data[i];
		}
		clonedOb.setAddress(data);
		return clonedOb;
	}
}