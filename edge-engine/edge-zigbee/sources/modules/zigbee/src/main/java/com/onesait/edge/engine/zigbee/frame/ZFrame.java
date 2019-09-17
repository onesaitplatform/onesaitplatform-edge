package com.onesait.edge.engine.zigbee.frame;

import java.util.Arrays;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

/**
 * [SoF][LEN][CMD.H][CMD.L][DATA....][CHK]=PACKET
 * 
 * @author fgminambre
 * 
 */
public class ZFrame {
	
	public final static int PAYLOAD_START_INDEX = 4;
	public final static int SoF = 0xFE;
	private int LEN = 0;
	protected DoubleByte CMD;
	private int CHK = 0;
	protected int[] packet;
	protected int[] Data;

	public ZFrame() {
		super();
	}

	public ZFrame(int[] pck) {
		if (pck.length >= pck[1] + 5) {
			this.packet = new int[pck[1] + 5];
			for (int i = 0; i < pck[1] + 5; i++)
				this.packet[i] = pck[i];
			buildData();
		}
	}

	public ZFrame(DoubleByte cmd, int[] frameData) {
		this.CMD = cmd;
		this.Data = frameData;
		this.LEN = frameData.length;
		buildPacket();
	}

	/**
	 * Builds a ZFrame: FE CMDMSB CMDLSB LENGTH FRAMEDATA CHSUM
	 * @param cmd
	 * @param frameData
	 */
	protected void buildPacket(DoubleByte cmd, int[] frameData) {
		this.CMD = cmd;
		this.Data = frameData;
		this.LEN = frameData.length;
		buildPacket();
	}

	public void buildData() {
		this.LEN = this.packet[1];
		this.CMD = new DoubleByte(this.packet[2], this.packet[3]);
		this.CHK = ZFrame.getCRC(this.packet);
		this.Data = new int[this.packet[1]];
		for (int i = PAYLOAD_START_INDEX, x = 0; x < this.packet[1]; ++i, ++x)
			this.Data[x] = this.packet[i];

	}

	public void buildPacket() {
		// packet size is start byte + len byte + 2 cmd bytes + data + checksum
		packet = new int[this.Data.length + 5];
		packet[0] = SoF;
		// Packet length does not include escape bytes
		packet[1] = this.LEN;
		// msb Cmd0 -> Type & Subsystem
		packet[2] = this.CMD.getMsb();
		// lsb Cmd1 -> ID
		packet[3] = this.CMD.getLsb();

		// data
		for (int i = 0; i < this.Data.length; i++) {
			if (this.Data[i] > 255) {
				throw new RuntimeException("Value is greater than one byte: " + this.Data[i]);
			}
			packet[PAYLOAD_START_INDEX + i] = this.Data[i];
		}
		// set last byte as checksum
		this.CHK = ZFrame.getCRC(packet);
		packet[packet.length - 1] = this.CHK;

	}

	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < packet.length; i++) {
			str += String.format("%02X", (byte) packet[i]).toUpperCase();
			if (i < packet.length - 1) {
				str += " ";
			}
		}
		return str;
	}

	public int getCHK() {
		return CHK;
	}

	public DoubleByte getMtCmdId() {
		return CMD;
	}

	public byte[] getBytePacket() {
		byte[] barray = new byte[this.packet.length];
		for (int i = 0; i < barray.length; i++) {

			barray[i] = (byte) this.packet[i];
		}
		return barray;
	}

	/**
	 * @return the packet
	 */
	public int[] getPacket() {
		return packet;
	}

	/**
	 * @return the data
	 */
	public int[] getData() {
		return Data;
	}

	/**
	 * Obtiene el byte CRC para la trama a enviar
	 * 
	 * @param bytes
	 *            la trama a enviar
	 * @return el byte a concatenar al final de la trama a envar
	 */
	public static byte getCRC(byte[] bytes) {
		int start = 1;
		byte checksum = (byte) 0x00;
		for (int i = start; i < bytes.length - 1; i++) {
			checksum = (byte) (bytes[i] ^ checksum);
		}
		return checksum;
	}

	/**
	 * Obtiene el byte CRC para la trama a enviar
	 * 
	 * @param data
	 *            la trama a enviar
	 * @return el byte a concatenar al final de la trama a envar
	 */
	public static int getCRC(int[] data) {
		int start = 1;
		int checksum = 0x00;
		for (int i = start; i < data.length - 1; i++) {
			checksum = (data[i] ^ checksum);
		}
		return checksum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + CHK;
		result = prime * result + ((CMD == null) ? 0 : CMD.hashCode());
		result = prime * result + Arrays.hashCode(Data);
		result = prime * result + LEN;
		result = prime * result + Arrays.hashCode(packet);
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
		ZFrame other = (ZFrame) obj;
		if (CHK != other.CHK)
			return false;
		if (CMD == null) {
			if (other.CMD != null)
				return false;
		} else if (!CMD.equals(other.CMD))
			return false;
		if (!Arrays.equals(Data, other.Data))
			return false;
		if (LEN != other.LEN)
			return false;
		if (!Arrays.equals(packet, other.packet))
			return false;
		return true;
	}

}
