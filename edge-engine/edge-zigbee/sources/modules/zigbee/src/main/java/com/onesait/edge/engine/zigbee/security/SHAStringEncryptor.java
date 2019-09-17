
package com.onesait.edge.engine.zigbee.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAStringEncryptor extends StringEncryptor  {
	
	private static final String DEFAULT_ALGORITHM = "SHA-1";
	
	private static final String STRING_TO_APPEND = "$Er5ç(O";
	
	private static final String STRING_TO_SF2 ="SF2";
	
	private String algorithm;
	
	private boolean appendRandomString;
	
	public SHAStringEncryptor(String algorithm, boolean appendRandomString) {
		this.algorithm = algorithm;
		this.appendRandomString = appendRandomString;
	}
	
	public SHAStringEncryptor() {
		this(DEFAULT_ALGORITHM, false);
	}

	@Override
	public byte[] encrypt2Bytes(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(this.algorithm);	
		if (this.appendRandomString){
			input = input + STRING_TO_APPEND;
		}
		byte[] output = md.digest(input.getBytes());
		return output;
	}
	
	@Override
	public byte[] encrypt2BytesSF2(String input) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance(this.algorithm);
		input = input.concat(STRING_TO_SF2);
		byte[] output = md.digest(input.getBytes());
		return output;
	}
	
	@Override
	public int getMinEncryptedMessageLength() {
		return 64;
	}

}