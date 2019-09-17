package com.onesait.edge.engine.zigbee.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.TooManyListenersException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.onesait.edge.engine.zigbee.io.ConnectDisconnectPort;
import com.onesait.edge.engine.zigbee.io.InputSerialZigbee;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.model.ZclCoordinator;
import com.onesait.edge.engine.zigbee.util.FileUtils;

import gnu.io.SerialPort;

@Service
public class SerialZigbeeConnector  {

	// private static final String propertySerialPortNameZigbee =
	// "serial.port.name.zigbee.";
	// private static final String propertyUartBaudrate = "baudrate";
	// private static final String propertyUartParity = "parity";
	// private static final String propertyUartDatabits = "databits";
	// private static final String propertyUartStopbits = "stopbits";
	// private static final String propertyUartConfig = "uart.config.";
	// private static final String propertyTypeUSB_UART =
	// "serial.port.communication.zigbee";

	@Value("${zigbee.port.type}")
	private String zigbeeporttype;
	@Value("${zigbee.port.name}")
	private String zigbeeport;
	@Value("${zigbee.port.baudrate}")
	private String baudrate;
	@Value("${zigbee.port.databits}")
	private String portdatabits;
	@Value("${zigbee.port.stopbits}")
	private String stopbits;
	@Value("${zigbee.port.flowcontrol}")
	private String flowcontrol;
	@Value("${zigbee.port.parity}")
	private String parity;
	@Autowired
	ZclService zclService;
	@Autowired
	DeviceManager deviceManager;
	@Autowired
	MqttConnection mqttConnection;

	private static final Logger LOG = LoggerFactory.getLogger(SerialZigbeeConnector.class);
	private SerialPort serialPort = null;
	private InputSerialZigbee inputSerial;
	private OutputSerialZigbee outputSerial;
	private ConnectDisconnectPort cdPort;
	private ZclCoordinator zclcoor;

	@PostConstruct
	public synchronized void initMethod() {
		FileUtils.loadRxtxLib();
		this.cdPort = new ConnectDisconnectPort(zigbeeporttype, zigbeeport, baudrate, portdatabits, stopbits,
				flowcontrol, parity);
		this.serialPort = cdPort.getSerialPort();
		createOutputSerial();
		createZCoordinator();
		createInputSerial();
		cdPort.setInputSerial(inputSerial);
		cdPort.setOutputSerial(outputSerial);
		this.zclcoor.initCoor();
		checkZcoordIeeeReceived();

	}

	private void checkZcoordIeeeReceived() {
		for (int i = 0; i < 4; i++) {
			if (this.zclcoor.getIeeeAddress()== null) {
				LOG.error("Coordinator mac does not received yet. Attempt number: {}",i);
				this.zclcoor.initCoor();
				waitMs(2000);
			}else{
				LOG.info("Coordinator mac finally received");
				return;
			}
		}
		LOG.error("CRITICAL ERROR: could not be obtained the coordinator mac");		
	}

	private void createZCoordinator() {
		this.zclcoor = new ZclCoordinator(zclService, this.outputSerial, deviceManager, cdPort);

	}

	public InputSerialZigbee getInputSerial() {
		return inputSerial;
	}

	public OutputSerialZigbee getOutputSerial() {
		return outputSerial;
	}

	@PreDestroy
	public void disconnect() {
		if (cdPort != null) {
			if (!cdPort.disconnect()) {
				LOG.error("{}: There is no port to close",this.getClass().getName());
			}
		} else {
			LOG.error("{}: There is no port to close",this.getClass().getName());
		}
	}

	public OutputSerialZigbee createOutputSerial() {
		OutputSerialZigbee returnedOutput = null;
		try {
			if (this.serialPort != null) {
				this.outputSerial = new OutputSerialZigbee(serialPort.getOutputStream(), deviceManager);
			} else {
				this.outputSerial = new OutputSerialZigbee(null, null);
			}
			returnedOutput = this.outputSerial;
		} catch (Exception e) {
			LOG.error("Exception: {}",e.getLocalizedMessage());
		}
		return returnedOutput;
	}

	public InputSerialZigbee createInputSerial() {
		InputSerialZigbee returnedInput = null;
		try {
			if (serialPort != null) {
				this.inputSerial = new InputSerialZigbee(new BufferedInputStream(serialPort.getInputStream()),
						this.zclcoor, this.mqttConnection);
				this.serialPort.addEventListener(this.inputSerial);
			}
			returnedInput = this.inputSerial;
		} catch (IOException e) {
			LOG.error("Exception: {}",e.getLocalizedMessage());
		} catch (TooManyListenersException e) {
			LOG.error("Exception: SerialPortEventListener already associated.");
		}
		return returnedInput;
	}

	public ZclCoordinator getZclcoor() {
		return zclcoor;
	}
	public void waitMs(int ms)  {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: {}",e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public String toString() {
		return "SerialZigbeeConnector [ serialPort=" + serialPort + "]";
	}
}
