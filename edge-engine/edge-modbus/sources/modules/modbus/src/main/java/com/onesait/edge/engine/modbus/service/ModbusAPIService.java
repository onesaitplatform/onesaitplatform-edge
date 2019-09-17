package com.onesait.edge.engine.modbus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.onesait.edge.engine.modbus.influxdb.ModbusDbService;
import com.onesait.edge.engine.modbus.influxdb.json.DbResult;
import com.onesait.edge.engine.modbus.influxdb.json.DbSeries;
import com.onesait.edge.engine.modbus.influxdb.json.QueryForm;
import com.onesait.edge.engine.modbus.json.DataGraph;
import com.onesait.edge.engine.modbus.json.Dataset;
import com.onesait.edge.engine.modbus.json.Gauge;
import com.onesait.edge.engine.modbus.json.Series;
import com.onesait.edge.engine.modbus.model.Alert;
import com.onesait.edge.engine.modbus.model.DataType;
import com.onesait.edge.engine.modbus.model.Device;
import com.onesait.edge.engine.modbus.model.ModbusStatisticalInfo;
import com.onesait.edge.engine.modbus.model.RegisterType;
import com.onesait.edge.engine.modbus.model.Signal;
import com.onesait.edge.engine.modbus.util.ErrorResponse;
import com.onesait.edge.engine.modbus.util.ModbusUtils;
import com.serotonin.modbus4j.exception.ModbusTransportException;

