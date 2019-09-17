package com.onesait.edge.engine.modbus.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.onesait.edge.engine.modbus.model.Alert;
import com.onesait.edge.engine.modbus.model.AlertType;
import com.onesait.edge.engine.modbus.model.Device;
import com.onesait.edge.engine.modbus.model.MqttMsgDetail;
import com.onesait.edge.engine.modbus.model.Protocol;
import com.onesait.edge.engine.modbus.model.RegisterType;
import com.onesait.edge.engine.modbus.model.Signal;
import com.onesait.edge.engine.modbus.model.State;
import com.onesait.edge.engine.modbus.service.ModbusService;
import com.onesait.edge.engine.modbus.service.MqttConnection;
import com.serotonin.modbus4j.exception.ModbusTransportException;

public class Task implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);
	private final Device device;
	private final MqttConnection mqttConnection;
	private final ModbusService modbusService;

	/**
	 * Constructor
	 */
	public Task(Device device, MqttConnection mqttConnection, ModbusService modbusService) {

		super();

		this.device = device;
		this.mqttConnection = mqttConnection;
		this.modbusService = modbusService;
		LOGGER.info("Created Task for device {}", device.getId());
		ModbusUtils.connect(device);
		LOGGER.info("Trying to connect: {}:{}", device.getId(), device.getProtocolType());
		if (Protocol.RTU.toString().equals(device.getProtocolType()) && !ModbusService.isLoadedRxtx()) {
			device.setOnError(Boolean.TRUE);
			device.setDescError("RXTX lib is not loaded");
		} else {
			ModbusUtils.initConnetion(device);
		}
	}

	@Override
	public void run() {

		Long duration = 0L;

		try {
			if (Protocol.RTU.toString().equals(device.getProtocolType()) && !ModbusService.isLoadedRxtx()) {
				device.setOnError(Boolean.TRUE);
				device.setDescError("RXTX lib is not loaded");
			} else {

				if (!ModbusUtils.isConnectionInitialized(device)) {
					LOGGER.debug("Connection for device {} is not initialized, initializing...", device.getId());
					ModbusUtils.initConnetion(device);
				}

				Long start = System.currentTimeMillis();
				this.monitorSignals();
				Long stop = System.currentTimeMillis();

				duration = stop - start;
				device.addMonitoringTaskTime(duration);

				Alert alert = new Alert(State.ERROR, AlertType.DEVICE_ERROR.getValue(), device.getId(), null);

				if (!device.getOnError()) {
					this.sendThroughMqtt();
					for (int i = 0; i < modbusService.getAlertList().size(); i++) {
						Alert al = modbusService.getAlertList().get(i);
						if (alert.getAlertType().equalsIgnoreCase(al.getAlertType()) && alert.getDeviceId().equalsIgnoreCase(al.getDeviceId())) {
							modbusService.getAlertList().remove(i);
							break;
						}
					}

				} else {
					LOGGER.warn("Device {} is on error!", device.getId());
					if (modbusService.getAlertList().size() == 0) {
						modbusService.getAlertList().add(0, alert);
					} else {
						Boolean encontrado = Boolean.FALSE;
						for (int i = 0; i < modbusService.getAlertList().size(); i++) {
							Alert al = modbusService.getAlertList().get(i);
							if (alert.getAlertType().equalsIgnoreCase(al.getAlertType()) && alert.getDeviceId().equalsIgnoreCase(al.getDeviceId())) {
								encontrado = Boolean.TRUE;
								break;
							}
						}
						// Si lo encuentra no añade nada (sólo se añade alerta si no existe ya)
						if (!encontrado) {
							modbusService.getAlertList().add(0, alert);
						}
					}
				}
				modbusService.checkAlertList();
			}
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getMessage() == null)
				message = e.toString();
			LOGGER.error(message);
			e.printStackTrace();
		}
	}

	private MqttMsgDetail getMqttConnectionMsgDetailsConf(Signal signal) {
		MqttMsgDetail details = new MqttMsgDetail();
		details.setProfile("modbus");
		details.setDeviceId(device.getId());
		details.setDeviceType(device.getDeviceType());
		details.setSignalId(signal.getId());
		details.setUnit(signal.getUnit());
		details.setSignal(signal.getSignalType());
		details.setDescription(signal.getDescription());
		details.setTimeStamp(TimeUnit.NANOSECONDS.toMillis(signal.getTimestampValueInNanos()));
		details.setTimeStampInNanos(signal.getTimestampValueInNanos());
		details.setValue(signal.getValue().toString());
		details.setNumber(signal.getConvertedValue());
		return details;
	}

	private void sendThroughMqtt() throws Exception {

		if (mqttConnection != null && mqttConnection.getMqttClient() != null && mqttConnection.getMqttClient().isConnected()) {

			List<String> jsonArray = new ArrayList<>();

			MqttMsgDetail msg = null;
			for (Signal signal : this.device.getSignals()) {
				if (!signal.getOnError()) {
					msg = this.getMqttConnectionMsgDetailsConf(signal);
					String jsonResponse = new Gson().toJsonTree(msg).toString();
					jsonArray.add(jsonResponse);
				}
			}

			if(modbusService.getBulkFormat() == null || modbusService.getBulkFormat()) {
				mqttConnection.sendSignal(jsonArray, mqttConnection.getTopicSignalsIn());
			}else {
				for (String json : jsonArray) {
					mqttConnection.sendSignal(json, mqttConnection.getTopicSignalsIn());
				}
			}
		}
	}

	// Monitoring signals, first cataloging by type, and after, by block for the
	// signals of the same type
	private void monitorSignals() {
		if (device.containsRegistries(RegisterType.CS)) {
			monitorSignalsByBlock(RegisterType.CS);
		}
		if (device.containsRegistries(RegisterType.IS)) {
			monitorSignalsByBlock(RegisterType.IS);
		}
		if (device.containsRegistries(RegisterType.HR)) {
			monitorSignalsByBlock(RegisterType.HR);
		}
		if (device.containsRegistries(RegisterType.IR)) {
			monitorSignalsByBlock(RegisterType.IR);
		}
	}

	private void monitorSignalsByBlock(RegisterType type) {

		Integer blockmin = this.device.getMinModbusDeviceRegistryByType(type) / this.device.getMonitoringBlocks();
		Integer blockmax = this.device.getMaxModbusDeviceRegistryByType(type) / this.device.getMonitoringBlocks();

		for (int i = blockmin; i <= blockmax; i++) {
			Integer register = device.minRegisterinBlock(this.device.getMonitoringBlocks(), i, type);

			if (register != null) {
				int count = device.maxRegisterinBlock(this.device.getMonitoringBlocks(), i, type) - register + 1;
				short[] rawData = null;

				try {

					TimeUnit.MILLISECONDS.sleep(this.device.getMonitoringDelayMs());
					rawData = ModbusUtils.readDataFromRegister(device, type, register, count);

					if (rawData != null && rawData.length > 0) {
						device.setValuesInSignalsByRegisterType(rawData, type, register);
						device.setOnError(Boolean.FALSE);
					} else {
						device.setOnError(Boolean.TRUE);
					}

				} catch (InterruptedException e) {
					LOGGER.error("Interrupted Exception {} {}", device.getId(), e.getMessage());
					Thread.currentThread().interrupt();
				} catch (ModbusTransportException e) {

					// Invalid offset: 190000
					markSignalOnError(e.getMessage());
					device.setOnError(Boolean.TRUE);
					device.setDescError("I/O error");
					LOGGER.error("Modbus exception in device: {} - {}", device.getId(), e.getMessage());
				}
			}
		}
	}

	private String findWrongOffsetInMsg(@NotNull String msg) {
		String wrongOffset = null;
		String[] parts = msg.split(":");
		if (parts != null && parts.length == 2) {
			wrongOffset = parts[1].trim();
		}
		return wrongOffset;
	}

	private void markSignalOnError(String descError) {

		String signalId = null;

		if (descError != null && descError.contains("Invalid offset")) {

			signalId = findWrongOffsetInMsg(descError);

			for (Signal signal : device.getSignals()) {
				if (signal.getRegister().equals(Integer.valueOf(signalId))) {
					LOGGER.error("Signal on error: {} - Invalid offset", signalId);
					signal.setOnError(Boolean.TRUE);
					signal.setDescError(descError);
					break;
				}
			}
		}
	}
}
