package com.onesait.edge.engine.modbus.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.modbus.conf.GlobalConstants;

public class InfluxDBUtils {

	private InfluxDBUtils() {
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBUtils.class);
	private static ThreadLocal<InfluxDB> threadLocal;

	public static InfluxDB getConnection(String path, String port, String user, String password) throws Exception {

		String url = "http://" + path + ":" + port;

		LOGGER.info(Configuration.getInstance().getProperty(GlobalConstants.DataBase.GETTING_CONNECTION_DB), path);
		InfluxDB influxConn = InfluxDBFactory.connect(url, user, password);

		threadLocal = new ThreadLocal<InfluxDB>();
		threadLocal.set(influxConn);

		InfluxDB conn = threadLocal.get();
		if (conn == null) {
			conn = InfluxDBFactory.connect(url, user, password);
			threadLocal.set(conn);
		}
		return conn;
	}

	public static void closeConnection() {
		LOGGER.info("Closing InfluxDb connections...");
		if (threadLocal != null) {
			InfluxDB conn = threadLocal.get();
			threadLocal.set(null);

			if (conn != null)
				conn.close();
		}
	}

	public static Boolean isConnected() {
		Boolean isConnected = Boolean.FALSE;
		InfluxDB conn = threadLocal.get();
		if (conn != null) {
			Pong response = conn.ping();
			if (!GlobalConstants.DataBase.UNKNOWN_DB.equalsIgnoreCase(response.getVersion())) {
				isConnected = Boolean.TRUE;
			}
		}
		return isConnected;
	}
}
