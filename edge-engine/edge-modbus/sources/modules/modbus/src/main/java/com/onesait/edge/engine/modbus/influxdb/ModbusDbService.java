package com.onesait.edge.engine.modbus.influxdb;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.onesait.edge.engine.modbus.conf.GlobalConstants;
import com.onesait.edge.engine.modbus.influxdb.json.QueryForm;

@Service
public class ModbusDbService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModbusDbService.class);
	
	@Value("${com.onesait.edge.engine.apidb.influx.path}")
	private String path;
	@Value("${com.onesait.edge.engine.apidb.influx.port}")
	private String port;
	@Value("${com.onesait.edge.engine.apidb.influx.name}")
	private String name;
	@Value("${com.onesait.edge.engine.apidb.influx.user}")
	private String user;
	@Value("${com.onesait.edge.engine.apidb.influx.pwd}")
	private String pwd;

	public String getGraphValues(QueryForm form) throws NoSuchFieldException {

		try {
			InfluxDB influxDB = InfluxDBUtils.getConnection(path, port, user, pwd);
			Query query = null;
			String result = null;

			List<Result> res = new ArrayList<>();

			try {
				query = new Query(this.getQuery(form), name);

				res = influxDB.query(query).getResults();
				if (res.get(0).getError() != null) {
					LOGGER.error(Configuration.getInstance().getProperty(GlobalConstants.MessagesKey.QUERY_ERROR), res.get(0).getError());
				}
				LOGGER.info("RESULT: {}", new Gson().toJsonTree(res.get(0)));

			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			} finally {
				influxDB.close();
			}
			return new Gson().toJsonTree(res.get(0)).toString();
		} catch (Exception e) {
			LOGGER.error("No database access");
			return null;
		}
	}

	@PreDestroy
	public void destroy() {
		InfluxDBUtils.closeConnection();
	}

	private String getQuery(QueryForm form) throws NoSuchFieldException {

		StringBuilder strBuilder = new StringBuilder();
		
		if(form.getArgument().equalsIgnoreCase("mean")) {
			strBuilder.append("SELECT mean(\"value\") ");
		}else if(form.getArgument().equalsIgnoreCase("count")) {
			strBuilder.append("SELECT count(\"value\") ");
		}

		strBuilder.append("FROM \"").append(form.getDeviceId()).append("\" ").append("WHERE ");

		if (form.getSignalId() != null && !form.getSignalId().isEmpty()) {
			strBuilder.append("(\"signalId\" = '").append(form.getSignalId()).append("') ").append("AND ");
		}

		if (form.getInitDate() != null) {
			Long dateBegin = form.getInitDate();
			strBuilder.append("time >= ").append(dateBegin).append("ms ");
		}

		if (form.getEndDate() != null) {
			Long dateEnd = form.getEndDate();
			Long allDay = (long) 86399999; //  23,999999722 horas
			strBuilder.append("AND time <= ").append(dateEnd + allDay).append("ms ");

		}

		if (form.getGroupBy() != null && !form.getGroupBy().isEmpty()) {
			strBuilder.append("GROUP BY ").append("time(").append(form.getGroupBy()).append(") fill(0)");
		}

		LOGGER.info("QUERY: {}", strBuilder.toString());
		return strBuilder.toString();
	}
}