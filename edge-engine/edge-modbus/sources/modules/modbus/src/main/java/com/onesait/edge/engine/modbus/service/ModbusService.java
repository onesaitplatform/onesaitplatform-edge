package com.onesait.edge.engine.modbus.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesait.edge.engine.modbus.conf.GlobalConstants;
import com.onesait.edge.engine.modbus.model.Alert;
import com.onesait.edge.engine.modbus.model.AlertType;
import com.onesait.edge.engine.modbus.model.Command;
import com.onesait.edge.engine.modbus.model.Device;
import com.onesait.edge.engine.modbus.model.ModbusEnvironment;
import com.onesait.edge.engine.modbus.model.ModbusStatisticalInfo;
import com.onesait.edge.engine.modbus.model.SerialPort;
import com.onesait.edge.engine.modbus.model.Signal;
import com.onesait.edge.engine.modbus.model.State;
import com.onesait.edge.engine.modbus.util.FileUtils;
import com.onesait.edge.engine.modbus.util.ModbusUtils;

@Service
public class ModbusService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ModbusService.class);

	private List<String> serialPorts;

	@Value("${com.onesait.edge.engine.modbus.schema}")
	private String modbusSchemaUrl;
	@Value("${com.onesait.edge.engine.modbus.schema.process}")
	private String process;
	@Value("${com.onesait.edge.engine.modbus.schema.corethreadpoolsize}")
	private Integer coreThreadPoolSize;

	@Value("${com.onesait.edge.engine.modbus.version}")
	private String version;

	private List<Alert> alertList = new ArrayList<>();

	private ModbusEnvironment modbusEnvironment;
	private File file;
	private static Boolean isLoadedRxtx = Boolean.FALSE;

	public static Boolean isLoadedRxtx() {
		return ModbusService.isLoadedRxtx;
	}

	private static void setLoadedRxtx(Boolean isLoaded) {
		ModbusService.isLoadedRxtx = isLoaded;
	}

	@Autowired
	private final ModbusMonitoringService modbusMonitoringService;

	public ModbusService(ModbusMonitoringService service) {
		this.modbusMonitoringService = service;
	}

	public List<Alert> getAlertList() {
		return alertList;
	}

	@PostConstruct
	public void init() {

		LOGGER.info("Modbus version: {}", this.version);

		try {
			file = new File(this.modbusSchemaUrl);
			try {
				ModbusService.setLoadedRxtx(FileUtils.loadRxtxLib());
				this.serialPorts = SerialPort.listSerialPorts();
			} catch (java.lang.UnsatisfiedLinkError e) {
				LOGGER.warn("SerialPorts initializacion problem: {} , SerialPorts not available", e.getMessage());
			}
			LOGGER.info("Modbus Schema this.file: {}", this.modbusSchemaUrl);
			this.modbusEnvironment = new ModbusEnvironment();

			if (this.file.exists()) {
				ObjectMapper mapper = new ObjectMapper();
				String json;

				json = FileUtils.readModbusEnvironment(this.file.getAbsolutePath());
				this.modbusEnvironment = mapper.readValue(json, ModbusEnvironment.class);
				Integer coreThreadPoolSize = modbusEnvironment.getCoreThreadPoolSize();
				this.modbusMonitoringService.setMaxCoreThreadPoolSize((coreThreadPoolSize != null) ? coreThreadPoolSize : 1);
				this.saveFile();
			} else {
				this.modbusEnvironment = FileUtils.getCleanModbusEnvironment(this.modbusSchemaUrl, process, coreThreadPoolSize);
			}

		} catch (IOException | SecurityException | IllegalArgumentException e) {
			LOGGER.warn("Malformed schema json: {}", e.getMessage());
			this.modbusEnvironment = FileUtils.getCleanModbusEnvironment(this.modbusSchemaUrl, process, coreThreadPoolSize);
		}

		this.modbusMonitoringService.start(this);
		for (Device device : modbusEnvironment.getDevices()) {
			this.modbusMonitoringService.addMonitor2Device(device);
		}
	}

	/**
	 * Get stadistical info from the modbus system.
	 * 
	 * @return ModbusStatisticalInfo
	 */
	public ModbusStatisticalInfo getModbusInfo() {

		Integer totalDevides = 0;
		Integer totalDeviceOnError = 0;
		Integer totalSignals = 0;
		Integer totalSignalOnError = 0;

		// getting the total number of devices
		totalDevides = this.getDevices().size();
		// for each devices add the number of its signals plus the total signals
		for (Device device : this.getDevices()) {
			totalSignals = totalSignals + device.getSignals().size();

			// if the device is on error or it has no signal is like it is in error, add the
			// whole number of its signals to the total of signal on error
			if (device.getOnError() || device.getSignals().size() == 0) {
				totalDeviceOnError++;
				totalSignalOnError = totalSignalOnError + device.getSignals().size();
			} else {
				// checking the errors in device signals
				for (Signal signal : device.getSignals()) {
					if (signal.getOnError())
						totalSignalOnError++;
				}
			}
		}

		ModbusStatisticalInfo info = new ModbusStatisticalInfo();

		info.setTotalDevides(totalDevides);
		info.setTotalDeviceOnError(totalDeviceOnError);
		info.setTotalSignals(totalSignals);
		info.setTotalSignalOnError(totalSignalOnError);

		return info;
	}

	public List<String> getSerialPorts() {
		return serialPorts;
	}

	public List<Device> getDevices() {
		return modbusEnvironment.getDevices();
	}

	public Device getDevice(String deviceId) {
		Device device = null;
		for (Device d : modbusEnvironment.getDevices()) {
			if (d.getId().equals(deviceId)) {
				device = d;
				break;
			}
		}
		return device;
	}

	public Boolean getBulkFormat() {
		return modbusEnvironment.getBulkFormat();
	}

	public void setBulkFormat(Boolean bool) {
		modbusEnvironment.setBulkFormat(bool);
	}

	public Signal getSignal(String deviceId, String signalId) {
		Signal signal = null;
		for (Device device : modbusEnvironment.getDevices()) {
			if (device.getId().equals(deviceId)) {
				for (Signal s : device.getSignals()) {
					if (s.getId().equals(signalId)) {
						signal = s;
						break;
					}
				}
			}
		}
		return signal;
	}
	
	public Signal getSignalByBusinessId(String deviceId, String businessId) {
		Signal signal = null;
		for (Device device : modbusEnvironment.getDevices()) {
			if (device.getId().equals(deviceId)) {
				for (Signal s : device.getSignals()) {
					if (s.getBusinessId().equals(businessId)) {
						signal = s;
						break;
					}
				}
			}
		}
		return signal;
	}

	public Boolean saveDevice(Device device) throws IOException {
		Boolean output = (this.getDevice(device.getId()) == null);
		if (output) {
			modbusEnvironment.getDevices().add(device);
			this.saveFile();
			this.modbusMonitoringService.addMonitor2Device(device);

			Alert alert = new Alert(State.INFO, AlertType.DEVICE_CREATED.getValue(), device.getId(), null);
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return output;

	}

	public Boolean updateDevice(Device device) throws IOException {

		// we get the complete information of the device to update
		Device toUpdateDevice = this.getDevice(device.getId());
		Boolean ok = toUpdateDevice != null;
		if (ok) {

			toUpdateDevice.setAddress(device.getAddress());
			toUpdateDevice.setInfo(device.getInfo());
			toUpdateDevice.setManufacturer(device.getManufacturer());
			toUpdateDevice.setModel(device.getModel());
			toUpdateDevice.setMonitoringBlocks(device.getMonitoringBlocks());
			toUpdateDevice.setMonitoringTimeoutSec(device.getMonitoringTimeoutSec());
			toUpdateDevice.setMonitoringTimeMs(device.getMonitoringTimeMs());
			toUpdateDevice.setMonitoringDelayMs(device.getMonitoringDelayMs());
			toUpdateDevice.setDeviceType(device.getDeviceType());

			if (toUpdateDevice.getProtocolType().equals(GlobalConstants.Form.TCP)) {
				toUpdateDevice.setIp(device.getIp());
				toUpdateDevice.setPort(device.getPort());
			} else if (toUpdateDevice.getProtocolType().equals(GlobalConstants.Form.RTU)) {
				// toUpdateDevice.setAdditionalProperty("serial",
				// device.getAdditionalProperties().get("serial"));
				// toUpdateDevice.setAdditionalProperty("bauds",
				// device.getAdditionalProperties().get("bauds"));
				toUpdateDevice.setSerial(device.getSerial());
				toUpdateDevice.setBauds(device.getBauds());
			}

			this.saveFile();

			this.modbusMonitoringService.removeDeviceFromMonitoringThread(device);
			this.modbusMonitoringService.addMonitor2Device(toUpdateDevice);
			LOGGER.info("Updating Monitor for device:{}", device.getId());

			Alert alert = new Alert(State.INFO, AlertType.DEVICE_UPDATED.getValue(), device.getId(), null);
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return ok;

	}

	public Boolean saveSignal(String deviceId, Signal signal) throws IOException {
		Boolean okDeviceId = this.existDeviceId(deviceId);
		Boolean okSignalId = this.existSignalId(deviceId, signal.getId());
		Boolean ok = okDeviceId && !okSignalId;
		if (ok) {
			this.getDevice(deviceId).getSignals().add(signal);
			this.saveFile();

			Alert alert = new Alert(State.INFO, AlertType.SIGNAL_CREATED.getValue(), deviceId, signal.getId());
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return ok;

	}

	public Boolean updateSignal(String deviceId, Signal signal) throws IOException {
		Boolean okDeviceId = this.existDeviceId(deviceId);
		Boolean okSignalId = this.existSignalId(deviceId, signal.getId());
		Boolean ok = okDeviceId && okSignalId;
		if (ok) {
			for (Signal sig : this.getDevice(deviceId).getSignals()) {
				if (sig.getId().equals(signal.getId())) {
					sig.setBusinessId(signal.getBusinessId());
					sig.setSignalType(signal.getSignalType());
					sig.setUnit(signal.getUnit());
					sig.setConvFactor(signal.getConvFactor());
					sig.setRegisterType(signal.getRegisterType());
					sig.setRegister(signal.getRegister());
					sig.setDataType(signal.getDataType());
					sig.setBigEndian(signal.getBigEndian());
					sig.setDescription(signal.getDescription());
					sig.setIsCommandable(signal.getIsCommandable());
					break;
				}
			}
			this.saveFile();

			Alert alert = new Alert(State.INFO, AlertType.SIGNAL_UPDATED.getValue(), deviceId, signal.getId());
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return ok;
	}

	public Boolean removeDevice(String deviceId) throws IOException {
		Device device = this.getDevice(deviceId);
		Boolean output = (device != null);
		if (output) {
			output = modbusEnvironment.getDevices().remove(device);
			this.saveFile();
			LOGGER.info("Removing monitoring from device:{}", deviceId);
			this.modbusMonitoringService.removeDeviceFromMonitoringThread(device);

			Alert alert = new Alert(State.INFO, AlertType.DEVICE_DELETED.getValue(), device.getId(), null);
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return output;
	}

	public Boolean existDeviceId(String deviceId) {
		Boolean exist = Boolean.FALSE;
		for (Device device : this.modbusEnvironment.getDevices()) {
			if (device.getId() != null && device.getId().equals(deviceId)) {
				exist = Boolean.TRUE;
				break;
			}
		}
		return exist;
	}

	public Boolean removeSignal(String deviceId, String signalId) throws IOException {
		Boolean okDeviceId = this.existDeviceId(deviceId);
		Boolean okSignalId = this.existSignalId(deviceId, signalId);
		Boolean ok = okDeviceId && okSignalId;
		if (ok) {
			for (Signal signal : this.getDevice(deviceId).getSignals()) {
				if (signal.getId().equals(signalId)) {
					this.getDevice(deviceId).getSignals().remove(signal);
					break;
				}
			}
			this.saveFile();
			Alert alert = new Alert(State.INFO, AlertType.SIGNAL_DELETED.getValue(), deviceId, signalId);
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return ok;

	}

	public Command getCommand(String id, String signalId, String commId) {

		Signal sig = this.getSignal(id, signalId);
		Command command = null;

		if (sig != null) {
			for (Command com : sig.getCommands()) {
				if (com.getId().equals(commId)) {
					command = com;
					break;
				}
			}
		}
		return command;
	}

	public Boolean existBusinessId(String deviceId, String businessId) {
		Boolean exist = Boolean.FALSE;
		Device dev = this.getDevice(deviceId);
		if (dev != null) {
			for (Signal signal : dev.getSignals()) {
				if (signal.getBusinessId() != null && signal.getBusinessId().equals(businessId)) {
					exist = Boolean.TRUE;
					break;
				}
			}
		}
		return exist;
	}
	
	public Boolean existSignalId(String deviceId, String signalId) {
		Boolean exist = Boolean.FALSE;
		Device dev = this.getDevice(deviceId);
		if (dev != null) {
			for (Signal signal : dev.getSignals()) {
				if (signal.getId() != null && signal.getId().equals(signalId)) {
					exist = Boolean.TRUE;
					break;
				}
			}
		}
		return exist;
	}

	public Boolean existCommandId(@NotNull String deviceId, @NotNull String signalId, @NotNull String commandId) {
		Boolean exist = Boolean.FALSE;
		Signal sig = this.getSignal(deviceId, signalId);
		if (sig != null) {
			for (Command com : sig.getCommands()) {
				if (com.getId().equals(commandId)) {
					exist = Boolean.TRUE;
					break;
				}
			}
		}

		return exist;
	}

	public Boolean setCommandOnDevice(@NotNull Device dev, @NotNull String signalId, Integer value) {
		Boolean ok = Boolean.FALSE;
		for (Signal sigs : dev.getSignals()) {
			if (sigs.getId().equals(signalId)) {

				LOGGER.info("Trying to writing command for signal on device {}", dev.getId());
				ok = ModbusUtils.writeRegister(dev, sigs, value);

				Alert alert = new Alert(State.INFO, AlertType.COMMAND_EXECUTED.getValue(), dev.getId(), signalId);
				getAlertList().add(0, alert);
				this.checkAlertList();
				break;
			}
		}
		return ok;
	}

	public Boolean updateCommandOnDevice(@NotNull String deviceId, @NotNull String signalId, @NotNull Command command2Set) throws IOException {
		Boolean okDeviceId = this.existDeviceId(deviceId);
		Boolean okSignalId = this.existSignalId(deviceId, signalId);
		Boolean okCommandId = this.existCommandId(deviceId, signalId, command2Set.getId());
		Boolean ok = okDeviceId && okSignalId && okCommandId;
		if (ok) {
			for (Command command : this.getSignal(deviceId, signalId).getCommands()) {
				if (command.getId().equals(command2Set.getId())) {
					command.setDescription(command2Set.getDescription());
					command.setValue(command2Set.getValue());
					break;
				}
			}
			this.saveFile();
			Alert alert = new Alert(State.INFO, AlertType.COMMAND_UPDATED.getValue(), deviceId, signalId);
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return ok;
	}

	public Boolean saveCommandOnDevice(@NotNull String deviceId, @NotNull String signalId, @NotNull Command command2Set) throws IOException {
		Boolean okDeviceId = this.existDeviceId(deviceId);
		Boolean okSignalId = this.existSignalId(deviceId, signalId);
		Boolean okCommandId = this.existCommandId(deviceId, signalId, command2Set.getId());
		Boolean ok = okDeviceId && okSignalId && !okCommandId;
		if (ok) {
			this.getSignal(deviceId, signalId).getCommands().add(command2Set);
			this.saveFile();
			Alert alert = new Alert(State.INFO, AlertType.COMMAND_CREATED.getValue(), deviceId, signalId);
			getAlertList().add(0, alert);
			this.checkAlertList();
		}
		return ok;
	}

	public Boolean removeCommand(@NotNull String deviceId, @NotNull String signalId, @NotNull String commandId) throws IOException {
		Boolean okDeviceId = this.existDeviceId(deviceId);
		Boolean okSignalId = this.existSignalId(deviceId, signalId);
		Boolean okCommandId = this.existCommandId(deviceId, signalId, commandId);
		Boolean ok = okDeviceId && okSignalId && okCommandId;
		if (ok) {

			for (Command command : this.getSignal(deviceId, signalId).getCommands()) {
				if (command.getId().equals(commandId)) {
					this.getSignal(deviceId, signalId).getCommands().remove(command);
					break;
				}
			}
			this.saveFile();
			Alert alert = new Alert(State.INFO, AlertType.COMMAND_DELETED.getValue(), deviceId, signalId);
			getAlertList().add(0, alert);
			checkAlertList();
		}
		return ok;
	}

	public Integer getCoreThreadPoolSize() {
		return modbusEnvironment.getCoreThreadPoolSize();
	}

	public void checkAlertList() {
		if (alertList.size() > 10) {
			for (int i = 10; i < alertList.size(); i++) {
				alertList.remove(i);
			}
		}
	}

	public void saveFile() {
		try {
			FileUtils.saveModbusEnvironment(modbusEnvironment, this.file.getAbsolutePath());
			LOGGER.info("Saving file on: {}", this.file.getAbsolutePath());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}
}
