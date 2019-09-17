package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.exception.UnknownCoordinatorMacException;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZdoBindReq extends ZFrame implements ZdoZFrame {

	private DoubleByte dstAddr;
	
	public ZdoBindReq(DoubleByte nwkDst, OctaByte endDeviceIeee, int epSrc, 
			DoubleByte cluster, int addressingMode,	OctaByte coordinatorIeee, int epDst)
			throws UnknownCoordinatorMacException{
		this.setDstAddr(nwkDst);
		int[] framedata;
		if( addressingMode == 3 ){
			framedata = new int[23];
		}else{
			framedata = new int[16];
		}
		framedata[0] = nwkDst.getLsb();
		framedata[1] = nwkDst.getMsb();
		byte[] bytes = endDeviceIeee.getAddress();
		for (int i=0;i<8;i++){
			framedata[i+2]=bytes[7-i] & 0xFF;
		}
		framedata[10] = epSrc;
		framedata[11] = cluster.getLsb();
		framedata[12] = cluster.getMsb();
		framedata[13] = addressingMode;
		if (coordinatorIeee != null)
			bytes = coordinatorIeee.getAddress();
		else
			throw new UnknownCoordinatorMacException();
		if ( addressingMode == 3 ){
			for (int i=0;i<8;i++){
				framedata[i+14]=bytes[7-i] & 0xFF;
			}
			framedata[22] = epDst;
		}else{
			framedata[14]=bytes[7];
			framedata[15]=bytes[6];
		}

		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_BIND_REQ), framedata);
	}

	public DoubleByte getDstAddr() {
		return dstAddr;
	}

	public void setDstAddr(DoubleByte dstAddr) {
		this.dstAddr = dstAddr;
	}

	public class ADDRESS_MODE{
		private ADDRESS_MODE() {}
		public static final int ADDRESS_16_BIT = 2;
		public static final int ADDRESS_64_BIT = 3;
		public static final int ADDRESS_NOT_PRESENT = 0;
		public static final int BROADCAST = 15;
		public static final int GROUP_ADDRESS = 1;
	}

	
	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.dstAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.BindRequest;
	}
}
