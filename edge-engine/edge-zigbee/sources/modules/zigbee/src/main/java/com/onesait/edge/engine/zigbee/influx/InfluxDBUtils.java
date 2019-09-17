package com.onesait.edge.engine.zigbee.influx;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.util.GlobalConstants;

public class InfluxDBUtils{
	
	

	private InfluxDBUtils(){
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBUtils.class);
	private static  ThreadLocal<InfluxDB> threadLocal;
	private static  String path;
	private static  String port;
	private static  String url;
	private static  String user;
	private static  String password;
	
	public static InfluxDB getConnection() throws Exception{
		
		path = Configuration.getInstance().getProperty(GlobalConstants.DataBase.PERSISTENCE_PATH);
		port = Configuration.getInstance().getProperty(GlobalConstants.DataBase.PERSISTENCE_PORT);
		user = Configuration.getInstance().getProperty(GlobalConstants.DataBase.PERSISTENCE_USER);
		password = Configuration.getInstance().getProperty(GlobalConstants.DataBase.PERSISTENCE_PASS);
		url = "http://" + path + ":" + port;
		
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
		InfluxDB conn = threadLocal.get();
		threadLocal.set(null);
		
		if (conn != null)
			conn.close();
	}
	
	public static Boolean isConnected() {
		Boolean isConnected = Boolean.FALSE;
		InfluxDB conn = threadLocal.get();
		if (conn != null) {
			Pong response = conn.ping();
			if (!GlobalConstants.DataBase.UNKNOWN_DB.equalsIgnoreCase(response.getVersion())) {
				isConnected=Boolean.TRUE;
			}
		}
		return isConnected;		
	}
}
