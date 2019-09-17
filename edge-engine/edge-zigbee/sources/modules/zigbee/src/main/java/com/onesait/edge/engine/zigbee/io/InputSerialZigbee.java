package com.onesait.edge.engine.zigbee.io;

import java.io.InputStream;
import java.nio.IntBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.model.ZclCoordinator;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.util.ZFrameManagerThread;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;


public class InputSerialZigbee implements SerialPortEventListener{

	private static final int OFFSET_ZFRAME = 5;
	private static final Logger LOG = LoggerFactory.getLogger(InputSerialZigbee.class);
	
	private InputStream in;
	private IntBuffer buffer = IntBuffer.allocate(256 + 5);
	private final Object bytesReceivedLock = new Object();
	private ZclCoordinator zclcoor;
	private MqttConnection mqttConnection;
	
	private Long bytesReceived = 0l;
	
	public InputSerialZigbee(InputStream in, ZclCoordinator zclcoor, MqttConnection mqttConnection) {
		if (in == null) throw new IllegalArgumentException("serial input null");
		this.in = in;
		this.zclcoor=zclcoor;
		this.mqttConnection=mqttConnection;
	}

	public synchronized void setIn(InputStream in) {
		if (in != null) {
			this.in = in;
		}
	}

	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		try {
			while (in.available() > 0) {
				for (int i = 0; i < in.available(); i++) {
					int val = in.read();
					if (buffer.position() == 0) {
						if (val == ZFrame.SoF)
							buffer.put(val);
					} else {
						buffer.put(val);
						if (buffer.position() > 2 && buffer.position() == buffer.get(1) + OFFSET_ZFRAME) {
							ZFrame zFrame = new ZFrame(buffer.array());
							buffer = IntBuffer.allocate(256 + 5);
							if(LOG.isDebugEnabled()) {
							LOG.debug(
									"[READPORT ZFrame: {} ]",OutputSerialZigbee.serializer(zFrame.getBytePacket()));
							}
							if (val == zFrame.getCHK()) {
								ZFrameManagerThread zfm = new ZFrameManagerThread(zFrame, this.zclcoor,
										this.mqttConnection);
								zfm.start();
								synchronized (bytesReceivedLock) {
									bytesReceived += zFrame.getPacket()[1] + 5;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("error: {}", e.getMessage());

		}
	}
		
	
	
	public long getAndResetBytesReceived() {
		long bytesReceived;
		synchronized (this.bytesReceivedLock) {
			bytesReceived = this.bytesReceived.longValue();
			this.bytesReceived = 0l;
		}
		return bytesReceived;
	}
}
