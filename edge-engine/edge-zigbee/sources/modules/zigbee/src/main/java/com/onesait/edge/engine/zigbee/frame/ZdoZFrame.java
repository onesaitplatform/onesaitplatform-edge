package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;

public interface ZdoZFrame {

	public DoubleByte getZdoNwkAddr();
	public DoubleByte getZdoCmdId();
}
