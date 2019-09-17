/*
   Copyright 2008-2013 Andrew Rapp, http://code.google.com/p/xbee-api/

   Copyright 2008-2013 ITACA-TSB, http://www.tsb.upv.es/
   Instituto Tecnologico de Aplicaciones de Comunicacion 
   Avanzadas - Grupo Tecnologias para la Salud y el 
   Bienestar (TSB)


   See the NOTICE file distributed with this work for additional 
   information regarding copyright ownership

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.onesait.edge.engine.zigbee.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.regex.Pattern;

import com.onesait.edge.engine.zigbee.exception.GenericZigbeeException;

public class ByteUtils {
	private static final String TOO_BIG="too big";
	private static final String EXCEEDS_RANGE="Values exceeds byte range: ";
	private static final String JAVA_NOT_SUPPORT="Java int can't support a four byte value, with msb byte greater than 7e";
	private static final String TOO_MANY_BYTES="too many bytes can't be converted to long";
	private ByteUtils() {}

	/**
	 * Retorna la trama en hexadecimal
	 * @param byteArray
	 * @return la trama en formato hexadecimal
	 */
	public static final String showFrame(byte[] byteArray){
		StringBuilder result = new StringBuilder();
		for (byte b:byteArray) {
		    result.append(String.format("%02X", b));
		}
		return result.toString();
		
	}
	/**
	 * Retorna un tipo Long (64 bits) en base a 8 bytes pasados como parAmetros
	 * @param byte7 El mas significativo (H)
	 * @param byte6
	 * @param byte5
	 * @param byte4
	 * @param byte3
	 * @param byte2
	 * @param byte1 El menos significativo (L)
	 * @param byte0
	 * @return el dato en formato Long
	 */
	public static final long bytesToLong(byte byte7,byte byte6,byte byte5,byte byte4,byte byte3,byte byte2,byte byte1,byte byte0 ){
		ByteBuffer bb = ByteBuffer.allocate(Long.SIZE);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.put(byte7);
		bb.put(byte6);
		bb.put(byte5);
		bb.put(byte4);
		bb.put(byte3);
		bb.put(byte2);
		bb.put(byte1);
		bb.put(byte0);		
		return bb.getLong(0);
	}
	/**
	 * Convierte un long a bytes
	 * @param x el long a convertir
	 * @return la pareja de bytes
	 */
	public static final byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
	    buffer.putLong(x);
	    return buffer.array();
	}
	
	/**
	 * Convierte de short a bytes
	 * @param s el parametro a convertir
	 * @return El conjunto de bytes
	 */
	public static final byte[] shortToBytes(short s){
	    ByteBuffer buffer = ByteBuffer.allocate(Short.SIZE);	    
	    buffer.putShort(s);
	    return buffer.array();		
	}
	/**
	 * Convierte de short a bytes
	 * @param s el parametro a convertir
	 * @return El conjunto de bytes
	 */
	public static final byte[] intToBytes(int s){
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE);
	    buffer.putInt(0);
	    return buffer.array();		
	}	
	/**
	 * Convierte dos bytes en short
	 * @param byteH El byte mAs significativo
	 * @param byteL El byte menos significativo
	 * @return el dato de tipo short
	 */
	public static final short bytesToShort(byte byteH, byte byteL){
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.put(byteH);
		bb.put(byteL);
		return bb.getShort(0);
	}	
	public static int convertMultiByteToInt(int[] bytes) throws GenericZigbeeException {
		
		if (bytes.length > 4) {
			throw new GenericZigbeeException(TOO_BIG);
		} else if (bytes.length == 4 && ((bytes[0] & 0x80) == 0x80)) {
			// 0x80 == 10000000, 0x7e == 01111111
			throw new GenericZigbeeException(JAVA_NOT_SUPPORT);
		}
		
		int val = 0;
		
		for (int i = 0; i < bytes.length; i++) {
			
			if (bytes[i] > 0xFF) {
				throw new GenericZigbeeException(EXCEEDS_RANGE + bytes[i]);
			}
			
			if (i == (bytes.length - 1)) {
				val+= bytes[i];
			} else {
				val+= bytes[i] << ((bytes.length - i - 1) * 8);	
			}
		}
		
		return val;
	}
	
		public static int convertMultiByteToInt(List<Integer> bytes) throws GenericZigbeeException {
				
				if (bytes.size() > 4) {
					throw new GenericZigbeeException(TOO_BIG);
				} else if (bytes.size() == 4 && ((bytes.get(0) & 0x80) == 0x80)) {
					// 0x80 == 10000000, 0x7e == 01111111
					throw new GenericZigbeeException(JAVA_NOT_SUPPORT);
				}
				
				int val = 0;
				
				for (int i = 0; i < bytes.size(); i++) {
					
					if (bytes.get(i) > 0xFF) {
						throw new GenericZigbeeException(EXCEEDS_RANGE+ bytes.get(i));
					}
					
					if (i == (bytes.size() - 1)) {
						val+= bytes.get(i);
					} else {
						val+= bytes.get(i) << ((bytes.size() - i - 1) * 8);	
					}
				}
				
				return val;
			}
		public static int convertMultiByteToLong(List<Integer> bytes) throws GenericZigbeeException {
			
			if (bytes.size() > 8) {
				throw new GenericZigbeeException(TOO_BIG);
			} else if (bytes.size() == 8 && ((bytes.get(0) & 0x80) == 0x80)) {
				// 0x80 == 10000000, 0x7e == 01111111
				throw new GenericZigbeeException(JAVA_NOT_SUPPORT);
			}
			
			int val = 0;
			
			for (int i = 0; i < bytes.size(); i++) {
				
				if (bytes.get(i) > 0xFF) {
					throw new GenericZigbeeException(EXCEEDS_RANGE + bytes.get(i));
				}
				
				if (i == (bytes.size() - 1)) {
					val+= bytes.get(i);
				} else {
					val+= bytes.get(i) << ((bytes.size() - i - 1) * 8);	
				}
			}
			
			return val;
		}
        public static long convertMultiByteToLong(byte[] bytes) throws GenericZigbeeException {
		
		if (bytes.length > 8) {
			throw new IllegalArgumentException(TOO_MANY_BYTES);
		} else if (bytes.length == 8 && ((bytes[0] & 0x80) == 0x80)) {
			// 0x80 == 10000000, 0x7e == 01111111
			throw new GenericZigbeeException(JAVA_NOT_SUPPORT);
		}
		
		long val = 0;
		
		for (int i = 0; i < bytes.length; i++) {
			val += 0x000000FF & bytes[i];
			val = val << 8;	
		}
		
		return val;
	}
        
	
	public static int[] convertInttoMultiByte(int val) {
		
		// must decompose into a max of 4 bytes
		// b1		b2		 b3		  b4
		// 01111111 11111111 11111111 11111111
		// 127      255      255      255
		
		int size = 0;
		
		if ((val >> 24) > 0) {
			size = 4;
		} else if ((val >> 16) > 0) {
			size = 3;
		} else if ((val >> 8) > 0) {
			size = 2;
		} else {
			size = 1;
		}
		
		int[] data = new int[size];
		
		for (int i = 0; i < size; i++) {
			data[i] = (val >> (size - i - 1) * 8) & 0xFF;
		}
		
		return data;	
	}
        
        public static int[] convertLongtoMultiByte(long val) {
		
		int size = 0;
		
		if ((val >> 56) > 0) {
			size = 8;
		} else if ((val >> 48) > 0) {
			size = 7;
		} else if ((val >> 40) > 0) {
			size = 6;
		} else if ((val >> 32) > 0) {
			size = 5;
		} else if ((val >> 24) > 0) {
			size = 4;
		} else if ((val >> 16) > 0) {
			size = 3;
		} else if ((val >> 8) > 0) {
			size = 2;
		} else {
			size = 1;
		}
		
		int[] data = new int[size];
		
		for (int i = 0; i < size; i++) {
			data[i] = (int) ((val >> (size - i - 1) * 8) & 0xFF);
		}
		
		return data;	
	}
	public static final String toBase16(final int[] arr) {
		return toBase16(arr, 0, arr.length);
	}
	
	/**
	 * 
	 *  @since 0.6.0
	 */
	public static final String toBase16(final int[] arr, final int start) {
		return toBase16(arr, start, arr.length);
	}
	
	/**
	 * 
	 *  @since 0.6.0
	 */
	public static final String toBase16(final int[] arr, final int start, final int end) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = start; i < end; i++) {
			sb.append(toBase16(arr[i]));
			
			if (i < arr.length - 1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}
	

	public static int[] fromBase16toIntArray(String bytes){
		final String PATTERN = "\\s*((0x[0-9a-f]{2}|[0-9a-f]{2})\\s*)+";
		bytes = bytes.toLowerCase();
		if(!bytes.matches(PATTERN)){
			throw new IllegalArgumentException("Unable to parse "+bytes+" doesn't match regex "+PATTERN);
		}
		String[] singleBytes = bytes.split("\\s+");
		String item;
		int[] values = new int[singleBytes.length];
		for (int i = 0; i < singleBytes.length; i++) {
			item = singleBytes[i];
			if ( item.length() == 0 ) 
				continue;
			
			if( item.startsWith("0x") ) {
				item = item.substring(2);
			}
			
			values[i] = (Integer.parseInt(item, 16) & 0xFF);
		}
		return values;
	}
	
	/**
	 * This method return a <code>byte[]</code> from a <code>String</code>. It support the format<br>
	 * of the {@link #toBase16(byte[])} and in general it supports the following {@link Pattern}:<br>
	 * <pre>\s*((0x[0-9a-f]{2}|[0-9a-f]{2})\s*)+</pre>
	 * <b>Exmaple:</b>
	 * <pre>
	 * 0x23 0xab 0xfE 0xDD
	 * 0x23 0xab 0xfe 0xdd
	 * 0x23 ab 0xfE DD
	 * </pre>  
	 * <b>NOTE</b><br>
	 * The main difference with {@link #fromBase16toIntArray(String)} is that the data returned<br>
	 * is signed, so values goes from <code>-128 to 127</code>
	 * 
	 * @param bytes the String representing the bytes in hex form
	 * @return the formatted bytes
	 */
	public static byte[] fromBase16toByteArray(String bytes){
		int[] values = fromBase16toIntArray(bytes);
		byte[] returns = new byte[values.length];
		for (int i = 0; i < values.length; i++) {
			returns[i] = (byte) (values[i] & 0xFF);
		}
		return returns;
	}
	
	public static String toBase16(byte[] arr) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < arr.length; i++) {
			sb.append(toBase16(arr[i]));			
			if (i < arr.length - 1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}
	

	public static String toBase2(int[] arr) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < arr.length; i++) {
			sb.append(toBase2(arr[i]));
			
			if (i < arr.length - 1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}

	public static String toBase10(int[] arr) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < arr.length; i++) {
			sb.append((arr[i]));
			
			if (i < arr.length - 1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}
	
	public static String toChar(int[] arr) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < arr.length; i++) {
			sb.append((char)arr[i]);
		}
		
		return sb.toString();
	}	
	
	private static String padBase2(String s) {
		
		StringBuilder bld = new StringBuilder();		
		for (int i = s.length(); i < 8; i++) {
			bld.append("0"+s);
		}
		
		return bld.toString();
	}
	
	/**
	 * Returns true if the bit is on (1) at the specified position
	 * Position range: 1-8
	 */
	public static boolean getBit(int b, int position) {
		
		if (position < 1 || position > 8) {
			throw new IllegalArgumentException("Position is out of range");
		}
		
		if (b > 0xff) {
			throw new IllegalArgumentException("input value is larger than a byte");
		}
		
		if (((b >> (--position)) & 0x1) == 0x1) {
			return true;
		} 
		
		return false;		
	}
	/**
	 * 
	 * @param b the int value to check if it contains a byte representable value
	 * @return true if the value of the int could be expressed with 8 bits
	 */
	public static boolean isByteValue(int b) {
		return( (b & 0xffffff00) == 0 || (b & 0xffffff00) == 0xffffff00);
	}
	
	public static String toBase16(int b) {
		if (! isByteValue(b) ) {
			throw new IllegalArgumentException("Error converting "+b+" input value to hex string it is larger than a byte");
		}
		if ( b < 0) {
			return "0x" + Integer.toHexString(b).substring(6);
		} else if (b < 0x10) {
			return "0x0" + Integer.toHexString(b);
		} else if (b >= 0x10){
			return "0x" + Integer.toHexString(b);			
		} else {
			throw new IllegalArgumentException("Unable to recognize the value "+b);			
		}
	}
	
	public static String toBase2(int b) {
		
		if (b > 0xff) {
			throw new IllegalArgumentException("input value is larger than a byte");
		}
		
		return padBase2(Integer.toBinaryString(b));
	}
	
	public static String formatByte(int b) {
		return "base10=" + Integer.toString(b) + ",base16=" + toBase16(b) + ",base2=" + toBase2(b);
	}
	
	public static int[] stringToIntArray(String s) {
		int[] intArr = new int[s.length()];
		
		for (int i = 0; i < s.length(); i++) {
			intArr[i] = (int)s.charAt(i);
		}
		
		return intArr;
	}
	
	/**
	 * Parses a 10-bit analog value from the input stream
	 * @param pos relative position in packet (for logging only)
	 * @return
	 * @throws IOException
	 */
	public static int parse10BitAnalog(int msb, int lsb) {	
		msb = msb & 0xff;
		msb = (msb & 0x3) << 8;
		lsb = lsb & 0xff;
		return msb + lsb;
	}
		

	
	/**
	 * Parse N-th bit from a byte
	 * 
	 * @param byte from whitch exstract n-th bit
	 * 
	 * @return boolean
	 * 
	 */
	public static boolean getNthBitFromByte(byte thebyte, int position) {

		return (1 == ((thebyte >> position) & 1));

	}
	
	/**
	 * Indica si el valor de 32 bist es positivo/negativo en funcion del bit mas significativo
	 * @param value El valor de 32 bits
	 * @return
	 */
	public static boolean isPositive32(int value){
    	int mask =0x80000000;
    	return (value & mask)>>>31==0;
    
	}
	
	/**
	 * Indica si el valor de 32 bist es positivo/negativo en funcion del bit mas significativo
	 * @param value El valor de 32 bits
	 * @return
	 */
	public static int shift9BitsUnsigned(int value){
    	int mask =0x000000FF;
    	int ouput=(value)>>>(32-9);
    	return (ouput&mask);
    
	}
	
	public static int meazonConvertion(int value){
		
		// sign * 2^(exponent) * (1+mantissa/(2^23))
		int sign = (isPositive32(value))?1:-1;
		
		int mask1 = 0x007FFFFF;
		int value1 = ByteUtils.shift9BitsUnsigned(value);
		int exponent = value1 - 127;
		int mantissa = value & mask1;
		double value2 = (sign) *   Math.pow(2,exponent) * (1+mantissa/Math.pow(2, 23));
		return (int)value2;

	}
	private static final int RSSI_OFFSET_VALUE = -38;
	private static final int LQI_TOP = 63;
	
	/**
	 * Link Quality Indicator <br/>
	 * @see http://community.silabs.com/t5/Wireless-Knowledge-Base/How-do-I-obtain-LQI-and-RSSI-values-from-the-stack-What-do-the/ta-p/113203
	 * @see http://www2.cs.uh.edu/~donny/papers/LinkQualityInferenceModel.pdf
	 * @param b RSSI byte (Received Signal Strength Indication): The RSSI value is a signed 8-bit integer (int) ranging from approximately -100 (based on the receive sensitivity rating for the chip you are using) to 127, with each value representing the energy level (in dBm) at the radio�s receiver.
	 * <ul>
	 * </ul>
	 * @return  LQI value is an unsigned 8-bit integer (int8u) ranging from 0 to 255.
	 * <ul> 
	 * <li>Maximum value representing the best possible link quality.</li> 
	 * <li>LQIs below 200 represent links with high error rates, The LQI value of 200 represents approximately 80% reliability of receiving the packet intac</li>
	 * </ul>
	 */
	public static int lqi(byte b){
		int value1 = b;
		int value2 = (value1 < RSSI_OFFSET_VALUE)?0:value1-RSSI_OFFSET_VALUE;
		return (value2 > LQI_TOP)?255:value2*4;
	}
	
}
