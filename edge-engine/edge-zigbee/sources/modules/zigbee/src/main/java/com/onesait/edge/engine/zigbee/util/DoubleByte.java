package com.onesait.edge.engine.zigbee.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DoubleByte implements Serializable{
	private int msb;
	private int lsb;
	private int val;
	
	/**
	 * Decomposes a 16bit int into high and low bytes
	 * 
	 * @param val
	 */
	public DoubleByte(int val) {
		this.val = val > 0xFFFF ? 0xFFFF : val;
		msb = (this.val >> 8) & 0x00FF;
		lsb = this.val & 0x00FF;
	}

	public DoubleByte(long val) {
		this.val = val > 0xFFFF ? 0xFFFF : (int)val;
		msb = (this.val >> 8) & 0x00FF;
		lsb = this.val & 0x00FF;
	}
	
	public static final String toHexString(int val){
		DoubleByte db=new  DoubleByte(val);
		return db.toStr();
	}
	/**
	 * Constructs a 16bit value from two bytes (high and low)
	 * 
	 * @param msb
	 * @param lsb
	 */
	public DoubleByte(int msb, int lsb) {
		
		if (msb > 0xFF || lsb > 0xFF) {
			throw new IllegalArgumentException("msb or lsb are out of range");
		}

		this.msb = msb;
		this.lsb = lsb;
	}
	
	public DoubleByte(String saString) {
		this(Integer.decode(saString));
	}
	
	public int getMsb() {
		return msb;
	}

	public int getLsb() {
		return lsb;
	}	
	
	public int get16BitValue() {
		return ((this.msb << 8) + this.lsb );
	}

	public int intValue(){
		return ((int)((byte)this.getMsb()<<8 & 0x0000FF00)+((int)((byte)this.getLsb() & 0x000000FF)));
	}
	
	public long longValue(){
		return intValue();
	}
	
	public void setMsb(int msb) {
		this.msb = (byte)msb;
	}

	public void setLsb(int lsb) {
		this.lsb = (byte)lsb;
	}
	
	@Override
	public String toString(){
		String cab= "0x";
		return cab + this.toStr();
	}
	
	public String toStr(){
		return String.format("%02X", (byte)msb) + String.format("%02X", (byte)lsb);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lsb;
		result = prime * result + msb;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleByte other = (DoubleByte) obj;
		if ((byte)lsb != (byte)other.lsb)
			return false;
		if ((byte)msb != (byte)other.msb)
			return false;
		return true;
	}

	/**
	 * @return the val
	 */
	public int getVal() {
		return val;
	}
	
	public Boolean greaterThan(Object obj) {
		if (this == obj)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleByte other = (DoubleByte) obj;
		if ((byte)this.msb > (byte)other.msb)
			return true;
		if ((byte)this.msb < (byte)other.msb)
			return false;
		if ((byte)this.lsb > (byte)other.lsb)
			return true;

		return false;
	}
	
	public Boolean lowerThan(Object obj) {
		if (this == obj)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleByte other = (DoubleByte) obj;
		if ((byte)this.msb < (byte)other.msb)
			return true;
		if ((byte)this.msb > (byte)other.msb)
			return false;
		if ((byte)this.lsb < (byte)other.lsb)
			return true;

		return false;
	}
}
