package com.onesait.edge.engine.zigbee.util;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;

/**
 * Guarda un ZFrame para reenviarlo cada TIME_BTW_CHECKS_SEC segundos mientras
 * la peticion figure en el array de peticiones pendientes, y nunca mas de N_TX
 * veces. El mensaje que se reenvia es exactamente el mismo cada vez.
 * 
 * @author dyagues
 *
 */

public class ResponseChecker extends Thread {

	private OutputSerialZigbee serial;
	private ZFrame requestToRetry;
	private static final int TIME_BETWEEN_RETRIES_SEC = 7;
	private static final int NUMBER_OF_RETRIES = 3;
	private AfRequest afReq;
	private HashMap<AfRequest, AfDataRequest> afRequests;
	private static final Logger LOG = LoggerFactory.getLogger(ResponseChecker.class);

	public ResponseChecker(OutputSerialZigbee serial, ZFrame request, AfRequest afReq,
			HashMap<AfRequest, AfDataRequest> afRequests) {
		super();
		this.serial = serial;
		this.requestToRetry = request;
		this.afReq = afReq;
		this.afRequests = afRequests;
	}

	@Override
	public void run() {
		for (int i = 0; i < NUMBER_OF_RETRIES; i++) {
			waitMs(TIME_BETWEEN_RETRIES_SEC * 1000);
			synchronized (this.afRequests) {
				if (this.afRequests.get(this.afReq) == null) {
					break;
				}
			}
			this.serial.writeZFrame(requestToRetry);
		}
	}

	public ZFrame getRequest() {
		return requestToRetry;
	}

	public void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: " + e);
			Thread.currentThread().interrupt();
		}
	}
}
