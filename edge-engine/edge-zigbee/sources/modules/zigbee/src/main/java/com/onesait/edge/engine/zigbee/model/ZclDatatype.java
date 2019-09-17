package com.onesait.edge.engine.zigbee.model;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.onesait.edge.engine.zigbee.util.ByteUtils;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;

public class ZclDatatype implements Cloneable{
	/**
	 * String constants for datatype names
	 */
	public static final String NODATA_STR = "nodata";
	public static final String DATA8_STR = "data8";
	public static final String DATA16_STR = "data16";
	public static final String DATA24_STR = "data24";
	public static final String DATA32_STR = "data32";
	public static final String DATA40_STR = "data40";
	public static final String DATA48_STR = "data48";
	public static final String DATA56_STR = "data56";
	public static final String DATA64_STR = "data64";
	public static final String BOOLEAN_STR = "boolean";
	public static final String BITMAP8_STR = "bitmap8";
	public static final String BITMAP16_STR = "bitmap16";
	public static final String BITMAP24_STR = "bitmap24";
	public static final String BITMAP32_STR = "bitmap32";
	public static final String BITMAP40_STR = "bitmap40";
	public static final String BITMAP48_STR = "bitmap48";
	public static final String BITMAP56_STR = "bitmap56";
	public static final String BITMAP64_STR = "bitmap64";
	public static final String UINT8_STR = "uint8";
	public static final String UINT16_STR = "uint16";
	public static final String UINT24_STR = "uint24";
	public static final String UINT32_STR = "uint32";
	public static final String UINT40_STR = "uint40";
	public static final String UINT48_STR = "uint48";
	public static final String UINT56_STR = "uint56";
	public static final String UINT64_STR = "uint64";
	public static final String INT8_STR = "int8";
	public static final String INT16_STR = "int16";
	public static final String INT24_STR = "int24";
	public static final String INT32_STR = "int32";
	public static final String INT40_STR = "int40";
	public static final String INT48_STR = "int48";
	public static final String INT56_STR = "int56";
	public static final String INT64_STR = "int64";
	public static final String ENUM8_STR = "enum8";
	public static final String ENUM16_STR = "enum16";
	public static final String SEMI_STR = "semi";
	public static final String FLOAT_STR = "float";
	public static final String DOUBLE_STR = "double";
	public static final String OSTRING_STR = "ostring";
	public static final String CSTRING_STR = "cstring";
	public static final String LOSTRING_STR = "lostring";
	public static final String LCSTRING_STR = "lcstring";
	public static final String ARRAY_STR = "array";
	public static final String STRUCT_STR = "struct";
	public static final String SET_STR = "set";
	public static final String BAG_STR = "bag";
	public static final String TIME_STR = "time";
	public static final String DATE_STR = "date";
	public static final String UTC_STR = "utc";
	public static final String CID_STR = "cid";
	public static final String AID_STR = "aid";
	public static final String OID_STR = "oid";
	public static final String IEEE_STR = "ieee";
	public static final String SECKEY_STR = "seckey";

	private Byte id;
	private String name;
	private String description;
	private Integer length;
	private String invalid;
	private Boolean analog = Boolean.FALSE;

	public Boolean isAnalog() {
		return analog;
	}

	public void setAnalog(Boolean analog) {
		this.analog = analog;
	}

	public String getInvalid() {
		return invalid;
	}

	public void setInvalid(String invalid) {
		this.invalid = invalid;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Byte getId() {
		return id;
	}

	public void setId(Byte id) {
		this.id = id;
	}

	public ZclDatatype(Byte id, String name, String description, Integer length, String invalid, Boolean analog) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.length = length;
		this.invalid = invalid;
		this.analog = analog;
	}

	@Override
	public String toString() {
		return "ZclDatatype [id=0x" + String.format("%02X", id) + ", name=" + name + ", description=" + description
				+ ", length=" + length + ", invalid=" + invalid + ", analog=" + analog + "]";
	}

	public static boolean isPositive32(int value) {
		int mask = 0x80000000;
		return (value & mask) >>> 31 == 0;
	}

	public static boolean isPositive64(long value) {
		long mask = 0x8000000000000000l;
		return (value & mask) >>> 63 == 0;
	}

	public static boolean isPositive(byte[] b) {
		int mask = 0x80;
		return ((int) b[b.length - 1] & mask) >>> 7 == 0;
	}

