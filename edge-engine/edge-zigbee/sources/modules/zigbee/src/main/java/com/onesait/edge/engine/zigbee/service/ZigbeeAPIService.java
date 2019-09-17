package com.onesait.edge.engine.zigbee.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
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
import com.onesait.edge.engine.zigbee.exception.GenericZigbeeException;
import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.frame.ZdoActiveEpReq;
import com.onesait.edge.engine.zigbee.frame.ZdoIeeeAddrReq;
import com.onesait.edge.engine.zigbee.influx.ZigbeeDbService;
import com.onesait.edge.engine.zigbee.influxdb.json.DbResult;
import com.onesait.edge.engine.zigbee.influxdb.json.DbSeries;
import com.onesait.edge.engine.zigbee.influxdb.json.QueryForm;
import com.onesait.edge.engine.zigbee.io.OutputSerialZigbee;
import com.onesait.edge.engine.zigbee.json.DeviceOTVersion;
import com.onesait.edge.engine.zigbee.jsoncontroller.AttributesJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.Child;
import com.onesait.edge.engine.zigbee.jsoncontroller.Gauge;
import com.onesait.edge.engine.zigbee.jsoncontroller.Graph;
import com.onesait.edge.engine.zigbee.jsoncontroller.Series;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbCoordinatorJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbDeviceJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbDevicesGraph;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbGetDataJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbMeshView;
import com.onesait.edge.engine.zigbee.jsoncontroller.ZbShowNetwork;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclCoordinator;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.model.ZclEndpoint;
import com.onesait.edge.engine.zigbee.ota.ImageBlockResponse;
import com.onesait.edge.engine.zigbee.ota.ImageNotify;
import com.onesait.edge.engine.zigbee.ota.OtaManager;
import com.onesait.edge.engine.zigbee.types.CommandType;
import com.onesait.edge.engine.zigbee.types.CommandWeb;
import com.onesait.edge.engine.zigbee.types.DeviceType;
import com.onesait.edge.engine.zigbee.types.StatusResponse;
import com.onesait.edge.engine.zigbee.util.Alert;
import com.onesait.edge.engine.zigbee.util.BuildMqttMsg;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.FourByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ReturningValues;
import com.onesait.edge.engine.zigbee.util.StandardResponse;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

