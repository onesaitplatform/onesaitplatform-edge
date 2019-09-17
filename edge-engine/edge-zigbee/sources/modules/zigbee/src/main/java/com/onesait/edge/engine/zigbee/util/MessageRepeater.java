package com.onesait.edge.engine.zigbee.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;

public class MessageRepeater extends Thread{
	
	private ZFrame frame;
	private int numberOfSendings;
	private int timeBetweenSendings;
	private OutputSerialZigbee serial;
	private static final Logger LOG = LoggerFactory.getLogger(MessageRepeater.class);
	
	public MessageRepeater(ZFrame frame, int numberOfSendings, int timeBetweenSendings,
			OutputSerialZigbee serial) {
		super();
		this.frame = frame;
		this.numberOfSendings = numberOfSendings;
		this.timeBetweenSendings = timeBetweenSendings;
		this.serial = serial;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < this.numberOfSendings; i++) {
			this.serial.writeZFrame(frame);
			waitMs(timeBetweenSendings);
		}
	}
	private void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: " + e);
			Thread.currentThread().interrupt();
		}
	}

}
