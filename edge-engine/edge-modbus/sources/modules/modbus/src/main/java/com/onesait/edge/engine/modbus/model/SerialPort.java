package com.onesait.edge.engine.modbus.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.modbus.service.ModbusService;
import com.serotonin.modbus4j.serial.SerialPortWrapper;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;

public class SerialPort implements SerialPortWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(SerialPort.class);

	private gnu.io.SerialPort serialPortRxtx;
	private String commPortId; // serialPort
	private int baudRate; // bauds
	private int flowControlIn;
	private int flowControlOut;
	private int dataBits;
	private int stopBits;
	private int parity;

	public SerialPort(@NotNull String serialPortName, int baudRate) {
		// by Default serialPortName, bauds, 0, 0, 8, 1, 0
		this.commPortId = serialPortName;
		this.baudRate = baudRate;
		this.flowControlIn = 0;
		this.flowControlOut = 0;
		this.dataBits = 8;
		this.stopBits = 1;
		this.parity = 0;
	}

	public void freePort() {
		if (serialPortRxtx != null) {
	        try {
	            // close the i/o streams.
	        	this.serialPortRxtx.getOutputStream().close();
	            this.serialPortRxtx.getInputStream().close();
		        this.close();
	        } catch (IOException ex) {
	        	LOGGER.error(ex.getMessage());
	        } catch(Exception e) {
	        	LOGGER.error(e.getMessage());
	        }
	    }
	}
	
	@Override
	public void close() throws Exception {
		if (this.serialPortRxtx != null && ModbusService.isLoadedRxtx()) {
			serialPortRxtx.close();
		} else {
			LOGGER.error("There is no port {} to close",this.commPortId);
		}
	}

	@Override
	public int getBaudRate() {
		return this.getBaudRate();
	}

	@Override
	public int getDataBits() {
		return this.dataBits;
	}

	@Override
	public int getFlowControlIn() {
		return this.flowControlIn;
	}

	@Override
	public int getFlowControlOut() {
		return this.flowControlOut;
	}

	@Override
	public InputStream getInputStream() {
		try {
			return this.serialPortRxtx.getInputStream();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}

	@Override
	public OutputStream getOutputStream() {
		try {
			return this.serialPortRxtx.getOutputStream();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}

	@Override
	public int getParity() {
		return this.parity;
	}

	@Override
	public int getStopBits() {
		return this.stopBits;
	}
	
	public String getCommPortId() {
		return commPortId;
	}

	@Override
	public void open() throws Exception {

		listSerialPorts();

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.commPortId);

		if (portIdentifier != null) {
			
			CommPort commPort = null;
			if (!portIdentifier.isCurrentlyOwned()) {
				commPort = portIdentifier.open(this.getClass().getName(), 2000);
			} else {
				commPort = this.serialPortRxtx;
			}

			if (commPort instanceof gnu.io.SerialPort) {
				this.serialPortRxtx = (gnu.io.SerialPort) commPort;
				this.serialPortRxtx.setSerialPortParams(this.baudRate, this.dataBits, this.stopBits, this.parity);
				this.serialPortRxtx.notifyOnDataAvailable(true);
			} else {
				LOGGER.error("Instance of com Port is not gnu.io.SerialPort");
			}
		}

	}

	public static List<String> listSerialPorts() {
		List<String> portList = new ArrayList<>();
		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements()) {
			CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
//			LOGGER.info("Available port: {}", port.getName());
			portList.add(port.getName());
		}
		return portList;
		
	}
	
	public static List<Integer> listBaudrates() {
		return new ArrayList<>(Arrays.asList(50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200));
	}
		
}
