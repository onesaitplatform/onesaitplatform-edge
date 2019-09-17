package com.onesait.edge.engine.zigbee.util;

public class FourByte {
	private int lsb;
	private int msb3;
	private int msb2;
	private int msb1;
	private int[] data=new int[4];

	/**
	 * Clase encargada de manejar un numero de 4 bytes siendo este 0x msb3 msb2
	 * msb1 lsb
	 * 
	 * @param numero
	 *            entero
	 */
	public FourByte(int number) {

		msb3 = (number >> 24) & 0x000000FF;
		msb2 = (number >> 16) & 0x000000FF;
		msb1 = (number >> 8) & 0x000000FF;
		lsb = number & 0x000000FF;
		data[3] = lsb;
		data[2] = msb1;
		data[1] = msb2;
		data[0] = msb3;
	}
	
	public FourByte(long number){
		msb3 = (int) ((number >> 24) & 0x000000FF);
		msb2 = (int) ((number >> 16) & 0x000000FF);
		msb1 = (int) ((number >> 8) & 0x000000FF);
		lsb = (int) (number & 0x000000FF);
		data[3] = lsb;
		data[2] = msb1;
		data[1] = msb2;
		data[0] = msb3;
		
		
	}

	public FourByte(byte b3, byte b2, byte b1, byte lsb) {
		this.lsb = lsb & 0xFF;
		this.msb1 = b1 & 0xFF;
		this.msb2 = b2 & 0xFF;
		this.msb3 = b3 & 0xFF;
		data[3] = lsb & 0xFF;
		data[2] = b1 & 0xFF;
		data[1] = b2 & 0xFF;
		data[0] = b3 & 0xFF;


	}
	public FourByte(byte [] buffer){
		for(int i=0; i<buffer.length;i++){
			data[i]=buffer[i] & 0xFF;
		}
		this.lsb=data[3];
		this.msb1=data[2];
		this.msb2=data[1];
		this.msb3=data[0];
	}

	public int getLsb() {
		return lsb;
	}

	public void setLsb(int lsb) {
		this.lsb = lsb;
	}

	public int getMsb3() {
		return msb3;
	}

	public void setMsb3(int msb3) {
		this.msb3 = msb3;
	}

	public int getMsb2() {
		return msb2;
	}

	public void setMsb2(int msb2) {
		this.msb2 = msb2;
	}

	public int getMsb1() {
		return msb1;
	}

	public void setMsb1(int msb1) {
		this.msb1 = msb1;
	}
	
	public String getMsb1String() {
		return String.format("%02X", (byte)msb1);
	}

	public String getMsb2String() {
		return String.format("%02X", (byte)msb2);
	}

	public String getMsb3String() {
		return String.format("%02X", (byte)msb3);
	}

	public String getLsbString() {
		return String.format("%02X", (byte)lsb);
	}

	public int[] getData() {
		return data;
	}

	public int[] getReverseData() {
		int[] dataR = new int[this.data.length];
		int r=3;
		for (int i = 0; i < this.data.length; i++) {
			dataR[r] = data[i];
			r--;
		}
		return dataR;
	}

	public void setFrame(int[] data) {
		this.data = data;
	}

	public long longValue(){
		return (((long)((byte)this.getMsb3()<<24 & 0xFF000000))+((long)((byte)this.getMsb2()<<16 & 0x00FF0000))+((long)((byte)this.getMsb1()<<8 & 0x0000FF00)+((long)((byte)this.getLsb() & 0x000000FF))));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lsb;
		result = prime * result + msb1;
		result = prime * result + msb2;
		result = prime * result + msb3;
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
		FourByte other = (FourByte) obj;
		if (lsb != other.lsb)
			return false;
		if (msb1 != other.msb1)
			return false;
		if (msb2 != other.msb2)
			return false;
		if (msb3 != other.msb3)
			return false;
		return true;
	}

	public Boolean greaterThan(Object obj) {
		if (this == obj)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FourByte other = (FourByte) obj;
		if ((byte) this.msb3 > (byte) other.msb3)
			return true;
		if ((byte) this.msb3 < (byte) other.msb3)
			return false;
		if ((byte) this.msb2 > (byte) other.msb2)
			return true;
		if ((byte) this.msb2 < (byte) other.msb2)
			return false;
		if ((byte) this.msb1 > (byte) other.msb1)
			return true;
		if ((byte) this.msb1 < (byte) other.msb1)
			return false;
		if ((byte) this.lsb > (byte) other.lsb)
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
		FourByte other = (FourByte) obj;
		if ((byte) this.msb3 < (byte) other.msb3)
			return true;
		if ((byte) this.msb3 > (byte) other.msb3)
			return false;
		if ((byte) this.msb2 < (byte) other.msb2)
			return true;
		if ((byte) this.msb2 > (byte) other.msb2)
			return false;
		if ((byte) this.msb1 < (byte) other.msb1)
			return true;
		if ((byte) this.msb1 > (byte) other.msb1)
			return false;
		if ((byte) this.lsb < (byte) other.lsb)
			return true;

		return false;
	}
	@Override
	public String toString(){
		String cab= "0x";
		return cab + this.toStr();
	}
	public String toStr(){
		return String.format("%02X", (byte)msb3)+String.format("%02X", (byte)msb2)+String.format("%02X", (byte)msb1) + String.format("%02X", (byte)lsb);
	}

}
