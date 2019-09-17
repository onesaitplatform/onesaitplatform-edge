package com.onesait.edge.engine.modbus.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.onesait.edge.engine.modbus.service.ModbusAPIService;

import spark.Service;

@Controller
public class ModbusSparkController {

	public static final Logger LOGGER = LoggerFactory.getLogger(ModbusSparkController.class);

	@Autowired
	private ModbusAPIService modbusApiService;

	@Value("${spark.port}")
	private Integer sparkPort;
	
	public static final String APP_PACKAGE = "com.onesait.edge.engine.modbus";

	@PostConstruct
	public void init() {
		Service ignite = Service.ignite().port(sparkPort);
		modbusApiService.api(ignite);
	}
}
