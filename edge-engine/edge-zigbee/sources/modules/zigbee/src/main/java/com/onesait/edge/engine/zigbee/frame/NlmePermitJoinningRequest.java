package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class NlmePermitJoinningRequest extends ZFrame {
	
	public NlmePermitJoinningRequest(int duration) {
		int []frameData = {duration};
        super.buildPacket(new DoubleByte(ZToolCMD.NLME_PERMITJOINING_REQUEST), frameData);
	}
	
	
	
	

}
