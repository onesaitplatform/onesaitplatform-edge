package com.onesait.edge.engine.modbus;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@EnableScheduling
public class ModbusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModbusApplication.class, args);
	}
}
