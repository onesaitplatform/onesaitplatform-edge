package com.onesait.edge.engine.zigbee.frame;


import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class AppConfBdbSetTcRequireKeyExchange extends ZFrame{

	private int bdbTrustCenterRequireKeyExchange;

	public AppConfBdbSetTcRequireKeyExchange(int requeireKeyExchange){
		bdbTrustCenterRequireKeyExchange=requeireKeyExchange;
		int  []frameData = {bdbTrustCenterRequireKeyExchange};
		super.buildPacket(new DoubleByte(ZToolCMD.APP_CNF_BDB_SET_TC_REQUIRE_KEY_EXCHANGE),frameData);
		
		}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + bdbTrustCenterRequireKeyExchange;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppConfBdbSetTcRequireKeyExchange other = (AppConfBdbSetTcRequireKeyExchange) obj;
		if (bdbTrustCenterRequireKeyExchange != other.bdbTrustCenterRequireKeyExchange)
			return false;
		return true;
	}
	
}
