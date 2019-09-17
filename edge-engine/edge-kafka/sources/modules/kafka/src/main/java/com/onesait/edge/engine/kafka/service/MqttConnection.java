package com.onesait.edge.engine.kafka.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import javax.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.onesait.edge.engine.kafka.model.KafkaSendThread;

@Service
public class MqttConnection implements MqttCallback {

	private static final Logger LOG = LoggerFactory.getLogger(MqttConnection.class);

	public static final String CLIENT_ID = "mqttkafka";
	private static final Integer KEEP_ALIVE = 30;
	private static final Integer QOS = 1;
	@Value("${mqtt.topic}")	
	private String mqttTopic;
	@Value("${mqtt.ip}")	
	private String mqttIp;
	@Value("${mqtt.port}")	
	private Integer mqttPort;

	private MqttClient mqttClient;

	@Autowired
	KafkaConnection kafkaConnection;

	public MqttClient getMqttClient() {
		return mqttClient;
	}

	@Scheduled(fixedRate = 10000)
	public void connect() {

		String clientId;

		if (mqttClient == null || !mqttClient.isConnected()) {

			String brokerUrl = "tcp://" + mqttIp + ":" + mqttPort;
			try {
				LOG.info("MQTT connecting...");
				LOG.info("MQTT Cleaning temporal files : {}", this.cleanFiles());

				MqttConnectOptions connOpt = new MqttConnectOptions();
				connOpt.setCleanSession(true);
				connOpt.setKeepAliveInterval(KEEP_ALIVE);
				clientId = CLIENT_ID + "-" + new Date().getTime();
				mqttClient = new MqttClient(brokerUrl, clientId);
				mqttClient.setCallback(this);
				mqttClient.connect(connOpt);
				mqttClient.subscribe(mqttTopic, QOS);
				LOG.info("MQTT CONNECTION READY -->  {} with clientId: {}; suscribe to: {}", brokerUrl, clientId, mqttTopic);

			} catch (Exception e) {
				LOG.info("(!) MQTT CONNECTION ERROR: {}", brokerUrl);
			}
		}
	}

	@PreDestroy
	public void disconnect() {
		if (mqttClient.isConnected()) {
			try {
				LOG.info("Closing mqtt connection...");
				mqttClient.disconnect();

			} catch (MqttException e) {
				LOG.error("Can`t close mqtt connection...");
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
					LOG.info("File/dir to delete {}", ficheros[x].getName());
					this.deleteFolder(ficheros[x]);
				}
			}
			result = Boolean.TRUE;
		} catch (Exception e) {
			LOG.error("cleanFiles: {}", e.getMessage());
		}
		return result;
	}

	private void deleteFolder(File fileDel) {

		try {
			if (fileDel.isDirectory()) {
				if (fileDel.list().length == 0) {
					Files.delete(Paths.get(fileDel.getAbsolutePath()));
				} else {
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
		} catch (IOException e) {
			LOG.error("Error while trying to delete the folder {}: {}", fileDel.getAbsoluteFile(), e.getMessage());
		}
	}

	@Override
	public void connectionLost(Throwable arg0) {
		LOG.info("CONNECTION LOST: {}", arg0);
		this.connect();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// Nothing to do here
	}

	@Override
	public void messageArrived(String arg0, MqttMessage msg) {
		new Thread(new KafkaSendThread(kafkaConnection, msg)).start();
	}
}
