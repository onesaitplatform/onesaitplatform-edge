package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoIeeeAddrReq extends ZFrame implements ZdoZFrame {

	private DoubleByte nwkAddr;

	public short getShortAddress() {
		return (short) (packet[PAYLOAD_START_INDEX + 0] + (packet[PAYLOAD_START_INDEX + 1] << 8));
	}

	public REQ_TYPE getRequestType() {
		return REQ_TYPE.fromValue(packet[PAYLOAD_START_INDEX + 2]);
	}

	public int getStartIndex() {
		return super.packet[PAYLOAD_START_INDEX + 3];
	}

	public ZdoIeeeAddrReq(DoubleByte nwkAddress, int reqType1, int startIndex) {
		int[] framedata = new int[4];
		this.nwkAddr = nwkAddress;
		framedata[0] = nwkAddress.getLsb();
		framedata[1] = nwkAddress.getMsb();
		framedata[2] = reqType1;
		framedata[3] = startIndex;
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_IEEE_ADDR_REQ), framedata);
	}

	public ZdoIeeeAddrReq(DoubleByte nwkAddress, boolean recursive) {
		int[] framedata = new int[4];
		this.nwkAddr = nwkAddress;
		framedata[0] = nwkAddress.getLsb();
		framedata[1] = nwkAddress.getMsb();
		framedata[2] = recursive ? 0x01 : 0x00;
		framedata[3] = 0x00;
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_IEEE_ADDR_REQ), framedata);
	}

	public ZdoIeeeAddrReq(DoubleByte nwkAddress, REQ_TYPE reqtype, int startIndex) {
		int[] framedata = new int[4];
		framedata[0] = nwkAddress.getLsb();
		framedata[1] = nwkAddress.getMsb();
		framedata[2] = reqtype.value;
		framedata[3] = startIndex;
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_IEEE_ADDR_REQ), framedata);
	}

	/** Request type */
	public enum REQ_TYPE {
		EXTENDED(1), SINGLE_DEVICE_RESPONSE(0);

		private int value;

		private REQ_TYPE(int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}

		public static REQ_TYPE fromValue(int v) {
			REQ_TYPE[] values = REQ_TYPE.values();
			for (int i = 0; i < values.length; i++) {
				if (values[i].value == v)
					return values[i];
			}
			return null;
		}

	}

	public DoubleByte getNwkAddr() {
		return nwkAddr;
	}

	public void setNwkAddr(DoubleByte nwkAddr) {
		this.nwkAddr = nwkAddr;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.nwkAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.IEEEAddressRequest;
	}
}
