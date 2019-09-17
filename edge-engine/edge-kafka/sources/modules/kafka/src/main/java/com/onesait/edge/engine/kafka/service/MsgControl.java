package com.onesait.edge.engine.kafka.service;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.onesait.edge.engine.kafka.model.StoreMsg;

@Service
public class MsgControl {

	private static final Logger LOG = LoggerFactory.getLogger(MsgControl.class);

	// Number Of Messages To Send
	@Value("${kafka.resend.messages}")
	private Integer nomts;
	// Tiempo máximo que un msg pertenece en HashMap (En Horas)
	@Value("${kafka.resend.persistence.h}")
	private Integer duration;
	// Cada cuanto se realiza la comprobación de reenvio de mensages (En segundos)
	@Value("${kafka.resend.sec}")
	private Integer resend;

	private Integer counter = 0;

	@Autowired
	KafkaConnection kafkaConnection;

	public static ConcurrentLinkedQueue<StoreMsg> messages = new ConcurrentLinkedQueue<>();

	@Scheduled(fixedRate = 1000)
	public void init() {
		/*
		 * counter: Establece las iteración dependiendo del valor resend (Controla que
		 * solo se recorra el numero que dice el resend) count: Controla el recorrido de
		 * toda la lista si es menor del valor de nomts contador:
		 */
		if (counter == resend) {
			counter = 0;
			try {
				this.checkTimeMsgs();
				if (kafkaConnection.getProducer() != null) {
					LOG.info("Stored messages to resend: {}, Mqtt messages received: {}", messages.size(), kafkaConnection.getMqttCounter());
					if (!messages.isEmpty()) {
						Integer count = (MsgControl.messages.size() < nomts) ? MsgControl.messages.size() : nomts;
						int contador = 0;
						for (StoreMsg storeMsg : MsgControl.messages) {
							kafkaConnection.sendInsertInstance(storeMsg.getMessage());
							messages.remove(storeMsg);
							if (contador >= count) {
								break;
							}
							contador += 1;
						}
					}
				} else {
					LOG.warn("Producer not Active, cannot send store messages...");
				}
			} catch (Exception e) {
				LOG.error("ERROR sending messages: {}", e);
			}
		} else {
			counter += 1;
		}
	}

	private void checkTimeMsgs() {
		Long now = new Date().getTime();

		Long limitTime = now - (duration * 60 * 60 * 1000);

		for (StoreMsg storeMsg : messages) {
			Date msgTime = storeMsg.getDate();
			if (msgTime.getTime() < limitTime) {
				messages.remove(storeMsg);
				LOG.info("Deleting message; {}", storeMsg);
			}
		}
	}
}