@Service
public class ZigbeeAPIService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ZigbeeAPIService.class);
	private ResourceBundle resourceBundle;
	private HashMap<String, Object> userLocationMap;
	private static final String SHOWUNSUPPORTED = "showunsupported";
	private static final String CLUSTER = "cluster";
	private static final String MAC = "mac";
	private static final String ID = "id";
	private static final String INTERVAL = "interval";
	private static final String[] OPTIONS = { "status", "abort", "upgrade" };
	private static final String ACTION = "action";
	private static final String FILEPATH = "filepath";
	private static final String MODE = "mode";
	private static final String TEMPERATURE = "temperature";
	private static final String CHANNEL = "channel";
	private static final String ATTRIBUTE = "attribute";
	private static final String MINTIME = "mintime";
	private static final String MAXTIME = "maxtime";
	private static final String COMMANDID = "commandid";
	private static final String STATUS = "status";
	private static final String TIME = "time";
	private static final String ONLYWITHATTREPORTABLE = "onlyreportables";
	private static final long OFFSET_BYTES_FILEVERSION = 14;
	private static final int BUFFER_SIZE = 4;
	private static final int PAYLOAD_TYPE = 0x00;
	private static final int QUERY_JITTER = 100;

	@Autowired
	private ZigbeeService zigbeeService;
	@Autowired
	private DeviceManager devicemanager;
	@Autowired
	private SerialZigbeeConnector serialConnector;
	@Autowired
	private ZigbeeDbService zigbeeDbService;

	@Value("${spark.context.path.api}")
	private String contextpathapi;

	public void api(spark.Service ignite) {

		ignite.before("/", (req, res) -> {
			res.redirect("/zigbee/");
		});

		ignite.before("", (req, res) -> {
			res.redirect("/zigbee/");
		});

		ignite.options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return "OK";
		});
		// Solo desarrollo
		ignite.after((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Credentials", "true");
		});

		String context = contextpathapi;
		ignite.path(context, () -> {

			/** GETZBVERSION */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/getzbversion]", context);
			ignite.get("/getzbversion", (request, response) -> {
				response.type("application/json");
				try {
					String fwversion = zigbeeService.getFwVersion(true);
					if (fwversion != null) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(fwversion)));
					}
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Firmware can not be obtained"));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

			/** ZBACTIVENET */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbactivenet]", context);
			ignite.get("/zbactivenet", (request, response) -> {
				response.type("application/json");
				try {
					if (zigbeeService.activenet()) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
					} else {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The network could not be opened"));
					}
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
				}
			});

			/** ZBCLUSTERS */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbcluster/:mac/:onlyreportables]", context);
			ignite.get("/zbclusters", (request, response) -> {
				response.type("application/json");
				try {
					List<String> clusters = new ArrayList<>();
					String mac = request.queryParams(MAC);
					String showOnlyReportable = request.queryParams(ONLYWITHATTREPORTABLE);
					if (showOnlyReportable != null) {
						clusters = zigbeeService.buildZbClustersJson(mac, true);
					} else {
						clusters = zigbeeService.buildZbClustersJson(mac, false);
					}
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(clusters)));

				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Could not be obtained the devices"));

				}
			});
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbgetversion]", context);

			ignite.get("/zbgetversion", (request, response) -> {
				String mac = request.queryParams(MAC);

				this.serialConnector.getOutputSerial().writeZFrame(new ZdoActiveEpReq(new DoubleByte(0x0000), new DoubleByte(0x0000)));
				zigbeeService.waitMs(1000);
				ZclCoordinator coor = this.serialConnector.getZclcoor();

				boolean otaEndPoint = coor.hasOtaEndPoint();
				if (otaEndPoint) {

					ZclDevice device = devicemanager.getDeviceByMac(new OctaByte(OctaByte.convertMac(mac)));
					OtaManager otamng = device.getOtamanager();
					ZclEndpoint ep = device.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA);
					try {
						if (ep == null) {
							// el dev no tiene el cluster ota
							// Cluster %s no encontrado en dispositivo %s
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Cluster " + ZClusterLibrary.ZCL_CLUSTER_ID_OTA + " not found on device"));
						} else {
							// si el dispositivo no esta en OTA
							if (!otamng.isOtaRequest() && !otamng.isOta()) {
								ImageNotify imNotify = new ImageNotify(PAYLOAD_TYPE, QUERY_JITTER, null, null, null);
								int[] frame = zigbeeService.buildZclFrame(true, 0, ZClusterLibrary.ZCL_OTA_IMAGE_NOTIFY, imNotify.getFrame());
								ZFrame zframe = new AfDataRequest(device.getShortAddress(), ep.getId(), ZigbeeConstants.COORDINATOR_OTA_ENDPOINT,
										ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0, frame);
								otamng.setInfoRequested(true);
								this.serialConnector.getOutputSerial().writeZFrame(zframe);
								zigbeeService.waitMs(3000);
								if (!otamng.isInfoRequested()) {

									FourByte currentVersion = otamng.getCurrentVersion();

									DeviceOTVersion deviceVersion = new DeviceOTVersion();
									deviceVersion.setCurrentFileVersion(currentVersion.toString());
									deviceVersion.setStackBuild(String.valueOf(currentVersion.getMsb3()));
									deviceVersion.setStackRelease(String.valueOf(currentVersion.getMsb2()));
									deviceVersion.setApplicationBuild(String.valueOf(currentVersion.getMsb1()));
									deviceVersion.setApplicationRelease(String.valueOf(currentVersion.getLsb()));
									return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(deviceVersion)));

								} else {
									// el dispositivo no ha respondido
									otamng.setInfoRequested(false);
									return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Device did not respond"));

								}

							} else {
								// el dispositivo se encuentra en estado ota (actualizandose)
								return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The device is in OTA process"));
							}
						}

					} catch (Exception e) {
						response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Error obtaining the info"));
					}
				} else {
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The coordinator has not the OTA end point"));
				}

			});

			/** ZBDEVICES */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbdevices]", context);
			ignite.get("/zbdevices", (request, response) -> {
				response.type("application/json");
				try {
					List<ZbDeviceJson> zbdevices = zigbeeService.buildZbdevicesJson();
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(zbdevices)));
				} catch (Exception e) {
					LOGGER.error(e.getLocalizedMessage());
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Could not be obtained the devices"));

				}
			});

			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbdevice/:mac]", context);
			ignite.get("/zbdevicesgraph", (request, response) -> {
				response.type("application/json");
				try {
					List<ZbDevicesGraph> zbdevicesgraph = zigbeeService.buildZbdevicesGraph();
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(zbdevicesgraph)));
				} catch (Exception e) {
					LOGGER.error(e.getLocalizedMessage());
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Could not be obtained the devices"));

				}
			});

			/** ZBDEVICESGRAPH */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbdevicesgraph]", context);
			ignite.get("/zbdevicesgraph", (request, response) -> {
				response.type("application/json");
				try {
					List<ZbDevicesGraph> zbdevicesgraph = zigbeeService.buildZbdevicesGraph();
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(zbdevicesgraph)));
				} catch (Exception e) {
					LOGGER.error(e.getLocalizedMessage());
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Could not be obtained the devices"));

				}
			});

			/** ZBATTRIBUTES */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbattributes/:mac/:cluster]", context);
			ignite.get("/zbattributes", (request, response) -> {
				response.type("application/json");
				try {
					String mac = request.queryParams(MAC);
					String cluster = request.queryParams(CLUSTER);
					List<String> att = zigbeeService.buildAttributesJson(mac, cluster);
					if (att != null) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(att)));
					} else {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "No reportable attributes found in cluster " + cluster));
					}

				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Could not be obtained the devices"));

				}
			});

			/** ZBSTATUSDEVICES */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbattstatus", context);
			ignite.get("/zbattstatus", (request, response) -> {
				response.type("application/json");
				try {
					List<ZbDeviceJson> attsInfo = zigbeeService.buildZbStatusDevices();
					Integer nDevices = ZigbeeService.getnDevices();
					Integer nAttOK = ZigbeeService.getnAttOK();
					Integer nAttKO = ZigbeeService.getnAttKO();
					double porcentaje = 0;
					if (nDevices == 0) {
						porcentaje = 0;
					} else {
						porcentaje = ((double) ZigbeeService.getnDevicesOK() * 100) / (double) ZigbeeService.getnDevices();
					}
					porcentaje = BuildMqttMsg.round(porcentaje, 2, null, null, true);
					Gauge gauge = new Gauge();
					Series series = new Series("Value", porcentaje + "");
					gauge.getSeries().add(series);
					StandardResponse standardResponse = new StandardResponse(StatusResponse.SUCCESS, nDevices, nAttKO, nAttOK, new Gson().toJsonTree(attsInfo),
							new Gson().toJsonTree(gauge), ZigbeeService.getnDevicesKO());
					return new Gson().toJsonTree(standardResponse);
				} catch (Exception e) {
					LOGGER.error(e.getLocalizedMessage());
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Could not be obtained the devices"));

				}
			});

			/** ZBGETDATA */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbgetdata/:cluster/:showunsupported", context);
			ignite.get("/zbgetdata", (request, response) -> {
				response.type("application/json");
				try {
					List<ZbGetDataJson> clusters = new ArrayList<>();
					String cluster = request.queryParams(CLUSTER);
					String showunsu = request.queryParams(SHOWUNSUPPORTED);
					if (showunsu == null) {
						showunsu = "false";
					}
					OctaByte macOb = new OctaByte(OctaByte.convertMac(request.queryParams("mac")));
					ZclDevice zdev = devicemanager.getDeviceByMac(macOb);
					if (zdev == null) {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Could not be obtained the device"));
					}
					ZclCluster zcl = zdev.getZclCluster(cluster);
					if (zcl == null) {
						// Intentamos con formato hexadecimal
						if (!cluster.startsWith("0x")) {
							cluster = "0x" + cluster;
						}

						zcl = zdev.getZclCluster(new DoubleByte(Integer.decode(cluster)));
						if (zcl == null) {
							response.status(HttpStatus.NOT_FOUND_404);
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Cluster not found on the device"));
						}
					}

					for (ZclAttribute zatt : zcl.getAttributes().values()) {
						ZbGetDataJson clusterinfo = new ZbGetDataJson();

						if (zatt.isUnsupported() && !showunsu.equalsIgnoreCase("true"))
							continue;

						if (zatt.getLastTimeUpdated() != null) {
							clusterinfo.setTimestamp((new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(zatt.getLastTimeUpdated())));
						} else {
							clusterinfo.setTimestamp("");
						}
						clusterinfo.setName(zatt.getName());
						clusterinfo.setAttribute(zatt.getId().toStr());
						clusterinfo.setNReports(zatt.getnReports() + "");
						clusterinfo.setBytes(zigbeeService.serializer(zatt.getBigEndianValue()));
						clusterinfo.setValue(zatt.getConvertedValue().toString());

						zdev.getEndpointByCluster(zcl.getId()).getId();
						DoubleByte maxReportTime = zatt.getMaxReportingTime(zdev.getZclEndpoint((byte) zdev.getEndpointByCluster(zcl.getId()).getId()).getDeviceId());
						DoubleByte minReportTime = zatt.getMinReportingTime(zdev.getZclEndpoint((byte) zdev.getEndpointByCluster(zcl.getId()).getId()).getDeviceId());
						if (minReportTime != null && maxReportTime != null) {
							clusterinfo.setMinTime2Report(Integer.toString(minReportTime.intValue()));
							clusterinfo.setMaxTime2Report(Integer.toString(maxReportTime.intValue()));
						}

						clusterinfo.setUnsupported(zatt.isUnsupported() + "");
						clusters.add(clusterinfo);
					}
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(clusters)));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
				}
			});

			/** MAC */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbmac", context);
			ignite.get("/zbmac", (request, response) -> {
				response.type("application/json");
				String mac = zigbeeService.getZbMac(true);
				if (mac != null) {
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(mac)));
				}
				response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
				return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
			});

			/** CHANNEL */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbgetchannel", context);
			ignite.get("/zbgetchannel", (request, response) -> {
				response.type("application/json");
				try {
					String channel = zigbeeService.getChannel(true);
					if (channel != null) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, channel));
						// return new Gson()
						// .toJsonTree(new StandardResponse(StatusResponse.SUCCESS, "hola"));
					} else {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The channel could not be obtained"));

					}
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}
			});

			/** ZBCOORINFO **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbcoorinfo", context);
			ignite.get("/zbcoorinfo", (request, response) -> {
				response.type("application/json");
				try {
					ZbCoordinatorJson coorinfo = zigbeeService.buildZbCoordinatorJson();
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(coorinfo)));

				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

			/** SHOWNETWORK */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbshownetwork", context);
			ignite.get("/zbshownetwork", (request, response) -> {
				response.type("application/json");

				try {
					ZclCoordinator coor = this.serialConnector.getZclcoor();
					ZFrame frame = new ZdoIeeeAddrReq(coor.getShortAddress(), true);
					this.serialConnector.getOutputSerial().writeZFrame(frame);
					zigbeeService.waitMs(2000);
					List<ZbShowNetwork> meshmap = new ArrayList<>();

					for (ZclDevice dev : devicemanager.devices.values()) {
						if (dev.isEndDevice() == null || !dev.isEndDevice()) {
							if (dev.getManufacturerName() == null || dev.getManufacturerName().equals("")) {
								ZclCluster basic = dev.getZclCluster(new DoubleByte(0));
								if (basic != null) {
									ZclAttribute manName = basic.getAttribute(new DoubleByte(0x4));
									if (manName != null && manName.isRspReceived()) {
										String manNameStr = (String) manName.getConvertedValue();
										dev.setManufacturerName(manNameStr);
									} else if (manName != null) {
										ArrayList<DoubleByte> manNameAr = new ArrayList<>();
										manNameAr.add(new DoubleByte(0x0004));
										ZFrame[] frames = basic.buildReadAttributes(manNameAr);
										this.serialConnector.getOutputSerial().writeZFrame(frames[0]);
									}
								}
							}
							ZFrame frame1 = new ZdoIeeeAddrReq(dev.getShortAddress(), true);
							this.serialConnector.getOutputSerial().writeZFrame(frame1);
							zigbeeService.waitMs(2000);
						}
					}
					zigbeeService.waitMs(6000);
					ZbShowNetwork coorparent = new ZbShowNetwork();
					// short add coor
					coorparent.setShortaddress(coor.getShortAddress().toString());
					// mac coor
					coorparent.setMac(coor.getIeeeAddress().toString());
					// manufacture coor
					coorparent.setManufacturer(coor.getManufacturerName());
					// dev type
					coorparent.setDeviceType(DeviceType.COORDINATOR.toString());
					// hijos coor
					List<Child> coorchilds = new ArrayList<>();
					if (coor.getChildren() != null) {
						// zbshownetwork
						coorchilds = zigbeeService.listChildDevices(coor.getChildren());
					}
					coorparent.setChildren(coorchilds);
					meshmap.add(coorparent);
					for (ZclDevice dev : devicemanager.devices.values()) {
						if (dev.isEndDevice() == null || !dev.isEndDevice()) {
							ZbShowNetwork devparent = new ZbShowNetwork();
							// devparent nwkaddr
							devparent.setShortaddress(dev.getShortAddress().toString());
							// dev parent mac
							devparent.setMac(dev.getIeeeAddress().toString());
							// devparent manufacturer
							devparent.setManufacturer(dev.getManufacturerName());
							// dev type
							devparent.setDeviceType(dev.getGeneralDeviceType().toString());
							List<Child> devchilds = new ArrayList<>();
							if (dev.getChildren() != null) {
								devchilds = zigbeeService.listChildDevices(dev.getChildren());
							}
							devparent.setChildren(devchilds);
							meshmap.add(devparent);
						}
					}

					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(meshmap)));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}
			});

			/** MESHVIEW */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbmeshview", context);
			ignite.get("/zbmeshview", (request, response) -> {
				response.type("application/json");
				try {
					List<ZbMeshView> mesh = new ArrayList<>();
					zigbeeService.createmesh();
					zigbeeService.meshview(mesh);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(mesh)));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
				}
			});

			/** READCOLOR */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbgetcolor/:mac", context);
			ignite.get("/zbreadcolor", (request, response) -> {
				response.type("application/json");
				String mac = request.queryParams(MAC);
				ZclCluster colorControlCl = zigbeeService.findClusterOnDevice(mac, ZClusterLibrary.ZCL_CLUSTER_ID_LIGHTING_COLOR_CONTROL);
				if (colorControlCl == null) {
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The device has not the lighting color control cluster"));
				}

				ArrayList<DoubleByte> atIds = new ArrayList<>();
				atIds.add(new DoubleByte(0x0000));
				atIds.add(new DoubleByte(0x0001));
				ZFrame[] frames = colorControlCl.buildReadAttributes(atIds);
				this.serialConnector.getOutputSerial().writeZFrames(frames);
				// aqui faltaria mostrar el color leido
				return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));

			});

			// ** ZBGETALERTS **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbgetalerts", context);
			ignite.get("/zbgetalerts", (request, response) -> {
				response.type("application/json");
				try {
					List<Alert> list = ZigbeeService.getAlertList();
					return new Gson().toJsonTree(list);
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
				}
			});

			/** THERMOSTATINFO */
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbthermostatinfo/:mac", context);
			ignite.get("/zbthermostatinfo", (request, response) -> {
				response.type("application/json");
				String mac = request.queryParams(MAC);
				if (mac == null) {
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Invalid mac"));
				}
				try {
					AttributesJson atts = zigbeeService.buildThermostatValues(mac);
					if (atts == null) {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Device has not the info requested"));
					}
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(atts)));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
				}
			});

			/** ZBWRCHECKIN **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET {}/zbreadpollcontrol/:mac:/id", context);
			ignite.get("/zbreadpollcontrol", (request, response) -> {
				response.type("application/json");
				try {
					String mac = request.queryParams(MAC);
					DoubleByte id = new DoubleByte(Integer.parseInt(request.queryParams(ID)));
					zigbeeService.pollControlManagement(mac, CommandType.READ, id, null, null);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));

					// } catch (GenericZigbeeException e) {
					// response.status(HttpStatus.NOT_FOUND_404);
					// return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR,
					// e.getMessage()));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

			/** GRAPH **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/graph/", context);
			ignite.put("/graph", (request, response) -> {
				response.type("application/json");

				try {
					Graph graph = new Graph();
					QueryForm queryForm = new Gson().fromJson(request.body(), QueryForm.class);
					// queryForm.setUTCTimezone();
					String result = zigbeeDbService.getGraphValues(queryForm);
					LOGGER.info("Resultado: " + result);
					if (result != null) {
						DbResult dbResult = new Gson().fromJson(result, DbResult.class);
						DbSeries listSeries = dbResult.getSeries().get(0);
						List<List<String>> values = listSeries.getValues();

						List<Series> seriesList = new ArrayList<>();

						for (int i = 0; i < values.size(); i++) {
							List<String> list = values.get(i);
							if (values.size() < 10 || (i % (values.size() / 10) == 0)) {
								graph.getLabels().add(zigbeeService.getTimeSystemTimeZone(list.get(0)));
							} else {
								graph.getLabels().add("");
							}
							Series serie = new Series(zigbeeService.getTimeSystemTimeZone(list.get(0)), list.get(1));
							seriesList.add(serie);
						}
						graph.getSeries().add(seriesList);
						return new Gson().toJsonTree(graph);
					} else {
						return new Gson().toJsonTree("NODB");
					}
				} catch (Exception e) {
					return new Gson().toJsonTree(null);
				}
			});

			/** IDENTIFY */
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbidentify/:mac/:time", context);
			ignite.put("/zbidentify", (request, response) -> {
				response.type("application/json");
				try {
					String mac = request.queryParams(MAC);
					String time = request.queryParams(TIME);
					if (mac == null) {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Invalid mac"));
					}
					if (time == null) {
						time = "8";
					}
					zigbeeService.identifyDevice(mac, time);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));

				} catch (GenericZigbeeException e) {
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

			/** ZBWRCHECKIN **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbsetpollcontrol/:mac:/interval/:id", context);
			ignite.put("/zbsetpollcontrol", (request, response) -> {
				response.type("application/json");
				try {
					String mac = request.queryParams(MAC);
					String interval = request.queryParams(INTERVAL);
					String att = request.queryParams(ID);
					String body = request.body();
					DoubleByte id = new DoubleByte(Integer.parseInt(att));
					if (body.contains("write")) {
						zigbeeService.pollControlManagement(mac, CommandType.WRITE, id, interval, null);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
					} else if (body.contains("command")) {
						long[] cmdParams = { Long.parseLong(interval) };
						zigbeeService.pollControlManagement(mac, CommandType.POLLCOMMAND, id, interval, cmdParams);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
					}
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));

					// } catch (GenericZigbeeException e) {
					// response.status(HttpStatus.NOT_FOUND_404);
					// return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR,
					// e.getMessage()));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

			/** ZBOTAUPGRADE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbotaupgrade/:mac/:action/:filepath", context);
			ignite.put("/zbotaupgrade", (request, response) -> {
				response.type("application/json");
				OutputSerialZigbee out = serialConnector.getOutputSerial();
				ZclCoordinator coor = serialConnector.getZclcoor();
				String mac = request.queryParams(MAC);
				String action = request.queryParams(ACTION);
				String filepath = request.queryParams(FILEPATH);
				try {

					out.writeZFrame(new ZdoActiveEpReq(new DoubleByte(0x0000), new DoubleByte(0x0000)));
					zigbeeService.waitMs(1000);
					boolean otaEndPoint = coor.hasOtaEndPoint();
					if (otaEndPoint) {
						boolean found = false;
						int pos = 0;
						ZclDevice device = devicemanager.getDeviceByMac(new OctaByte(OctaByte.convertMac(mac)));
						OtaManager otamng = device.getOtamanager();
						for (int i = 0; i < OPTIONS.length && !found; i++) {
							if (action.toLowerCase().equals(OPTIONS[i])) {
								found = true;
								pos = i;
							}
						}
						if (found) {
							switch (pos) {
							case 0:
								if (otamng.isOta()) {
									if (otamng.isInstalling()) {
										return new Gson().toJsonTree(
												new StandardResponse(StatusResponse.SUCCESS, "Status: Dev " + device.getIeeeAddress().toString() + " is installing the image"));
									} else {
										long porcentaje = otamng.getPorcentaje();
										return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS,
												"Status: Downloading. Progress: " + porcentaje + "% in dev: " + device.getIeeeAddress().toString()));
									}
								} else {
									return new Gson().toJsonTree(
											new StandardResponse(StatusResponse.SUCCESS, "Status: Dev " + device.getIeeeAddress().toString() + " is not in OTA process"));

								}
							case 1:
								otamng.setAbort(true);
								zigbeeService.waitMs(3000);
								if (otamng.isOtaRequest()) {
									if (!otamng.isOta()) {
										otamng.setOtaRequest(false);
										return new Gson()
												.toJsonTree(new StandardResponse(StatusResponse.SUCCESS, "Download aborted in dev: " + device.getIeeeAddress().toString()));
									} else {
										ImageBlockResponse imBlockRsp = new ImageBlockResponse(ZClusterLibrary.ZCL_STATUS_OTA_ABORT);
										int[] zclFrame = zigbeeService.buildZclFrame(true, 0, ZClusterLibrary.ZCL_OTA_IMAGE_BLOCK_RSP, imBlockRsp.getFrame());

										ZFrame zframe = new AfDataRequest(device.getShortAddress(), device.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA).getId(),
												ZigbeeConstants.COORDINATOR_OTA_ENDPOINT, ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0, zclFrame);
										device.getOtamanager().setAbort(false);
										device.getOtamanager().setInstalling(false);
										device.getOtamanager().setOta(false);
										out.writeZFrame(zframe);
										return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, "The download could not be aborted. Forcing the download stop"));
									}
								} else {
									return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The device is not in OTA process yet"));
								}
							case 2:
								// lo primero es comprobar que el dispositivo no se encuentra actualizandose
								if (!device.getOtamanager().isOta()) {
									// lo segundo es comprobar si el fichero existe o no
									File fichero = new File(filepath);

									if (fichero.exists()) {
										// tamanio del fichero en bytes
										long tamanio = fichero.length();
										FourByte fileSize = new FourByte((int) tamanio);
										device.getOtamanager().setNewImageSize(fileSize);
										// LOG.info("El tamanio es de: "+tamanio+"
										// bytes");
										BufferedInputStream bufferlectura = new BufferedInputStream(new FileInputStream(filepath));
										byte[] buffer = new byte[BUFFER_SIZE];
										// bufferlectura.read(buffer, 0, buffer.length);

										if (bufferlectura.skip(OFFSET_BYTES_FILEVERSION) > 0) {
											bufferlectura.read(buffer, 0, buffer.length);

											/*
											 * StringBuilder sb = new StringBuilder(); for (byte b : buffer) {
											 * sb.append(String.format("%02X ", b)); } System.out.println(sb.toString());
											 */
											FourByte fileversion = new FourByte(buffer);
											device.getOtamanager().setVersion2upgrade(fileversion);
											bufferlectura.close();

											// establecemos la ruta del fichero
											device.getOtamanager().setFilePath(filepath);
											// enviamos el msg de notificacion
											ImageNotify imNotify = new ImageNotify(PAYLOAD_TYPE, QUERY_JITTER, null, null, null);
											int[] frame = zigbeeService.buildZclFrame(true, 0, ZClusterLibrary.ZCL_OTA_IMAGE_NOTIFY, imNotify.getFrame());

											ZclEndpoint ep = device.getEndpointByCluster(ZClusterLibrary.ZCL_CLUSTER_ID_OTA);
											if (ep == null) {
												return new Gson().toJsonTree(
														new StandardResponse(StatusResponse.ERROR, "Cluster OTA not found on device: " + device.getIeeeAddress().toString()));
											} else {
												ZFrame zframe = new AfDataRequest(device.getShortAddress(), ep.getId(), ZigbeeConstants.COORDINATOR_OTA_ENDPOINT,
														ZClusterLibrary.ZCL_CLUSTER_ID_OTA, (byte) 0, 0, 0, frame);
												device.getOtamanager().setOtaRequest(true);

												out.writeZFrame(zframe);
												if (zigbeeService.checkLaunchOTA(5000)) {
													return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, "OTA request launched"));
												} else {
													return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Device with last version"));
												}
											}
										} else {
											bufferlectura.close();
											return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Error skiping n bytes in file"));
										}

									} else {
										// fichero no existe
										return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "File does not exist"));
									}
								} else {
									// el dispositivo esta en ota
									return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Device is already in OTA process"));
								}
							default:

								break;
							}
						} else {
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Incorrect option. Valid values: <status/abort/upgrade>"));
						}
					} else {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The coordinator has not the OTA end point"));
					}
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
				}
				response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
				return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));

			});

			/** ZBTOGGLE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbtoggle/:mac", context);
			ignite.put("/zbtoggle", (request, response) -> {
				try {
					response.type("application/json");
					String mac = request.queryParams(MAC);
					ZclCluster onOff = zigbeeService.findClusterOnDevice(mac, ZClusterLibrary.ZCL_CLUSTER_ID_GEN_ON_OFF);
					if (onOff == null) {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The has not the ON/OFF cluster"));
					}
					zigbeeService.toggleDevice(mac);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
				} catch (Exception e) {
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}
			});
			/** ZBSETPOINT **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbsetpoint/:mac/:temperature", context);
			ignite.put("/zbsetpoint", (request, response) -> {
				try {
					String mac = request.queryParams(MAC);
					String temp = request.queryParams(TEMPERATURE);
					response.type("application/json");
					boolean success = zigbeeService.setSetPoint(mac, temp);
					if (success) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
					} else {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
					}
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

			/** ZBSETMODE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbsetmode/:mac/:mode", context);
			ignite.put("/zbsetmode", (request, response) -> {

				try {
					response.type("application/json");
					String mac = request.queryParams(MAC);
					String mode = request.queryParams(MODE);
					if (zigbeeService.setMode(mac, mode)) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
					}
					response.status(HttpStatus.NOT_FOUND_404);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}
			});

			/** ZBCHANGESELECTEDCHANNEL **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbchangeselectedchannel/:channel", context);
			ignite.put("/zbchangeselectedchannel", (request, response) -> {
				try {
					boolean channelChanged;
					String channel = request.queryParams(CHANNEL);
					response.type("application/json");
					if (channel != null) {
						byte ch = Byte.parseByte(channel);
						channelChanged = this.serialConnector.getZclcoor().changeChannel(ch);
					} else {
						channelChanged = this.serialConnector.getZclcoor().changeChannel();
					}
					if (channelChanged) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
					} else if (channel != null) {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Specified channel isn't valid"));
					} else {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, ("New channel not selected yet")));

					}
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}
			});
			/** ZBCONFIGREPORT **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbconfigreport/:mac/:cluster/:attribute/:mintime/:maxtime", context);
			ignite.put("/zbconfigreport", (request, response) -> {
				try {
					response.type("application/json");
					ReturningValues jsonresponse;
					String mac = request.queryParams(MAC);
					String cluster = request.queryParams(CLUSTER);
					String att = request.queryParams(ATTRIBUTE);
					String minTime = request.queryParams(MINTIME);
					String maxTime = request.queryParams(MAXTIME);

					if (cluster != null && att != null && minTime != null && maxTime != null) {
						jsonresponse = zigbeeService.sendCommand(CommandWeb.CONFIGREPORT, mac, cluster, att, minTime, maxTime);
						if (jsonresponse == null) {
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
						} else {
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(jsonresponse.getConfigReport())));
						}
					} else {
						jsonresponse = zigbeeService.sendCommand(CommandWeb.CONFIGREPORT, mac);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(jsonresponse.getConfigReport())));
					}
				} catch (GenericZigbeeException e) {
					response.status(HttpStatus.NOT_FOUND_404);
					LOGGER.error(e.getMessage());
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

			/** ZBSENDCLUSTERCOMMAND **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zbconfigreport/:mac/:cluster/:commandid", context);
			ignite.put("/zbsendclustercommand", (request, response) -> {

				try {
					response.type("application/json");
					String mac = request.queryParams(MAC);
					String cluster = request.queryParams(CLUSTER);
					String commandId = request.queryParams(COMMANDID);
					ZclDevice zdev = devicemanager.getDeviceByMac(new OctaByte(OctaByte.convertMac(mac)));
					request.queryParams();
					if (zdev == null) {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
					}
					ZclCluster zcl = zdev.getZclCluster(CLUSTER);
					if (zcl == null) {
						// Intentamos con formato hexadecimal
						if (!cluster.startsWith("0x")) {
							cluster = "0x" + cluster;
						}
						zcl = zdev.getZclCluster(new DoubleByte(Integer.decode(cluster)));
						if (zcl == null) {
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Cluster not found in device"));
						}
					}
					long[] cmdParams = null;
					if ((request.queryParams().size() - 3) > 0) {
						cmdParams = new long[request.queryParams().size() - 3];

						// Recorremos el hashMap y mostramos por pantalla el par valor y clave
						int i = 0;
						for (String entry : request.queryParams()) {
							String key = entry;
							if (i >= 3) {
								cmdParams[i++] = Long.parseLong(key);
							}

						}
					}

					if (!commandId.startsWith("0x")) {
						commandId = "0x" + commandId;
					}
					ZFrame zf = zcl.buildCmd(Byte.decode(commandId), cmdParams);
					if (zf == null) {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR));
					}
					this.serialConnector.getOutputSerial().writeZFrame(zf);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}
			});

			/** ZBONOFF **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[PUT {}/zonoff/:mac/:status", context);
			ignite.put("/zbonoff", (request, response) -> {
				response.type("application/json");
				String mac = request.queryParams(MAC);
				String status = request.queryParams(STATUS);
				try {
					if (zigbeeService.meterOnOff(mac, status)) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS));
					} else {
						response.status(HttpStatus.NOT_FOUND_404);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "The device could not change"));
					}
				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}
			});

			/** ZBREMOVEDEVICE **/
			LOGGER.info("SPARK API_CONTEXT_PATH :[DELETE {}/zbremovedevice/:mac", context);
			ignite.delete("/zbremovedevice", (request, response) -> {
				// ignite.delete("/zbremovedevice/:mac", (request, response) -> {
				try {
					response.type("application/json");
					String mac = request.queryParams(MAC);
					try {
						boolean result = zigbeeService.deleteDevice(mac);
						if (!result) {
							response.status(HttpStatus.NO_CONTENT_204);
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Device not found " + mac));
						} else {
							return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, 0, "Device removed succesfully"));
						}
					} catch (NumberFormatException e) {
						response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, "Device " + mac + " error"));
					} catch (GenericZigbeeException ex) {
						return new Gson().toJsonTree(new StandardResponse(StatusResponse.SUCCESS, ex.getExceptionCause().getErrorCode(), ex.getExceptionCause().getCause()));
					}

				} catch (Exception e) {
					response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
					return new Gson().toJsonTree(new StandardResponse(StatusResponse.ERROR, e.getMessage()));
				}

			});

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

}
