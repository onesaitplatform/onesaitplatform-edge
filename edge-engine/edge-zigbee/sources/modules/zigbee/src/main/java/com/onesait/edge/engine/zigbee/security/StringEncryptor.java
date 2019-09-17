
package com.onesait.edge.engine.zigbee.security;

import java.security.NoSuchAlgorithmException;

public abstract class StringEncryptor {
	
	public abstract byte[] encrypt2Bytes(String input) throws NoSuchAlgorithmException;

	public String encrypt2String(String input) throws NoSuchAlgorithmException {
		return HexConverter.toHexString(this.encrypt2Bytes(input));
	}
	
	public abstract int getMinEncryptedMessageLength();

	public abstract byte[] encrypt2BytesSF2(String input) throws NoSuchAlgorithmException;
	
	public String encrypt2BSF2String(String input) throws NoSuchAlgorithmException {
		return HexConverter.toHexString(this.encrypt2BytesSF2(input));
	}
}
