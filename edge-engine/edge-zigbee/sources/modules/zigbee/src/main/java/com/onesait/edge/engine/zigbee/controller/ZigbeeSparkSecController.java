package com.onesait.edge.engine.zigbee.controller;

import java.io.File;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.onesait.edge.engine.zigbee.service.ZigbeeAPIService;
import com.onesait.edge.engine.zigbee.swagger.RouteBuilder;
import com.onesait.edge.engine.zigbee.swagger.SwaggerParser;

import spark.Service;

@Controller
public class ZigbeeSparkSecController {

	public static final Logger LOGGER = LoggerFactory.getLogger(ZigbeeSparkSecController.class);

	@Autowired
	private ZigbeeAPIService zigbeeAPIService;

	@Value("${spark.secure.port}")
	private Integer sparkSecPort;
	@Value("${server.ssl.enabled}")
	private Boolean sslEnabled;
	@Value("${server.ssl.key-store}")
	private String keystore;
	@Value("${server.ssl.key-password}")
	private String password;
	@Value("${server.ssl.key-store-password}")
	private String storePassword;

	public static final String APP_PACKAGE = "com.onesait.edge.engine.zigbee";

	@PostConstruct
	public void init() {

		Service ignite = Service.ignite();
		ignite.port(sparkSecPort);
		ignite.staticFileLocation("/vue/zigbee");
		if (sslEnabled) {
			// Eclipse default location
			URL url = getClass().getClassLoader().getResource(keystore);
			// Dockerfile configuration; SERVER_SSL_KEY_STORE is declared into container
			File file = new File(keystore);
			if (url != null) {
				// If file is not present, it could be into classpath (SERVER_SSL_KEY_STORE is
				// not declared)
				file = new File(url.getFile());
			}
			ignite.secure(file.getAbsolutePath(), password, file.getAbsolutePath(), storePassword);
		}
		zigbeeAPIService.api(ignite);

		try {

			// Scan classes with @Api annotation and add as routes
			RouteBuilder.setupRoutes(APP_PACKAGE);

			// Build swagger json description
			final String swaggerJson = SwaggerParser.getSwaggerJson(APP_PACKAGE);
			LOGGER.info("SPARK API_CONTEXT_PATH :[GET /swagger]");
			ignite.get("/swagger", (req, res) -> {
				return swaggerJson;
			});

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
