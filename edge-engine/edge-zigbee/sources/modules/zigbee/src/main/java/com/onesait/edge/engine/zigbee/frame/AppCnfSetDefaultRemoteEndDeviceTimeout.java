package com.onesait.edge.engine.zigbee.frame;


import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class AppCnfSetDefaultRemoteEndDeviceTimeout extends ZFrame  {


/** 	0x00 -> 10s
 *	 	0x01 -> 2 min
 *		0x02 -> 4 min
 *		0x03 -> 8 min
 *		0x04 -> 16 min
 *		0x05 -> 32 min
 *		0x06 -> 64 min
 *		0x07 -> 128 min
 *		0x08 -> 256 min
 *		0x09 -> 512 min
 *		0x0A -> 1024 min
 *		0x0B -> 2048 min
 *		0x0C -> 4096 min
 *		0x0D -> 8192 min
 *		0x0E -> 16384 min
 */

	private int duracion;
	

	public AppCnfSetDefaultRemoteEndDeviceTimeout(int duracion){
		this.duracion=duracion;
		int []frameData = {this.duracion};
		super.buildPacket(new DoubleByte(ZToolCMD.APP_CNF_SET_DEFAULT_REMOTE_ENDDEVICE_TIMEOUT),frameData);
		
		}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + duracion;
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
		AppCnfSetDefaultRemoteEndDeviceTimeout other = (AppCnfSetDefaultRemoteEndDeviceTimeout) obj;
		if (duracion != other.duracion)
			return false;
		return true;
	}
}
