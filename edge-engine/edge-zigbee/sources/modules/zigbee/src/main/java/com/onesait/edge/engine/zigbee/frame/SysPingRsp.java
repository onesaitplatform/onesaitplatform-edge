package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class SysPingRsp extends ZFrame {

    public SysPingRsp(int[] framedata) {
        super.buildPacket(new DoubleByte(ZToolCMD.SYS_RESET_RESPONSE), framedata);
    }
	
}
