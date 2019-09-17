package com.onesait.edge.engine.kafka.service;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.onesait.edge.engine.kafka.model.ProducerCallback;

@Service
public class KafkaConnection {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaConnection.class);

	@Value("${kafka.url}")
	private String url;
	@Value("${kafka.port}")
	private Integer port;
	@Value("${kafka.prefix}")
	private String prefix;
	@Value("${kafka.ontology}")
	private String ontology;
	@Value("${kafka.sasl.jaas.config}")
	private String jassConfig;

	Integer mqttCounter = 0;

	private KafkaProducer<String, String> producer;

	public KafkaProducer<String, String> getProducer() {
		return producer;
	}

	public Integer getMqttCounter() {
		return mqttCounter;
	}

	public void incrementCounter() {
		this.mqttCounter++;
	}

	@Scheduled(fixedRate = 30000)
	public void connect() {
		if (producer == null) {
			try {
				Properties config = createConfig();
				LOG.info("Configuration STABLISHED");
				producer = new KafkaProducer<>(config);
				LOG.info("Kafka producer CREATED");
			} catch (KafkaException e) {
				LOG.error("Unable to connect to Kafka: {}:{}", this.url, this.port);
			}
		}
	}

	@PreDestroy
	public void destroy() {
		if (producer != null) {
			producer.flush();
			producer.close();
			LOG.info("Kafka producer closed");
		}
	}

	private Properties createConfig() {
		Properties config = new Properties();
		config.put(ProducerConfig.CLIENT_ID_CONFIG, "localhost");
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url + ":" + port);
		config.put("security.protocol", "SASL_PLAINTEXT");
		config.put("sasl.mechanism", "PLAIN");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put("sasl.jaas.config", jassConfig);
		return config;
	}

	private static JsonNode getInstances(String msg) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(msg);
	}

	private static void sendToKafka(KafkaProducer<String, String> producer, String ontology, String instance, String prefix) {

		ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(prefix + ontology, instance);
		ProducerCallback callback = new ProducerCallback(instance);
		producer.send(producerRecord, callback);
	}

	public void send(MqttMessage msg) throws IOException {

		JsonNode rootNode = getInstances(msg.toString());
		JsonNode allData = rootNode.get("data");
		if (allData == null) {
			allData = rootNode;
		}
		if (rootNode.get("ontology") != null) {
			ontology = rootNode.get("ontology").toString().replaceAll("\"", "");
		}

		Consumer<JsonNode> insertInstance = new Consumer<JsonNode>() {

			@Override
			public void accept(JsonNode instance) {
				String instanceString = instance.toString();
				sendToKafka(producer, ontology, instanceString, prefix);
			}
		};

		if (!allData.isArray()) {
			allData = getInstances("[" + msg + "]");
		}

		if (allData.isArray()) {
			ArrayNode arrayInstances = (ArrayNode) allData;
			arrayInstances.forEach(insertInstance);
		}
		if (producer != null) {
			producer.flush();
		}
	}

	public void sendInsertInstance(String instanceString) {
		sendToKafka(producer, ontology, instanceString, prefix);
		if (producer != null) {
			producer.flush();
		}
	}
}
