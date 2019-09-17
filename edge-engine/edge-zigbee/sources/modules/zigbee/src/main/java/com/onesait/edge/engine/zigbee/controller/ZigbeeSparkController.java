package com.onesait.edge.engine.zigbee.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.onesait.edge.engine.zigbee.service.ZigbeeAPIService;

import spark.Service;

@Controller
public class ZigbeeSparkController {

	public static final Logger LOGGER = LoggerFactory.getLogger(ZigbeeSparkController.class);

	@Autowired
	private ZigbeeAPIService zigbeeAPIService;

	@Value("${spark.port}")
	private Integer sparkPort;

	@PostConstruct
	public void init() {
		Service ignite = Service.ignite().port(sparkPort);
		zigbeeAPIService.api(ignite);
	}
}
