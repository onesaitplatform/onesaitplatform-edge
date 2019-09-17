package com.onesait.edge.engine.zigbee;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@EnableScheduling
public class ZigbeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZigbeeApplication.class, args);
	}
}
