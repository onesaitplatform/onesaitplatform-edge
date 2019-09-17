package com.onesait.edge.engine.kafka.model;

import java.util.Date;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.kafka.service.MsgControl;

public class ProducerCallback implements Callback{
	
	private static final Logger LOG = LoggerFactory.getLogger(ProducerCallback.class);
	
	private String instance;
	
	public ProducerCallback(String instance) {
		super();
		this.instance = instance;
	}

	@Override
	public void onCompletion(RecordMetadata metadata, Exception exception) {
		if(exception != null) {
			LOG.error("Error inserting data over kafka: {}; {}", instance, exception.getMessage());
			StoreMsg storeMsg = new StoreMsg(instance, new Date());
			MsgControl.messages.add(storeMsg);
			LOG.info("Message Store in memory. Store Message size: {}", MsgControl.messages.size());
		}
	}
}