	public static Float bytes2float(byte[] b) {
		Float f = null;
		if (b != null) {
			// sign * 2^(exponent) * (1+mantissa/(2^23))
			int value = new BigInteger(b).intValue();
			int sign = (isPositive32(value)) ? 1 : -1;

			int mask1 = 0x007FFFFF;
			int value1 = ByteUtils.shift9BitsUnsigned(value);
			int exponent = value1 - 127;
			int mantissa = value & mask1;
			f = (float) ((sign) * Math.pow(2, exponent) * (1 + mantissa / Math.pow(2, 23)));
		}
		return f;
	}

	public static String bytes2string(byte[] b) {
		String s = "";
		if (b != null) {
			int length = 0;
			for (int i = 0; i < b.length; i++) {
				if (b[b.length - 1 - i] != 0) {
					length++;
				} else {
					break;
				}
			}
			ByteBuffer bb = ByteBuffer.allocate(length);
			for (int i = 0; i < b.length; i++) {
				if (b[b.length - 1 - i] != 0) {
					bb.put(b[b.length - 1 - i]);
				}
			}
			s = new String(bb.array());
		}
		return s;
	}

	public static Long bytes2int(byte[] b) {
		Long result = null;
		if (b != null) {
			result = new BigInteger(b).longValue();
		}
		return result;
	}

	public static BigInteger bytes2uint(byte[] b) {
		BigInteger result = null;
		if (b != null) {
			byte[] positiveAr = new byte[b.length + 1];
			positiveAr[0] = 0;
			for (int i = 0; i < b.length; i++) {
				positiveAr[i + 1] = b[i];
			}
			result = new BigInteger(positiveAr);
		}
		return result;
	}

	public static Boolean bytes2boolean(byte[] b) {
		Boolean bool = null;
		if (b != null) {
			int value = new BigInteger(b).intValue();
			bool = value > 0 ? Boolean.TRUE : Boolean.FALSE;
		}
		return bool;
	}

	public static byte[] strValue2byteArray(ZclAttribute att, String strValue) {
		byte[] retArr = new byte[0];
		switch (att.getDatatype().getId()) {
		case 0x00:
			retArr = new byte[0];
			break;
		case ZClusterLibrary.ZCL_DATATYPE_BOOLEAN:
			Boolean b = new Boolean(strValue);
			retArr = b? new byte[] { 0x01 } : new byte[] { 0x00 };
			break;
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP8:
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP16:
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP24:
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP32:
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP40:
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP48:
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP56:
		case ZClusterLibrary.ZCL_DATATYPE_BITMAP64:
			//TODO: Comprobar escritura/lectura/reporte de desintos tipos de bitmap
			retArr = strValue2BitmapByteArray(strValue, att);
			break;
		case ZClusterLibrary.ZCL_DATATYPE_UINT8:
		case ZClusterLibrary.ZCL_DATATYPE_UINT16:
		case ZClusterLibrary.ZCL_DATATYPE_UINT24:
		case ZClusterLibrary.ZCL_DATATYPE_UINT32:
		case ZClusterLibrary.ZCL_DATATYPE_UINT40:
		case ZClusterLibrary.ZCL_DATATYPE_UINT48:
		case ZClusterLibrary.ZCL_DATATYPE_UINT56:
		case ZClusterLibrary.ZCL_DATATYPE_UINT64:
		case ZClusterLibrary.ZCL_DATATYPE_INT8:
		case ZClusterLibrary.ZCL_DATATYPE_INT16:
		case ZClusterLibrary.ZCL_DATATYPE_INT24:
		case ZClusterLibrary.ZCL_DATATYPE_INT32:
		case ZClusterLibrary.ZCL_DATATYPE_INT40:
		case ZClusterLibrary.ZCL_DATATYPE_INT48:
		case ZClusterLibrary.ZCL_DATATYPE_INT56:
		case ZClusterLibrary.ZCL_DATATYPE_INT64: 
			retArr = strInt2littleEndianBytes(strValue, att.getDatatype());
			break;
		case ZClusterLibrary.ZCL_DATATYPE_ENUM8:
			retArr = strValue2Enum8ByteArray(strValue);
			break;
		case ZClusterLibrary.ZCL_DATATYPE_ENUM16:
			retArr = strValue2Enum16ByteArray(strValue);
			break;
		case ZClusterLibrary.ZCL_DATATYPE_SINGLE_PREC:
			retArr = ByteBuffer.allocate(Float.SIZE / Byte.SIZE).putFloat(Float.parseFloat(strValue)).array();
			break;
		case ZClusterLibrary.ZCL_DATATYPE_DOUBLE_PREC:
			retArr = ByteBuffer.allocate(Double.SIZE / Byte.SIZE).putDouble(Double.parseDouble(strValue)).array();
			break;
		// Strings:
		case 0x41:
		case 0x42:
			retArr = strValue2StringByteArray(strValue);
			break;
		// Long strings:
		case 0x43:
		case 0x44:
			retArr = strValue2LongStringByteArray(strValue);
			break;
		case ZClusterLibrary.ZCL_DATATYPE_UTC:
			retArr = strValue2UtcByteArray(strValue);
			break;
		case ZClusterLibrary.ZCL_DATATYPE_IEEE_ADDR:
			retArr = new OctaByte(Long.decode(strValue)).getAddressReverse();
			break;
		}

		return retArr;
	}

