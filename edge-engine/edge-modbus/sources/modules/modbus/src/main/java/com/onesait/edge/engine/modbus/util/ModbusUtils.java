package com.onesait.edge.engine.modbus.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.modbus.model.DataType;
import com.onesait.edge.engine.modbus.model.Device;
import com.onesait.edge.engine.modbus.model.Protocol;
import com.onesait.edge.engine.modbus.model.RegisterType;
import com.onesait.edge.engine.modbus.model.SerialPort;
import com.onesait.edge.engine.modbus.model.Signal;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadCoilsRequest;
import com.serotonin.modbus4j.msg.ReadCoilsResponse;
import com.serotonin.modbus4j.msg.ReadDiscreteInputsRequest;
import com.serotonin.modbus4j.msg.ReadDiscreteInputsResponse;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersResponse;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteCoilResponse;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;

public class ModbusUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModbusUtils.class);

	/*
	 * For each IP (device) only one connnection to share
	 */
	private static final HashMap<String, ModbusMaster> modbusMarterConnectionPool = new HashMap<>();
	private static final HashMap<String, SerialPort> modbusRTUSerialPorts = new HashMap<>();
	// private static final String SERIALPORT = "serial";

	private ModbusUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get ModbusMaster to connect to device. If the connection exist for some IP
	 * that already contains the Map, it returns it. If not, it create one new
	 * 
	 * @param device
	 *            to connect
	 * @return {@link com.serotonin.modbus4j.ModbusMaster ModbusMaster}
	 * @throws Exception
	 */
	public static final void connect(Device device) {

		String key = ModbusUtils.getKey(device);

		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);

		if (modbusMaster == null) {
			if (Protocol.TCP.toString().equals(device.getProtocolType())) {
				modbusMaster = ModbusUtils.tcpConnect(device);
				// LOGGER.info("IP:{}, port:{}", device.getIp(), device.getPort());
			} else if (Protocol.RTU.toString().equals(device.getProtocolType())) {
				modbusMaster = ModbusUtils.rtuConnect(device);
				// LOGGER.info("Serial Port:{}", device.getSerial());
			} else {
				LOGGER.warn("Undefined protocol in device.getProtocolType():{}", device.getId());
			}
		}
		modbusMarterConnectionPool.put(key, modbusMaster);
	}

	/**
	 * Connect to a tcp modbus device
	 * 
	 * @param device
	 * @return ModbusMaster with the connect
	 */
	private static final ModbusMaster tcpConnect(Device device) {

		ModbusFactory factory = new ModbusFactory();
		IpParameters params = new IpParameters();

		params.setHost(device.getIp());
		params.setPort(device.getPort());
		params.setEncapsulated(false);

		ModbusMaster modbusMaster = factory.createTcpMaster(params, true);
		modbusMaster.setTimeout(device.getMonitoringTimeoutSec() * 1000);
		modbusMaster.setRetries(0);

		return modbusMaster;
	}

	/**
	 * Connect to a rtu modbus device
	 * 
	 * @param device
	 * @return ModbusMaster with the connect
	 * @throws Exception
	 */
	private static final ModbusMaster rtuConnect(Device device) {

		ModbusMaster modbusMaster = null;

		Integer bauds = device.getBauds();

		if (bauds == null)
			bauds = Integer.valueOf(9800);

		if (device.getSerial() != null && device.getSerial().length() > 0) {
			SerialParameters params = new SerialParameters();
			params.setCommPortId(device.getSerial()); // serialPort
			params.setBaudRate(bauds); // bauds
			params.setDataBits(8);
			params.setStopBits(1);
			params.setParity(0);

			SerialPort serialPort = new SerialPort(device.getSerial(), bauds);

			ModbusFactory factory = new ModbusFactory();
			modbusMaster = factory.createRtuMaster(serialPort);
			modbusMaster.setTimeout(device.getMonitoringTimeoutSec() * 1000);
			modbusMaster.setRetries(0);

			modbusRTUSerialPorts.put(device.getSerial(), serialPort);
		}

		return modbusMaster;
	}

	/**
	 * Initialize the connection with the modbus device, only if the connection
	 * exits and is not initialized
	 * 
	 * @param device
	 *            Device which is connected and we are going to initialize
	 */
	public static final synchronized void initConnetion(Device device) {

		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);

		if (modbusMaster != null && !modbusMaster.isInitialized()) {
			try {
				modbusMaster.init();
				if (Protocol.RTU.toString().equals(device.getProtocolType())) {
					LOGGER.info("Connected: {}:{}", device.getId(), device.getSerial());
				} else {
					LOGGER.info("Connected: {}:{}:{}", device.getId(), device.getPort(), device.getIp());
				}
			} catch (ModbusInitException e) {
				// LOGGER.error("Device {}, {}",device.getId(),e.getMessage());
				device.setOnError(Boolean.TRUE);
				device.setDescError(e.getMessage());
			}
		}
	}

	public static final Boolean isConnected(Device device) {

		Boolean result = Boolean.FALSE;
		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);
		if (modbusMaster != null)
			result = modbusMaster.isConnected();

		return result;
	}

	public static final Boolean isConnectionInitialized(Device device) {

		Boolean result = Boolean.FALSE;

		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);
		if (modbusMaster != null && modbusMaster.isInitialized()) {
			result = Boolean.TRUE;
		}
		return result;
	}

	private static final ModbusMaster getMaster(@NotNull Device device) {

		String key = ModbusUtils.getKey(device);
		return ModbusUtils.modbusMarterConnectionPool.get(key);
	}

	private static final String getKey(@NotNull Device device) {
		String key = null;

		if (Protocol.TCP.toString().equals(device.getProtocolType())) {
			key = device.getIp();
		} else {
			key = (String) device.getSerial();
		}
		return key;
	}

	public static final Boolean writeRegister(@NotNull Device device, @NotNull Signal signal, Integer value) {

		Boolean result = Boolean.FALSE;

		try {

			if (RegisterType.CS.value().equals(signal.getRegisterType())) {

				Boolean value2set = Boolean.FALSE;

				value2set = (value.intValue() == 0) ? Boolean.FALSE : Boolean.TRUE;
				writeCoilRegister(device, signal.getRegister(), value2set);
				result = Boolean.TRUE;

			} else if (RegisterType.HR.value().equals(signal.getRegisterType())) {

				DataType dataType = DataType.fromValue(signal.getDataType());
				if (DataType.offset(dataType) == 0) {
					modbusWriteRegister(device, signal.getRegister(), value);
				} else {
					short[] commandValue = null;
					commandValue = NumberUtils.getShorts(dataType, value, signal.getBigEndian());
					modbusWriteRegisters(device, signal.getRegister(), commandValue);
				}
				result = Boolean.TRUE;

			} else {
				LOGGER.error("Not valid Registry Type for writing");
			}

		} catch (ModbusTransportException e) {
			LOGGER.error("Not possible to write value {} in registry {} of type {}: {}", value, signal.getRegister(), signal.getRegisterType(), e.getMessage());
		}
		return result;
	}

	private static final synchronized void writeCoilRegister(Device device, int startRegister, boolean writeValue) throws ModbusTransportException {

		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);
		if (modbusMaster == null || !modbusMaster.isInitialized())
			return;

		WriteCoilRequest request = new WriteCoilRequest(device.getAddress(), startRegister, writeValue);
		WriteCoilResponse response = (WriteCoilResponse) modbusMaster.send(request);

		if (response.isException()) {
			LOGGER.error("Exception response: message = {}", response.getExceptionMessage());
		} else {
			LOGGER.info("Success changing register");
		}
	}

	private static final synchronized void modbusWriteRegister(Device device, int startRegister, int writeValue) throws ModbusTransportException {

		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);
		if (modbusMaster == null || !modbusMaster.isInitialized())
			return;

		WriteRegisterRequest request = new WriteRegisterRequest(device.getAddress(), startRegister, writeValue);
		WriteRegisterResponse response = (WriteRegisterResponse) modbusMaster.send(request);

		if (response.isException()) {
			LOGGER.error("Exception response: message = {}", response.getExceptionMessage());
		} else {
			LOGGER.info("Success changing register");
		}
	}

	private static final synchronized void modbusWriteRegisters(Device device, int startRegister, short[] writeValue) throws ModbusTransportException {

		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);
		if (modbusMaster == null || !modbusMaster.isInitialized())
			return;

		WriteRegistersRequest request = new WriteRegistersRequest(device.getAddress(), startRegister, writeValue);
		WriteRegisterResponse response = (WriteRegisterResponse) modbusMaster.send(request);

		if (response.isException()) {
			LOGGER.error("Exception response: message = {}", response.getExceptionMessage());
		} else {
			LOGGER.info("Success changing register");
		}
	}

	/**
	 * Close connection with the device and destroy it. Also removes the connection
	 * from map.
	 * 
	 * @param device
	 */
	public static final synchronized void closeConnectionModbus(@NotNull Device device) {
		LOGGER.info("Closing connection for {}", device.getId());
		if (Protocol.RTU.toString().equals(device.getProtocolType())) {
			ModbusUtils.closeConnectionRtuModbus((String) device.getSerial());
		} else {
			ModbusUtils.closeConnectionTCPModbus(device.getIp());
		}
	}

	private static final synchronized void closeConnectionTCPModbus(@NotNull String key) {
		ModbusMaster modbusMaster = ModbusUtils.modbusMarterConnectionPool.get(key);
		if (modbusMaster != null && modbusMaster.isInitialized()) {
			modbusMaster.destroy();
		}
		ModbusUtils.modbusMarterConnectionPool.remove(key);
	}

	private static final synchronized void closeConnectionRtuModbus(@NotNull String key) {
		SerialPort port = ModbusUtils.modbusRTUSerialPorts.get(key);
		if (port != null)
			port.freePort();
		ModbusUtils.modbusRTUSerialPorts.remove(key);
	}

	/**
	 * Get formatted request from a device's connected register, from a concrete
	 * register type, index of start register and number of them.
	 * 
	 * @param device
	 *            where we want to read the register
	 * @param type
	 *            of the register
	 * @param startRegister
	 *            index
	 * @param count,
	 *            how many register
	 * @return modbusRequest formatted as short[]
	 * @throws ModbusTransportException
	 */
	public static final short[] readDataFromRegister(Device device, RegisterType type, int startRegister, int count) throws ModbusTransportException {

		ModbusMaster modbusMaster = ModbusUtils.getMaster(device);

		if (modbusMaster == null || !modbusMaster.isInitialized())
			return new short[0];

		if (RegisterType.CS.equals(type)) {
			return getDataFromModbusCS(modbusMaster, device, startRegister, count);
		} else if (RegisterType.IS.equals(type)) {
			return getDataFromModbusIS(modbusMaster, device, startRegister, count);
		} else if (RegisterType.HR.equals(type)) {
			return getDataFromModbusHR(modbusMaster, device, startRegister, count);
		} else if (RegisterType.IR.equals(type)) {
			return getDataFromModbusIR(modbusMaster, device, startRegister, count);
		} else {
			return new short[0];
		}
	}

	private static final short[] getDataFromModbusHR(ModbusMaster modbusMaster, Device device, int startRegister, int count) throws ModbusTransportException {
		ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(device.getAddress(), startRegister, count);
		ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) modbusMaster.send(request);
		if (response == null) {
			return null;
		}
		return response.getShortData();
	}

	private static final short[] getDataFromModbusCS(ModbusMaster modbusMaster, Device device, int startRegister, int count) throws ModbusTransportException {
		ReadCoilsRequest request = new ReadCoilsRequest(device.getAddress(), startRegister, count);
		ReadCoilsResponse response = (ReadCoilsResponse) modbusMaster.send(request);
		if (response == null) {
			return null;
		}
		return toShortArray(response.getBooleanData());
	}

	private static final short[] getDataFromModbusIS(ModbusMaster modbusMaster, Device device, int startRegister, int count) throws ModbusTransportException {
		ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(device.getAddress(), startRegister, count);
		ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) modbusMaster.send(request);
		if (response == null) {
			return null;
		}
		return toShortArray(response.getBooleanData());
	}

	private static final short[] getDataFromModbusIR(ModbusMaster modbusMaster, Device device, int startRegister, int count) throws ModbusTransportException {
		ReadInputRegistersRequest request = new ReadInputRegistersRequest(device.getAddress(), startRegister, count);
		ReadInputRegistersResponse response = (ReadInputRegistersResponse) modbusMaster.send(request);
		if (response == null) {
			return null;
		}
		return response.getShortData();
	}

	private static final short[] toShortArray(boolean[] arrayBooleanInput) {
		short[] newArray = new short[arrayBooleanInput.length];

		for (int i = 0; i < arrayBooleanInput.length; i++)
			newArray[i] = (short) (arrayBooleanInput[i] ? 1 : 0);

		return newArray;
	}

	private static final void closeAllMasteredStillOpened() {
		int totalConnection = modbusMarterConnectionPool.size();
		LOGGER.info("Number of total Connections still open: {}", totalConnection);

		for (ModbusMaster master : modbusMarterConnectionPool.values()) {
			master.destroy();
			LOGGER.info("Connection closed");
		}
		modbusMarterConnectionPool.clear();
	}

	private static final void closeAllSerialPortStillOpened() {

		int totalSerialPortConnections = modbusMarterConnectionPool.size();
		LOGGER.info("Number of total Serial port still open: {}", totalSerialPortConnections);

		for (SerialPort port : ModbusUtils.modbusRTUSerialPorts.values()) {
			if (port != null) {
				port.freePort();
				LOGGER.info("Serial Port: {} free", port.getCommPortId());
			}
		}
		modbusMarterConnectionPool.clear();
	}

	public static final void closeAllOpennedConnections() {

		LOGGER.info("Closing Conections...");
		ModbusUtils.closeAllMasteredStillOpened();

		LOGGER.info("Closing Serial Ports...");
		ModbusUtils.closeAllSerialPortStillOpened();
	}

	public static final String getTimeSystemTimeZone(String fecha) {

		String resp = "";
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date d = formatter.parse(fecha);

			DateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			formatter2.setTimeZone(TimeZone.getDefault());

			resp = formatter2.format(d.getTime());
		} catch (Exception e) {
			LOGGER.error("Error parsin date.");
			e.printStackTrace();
		}
		return resp;
	}
}
