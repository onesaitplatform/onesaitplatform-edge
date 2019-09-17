package com.onesait.edge.engine.zigbee.util;

public class ZigbeeConstants {
	
	public static final byte COORDINATOR_ENDPOINT=0x08;
	public static final byte COORDINATOR_OTA_ENDPOINT=0x0E;
	public static final OctaByte INVALID_IEEE_ADDRESS = new OctaByte(0);
	public static final int ZIGBEE_ACTIVENET_SEC = 180;
	public static final int NWK_KEY_LENGTH_BYTES = 16;
	
	public static final DoubleByte ZB_PROFILE_HOME_AUTOMATION = new DoubleByte(0x0104);
	public static final DoubleByte ZB_PROFILE_LIGHT_LINK = new DoubleByte(0xC05E);
	
	public static final DoubleByte COORDINATOR_SHORT_ADDRESS =new DoubleByte(0x0000);
	public static final DoubleByte BROADCAST_SHORT_ADDRESS = new DoubleByte(0xFFFF);
	public static final DoubleByte ROUTING_DEVICES_BROADCAST_SHORT_ADDRESS = new DoubleByte(0xFFFC);
	public static final DoubleByte RX_ON_WHEN_IDDLE_DEVICES_BROADCAST_SHORT_ADDRESS = new DoubleByte(0xFFFD);
	
	public static final byte LOWEST_CHANNEL = 0x0B;
	public static final byte HIGHEST_CHANNEL = 0x1A;
	public static final int ALL_CHANNELS_MASK = 0x7FFF800;
	public static final int N_CHANNELS = HIGHEST_CHANNEL - LOWEST_CHANNEL + 1;
	public static final int WIFI_SHARED_CHANNELS_MASK = 0xE73000; // 12, 13, 16, 17, 18, 21, 22, 23 not allowed
	public static final int CHANNEL_B_MASK = 0x800;
	public static final long TIME_STANDARD_ORIGIN_EPOCH_SEC = 946684800;
	
	
	public static final String propertyTypeUSB_UART = "serial.port.communication.zigbee";
	
	private ZigbeeConstants() {
		throw new IllegalStateException("Constants class");
	}
}
