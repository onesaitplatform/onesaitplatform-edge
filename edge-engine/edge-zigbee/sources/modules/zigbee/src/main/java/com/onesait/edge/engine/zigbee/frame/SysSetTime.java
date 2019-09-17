package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.exception.InvalidTimeException;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class SysSetTime extends ZFrame {

	private int [] frameData=new int [11];

	public SysSetTime (int hour,int minute,int seconds,int month,int day, DoubleByte year) throws InvalidTimeException{
		frameData[0]=0x00;
		frameData[1]=0x00;
		frameData[2]=0x00;
		frameData[3]=0x00;
		if((hour>=0) && (hour<=23)){
			frameData[4]=hour;
		}else{
			throw new InvalidTimeException("Hour range no valid. Proper range: (0-23)");
		}
		if((minute>=0)&&(minute<=59)){
			frameData[5]=minute;	
		}else{
			throw new InvalidTimeException("Minutes range no valid. Proper range: (0-59)");
		}
		
		if((seconds>=0)&&(seconds<=59)){
			frameData[6]=seconds;	
		}else{
			throw new InvalidTimeException("Seconds range no valid. Proper range: (0-59)");
		}
		if((month>=0)&&(month<=12)){
			frameData[7]=month;	
		}else{
			throw new InvalidTimeException("Month range no valid. Proper range: (0-12)");
		}
		if((day>=0)&&(day<=31)){
			frameData[8]=day;	
		}else{
			throw new InvalidTimeException("Day range no valid. Proper range: (0-31)");
		}
		if(year.getMsb()>=7){
			if(year.getLsb()>=208){
				frameData[9]=year.getLsb();
				frameData[10]=year.getMsb();
			}else{
				throw new InvalidTimeException("Year range no valid. Proper range: (2000- )");
			}
		}else{
				throw new InvalidTimeException("Year range no valid. Proper range: (2000- )");
		}
		super.buildPacket(new DoubleByte(ZToolCMD.SYS_SET_TIME),frameData);
		
		
		}
	public SysSetTime(int utctime){
		frameData[3]=(utctime>>24) & 0x000000FF;
		frameData[2]=(utctime>>16) & 0x000000FF;
		frameData[1]=(utctime>>8) &  0x000000FF;
		frameData[0]=utctime & 0x000000FF;
		

		for (int i=4;i<frameData.length;i++){
			frameData[i]=0x00;
		}
		super.buildPacket(new DoubleByte(ZToolCMD.SYS_SET_TIME),frameData);
		
		
	}
}
