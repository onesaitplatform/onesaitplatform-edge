package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class SysOsalNvWrite extends ZFrame{
	
	public SysOsalNvWrite(DoubleByte id,int offset,int length, int []value){
		int []frame=new int[4+length];
		int i=0;
		frame[i++]=id.getMsb();
		frame[i++]=id.getLsb();
		frame[i++]=offset;
		frame[i++]=length;
		int pos=0;
		for (int j=i;j<frame.length;j++){
			frame[j]=value[pos++];
			
		}
		super.buildPacket(new DoubleByte(ZToolCMD.SYS_OSAL_NV_WRITE), frame);
		
		
	}
	

}
