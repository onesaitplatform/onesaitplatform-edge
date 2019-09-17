package com.onesait.edge.engine.zigbee.security;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyGenerator {

	String seed = "";
	private static final Logger LOG = LoggerFactory.getLogger(KeyGenerator.class);
	
	public KeyGenerator(String seed) {
		this.seed = seed;
	}
	
	public byte[] getNwkKey() {
		SHAStringEncryptor encriptor = new SHAStringEncryptor();
		encriptor.getMinEncryptedMessageLength();
		try {
			return encriptor.encrypt2Bytes(seed + SecurityConstants.ZB_CONCAT);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
}
