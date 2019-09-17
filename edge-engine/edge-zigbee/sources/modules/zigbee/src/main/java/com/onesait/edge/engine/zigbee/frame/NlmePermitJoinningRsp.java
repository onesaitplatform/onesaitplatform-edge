package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class NlmePermitJoinningRsp extends ZFrame{

	
	public NlmePermitJoinningRsp(int status){
		int [] frame=new int[1];
		frame[0]=status;
		
		super.buildPacket(new DoubleByte(ZToolCMD.NLME_PERMITJOINING_RESPONSE), frame);
	}
	
	
}
