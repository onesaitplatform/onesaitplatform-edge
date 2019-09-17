package com.onesait.edge.engine.zigbee.model;

import java.nio.ByteBuffer;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class ZclParam {

	private String name;
	private ZclDatatype datatype;
	/**
	 * parameter value. Must be set before sending it in a command
	 */
	private Long value = null;
	private DoubleByte attId = null;
	
	public ZclParam(String name, ZclDatatype zcldatatype) {
		super();
		this.name = name;
		this.datatype = (ZclDatatype) zcldatatype.clone();
	}

	public ZclDatatype getDatatype() {
		return datatype;
	}

	@Override
	public String toString() {
		return "ZclParam [name=" + name + ", type=" + datatype.getName() + "]";
	}

	public Long getValue() {
		return value;
	}
	
	/**
	 * returns parameter value in a byte array. MSB is on 0 position
	 * @return byte[] parameter value. Size of parameter is the size of the array
	 */
	public byte[] getValueBytes() {
		byte[] result = new byte[this.datatype.getLength()];
		
		byte[] valueBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(this.value.longValue()).array();
		
		for (int i = 0; i < result.length; i++) {
			result[i] = valueBytes[valueBytes.length - result.length + i];
		}
		return result;
	}
	
	/**
	 * returns parameter value in a byte array. MSB is on last position
	 * @return byte[] parameter value. Size of parameter is the size of the array
	 */
	public byte[] getValueBytesReverse() {
		byte[] result = new byte[this.datatype.getLength()];
		
		byte[] valueBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(this.value.longValue()).array();
		
		for (int i = 0; i < result.length; i++) {
			result[result.length - 1 - i] = valueBytes[i];
		}
		return result;
	}

	public void setValue(Long value) {
		if (value != null) {
			this.value = value;
		} else {
			this.value = null;
		}
	}

	public DoubleByte getAttId() {
		return attId;
	}

	public void setAttId(DoubleByte attId) {
		this.attId = attId;
	}

	public String getName() {
		return name;
	}
}
