package com.onesait.edge.engine.modbus.util;

import java.nio.ByteBuffer;

import javax.validation.constraints.NotNull;

import com.onesait.edge.engine.modbus.model.DataType;

public class NumberUtils {
	
	private NumberUtils() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static final byte[] getBytes(@NotNull short[] rawData, boolean bigEndiand, DataType dataType) {
		
		if (DataType.SINT16.equals(dataType)) {
			return NumberUtils.getWord16(rawData[0], bigEndiand);
		} else if (DataType.SINT32.equals(dataType)) {
			return NumberUtils.getWord32(rawData, bigEndiand);
		} else if (DataType.SINT64.equals(dataType)) {
			return NumberUtils.getWord64(rawData, bigEndiand);
		} else if (DataType.UINT16.equals(dataType)) {
			return NumberUtils.getWord16(rawData[0], bigEndiand);
		} else if (DataType.UINT32.equals(dataType)) {
			return NumberUtils.dataUnsignedInt32(rawData, bigEndiand);
		} else if (DataType.UINT64.equals(dataType)) {
			return NumberUtils.dataUnsignedInt64(rawData, bigEndiand);
		} else if(DataType.CHART16.equals(dataType)) {
			return NumberUtils.getWord16(rawData[0], bigEndiand);
		} else if(DataType.CHART32.equals(dataType)) {
			return NumberUtils.getWord32(rawData, bigEndiand);
		} else if (DataType.BOOLEAN.equals(dataType)) {
			return new byte[] { (byte) 0, (byte) (rawData[0] & 0x0001) };
		} else {
			// float 32
			return NumberUtils.getWord32(rawData, bigEndiand); 
		}
	}
	
	/**
	 * getWord 16 byte from raw short[] value
	 * @param rawData short (length 1 -> 16byte), to get the word in bytes.
	 * @param bigEndiand - ordering
	 * @return word in byte[]
	 */
	public static final byte[] getWord16(short rawData, boolean bigEndiand) {
		
		byte[] data = new byte[] { (byte) (rawData & 0x00FF), (byte) ((rawData & 0xFF00) >> 8) };

		if (bigEndiand) {
			data = new byte[] { data[1], data[0] };
		}
		return data;
	}
	
	/**
	 * getWord 32 byte from raw short[] value. In the array, in position [0] -> LSB, low part of the 32 byte word. 
	 * In the position [1] -> MSB, high part of the 32 byte word. Number of elements necessary are 2.
	 * @param rawData  short (length 2 -> 32byte), to get the word in bytes.
	 * @param bigEndiand - ordering
	 * @return word in byte[]
	 */
	public static final byte[] getWord32(short[] rawData, boolean bigEndiand) {
		
		byte[] data0 = new byte[] { (byte) (rawData[0] & 0x00FF), (byte) ((rawData[0] & 0xFF00) >> 8) };
		byte[] data1 = new byte[] { (byte) (rawData[1] & 0x00FF), (byte) ((rawData[1] & 0xFF00) >> 8) };
		
		byte[] data32 = new byte[] { data0[1], data0[0], data1[1], data1[0] };
		
		if (bigEndiand) {
			data32 = new byte[] { data1[1], data1[0], data0[1], data0[0] };
		}
		return data32;
	}
	
	/**
	 * getWord 64 byte from raw short[] value. In the array, in position [0] -> LSB, low part of the 64 byte word. 
	 * In the position [3] -> MSB, high part of the 64 byte word. Number of elements necessary are 4.
	 * @param rawData short (length 4 -> 64byte), to get the word in bytes.
	 * @param bigEndiand - ordering
	 * @return word in byte[]
	 */
	public static final byte[] getWord64(short[] rawData, boolean bigEndiand) {
		
		byte[] data0 = new byte[] { (byte) (rawData[0] & 0x00FF), (byte) ((rawData[0] & 0xFF00) >> 8) };
		byte[] data1 = new byte[] { (byte) (rawData[1] & 0x00FF), (byte) ((rawData[1] & 0xFF00) >> 8) };
		byte[] data2 = new byte[] { (byte) (rawData[2] & 0x00FF), (byte) ((rawData[2] & 0xFF00) >> 8) };
		byte[] data3 = new byte[] { (byte) (rawData[3] & 0x00FF), (byte) ((rawData[3] & 0xFF00) >> 8) };
		
		byte[] data64 = new byte[] { data0[1], data0[0], data1[1], data1[0], data2[1], data2[0], data3[1], data3[0] };
		
		if (bigEndiand) {
			data64 = new byte[] { data3[1], data3[0], data2[1], data2[0], data1[1], data1[0], data0[1], data0[0] };
		}
		return data64;
	}
	


