package com.onesait.edge.engine.zigbee.monitoring;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.SysSetTime;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;


public class SetChipClockTask extends TimerTask{
	public static final long PERIOD_MS = 1000 * 60 * 24 * 60l;
//	public static final long PERIOD_MS = 1000 * 4 * 60l;
	public static final long DELAY_MS = 0;
	private ZclCluster cluster;
	private static final DoubleByte TIME_ATTRIBUTE_ID = new DoubleByte(0);
	private static final DoubleByte TIME_ZONE_ATTRIBUTE_ID = new DoubleByte(2);
	private static final DoubleByte LOCAL_TIME_ATTRIBUTE_ID = new DoubleByte(7);
	private static final Logger LOG = LoggerFactory.getLogger(SetChipClockTask.class);
	private OutputSerialZigbee serial;


	
	public SetChipClockTask (OutputSerialZigbee serial,ZclCluster cluster){
		this.cluster=cluster;
		this.serial=serial;
	}
	@Override
	public void run() {
		try{
		ZFrame frame;
		Calendar calendar = Calendar.getInstance();
		long secsSinceEpoch = calendar.getTime().getTime() / 1000;
		long secsSince20000101 = secsSinceEpoch - ZigbeeConstants.TIME_STANDARD_ORIGIN_EPOCH_SEC;
		ZFrame zf = this.cluster.buildWriteAttributes(TIME_ATTRIBUTE_ID, Long.toString(secsSince20000101));
		this.serial.writeZFrame(zf);
		zf = this.cluster.buildWriteAttributes(LOCAL_TIME_ATTRIBUTE_ID, Long.toString(secsSince20000101));
		this.serial.writeZFrame(zf);
		TimeZone tz = Calendar.getInstance().getTimeZone();
		long timeZoneSec = tz.getOffset(calendar.getTime().getTime())/1000;
		zf = this.cluster.buildWriteAttributes(TIME_ZONE_ATTRIBUTE_ID, Long.toString(timeZoneSec));
		this.serial.writeZFrame(zf);
		//setear la hora del chip 
		frame = new SysSetTime((int) (secsSince20000101));
		this.serial.writeZFrame(frame);
		}catch(Exception e){
			LOG.error("Error setting the chip clock and time attributes cluster server");
			
		}

	}
}
	
	

