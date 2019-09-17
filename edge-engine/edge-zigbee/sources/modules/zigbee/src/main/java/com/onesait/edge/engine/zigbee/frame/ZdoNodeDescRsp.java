package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

/*
Copyright 2008-2013 ITACA-TSB, http://www.tsb.upv.es/
Instituto Tecnologico de Aplicaciones de Comunicacion 
Avanzadas - Grupo Tecnologias para la Salud y el 
Bienestar (TSB)
See the NOTICE file distributed with this work for additional 
information regarding copyright ownership
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 * Processes the Node Descriptor response. Only a single Node Descriptor is
 * available for each node.
 * <p>
 * The node descriptor contains information about the capabilities of the ZigBee
 * node and is mandatory for each node. There shall be only one node descriptor
 * in a node.
 * 
 * @author <a href="mailto:alfiva@aaa.upv.es">Alvaro Fides Valero</a>
 * @author <a href="mailto:chris@cd-jackson.com">Chris Jackson</a>
 */
public class ZdoNodeDescRsp extends ZFrame implements InputZdoZFrame {
	/**
	 * Node Flags assigned for APS. For V1.0 all bits are reserved
	 */
	private int apsFlags;
	/**
	 * Indicates size of maximum NPDU. This field is used as a high level indication
	 * for api
	 */
	private int bufferSize;
	private int capabilities;
	private boolean complexDescriptorAvailable;
	/**
	 * Specifies a manufacturer code that is allocated by ZigBee Alliance, relating
	 * to the manufacturer to the device
	 */
	private DoubleByte manufacturerCode;
	private int nodeType;
	private DoubleByte nwkAddr;
	/**
	 * Specifies the system server capability
	 */
	private int serverMask;
	private DoubleByte srcAddress;
	/**
	 * this field indicates either SUCCESS or FAILURE.
	 */
	private int status;
	/**
	 * Indicates maximum size of Transfer up to 0x7fff (This field is reserved in
	 * version 1.0 and shall be set to zero ).
	 */
	private DoubleByte transferSize;
	/**
	 * Indicates if user descriptor is available for the node
	 */
	private boolean userDescriptorAvailable;
	private int freqBand;

	/**
	 * Constructor
	 */
	public ZdoNodeDescRsp() {
	}

	public ZdoNodeDescRsp(int[] framedata) {
		this.srcAddress = new DoubleByte(framedata[1], framedata[0]);
		this.status = framedata[2];
		if (this.status == ZClusterLibrary.ZCL_STATUS_SUCCESS) {
			this.nwkAddr = new DoubleByte(framedata[4], framedata[3]);
			this.nodeType = framedata[5] & (0x07);/// Experimental
			// this.ComplexDescriptorAvailable = ((framedata[5] & (0x08)) >>> 3) == 1;
			// this.UserDescriptorAvailable = ((framedata[5] & (16)) >>> 4) == 1;
			// this.APSFlags = framedata[6] & (0x0F);
			// this.FreqBand = framedata[6] & (0xF0) >>> 4;
			this.capabilities = framedata[7];
			this.manufacturerCode = new DoubleByte(framedata[9], framedata[8]);
			// this.BufferSize = framedata[13];
			// this.TransferSize = new DoubleByte(framedata[15], framedata[14]);
			// this.ServerMask = new DoubleByte(framedata[17],
			// framedata[16]).get16BitValue();
			super.buildPacket(new DoubleByte(ZToolCMD.ZDO_NODE_DESC_RSP), framedata);
		}
	}

	/**
	 * Capability Information bitfield
	 */
	public class CAPABILITY_INFO {
		/**
		 * Capability Information bitfield
		 */
		public static final int ALLOCATE_ADDRESS = 0x80;
		/**
		 * Capability Information bitfield
		 */
		public static final int ALTER_PAN_COORD = 1;
		/**
		 * Capability Information bitfield
		 */
		public static final int DEVICE_TYPE = 2;
		/**
		 * Capability Information bitfield
		 */
		public static final int NONE = 0;
		/**
		 * Capability Information bitfield
		 */
		public static final int POWER_SOURCE = 4;
		/**
		 * Capability Information bitfield
		 */
		public static final int RECEIVER_ON_WHEN_IDLE = 8;
		/**
		 * Capability Information bitfield</summary>
		 */
		public static final int SECURITY_CAPABILITY = 0x40;
	}

	public class NODE_TYPE {

		public static final int COORDINATOR = 0;

		public static final int END_DEVICE = 2;

		public static final int ROUTER = 1;
	}

	public class SERVER_CAPABILITY {

		public static final int BACKUP_TRUST_CENTER = 2;

		public static final int BAK_BIND_TABLE_CACHE = 8;

		public static final int BAK_DISC_CACHE = 50;

		public static final int NONE = 0;

		public static final int PRIM_BIND_TABLE_CACHE = 4;

		public static final int PRIM_DISC_CACHE = 0x16;

		public static final int PRIM_TRUST_CENTER = 1;
	}

	@Override
	public String toString() {
		return "ZDO_NODE_DESC_RSP{" + "APSFlags=" + apsFlags + ", BufferSize=" + bufferSize + ", Capabilities="
				+ String.format("0x%02X", capabilities) + ", ComplexDescriptorAvailable=" + complexDescriptorAvailable
				+ ", ManufacturerCode=" + manufacturerCode + ", NodeType=" + nodeType + ", nwkAddr=" + nwkAddr
				+ ", ServerMask=" + String.format("0x%02X", serverMask) + ", SrcAddress=" + srcAddress + ", Status="
				+ status + ", TransferSize=" + transferSize + ", UserDescriptorAvailable=" + userDescriptorAvailable
				+ ", FreqBand=" + freqBand + '}';
	}

	public int getApsFlags() {
		return apsFlags;
	}

	public void setApsFlags(int apsFlags) {
		this.apsFlags = apsFlags;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(int capabilities) {
		this.capabilities = capabilities;
	}

	public boolean isComplexDescriptorAvailable() {
		return complexDescriptorAvailable;
	}

	public void setComplexDescriptorAvailable(boolean complexDescriptorAvailable) {
		this.complexDescriptorAvailable = complexDescriptorAvailable;
	}

	public DoubleByte getManufacturerCode() {
		return manufacturerCode;
	}

	public void setManufacturerCode(DoubleByte manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	public int getServerMask() {
		return serverMask;
	}

	public void setServerMask(int serverMask) {
		this.serverMask = serverMask;
	}

	public DoubleByte getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(DoubleByte srcAddress) {
		this.srcAddress = srcAddress;
	}

	public DoubleByte getTransferSize() {
		return transferSize;
	}

	public void setTransferSize(DoubleByte transferSize) {
		this.transferSize = transferSize;
	}

	public boolean isUserDescriptorAvailable() {
		return userDescriptorAvailable;
	}

	public void setUserDescriptorAvailable(boolean userDescriptorAvailable) {
		this.userDescriptorAvailable = userDescriptorAvailable;
	}

	public int getFreqBand() {
		return freqBand;
	}

	public void setFreqBand(int freqBand) {
		this.freqBand = freqBand;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.srcAddress;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.NodeDescriptorResponse;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
}