	/**
	 * getWord 32 byte from raw short[] value. In the array, in position [0] -> LSB, low part of the 32 byte word. 
	 * In the position [1] -> MSB, high part of the 32 byte word. Number of elements necessary are 2.
	 * @param rawData short (length 2 -> 32byte), to get the word in bytes.
	 * @param bigEndiand - ordering
	 * @return word in byte[]
	 */
	public static final byte[] dataUnsignedInt32(short[] rawData, boolean bigEndiand) {
		
		byte[] data32 = getWord32(rawData, bigEndiand);
		return new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, data32[0], data32[1], data32[2], data32[3] };
	}
	
	/**
	 * getWord 64 byte from raw short[] value. In the array, in position [0] -> LSB, low part of the 64 byte word. 
	 * In the position [3] -> MSB, high part of the 64 byte word. Number of elements necessary are 4.
	 * @param rawData short (length 4 -> 64byte), to get the word in bytes.
	 * @param bigEndiand - ordering
	 * @return word in byte[]
	 */
	public static final byte[] dataUnsignedInt64(short[] rawData, boolean bigEndiand) {
				
		byte[] data64 = getWord64(rawData, bigEndiand);
		return new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, data64[0], data64[1],
				data64[2], data64[3], data64[4], data64[5], data64[6], data64[7] };
	}
	
	/**
	 * Adapt byte data to different data types (CHAR_32, FLOAT_32, SINT_16, SINT_32, SINT_64, UINT_16, UINT_32, UINT_64 and BOOLEAN)
	 * @param dataType - DataType 
	 * @param data to adapt
	 * @return adapted Number.
	 */
	public static final Number getDataFormattedByDataType(DataType dataType, byte[] data) {
		if (DataType.CHART32.equals(dataType)) {
			return ByteBuffer.wrap(data).getInt();
		} else if (DataType.FLOAT32.equals(dataType)) {
			return ByteBuffer.wrap(data).getFloat();
		} else if (DataType.SINT16.equals(dataType)) {
			return ByteBuffer.wrap(data).getShort();
		} else if (DataType.SINT32.equals(dataType)) {
			return ByteBuffer.wrap(data).getInt();
		} else if (DataType.SINT64.equals(dataType)) {
			return ByteBuffer.wrap(data).getLong();
		} else if (DataType.UINT16.equals(dataType)) {
			return ByteBuffer.wrap(data).getShort();
		} else if (DataType.UINT32.equals(dataType)) {
			return ByteBuffer.wrap(data).getLong();
		} else if (DataType.UINT64.equals(dataType)) {
			return ByteBuffer.wrap(data).getLong(); // Puede fallar debido a que el unsigned de 64 son 128 bits no 64bits(long)
		} else if (DataType.BOOLEAN.equals(dataType)) {
			return ByteBuffer.wrap(data).getShort();
		} else {
			return ByteBuffer.wrap(data).getShort();
		}
	}
	
	/**
	 * Adapt byte data to different data types (CHAR_32, FLOAT_32, SINT_16, SINT_32, SINT_64, UINT_16, UINT_32, UINT_64 and BOOLEAN)
	 * @param dataType - DataType 
	 * @param data to adapt
	 * @param convFactor in case that raw data need to apply
	 * @return adapted Number.
	 */
	public static final Number getConvertedDataFormattedByDataType(DataType dataType, byte[] data, Float convFactor) {
		if (DataType.CHART32.equals(dataType)) {
			return ByteBuffer.wrap(data).getInt();
		} else if (DataType.FLOAT32.equals(dataType)) {
			return ByteBuffer.wrap(data).getFloat() * convFactor;
		} else if (DataType.SINT16.equals(dataType)) {
			return ByteBuffer.wrap(data).getShort() * convFactor;
		} else if (DataType.SINT32.equals(dataType)) {
			return ByteBuffer.wrap(data).getInt() * convFactor;
		} else if (DataType.SINT64.equals(dataType)) {
			return ByteBuffer.wrap(data).getLong() * convFactor;
		} else if (DataType.UINT16.equals(dataType)) {
			return ByteBuffer.wrap(data).getShort() * convFactor;
		} else if (DataType.UINT32.equals(dataType)) {
			return ByteBuffer.wrap(data).getLong() * convFactor;
		} else if (DataType.UINT64.equals(dataType)) {
			return ByteBuffer.wrap(data).getLong() * convFactor; // Puede fallar debido a que el unsigned de 64 son 128 bits no 64bits(long)
		} else if (DataType.BOOLEAN.equals(dataType)) {
			return ByteBuffer.wrap(data).getShort();
		} else {
			return ByteBuffer.wrap(data).getShort() * convFactor;
		}
	}
	
	public static final short[] getShorts(DataType dataType,@NotNull Number value2get, boolean bigEndiand) { 
		
		short[] resultData = null;
		
		if (DataType.CHART32.equals(dataType) && value2get instanceof Integer) {
			
		} else if (DataType.FLOAT32.equals(dataType) && value2get instanceof Float) {
			
		} else if (DataType.SINT16.equals(dataType) && value2get instanceof Short) {
			
		} else if (DataType.SINT32.equals(dataType) && value2get instanceof Integer) {
			
		} else if (DataType.SINT64.equals(dataType) && value2get instanceof Long) {
			
		} else if (DataType.UINT16.equals(dataType) && value2get instanceof Short) {
			
		} else if (DataType.UINT32.equals(dataType) && value2get instanceof Long) {
			
		} else if (DataType.UINT64.equals(dataType) && value2get instanceof Long) {
			
		} else if (DataType.BOOLEAN.equals(dataType) && value2get instanceof Short) {
			
		} else {
			
		}
		
		return resultData;
		
//		Integer a = Integer.valueOf("1000");
//		a.
//		
//		(i >> 24) & 0xff is the high-order byte of i
//		(i >> 16) & 0xff is the second byte of i
//		(i >> 8) & 0xff is the third byte of i
//		i & 0xff is the last byte of i
//		
//		MATH.
//        short[] sdata = new short[data.length / 2]; 
//        for (int i = 0; i < sdata.length; i++) 
//            sdata[i] = ModbusUtils.toShort(data[i * 2], data[i * 2 + 1]); 
//        return sdata; 
		
//		if(signal.getRegisterType().equals(registerType.toString()) && signal.getRegister().intValue() >= initCountReg && signal.getRegister().intValue() < initCountReg+values.length) {
//		
//		// searching the register in the block array values
//		DataType dataType = DataType.fromValue(signal.getDataType());
//		indexRegisterPosition = signal.getRegister().intValue() - initCountReg;
//		short[] register = Arrays.copyOfRange(values, indexRegisterPosition, indexRegisterPosition + DataType.offset(dataType)+1);
//		
//		// transform to bytes
//		byte[] resultTrans = NumberUtils.getBytes(register, signal.getBigEndian(), dataType);
//		signal.setValue(NumberUtils.getDataFormattedByDataType(dataType, resultTrans));
//		signal.setConvertedValue(NumberUtils.getConvertedDataFormattedByDataType(dataType, resultTrans, signal.getConvFactor()));
//		signal.setTimestampValueInNanos(timestamp++);

    }
	
//	 protected byte[] convertToBytes(short[] sdata) {
//	        int byteCount = sdata.length * 2;
//	        byte[] data = new byte[byteCount];
//	        for (int i = 0; i < sdata.length; i++) {
//	            data[i * 2] = (byte) (0xff & (sdata[i] >> 8));
//	            data[i * 2 + 1] = (byte) (0xff & sdata[i]);
//	        }
//	        return data;
//	    }
//
//	 protected short[] convertToShorts(byte[] data) {
//	        short[] sdata = new short[data.length / 2];
//	        for (int i = 0; i < sdata.length; i++)
//	            sdata[i] = ModbusUtils.toShort(data[i * 2], data[i * 2 + 1]);
//	        return sdata;
//	    }
	
}
