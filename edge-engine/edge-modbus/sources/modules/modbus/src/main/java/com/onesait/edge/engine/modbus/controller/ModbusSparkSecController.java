package com.onesait.edge.engine.modbus.controller;

import java.io.File;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.onesait.edge.engine.modbus.service.ModbusAPIService;
import com.onesait.edge.engine.modbus.swagger.RouteBuilder;
import com.onesait.edge.engine.modbus.swagger.SwaggerParser;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import spark.Service;

@SwaggerDefinition(host = "localhost:4443", //
		info = @Info(description = "Modbus API", //
				version = "V1.0", //
				title = "Modbus API", //
				contact = @Contact(name = "Javier Artal", url = "https://www.minsait.com/es")), //
		schemes = { SwaggerDefinition.Scheme.HTTPS }, //
		consumes = { "application/json" }, //
		produces = { "application/json" }, //
		tags = {@Tag(name = "Modbus"), @Tag(name = "Devices"), @Tag(name = "Signals"), @Tag(name = "Commands")})

@Controller
public class ModbusSparkSecController {

	public static final Logger LOGGER = LoggerFactory.getLogger(ModbusSparkSecController.class);

	@Autowired
	private ModbusAPIService modbusApiService;

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

	public static final String APP_PACKAGE = "com.onesait.edge.engine.modbus";

	@PostConstruct
	public void init() {

		LOGGER.info("INIT :-)");
		Service ignite = Service.ignite();
		ignite.port(sparkSecPort);
		ignite.staticFileLocation("/vue");
		if (sslEnabled) {			
		    // Eclipse default location
			URL url = getClass().getClassLoader().getResource(keystore);
			// Dockerfile configuration; SERVER_SSL_KEY_STORE is declared into container
			File file = new File(keystore);
			if (url !=null) {
				// If file is not present, it could be into classpath (SERVER_SSL_KEY_STORE is not declared) 
				file = new File(url.getFile());
			}
		    ignite.secure(file.getAbsolutePath(), password, file.getAbsolutePath(), storePassword);				
			
		}
		modbusApiService.api(ignite);
		// testPassword();

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
