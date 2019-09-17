
package com.onesait.edge.engine.zigbee.security;

public class HexConverter {
	private HexConverter() {}
	public static String toHexString(byte[] input){
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			String hex_code = Integer.toHexString(0xff & input[i]);
			String leading_zero = "";
			if (hex_code.length() == 1)
				leading_zero = "0";
			result.append(leading_zero + hex_code);
		}
		return result.toString();
	}
}