@Service
public class ModbusAPIService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ModbusAPIService.class);
	private ResourceBundle resourceBundle;
	private HashMap<String, Object> userLocationMap;

	@Autowired
	private ModbusService modBusService;
	@Autowired
	private ModbusDbService modbusDbService;

	@Value("${spark.context.path.api}")
	private String contextpathapi;

	public void api(spark.Service ignite) {

		ignite.before("/", (req, res) -> {
			res.redirect("/modbus/");
		});

		ignite.before("", (req, res) -> {
			res.redirect("/modbus/");
		});

		// Solo desarrollo
		ignite.after((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Credentials", "true");
		});

		/**************************** API ***************************/
		String context = contextpathapi + "/devices";
		ignite.path(context, () -> {

			/** GET BulkFormat **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/bulkFormat]", context);
			ignite.get("/bulkFormat", (request, response) -> {
				response.type("application/json");
				if (modBusService.getBulkFormat() == null) {
					modBusService.setBulkFormat(Boolean.TRUE);
					modBusService.saveFile();
				}
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(modBusService.getBulkFormat());
			});

			/** PUT BulkFormat **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/bulkFormat/:value]", context);
			ignite.put("/bulkFormat/:value", (request, response) -> {
				Boolean bool = Boolean.parseBoolean(request.params(":value"));
				modBusService.setBulkFormat(bool);
				modBusService.saveFile();
				response.type("application/json");
				response.status(HttpStatus.NO_CONTENT_204);
				return new Gson().toJsonTree(null);
			});

			/**
			 * GET MODBUS INFO **
			 * 
			 */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/info]", context);
			ignite.get("/info", (request, response) -> {
				response.type("application/json");
				ModbusStatisticalInfo info = modBusService.getModbusInfo();
				if (info == null) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new ErrorResponse("Info not found"));
				}
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(info);
			});

			/** GET DEVICES **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}]", context);
			ignite.get("", (request, response) -> {
				response.type("application/json");
				List<Device> devices = modBusService.getDevices();
				if (devices.isEmpty()) {
					response.status(HttpStatus.NO_CONTENT_204);
					return new Gson().toJsonTree(null);
				}
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(devices);
			});

			// OLD GRAPH
			// /** PUT graph **/
			// LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/graph]", context);
			// Spark.put("/graph", (request, response) -> {
			// response.type("application/json");
			//
			// Graph graph = new Graph();
			// List<Series> seriesList = new ArrayList<>();
			// try {
			// QueryForm queryForm = new Gson().fromJson(request.body(), QueryForm.class);
			// // queryForm.setUTCTimezone();
			// String result = modbusDbService.getGraphValues(queryForm);
			// if (result != null) {
			// DbResult dbResult = new Gson().fromJson(result, DbResult.class);
			// if (dbResult.getSeries() != null) {
			// DbSeries listSeries = dbResult.getSeries().get(0);
			// List<List<String>> values = listSeries.getValues();
			//
			// for (int i = 0; i < values.size(); i++) {
			// List<String> list = values.get(i);
			// if (values.size() < 10 || (i % (values.size() / 10) == 0)) {
			// graph.getLabels().add(ModbusUtils.getTimeSystemTimeZone(list.get(0)));
			// } else {
			// graph.getLabels().add("");
			// }
			// Series serie = new Series(ModbusUtils.getTimeSystemTimeZone(list.get(0)),
			// list.get(1));
			// seriesList.add(serie);
			// }
			// graph.getSeries().add(seriesList);
			// return new Gson().toJsonTree(graph);
			// } else {
			// return new Gson().toJsonTree(graph);
			// }
			// } else {
			// return new Gson().toJsonTree(null);
			// }
			// } catch (Exception e) {
			// return new Gson().toJsonTree(null);
			// }
			// });

			/** PUT graph **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/graph]", context);
			ignite.put("/graph", (request, response) -> {
				response.type("application/json");

				DataGraph dataGraph = new DataGraph();
				Dataset dataset = new Dataset();

				try {
					QueryForm queryForm = new Gson().fromJson(request.body(), QueryForm.class);
					String result = modbusDbService.getGraphValues(queryForm);
					if (result != null) {
						DbResult dbResult = new Gson().fromJson(result, DbResult.class);
						if (dbResult.getSeries() != null) {
							DbSeries listSeries = dbResult.getSeries().get(0);
							List<List<String>> values = listSeries.getValues();

							List<String> datasetValues = new ArrayList<>();
							for (int i = 0; i < values.size(); i++) {
								List<String> list = values.get(i);
								String fecha = ModbusUtils.getTimeSystemTimeZone(list.get(0));
								dataGraph.getLabels().add(fecha);
								datasetValues.add(values.get(i).get(1));
							}
							dataset.setLabel(queryForm.getSignalId());
							dataset.setData(datasetValues);

							List<Dataset> dataList = new ArrayList<>();
							dataList.add(dataset);

							dataGraph.setDatasets(dataList);
							response.status(HttpStatus.OK_200);
							return new Gson().toJsonTree(dataGraph);
						} else {
							response.status(HttpStatus.OK_200);
							return new Gson().toJsonTree(dataGraph);
						}
					} else {
						response.status(HttpStatus.NO_CONTENT_204);
						return new Gson().toJsonTree(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.status(HttpStatus.OK_200);
					return new Gson().toJsonTree(null);
				}
			});

			/** GET gauge **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/gauge]", context);
			ignite.get("/gauge", (request, response) -> {
				response.type("application/json");

				ModbusStatisticalInfo info = modBusService.getModbusInfo();

				Gauge gauge = new Gauge();

				Integer resta = info.getTotalSignals() - info.getTotalSignalOnError();
				Integer value = 0;
				if (info.getTotalSignals() > 0) {
					value = (resta * 100) / info.getTotalSignals();
				}

				Series series = new Series("Value", value + "");
				gauge.getSeries().add(series);
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(gauge);
			});

			/** GET Alerts **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/alerts]", context);
			ignite.get("/alerts", (request, response) -> {
				response.type("application/json");

				List<Alert> list = modBusService.getAlertList();
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(list);
			});

			/** GET DEVICE BY ID */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/:id]", context);
			ignite.get("/:id", (request, response) -> {
				response.type("application/json");
				Device device = modBusService.getDevice(request.params(":id"));
				if (device == null) {
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new ErrorResponse("Device not found"));
				}
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(device);
			});

			/** GET SIGNALS WITH COMMANDS */
			// LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/:id/signals/commands]",
			// context);
			// ignite.get("/:id/signals/commands", (request, response) -> {
			// response.type("application/json");
			// Device device = modBusService.getDevice(request.params(":id"));
			// if (device == null) {
			// response.status(HttpStatus.NOT_FOUND_404);
			// return new Gson().toJsonTree(new ErrorResponse("Device not found"));
			// } else {
			// List<Signal> signalList = new ArrayList<>();
			//
			// for (Signal signal : device.getSignals()) {
			// if (signal.getCommands().size() > 0) {
			// signalList.add(signal);
			// }
			// }
			// if (signalList.size() > 0) {
			// response.status(HttpStatus.OK_200);
			// return new Gson().toJsonTree(signalList);
			// } else {
			// response.status(HttpStatus.OK_200);
			// return new Gson().toJsonTree("NODATA");
			// }
			// }
			// });

			/** GET SIGNALS BY DEVICE ID */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/:id/signals]", context);
			ignite.get("/:id/signals", (request, response) -> {
				response.type("application/json");
				Device device = modBusService.getDevice(request.params(":id"));
				if (device == null) {
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new ErrorResponse("Device not found"));
				}
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(device.getSignals());
			});

			/** GET SIGNAL BY DEVICE AND SIGNAL IDs */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/:id/signals/:sid]", context);
			ignite.get("/:id/signals/:sid", (request, response) -> {
				response.type("application/json");
				Signal signal = modBusService.getSignal(request.params(":id"), request.params(":sid"));
				if (signal == null) {
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new ErrorResponse("Signal not found"));
				}
				response.status(HttpStatus.OK_200);
				return new Gson().toJsonTree(signal);
			});

			/** ADD DEVICE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[POST {}]", context);
			ignite.post("", (request, response) -> {
				response.type("application/json");
				Device dev = new Gson().fromJson(request.body(), Device.class);
				if (dev != null) {
					LOGGER.info("Save Device {}", dev.getId());
					if (dev.getId() != null && !modBusService.existDeviceId(dev.getId())) {
						LOGGER.info("Creating device {}", dev.getId());
						modBusService.saveDevice(dev);
						response.status(HttpStatus.CREATED_201);
						return new Gson().toJsonTree(null);
					} else {
						LOGGER.info("Device {} is into system. Imposible create as new", dev.getId());
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("Duplicated device id or not valid"));
					}
				} else {
					response.status(HttpStatus.BAD_REQUEST_400);
					return new Gson().toJsonTree(new ErrorResponse("Null device id"));
				}

			});

			/** REMOVE DEVICE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[DELETE {}/:id]", context);
			ignite.delete("/:id", (req, response) -> {
				response.type("application/json");
				String id = req.params(":id");
				LOGGER.info("Deleting device : {}", id);
				Device dev = modBusService.getDevice(id);
				if (dev != null) {
					LOGGER.info("Deleting device {}", dev.getId());
					try {
						this.modBusService.removeDevice(dev.getId());
						response.status(HttpStatus.NO_CONTENT_204);
						return new Gson().toJsonTree(null);
					} catch (IOException e) {
						LOGGER.error("Imposible to remove, device {} is not into system", dev.getId(), e);
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("Imposible to remove, device is not into system"));
					}
				} else {
					response.status(HttpStatus.BAD_REQUEST_400);
					return new Gson().toJsonTree(new ErrorResponse("Null device id"));
				}

			});

			/** UPDATE DEVICE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/:id]", context);
			ignite.put("/:id", (req, response) -> {
				response.type("application/json");
				Device dev = new Gson().fromJson(req.body(), Device.class);
				if (dev != null && dev.getId() != null && modBusService.existDeviceId(dev.getId())) {
					LOGGER.info("Update device {}", dev.getId());
					try {
						this.modBusService.updateDevice(dev);
						response.status(HttpStatus.NO_CONTENT_204);
						return new Gson().toJsonTree(null);
					} catch (IOException e) {
						LOGGER.error("Imposible to update, device {} is not into system", dev.getId(), e);
						response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
						return new Gson().toJsonTree(new ErrorResponse("Imposible to update, device is not into system"));
					}
				} else {
					response.status(HttpStatus.BAD_REQUEST_400);
					return new Gson().toJsonTree(new ErrorResponse("Null device id"));
				}

			});

			/** ADD SIGNAL **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[POST {}/:id/signals]", context);
			ignite.post("/:id/signals", (req, response) -> {
				response.type("application/json");
				String id = req.params(":id");
				Signal sig = new Gson().fromJson(req.body(), Signal.class);
				Device dev = modBusService.getDevice(id);
				if (dev != null) {
					Signal signal = modBusService.getSignal(id, sig.getId());
					if (signal == null) {
						if (dev.existBusinessId(sig.getBusinessId())) {
							response.status(HttpStatus.BAD_REQUEST_400);
							return new Gson().toJsonTree(new ErrorResponse("BusinessId already exists"));
						} else {
							try {
								this.modBusService.saveSignal(id, sig);
								response.status(HttpStatus.CREATED_201);
								return new Gson().toJsonTree(null);
							} catch (IOException e) {
								LOGGER.error("Imposible to create, signal {} is already into system", sig.getId(), e);
								response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
								return new Gson().toJsonTree(new ErrorResponse("Imposible to create, signal is not into system"));
							}
						}
					} else {
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("SignalId already exist"));
					}
				} else {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new ErrorResponse("DeviceId not found"));
				}

			});

			/** REMOVE SIGNAL **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[DELETE {}/:id/signals/:signalid]", context);
			ignite.delete("/:id/signals/:signalid", (req, response) -> {
				response.type("application/json");
				String id = req.params(":id");
				String signalId = req.params(":signalid");
				Device dev = modBusService.getDevice(id);
				if (dev != null) {
					Signal signal = modBusService.getSignal(id, signalId);
					if (signal != null) {
						try {

							this.modBusService.removeSignal(id, signalId);
							response.status(HttpStatus.NO_CONTENT_204);
							return new Gson().toJsonTree(null);
						} catch (IOException e) {
							LOGGER.error("Imposible to remove, signal {} is not into system", dev.getId(), e);
							response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
							return new Gson().toJsonTree(new ErrorResponse("Imposible to remove, signal is not into system"));
						}
					} else {
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("Null signal id"));
					}
				} else {
					response.status(HttpStatus.BAD_REQUEST_400);
					return new Gson().toJsonTree(new ErrorResponse("Null device id"));
				}

			});

			/** UPDATE SIGNAL **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/:id/signals/:signalid]", context);
			ignite.put("/:id/signals/:signalid", (req, response) -> {
				response.type("application/json");
				String id = req.params(":id");
				String signalId = req.params(":signalid");
				Signal sig = new Gson().fromJson(req.body(), Signal.class);
				Device dev = modBusService.getDevice(id);
				if (dev != null) {
					if (sig == null || sig.getId() == null || !sig.getId().equals(signalId)) {
						LOGGER.error("Imposible to update, signal not valid");
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("Null signal id, or not matching with device"));
					}

					Signal signal = modBusService.getSignal(id, signalId);
					if (signal != null) {
						try {
							if (dev.existBusinessId(sig.getBusinessId()) && !signal.getBusinessId().equalsIgnoreCase(sig.getBusinessId())) {
								response.status(HttpStatus.BAD_REQUEST_400);
								return new Gson().toJsonTree(new ErrorResponse("BusinessId already exists"));
							} else {
								this.modBusService.updateSignal(id, sig);
								response.status(HttpStatus.NO_CONTENT_204);
								return new Gson().toJsonTree(null);
							}
						} catch (IOException e) {
							LOGGER.error("Imposible to update, signal {} is not into system", signal.getId(), e.getMessage());
							response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
							return new Gson().toJsonTree(new ErrorResponse("Imposible to update, signal is not into system"));
						}
					} else {
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("Null signal id"));
					}
				} else {
					response.status(HttpStatus.BAD_REQUEST_400);
					return new Gson().toJsonTree(new ErrorResponse("Null device id"));
				}

			});

			/** ADD COMMAND **/
			// LOGGER.info("SPARK API_CONTEXT_PATH :[POST
			// {}/:id/signals/:signalid/command]", context);
			// ignite.post("/:id/signals/:signalid/command", (req, response) -> {
			// response.type("application/json");
			// String devId = req.params(":id");
			// String signalId = req.params(":signalid");
			// Command com = new Gson().fromJson(req.body(), Command.class);
			//
			// LOGGER.info("Command {} recieved, from the signal {} and the device {}",
			// com.getId(), signalId, devId);
			// if (com != null && com.getId() != null && com.getValue() != null &&
			// com.getDescription() != null) {
			// if (modBusService.existDeviceId(devId)) {
			// if (modBusService.existSignalId(devId, signalId)) {
			// if (!modBusService.existCommandId(devId, signalId, com.getId())) {
			// try {
			// this.modBusService.saveCommandOnDevice(devId, signalId, com);
			// response.status(HttpStatus.CREATED_201);
			// return new Gson().toJsonTree(null);
			// } catch (IOException e) {
			// LOGGER.error("Imposible to create command {}for signal {} in the device {}:
			// {}", com.getId(), signalId, devId, e.getMessage());
			// response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
			// return new Gson().toJsonTree(new ErrorResponse("Imposible to update command,
			// is not into signal " + signalId + " or device" + devId));
			// }
			// } else {
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("Command already exist"));
			// }
			//
			// } else {
			// LOGGER.error("The signal {} for device {} does not exist", signalId, devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The signal " + signalId + "
			// does not exist for device " + devId));
			// }
			//
			// } else {
			// LOGGER.error("The device {} does not exist", devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " does
			// not exist"));
			// }
			// } else {
			// LOGGER.error("Imposible to create command, command filled with not valid
			// fields");
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("Null in id, value or
			// description command." + signalId + " for device " + devId));
			// }
			// });

			/** GET COMMAND **/
			// LOGGER.info("SPARK API_CONTEXT_PATH :[GET
			// {}/:id/signals/:signalid/commands/:commandid]", context);
			// ignite.get("/:id/signals/:signalid/commands/:commandid", (request, response)
			// -> {
			// response.type("application/json");
			// String devId = request.params(":id");
			// String signalId = request.params(":signalid");
			// String commId = request.params(":commandid");
			//
			// Device dev = modBusService.getDevice(devId);
			//
			// if (dev != null) {
			// Signal signal = modBusService.getSignal(dev.getId(), signalId);
			// if (signal != null) {
			// Command command = modBusService.getCommand(dev.getId(), signal.getId(),
			// commId);
			// if (command != null) {
			// ExtendCommand extendCommand = new ExtendCommand(dev.getId(), signal.getId(),
			// command.getId(), command.getDescription(), command.getValue());
			// response.status(HttpStatus.OK_200);
			// return new Gson().toJsonTree(extendCommand);
			// } else {
			// LOGGER.error("Imposible to execute command {}, there is no command for signal
			// {} in the device {}", commId, signalId, devId);
			// response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
			// return new Gson().toJsonTree(new ErrorResponse("Imposible to execute command,
			// is not into signal " + signalId + " and device" + devId));
			// }
			//
			// } else {
			// LOGGER.error("The signal {} for device {} does not exist", signalId, devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The signal " + signalId + "
			// does not exist for device " + devId));
			// }
			//
			// } else {
			// LOGGER.error("The device {} does not exist", devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " does
			// not exist"));
			// }
			// });

			/** SEND COMMAND **/
			// LOGGER.info("SPARK API_CONTEXT_PATH :[PUT
			// {}/:id/signals/:signalid/commands/:commandid/execute]", context);
			// ignite.put("/:id/signals/:signalid/commands/:commandid/execute", (request,
			// response) -> {
			// response.type("application/json");
			// String devId = request.params(":id");
			// String signalId = request.params(":signalid");
			// String commId = request.params(":commandid");
			//
			// LOGGER.info("Command {} recieved, from the signal {} and the device {}",
			// commId, signalId, devId);
			// if (commId != null) {
			//
			// Device dev = modBusService.getDevice(devId);
			// if (dev != null) {
			//
			// if (dev.getOnError()) {
			// LOGGER.error("The device {} is on ERROR, can't execute command {}", devId,
			// commId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " is
			// on ERROR, can't execute command " + commId));
			// }
			//
			// if (modBusService.existSignalId(devId, signalId)) {
			// if (this.modBusService.setCommandOnDevice(dev, signalId, commId, null)) {
			// response.status(HttpStatus.ACCEPTED_202);
			// return new Gson().toJsonTree(null);
			// } else {
			// LOGGER.error("Imposible to execute command {}, there is no command for signal
			// {} in the device {}", commId, signalId, devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("Imposible to execute command,
			// is not into signal " + signalId + " and device" + devId));
			// }
			//
			// } else {
			// LOGGER.error("The signal {} for device {} does not exist", signalId, devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The signal " + signalId + "
			// does not exist for device " + devId));
			// }
			//
			// } else {
			// LOGGER.error("The device {} does not exist", devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " does
			// not exist"));
			// }
			//
			// } else {
			// LOGGER.error("Imposible to execute command, commandId null");
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("Null command id"));
			// }
			// });

			/** SEND COMMAND WITH VALUE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/:id/signals/:signalid/:value]", context);
			ignite.put("/:id/signals/:signalid/:value", (request, response) -> {
				response.type("application/json");
				String devId = request.params(":id");
				String signalId = request.params(":signalid");
				try {
					Integer value = Integer.parseInt(request.params(":value"));

					LOGGER.info("Value {} recieved, from the signal {} and the device {}", value, signalId, devId);
					Device dev = modBusService.getDevice(devId);
					if (dev != null) {
						if (dev.getOnError()) {
							LOGGER.error("The device {} is on ERROR, can't execute command.", devId);
							response.status(HttpStatus.BAD_REQUEST_400);
							return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " is on ERROR, can't execute command"));
						}
						if (modBusService.existSignalId(devId, signalId)) {
							Signal sig = this.modBusService.getSignal(devId, signalId);
							if (sig.getIsCommandable()) {

								Boolean ok = this.modBusService.setCommandOnDevice(dev, signalId, value);

								// Comprobamos que se ha escrito el correctamente el valor
								this.readModbusRegister(dev, sig);
								Boolean checkValue = this.modBusService.getSignal(devId, signalId).getValue().toString().equals(value.toString());
								if (ok && checkValue) {
									response.status(HttpStatus.ACCEPTED_202);
									return new Gson().toJsonTree(null);
								} else if (!ok) {
									response.status(HttpStatus.NOT_FOUND_404);
									return new Gson().toJsonTree(new ErrorResponse("Imposible to execute command"));
								} else {
									response.status(HttpStatus.FORBIDDEN_403);
									return new Gson().toJsonTree(new ErrorResponse("Value not valid"));
								}
							} else {
								LOGGER.error("Imposible to execute command, You do no have permission to change the signal value from signal {}", signalId);
								response.status(HttpStatus.BAD_REQUEST_400);
								return new Gson().toJsonTree(
										new ErrorResponse("Imposible to execute command, You do no have permission to change the signal value from signal " + signalId));
							}
						} else {
							LOGGER.error("The signal {} for device {} does not exist", signalId, devId);
							response.status(HttpStatus.BAD_REQUEST_400);
							return new Gson().toJsonTree(new ErrorResponse("The signal " + signalId + " does not exist for device " + devId));
						}
					} else {
						LOGGER.error("The device {} does not exist", devId);
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " does not exist"));
					}
				} catch (NumberFormatException e) {
					LOGGER.error("Value must be an Integer");
					response.status(HttpStatus.BAD_REQUEST_400);
					return new Gson().toJsonTree(new ErrorResponse("Value must be an Integer"));
				}
			});

			/** SEND COMMAND WITH VALUE WITH BUSINESSID **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/:id/businessIds/:businessId/:value]", context);
			ignite.put("/:id/businessIds/:businessId/:value", (request, response) -> {
				response.type("application/json");
				String devId = request.params(":id");
				String businessId = request.params(":businessId");
				try {
					Integer value = Integer.parseInt(request.params(":value"));

					LOGGER.info("Value {} recieved, from the businessId {} and the device {}", value, businessId, devId);
					Device dev = modBusService.getDevice(devId);
					if (dev != null) {
						if (dev.getOnError()) {
							LOGGER.error("The device {} is on ERROR, can't execute command.", devId);
							response.status(HttpStatus.BAD_REQUEST_400);
							return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " is on ERROR, can't execute command"));
						}

						if (modBusService.existBusinessId(devId, businessId)) {

							Signal sig = modBusService.getSignalByBusinessId(devId, businessId);

							if (sig.getIsCommandable()) {
								Boolean ok = this.modBusService.setCommandOnDevice(dev, sig.getId(), value);
								this.readModbusRegister(dev, sig);
								Boolean checkValue = this.modBusService.getSignal(devId, sig.getId()).getValue().toString().equals(value.toString());
								if (ok && checkValue) {
									response.status(HttpStatus.ACCEPTED_202);
									return new Gson().toJsonTree(null);
								} else if (!ok) {
									response.status(HttpStatus.NOT_FOUND_404);
									return new Gson().toJsonTree(new ErrorResponse("Imposible to execute command"));
								} else {
									response.status(HttpStatus.FORBIDDEN_403);
									return new Gson().toJsonTree(new ErrorResponse("Value not valid"));
								}
							} else {
								LOGGER.error("Imposible to execute command, You do no have permission to change the signal value from signal {}", sig.getId());
								response.status(HttpStatus.BAD_REQUEST_400);
								return new Gson().toJsonTree(
										new ErrorResponse("Imposible to execute command, You do no have permission to change the signal value from signal " + sig.getId()));
							}
						} else {
							LOGGER.error("The businessId {} for device {} does not exist", businessId, devId);
							response.status(HttpStatus.BAD_REQUEST_400);
							return new Gson().toJsonTree(new ErrorResponse("The businessId " + businessId + " does not exist for device " + devId));
						}
					} else {
						LOGGER.error("The device {} does not exist", devId);
						response.status(HttpStatus.BAD_REQUEST_400);
						return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " does not exist"));
					}
				} catch (NumberFormatException e) {
					LOGGER.error("Value must be an Integer");
					response.status(HttpStatus.BAD_REQUEST_400);
					return new Gson().toJsonTree(new ErrorResponse("Value must be an Integer"));
				}
			});

			/** UPDATE COMMAND **/
			// LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/:id/signals/:signalid/command]",
			// context);
			// ignite.put("/:id/signals/:signalid/command", (req, response) -> {
			// response.type("application/json");
			// String devId = req.params(":id");
			// String signalId = req.params(":signalid");
			// Command com = new Gson().fromJson(req.body(), Command.class);
			//
			// LOGGER.info("Command {} recieved, from the signal {} and the device {}",
			// com.getId(), signalId, devId);
			//
			// if (com != null && com.getId() != null && com.getValue() != null &&
			// com.getDescription() != null) {
			// Device dev = modBusService.getDevice(devId);
			// if (dev != null) {
			// if (modBusService.existSignalId(devId, signalId)) {
			// try {
			// this.modBusService.updateCommandOnDevice(devId, signalId, com);
			// response.status(HttpStatus.NO_CONTENT_204);
			// return new Gson().toJsonTree(null);
			// } catch (IOException e) {
			// LOGGER.error("Imposible to update command {} for signal {} in the device {}:
			// {}", com.getId(), signalId, devId, e.getMessage());
			// response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
			// return new Gson().toJsonTree(new ErrorResponse("Imposible to update command.
			// Command is not into signal " + signalId + " and device" + devId));
			// }
			//
			// } else {
			// LOGGER.error("The signal {} for device {} does not exist", signalId, devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The signal " + signalId + "
			// does not exist for device " + devId));
			// }
			//
			// } else {
			// LOGGER.error("The device {} does not exist", devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " does
			// not exist"));
			// }
			//
			// } else {
			// LOGGER.error("Imposible to update command, command filled with not valid
			// fields");
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("Null in id, value or
			// description command." + signalId + " for device " + devId));
			// }
			// });

			/** REMOVE COMMAND **/
			// LOGGER.info("SPARK API_CONTEXT_PATH :[DELETE
			// {}/:id/signals/:signalid/commands/:commandid]", context);
			// ignite.delete("/:id/signals/:signalid/commands/:commandId", (req, response)
			// -> {
			// response.type("application/json");
			// String devId = req.params(":id");
			// String signalId = req.params(":signalid");
			// String commandId = req.params(":commandid");
			//
			// LOGGER.info("Command {} recieved, from the signal {} and the device {}",
			// commandId, signalId, devId);
			// if (modBusService.existDeviceId(devId)) {
			// if (modBusService.existSignalId(devId, signalId)) {
			// if (modBusService.existCommandId(devId, signalId, commandId)) {
			// try {
			// this.modBusService.removeCommand(devId, signalId, commandId);
			// response.status(HttpStatus.NO_CONTENT_204);
			// return new Gson().toJsonTree(null);
			// } catch (IOException e) {
			// LOGGER.error("Imposible to remove command {} for signal {} in the device {}:
			// {}", commandId, signalId, devId, e.getMessage());
			// response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
			// return new Gson().toJsonTree(new ErrorResponse("Imposible to remove command,
			// is not into signal " + signalId + " and device" + devId));
			// }
			// } else {
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("Command does not exist"));
			// }
			// } else {
			// LOGGER.error("The signal {} for device {} does not exist", signalId, devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The signal " + signalId + "
			// does not exist for device " + devId));
			// }
			// } else {
			// LOGGER.error("The device {} does not exist", devId);
			// response.status(HttpStatus.BAD_REQUEST_400);
			// return new Gson().toJsonTree(new ErrorResponse("The device " + devId + " does
			// not exist"));
			// }
			// });
		});
	}

	protected void injectPropertiesI18n() {
		Enumeration<String> keysEnum = resourceBundle.getKeys();
		LOGGER.info("injecPropertiesI18n keysEnum");
		while (keysEnum.hasMoreElements()) {
			String key = keysEnum.nextElement();
			LOGGER.info("{}:{}", key.toString(), resourceBundle.getString(key));
			userLocationMap.put(key.toString(), resourceBundle.getString(key));
		}
	}

	private void readModbusRegister(Device device, Signal signal) throws ModbusTransportException {

		DataType dataType = DataType.fromValue(signal.getDataType());
		Integer max = signal.getRegister() + DataType.offset(dataType);

		int count = max - signal.getRegister() + 1;

		short[] rawData = ModbusUtils.readDataFromRegister(device, RegisterType.fromValue(signal.getRegisterType()), signal.getRegister().intValue(), count);
		if (rawData != null && rawData.length > 0) {
			device.setValuesInSignalsByRegisterType(rawData, RegisterType.fromValue(signal.getRegisterType()), signal.getRegister());
			device.setOnError(Boolean.FALSE);
		} else {
			device.setOnError(Boolean.TRUE);
		}
	}
}
