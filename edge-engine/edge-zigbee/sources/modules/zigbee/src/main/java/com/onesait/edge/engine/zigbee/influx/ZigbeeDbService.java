package com.onesait.edge.engine.zigbee.influx;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.onesait.edge.engine.zigbee.influxdb.json.QueryForm;
import com.onesait.edge.engine.zigbee.util.GlobalConstants;

@Service
public class ZigbeeDbService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZigbeeDbService.class);

	public String getGraphValues(QueryForm form) throws NoSuchFieldException {

		try {
			InfluxDB influxDB = InfluxDBUtils.getConnection();
			String dbName = Configuration.getInstance().getProperty(GlobalConstants.DataBase.PERSISTENCE_DB_NAME);
			Query query = null;
			List<Result> res = new ArrayList<>();

			try {
				query = new Query(this.getQuery(form), dbName);

				res = influxDB.query(query).getResults();
				if (res.get(0).getError() != null) {
					LOGGER.error(Configuration.getInstance().getProperty(GlobalConstants.MessagesKey.QUERY_ERROR), res.get(0).getError());
				}
				LOGGER.info("RESULT: {}", new Gson().toJsonTree(res.get(0)));
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			} finally {
				influxDB.close();
			}
			return String.valueOf(new Gson().toJsonTree(res.get(0)));
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
		}else {
			strBuilder.append("SELECT \"value\" ");
		}

		strBuilder.append("FROM \"").append(form.getDevice()).append("\" ").append("WHERE ");

		if (form.getAttribute() != null && !form.getAttribute().isEmpty()) {
			strBuilder.append("(\"signalId\" = '").append(form.getAttribute()).append("') ").append("AND ");
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

		if (!form.getGroupBy().equalsIgnoreCase("N/A")) {
			strBuilder.append("GROUP BY ").append("time(").append(form.getGroupBy()).append(") fill(0)");
		}

		LOGGER.info("QUERY: {}", strBuilder);
		return strBuilder.toString();
	}
}
