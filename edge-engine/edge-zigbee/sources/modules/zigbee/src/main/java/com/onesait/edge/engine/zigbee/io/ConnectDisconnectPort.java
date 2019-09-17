package com.onesait.edge.engine.zigbee.io;

import java.io.BufferedInputStream;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class ConnectDisconnectPort {
	private static final Logger LOG = LoggerFactory.getLogger(ConnectDisconnectPort.class);
	private String zigbeeporttype;
	private String zigbeeport;
	private String baudrate;
	private String portdatabits;
	private String stopbits;
	@SuppressWarnings("unused")
	private String flowcontrol;
	private String parity;
	private SerialPort serialPort = null;
	private InputSerialZigbee inputSerial;
	private OutputSerialZigbee outputSerial;
	

	public ConnectDisconnectPort(String zigbeeporttype, String zigbeeport, String baudrate, String portdatabits, String stopbits, String flowcontrol, String parity) {
		this.flowcontrol=flowcontrol;
		this.zigbeeporttype=zigbeeporttype;
		this.zigbeeport=zigbeeport;
		this.baudrate=baudrate;
		this.portdatabits=portdatabits;
		this.stopbits=stopbits;
		this.parity=parity;
		connectPort();

	}
public void connectPort() {
	if (zigbeeport != null) {
		try {
			Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
			while (ports.hasMoreElements()) {
				CommPortIdentifier port = ports.nextElement();
				LOG.info("Available port:{} ",port.getName());
			}
			// the CommPort object can be casted to a SerialPort object
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(zigbeeport);
			// añadir excepcion por si no vale el puerto indicado
			CommPort commPort = null;
			if (!portIdentifier.isCurrentlyOwned()) {
				commPort = portIdentifier.open(this.getClass().getName(), 2000);
			} else {
				commPort = this.serialPort;
			}
			if (commPort instanceof SerialPort) {
				this.serialPort = (SerialPort) commPort;
				this.loadAndSetSerialPortParams();
				this.serialPort.notifyOnDataAvailable(true);
				LOG.info("Zigbee connected to {} port", zigbeeport);
			} else {
				LOG.error("******* Error: ONLY SERIAL PORTS ARE HANDLED *******");
			}
		} catch (PortInUseException e) {
			LOG.error("Port in use. There is no access to port");
		} catch (NumberFormatException e) {
			LOG.error("UART configuration error: Incorrect values");
		} catch (gnu.io.NoSuchPortException e) {
			LOG.error("### PUERTO ZIGBEE NO ENCONTRADO ###");
			LOG.info("Declared zigbeePort: {}",zigbeeport);
		} catch (gnu.io.UnsupportedCommOperationException e) {
			LOG.info("Cannot set UART parameters to serial port: {}",zigbeeport);
		} catch (Exception e) {
			LOG.error("Exception: {}",e.getLocalizedMessage());
		}
	}
		
	}
public boolean connectSystem(){
	if (zigbeeport != null) {
		try {
			// the CommPort object can be casted to a SerialPort object
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(zigbeeport);
			CommPort commPort = null;
			if (!portIdentifier.isCurrentlyOwned()) {
				commPort = portIdentifier.open(this.getClass().getName(), 2000);
			} else {
				commPort = this.serialPort;
			}
			if (commPort instanceof SerialPort) {
				this.serialPort = (SerialPort) commPort;
				this.loadAndSetSerialPortParams();
				this.serialPort.notifyOnDataAvailable(true);
				LOG.info("Zigbee connected to {} port",zigbeeport); 
				if (this.outputSerial != null) {
					this.outputSerial.setOut(serialPort.getOutputStream());
				}
				if (this.inputSerial != null) {
					this.inputSerial.setIn(new BufferedInputStream(serialPort.getInputStream()));
					this.serialPort.addEventListener(this.inputSerial);
				}
				return true;
			} else {
				LOG.error("******* Error: ONLY SERIAL PORTS ARE HANDLED *******");
			}
		} catch (PortInUseException e) {
			LOG.error("Port in use. There is no access to port");
		} catch (NumberFormatException e) {
			LOG.error("UART configuration error: Incorrect values");
			LOG.error("UART configuration set to default");
		} catch (gnu.io.NoSuchPortException e) {
			LOG.error("### PUERTO ZIGBEE NO ENCONTRADO ###");
			LOG.info("Declared zigbeePort: {} ",zigbeeport);
		} catch (gnu.io.UnsupportedCommOperationException e) {
			LOG.info("Cannot set UART parameters to serial port: {}",zigbeeport);
		} catch (Exception e) {
			LOG.error("Exception: {}",e.getLocalizedMessage());
		}
	} else {
		LOG.error("There is no port to open");
	}
	return false;
}
private void loadAndSetSerialPortParams() throws UnsupportedCommOperationException {	
	if (!zigbeeporttype.equalsIgnoreCase("USB")) {
		if (baudrate == null || parity == null || stopbits == null || portdatabits == null) {
			throw new NumberFormatException();
		}
		int uartbaudrate = Integer.parseInt(baudrate.trim());
		int uartparity = Integer.parseInt(parity.trim());
		int uartstopbits = Integer.parseInt(stopbits.trim());
		int uartdatabits = Integer.parseInt(portdatabits.trim());
		if ((uartbaudrate != 9600 && uartbaudrate != 19200 && uartbaudrate != 38400 && uartbaudrate != 57600 && uartbaudrate != 115200) || (uartparity != SerialPort.PARITY_NONE && uartparity != SerialPort.PARITY_EVEN && uartparity != SerialPort.PARITY_ODD)
				|| (uartstopbits != SerialPort.STOPBITS_1 && uartstopbits != SerialPort.STOPBITS_2 && uartstopbits != SerialPort.STOPBITS_1_5) || (uartdatabits != SerialPort.DATABITS_5 && uartdatabits != SerialPort.DATABITS_6 && uartdatabits != SerialPort.DATABITS_7 && uartdatabits != SerialPort.DATABITS_8))
			throw new NumberFormatException();

		LOG.debug("SerialPort Baudrate: {}",baudrate);
		LOG.debug("SerialPort Databits: {}",uartdatabits);
		LOG.debug("SerialPort Parity: {}",parity);
		LOG.debug("SerialPort StopBits: {}",stopbits);
		
		this.serialPort.setSerialPortParams(uartbaudrate, uartdatabits, uartstopbits, uartparity);
	}
}
public SerialPort getSerialPort() {
	return serialPort;
}
public void setSerialPort(SerialPort serialPort) {
	this.serialPort = serialPort;
}
public void setInputSerial(InputSerialZigbee inputSerial) {
	this.inputSerial = inputSerial;
}
public void setOutputSerial(OutputSerialZigbee outputSerial) {
	this.outputSerial = outputSerial;
}
public boolean disconnect() {
	if(this.serialPort!=null){
		serialPort.close();
		return true;
	}else{
		return false;
	}
	
}

}