	private static byte[] strValue2BitmapByteArray(String strValue, ZclAttribute att) {
		byte[] retArr = new byte[att.getDatatype().getLength()];
		byte[] auxEnum = new BigInteger(strValue).abs().toByteArray();
		if (auxEnum.length > retArr.length)
			return new byte[0];
		for (int i = 0; i < auxEnum.length; i++) {
			retArr[i] = auxEnum[i];
		}
		return retArr;
	}

	private static byte[] strValue2Enum8ByteArray(String strValue) {
		byte[] retArr = new byte[1];
		byte[] aux = new BigInteger(strValue).toByteArray();
		if (aux == null || aux.length == 0)
			return new byte[0];
		retArr[0] = aux[0];
		return retArr;
	}

	private static byte[] strValue2Enum16ByteArray(String strValue) {
		byte[] retArr = new byte[2];
		byte[] aux = new BigInteger(strValue).toByteArray();
		if (aux == null || aux.length == 0)
			return new byte[0];
		retArr[0] = aux[0];
		if (aux.length > 1)
			retArr[1] = aux[1];
		return retArr;
	}

	private static byte[] strValue2StringByteArray(String strValue) {
		int length = strValue.length();
		DoubleByte lengthdb = new DoubleByte(length);
		if (lengthdb.getMsb() != 0) {
			return new byte[0];
		}
		ByteBuffer bb = ByteBuffer.allocate(strValue.length() + 1); 
		bb.put((byte)lengthdb.getLsb());
		bb.put(strValue.getBytes());
		return bb.array();
	}

	private static byte[] strValue2LongStringByteArray(String strValue) {
		int length = strValue.length();
		DoubleByte lengthdb = new DoubleByte(length);
		ByteBuffer bb = ByteBuffer.allocate(strValue.length() + 1);
		//TODO  Orden correcto?
		bb.put((byte)lengthdb.getLsb());
		bb.put((byte)lengthdb.getMsb());
		bb.put(strValue.getBytes());
		return bb.array();
	}

	private static byte[] strValue2UtcByteArray(String strValue) {
		byte[] aux = new BigInteger(strValue).toByteArray();
		byte[] retArr = new byte[] {0, 0, 0, 0};
		for (int i = 0; i < aux.length; i++) {
			retArr[i] = aux[aux.length - 1 - i];
		}
		return retArr;
	}

	private static byte[] strInt2littleEndianBytes(String strValue, ZclDatatype dt) {
		byte[] bigEndian = new BigInteger(strValue).toByteArray();
		byte[] littleEndian = new byte[dt.getLength()];
		if (dt.isSignedInteger() && (bigEndian[0] & 0x80) > 0) {
			for (int i = 0; i < littleEndian.length; i++) {
				littleEndian[i] = (byte) 0xFF;
			}
		}
		for (int i = 0; i < bigEndian.length; i++) {
			littleEndian[i] = bigEndian[bigEndian.length - 1 - i];
		}
		return littleEndian;
	}
	
	public Object clone() {
		ZclDatatype newType = new ZclDatatype(id, name, description, length, invalid, analog);
		return newType;
	}
	
	public boolean isSignedInteger() {
		return (this.id <= ZClusterLibrary.ZCL_DATATYPE_INT64 &&
				this.id >= ZClusterLibrary.ZCL_DATATYPE_INT8);
	}
}
