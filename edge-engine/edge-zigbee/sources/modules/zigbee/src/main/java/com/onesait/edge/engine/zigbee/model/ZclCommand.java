package com.onesait.edge.engine.zigbee.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ZclCommand {

	private Byte id;
	private String name;
	private List<ZclParam> params = new ArrayList<>();	
	private Boolean send = Boolean.FALSE;
	private Boolean server = Boolean.FALSE;
	
	public ZclCommand(Byte id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Byte getId() {
		return id;
	}
	public void setId(Byte id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void putParam(ZclParam param){
		this.params.add(param);
	}

	public List<ZclParam> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return "ZclCommand [id=" + id + ", name=" + name + "]";
	}		
	
	/**
	 * builds a specific cluster command zcl payload.
	 * @param paramValues array of longs. Each long corresponds with a parameter value.
	 * @return byte[] zcl payload
	 */
	public byte[] buildPayload(long[] paramValues) {
		if (paramValues == null)
			paramValues = new long[0];
		if (paramValues.length != this.params.size()) {
			return null;
		}
		int length = 0;
		for (ZclParam zclParam : params) {
			length += zclParam.getDatatype().getLength();
		}
		byte[] bytes = new byte[length];
		int bytesIdx = 0;
		for (int i = 0; i < this.params.size(); i++) {
			ZclParam param = this.params.get(i);
			param.setValue(paramValues[i]);
			byte[] valueBytes = param.getValueBytes();
			for (int j = 0; j < valueBytes.length; j++) {
				bytes[bytesIdx + j] = valueBytes[valueBytes.length - 1 - j];
			}
			bytesIdx += valueBytes.length;
		}
		return bytes;
	}
	
	/**
	 * builds a specific cluster command zcl payload.
	 * Parameters must be set to default in xml file.
	 * @return byte[] zcl payload
	 */
	public byte[] buildPayload() {
		int packetSize = 0;
		for (ZclParam zclParam : params) {
			packetSize += zclParam.getDatatype().getLength();
		}
		ByteBuffer zclByteBuffer = ByteBuffer.allocate(packetSize);
		for (ZclParam param : this.params) {
			if (param.getValue() != null) {
				// TODO: Revisar si es reverse o no y en que casos
				byte[] value = param.getValueBytes();
				zclByteBuffer.put(value);
			}
		}
		return zclByteBuffer.array();
	}

	public Boolean isSend() {
		return send;
	}

	public void setSend(Boolean send) {
		this.send = send;
	}

	public Boolean isServer() {
		return server;
	}

	public void setServer(Boolean server) {
		this.server = server;
	}
	
	public int getZclPayloadLength() {
		int len = 0;
		for (ZclParam zclParam : params) {
			len += zclParam.getDatatype().getLength();
		}
		return len;
	}
}
