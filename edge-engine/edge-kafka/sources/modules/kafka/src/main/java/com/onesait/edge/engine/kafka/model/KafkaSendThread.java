package com.onesait.edge.engine.kafka.model;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.kafka.service.KafkaConnection;

public class KafkaSendThread extends Thread{
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaSendThread.class);
	
	private KafkaConnection kafkaConnection;
	private MqttMessage msg;
	
	public KafkaSendThread(KafkaConnection kafkaConnection, MqttMessage msg) {
		this.kafkaConnection = kafkaConnection;
		this.msg = msg;
	}

	@Override
	public void run() {
		try{
			kafkaConnection.incrementCounter();
			kafkaConnection.send(msg);
		} catch (IOException e) {
			LOG.error("Mqtt message has not been send by kafka: {}",e.getLocalizedMessage());
		}
	}
}
