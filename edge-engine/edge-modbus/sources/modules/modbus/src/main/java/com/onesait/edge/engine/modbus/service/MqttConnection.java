package com.onesait.edge.engine.modbus.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MqttConnection implements MqttCallback {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttConnection.class);
	private static final Integer KEEPALIVE = 30;
	
	public static final String CLIENT_ID = "Mqtt-Modbus";

	@Value("${mqtt.topic.signals.output}")
	private String topicSignalsOut;

	@Value("${mqtt.topic.signals.input}")
	private String topicSignalsIn;
	
	private MqttClient mqttClient;
	private MqttTopic topicSignalIn;

	@Value("${mqtt.connection.host}")
	private String mqttHost;

	@Value("${mqtt.connection.port}")
	private String mqttPort;

	public MqttClient getMqttClient() {
		return mqttClient;
	}

	public MqttTopic getTopicSignalIn() {
		return topicSignalIn;
	}

	public String getTopicSignalsOut() {
		return topicSignalsOut;
	}

	public String getTopicSignalsIn() {
		return topicSignalsIn;
	}

	@Scheduled(fixedRate = 10000)
	public void connect() {
		if (mqttClient == null || !mqttClient.isConnected()) {
			
			String brokerUrl;
			brokerUrl = "tcp://" + mqttHost + ":" + mqttPort;
			LOGGER.info("Broker not found!! mqttHost: {}", mqttHost);
			LOGGER.info("Broker not found!! mqttPort: {}", mqttPort);

			try {

				// Delete connection files
				LOGGER.info("MQTT Cleaning temporal files : {}", this.cleanFiles());

				MqttConnectOptions connOpt = new MqttConnectOptions();
				connOpt.setCleanSession(true);
				connOpt.setKeepAliveInterval(KEEPALIVE);

				String clientId = CLIENT_ID + "-" + new Date().getTime();
				mqttClient = new MqttClient(brokerUrl, clientId);
				mqttClient.setCallback(this);
				mqttClient.connect(connOpt);

				topicSignalIn = mqttClient.getTopic(topicSignalsIn);

				LOGGER.info("MQTT CONNECTION READY -->  {} with clientId: {}", brokerUrl, clientId);
			} catch (Exception e) {
				LOGGER.error("MQTT CONNECTION ERROR: {}; CAUSE: {}", brokerUrl, e.getMessage());
			}
		}
	}

	private boolean cleanFiles() {
		
		Boolean result = Boolean.FALSE;
		
		try {
			File f = new File("./");
			String fileToDelete = CLIENT_ID;

			File[] ficheros = f.listFiles();
			for (int x = 0; x < ficheros.length; x++) {
				if (ficheros[x].getName().startsWith(fileToDelete)) {
					this.deleteFolder(ficheros[x]);
				}
			}
			result = Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error("cleanFiles: {}", e.getMessage());
		}
		return result;
	}

	private void deleteFolder(File fileDel) {
		
		try {
			if (fileDel.isDirectory()) {
				if (fileDel.list().length == 0) {
					Files.delete(Paths.get(fileDel.getAbsolutePath()));
				}else {
					for (String temp : fileDel.list()) {
						File fileDelete = new File(fileDel, temp);
						// recursive delete
						deleteFolder(fileDelete);
					}
					// check the directory again, if empty then delete it
					if (fileDel.list().length == 0) {
						Files.delete(Paths.get(fileDel.getAbsolutePath()));
					}
				}
			} else {
				// if file, then delete it
				Files.delete(Paths.get(fileDel.getAbsolutePath()));
			}
		}catch (IOException e) {
			LOGGER.error("Error while trying to delete the folder {}: {}",fileDel.getAbsoluteFile(), e.getMessage());
		}
	}

	public void sendSignal(String msg, String topic) {
		if (this.getMqttClient() != null && this.getMqttClient().isConnected()) {
			// Mandar datos por mqtt
			MqttMessage message = new MqttMessage(msg.getBytes());
			message.setQos(1);
			message.setRetained(false);
			// Publish the message
			try {
				if (topic.equalsIgnoreCase(this.topicSignalsIn)) {
					this.getTopicSignalIn().publish(message);
				}
			} catch (Exception e) {
				LOGGER.error("sendToMqtt(String json, String topic:{}", e.getMessage());
			}
		}
	}

	public void sendSignal(List<String> msg, String topic) {
		if (this.getMqttClient() != null && this.getMqttClient().isConnected()) {
			// Mandar datos por mqtt
//			LOGGER.info(msg.toString());
			MqttMessage message = new MqttMessage(msg.toString().getBytes());
			message.setQos(1);
			message.setRetained(false);
			// Publish the message
			try {
				if (topic.equalsIgnoreCase(this.topicSignalsIn)) {
					this.getTopicSignalIn().publish(message);
				}
			} catch (Exception e) {
				LOGGER.error("sendToMqtt(String json, String topic:{}", e.getMessage());
			}
		}
	}

	@Override
	public void connectionLost(Throwable arg0) {
		LOGGER.info("CONNECTION LOST: {}", arg0);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// Do nothing 
	}

	@Override
	public void messageArrived(String arg0, MqttMessage msg) {
		// Do nothing 
	}
}
