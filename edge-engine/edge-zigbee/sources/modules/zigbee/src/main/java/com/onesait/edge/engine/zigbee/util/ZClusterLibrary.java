package com.onesait.edge.engine.zigbee.util;


public class ZClusterLibrary {
	
	// General Clusters
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_BASIC = new DoubleByte(0x0000);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_POWER_CFG = new DoubleByte(0x0001);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_DEVICE_TEMP_CONFIG = new DoubleByte(0x0002);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_IDENTIFY = new DoubleByte(0x0003);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_GROUPS = new DoubleByte(0x0004);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_SCENES = new DoubleByte(0x0005);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_ON_OFF = new DoubleByte(0x0006);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_ON_OFF_SWITCH_CONFIG = new DoubleByte(0x0007);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_LEVEL_CONTROL = new DoubleByte(0x0008);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_ALARMS = new DoubleByte(0x0009);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_TIME = new DoubleByte(0x000A);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_LOCATION = new DoubleByte(0x000B);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_ANALOG_INPUT_BASIC = new DoubleByte(0x000C);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_ANALOG_OUTPUT_BASIC =	new DoubleByte(0x000D);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_ANALOG_VALUE_BASIC = new DoubleByte(0x000E);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_BINARY_INPUT_BASIC = new DoubleByte(0x000F);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_BINARY_OUTPUT_BASIC = new DoubleByte(0x0010);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_BINARY_VALUE_BASIC = new DoubleByte(0x0011);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_MULTISTATE_INPUT_BASIC = new DoubleByte(0x0012);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_MULTISTATE_OUTPUT_BASIC = new DoubleByte(0x0013);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_MULTISTATE_VALUE_BASIC = new DoubleByte(0x0014);
	public static final DoubleByte ZCL_CLUSTER_ID_GREEN_POWER_PROXY = new DoubleByte(0x001A);
	public static final DoubleByte ZCL_CLUSTER_ID_OTA = new DoubleByte(0x0019);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_POLL_CONTROL = new DoubleByte(0x0020);
	public static final DoubleByte ZCL_CLUSTER_ID_GEN_KEY_ESTABLISHMENT = new DoubleByte(0x0800);
	
	// Closures Clusters
	public static final DoubleByte ZCL_CLUSTER_ID_CLOSURES_SHADE_CONFIG = new DoubleByte(0x0100);

	// HVAC Clusters
	public static final DoubleByte ZCL_CLUSTER_ID_HVAC_PUMP_CONFIG_CONTROL = new DoubleByte(0x0200);
	public static final DoubleByte ZCL_CLUSTER_ID_HAVC_THERMOSTAT = new DoubleByte(0x0201);
	public static final DoubleByte ZCL_CLUSTER_ID_HAVC_FAN_CONTROL = new DoubleByte(0x0202);
	public static final DoubleByte ZCL_CLUSTER_ID_HAVC_DIHUMIDIFICATION_CONTROL = new DoubleByte(0x0203);
	public static final DoubleByte ZCL_CLUSTER_ID_HAVC_USER_INTERFACE_CONFIG = new DoubleByte(0x0204);

	// Lighting Clusters
	public static final DoubleByte ZCL_CLUSTER_ID_LIGHTING_COLOR_CONTROL = new DoubleByte(0x0300);
	public static final DoubleByte ZCL_CLUSTER_ID_LIGHTING_BALLAST_CONFIG = new DoubleByte(0x0301);
	
	// Measurement and Sensing Clusters
	public static final DoubleByte ZCL_CLUSTER_ID_MS_ILLUMINANCE_MEASUREMENT = new DoubleByte(0x0400);
	public static final DoubleByte ZCL_CLUSTER_ID_MS_ILLUMINANCE_LEVEL_SENSING_CONFIG = new DoubleByte(0x0401);
	public static final DoubleByte ZCL_CLUSTER_ID_MS_TEMPERATURE_MEASUREMENT = new DoubleByte(0x0402);
	public static final DoubleByte ZCL_CLUSTER_ID_MS_PRESSURE_MEASUREMENT = new DoubleByte(0x0403);
	public static final DoubleByte ZCL_CLUSTER_ID_MS_FLOW_MEASUREMENT = new DoubleByte(0x0404);
	public static final DoubleByte ZCL_CLUSTER_ID_MS_RELATIVE_HUMIDITY = new DoubleByte(0x0405);
	public static final DoubleByte ZCL_CLUSTER_ID_MS_OCCUPANCY_SENSING = new DoubleByte(0x0406);

	// Security and Safety (SS) Clusters
	public static final DoubleByte ZCL_CLUSTER_ID_SS_IAS_ZONE =	new DoubleByte(0x0500);
	public static final DoubleByte ZCL_CLUSTER_ID_SS_IAS_ACE = new DoubleByte(0x0501);
	public static final DoubleByte ZCL_CLUSTER_ID_SS_IAS_WD = new DoubleByte(0x0502);
	    
	// Protocol Interfaces
	public static final DoubleByte ZCL_CLUSTER_ID_PI_GENERIC_TUNNEL = new DoubleByte(0x0600);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_BACNET_PROTOCOL_TUNNEL = new DoubleByte(0x0601);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_ANALOG_INPUT_BACNET_REG = new DoubleByte(0x0602);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_ANALOG_INPUT_BACNET_EXT = new DoubleByte(0x0603);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_ANALOG_OUTPUT_BACNET_REG = new DoubleByte(0x0604);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_ANALOG_OUTPUT_BACNET_EXT = new DoubleByte(0x0605);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_ANALOG_VALUE_BACNET_REG = new DoubleByte(0x0606);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_ANALOG_VALUE_BACNET_EXT = new DoubleByte(0x0607);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_BINARY_INPUT_BACNET_REG = new DoubleByte(0x0608);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_BINARY_INPUT_BACNET_EXT = new DoubleByte(0x0609);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_BINARY_OUTPUT_BACNET_REG = new DoubleByte(0x060A);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_BINARY_OUTPUT_BACNET_EXT = new DoubleByte(0x060B);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_BINARY_VALUE_BACNET_REG = new DoubleByte(0x060C);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_BINARY_VALUE_BACNET_EXT = new DoubleByte(0x060D);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_MULTISTATE_INPUT_BACNET_REG = new DoubleByte(0x060E);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_MULTISTATE_INPUT_BACNET_EXT = new DoubleByte(0x060F);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_MULTISTATE_OUTPUT_BACNET_REG = new DoubleByte(0x0610);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_MULTISTATE_OUTPUT_BACNET_EXT = new DoubleByte(0x0611);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_MULTISTATE_VALUE_BACNET_REG = new DoubleByte(0x0612);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_MULTISTATE_VALUE_BACNET_EXT = new DoubleByte(0x0613);
	public static final DoubleByte ZCL_CLUSTER_ID_PI_11073_PROTOCOL_TUNNEL = new DoubleByte(0x0614);

	// Advanced Metering Initiative (SE) Clusters 
	public static final DoubleByte ZCL_CLUSTER_ID_SE_PRICING = new DoubleByte(0x0700);
	public static final DoubleByte ZCL_CLUSTER_ID_SE_LOAD_CONTROL = new DoubleByte(0x0701);
	public static final DoubleByte ZCL_CLUSTER_ID_SE_SIMPLE_METERING = new DoubleByte(0x0702);
	public static final DoubleByte ZCL_CLUSTER_ID_SE_MESSAGE = new DoubleByte(0x0703);
	public static final DoubleByte ZCL_CLUSTER_ID_SE_REGISTRATION = new DoubleByte(0x0704);
	public static final DoubleByte ZCL_CLUSTER_ID_SE_SE_TUNNELING = new DoubleByte(0x0705);
	public static final DoubleByte ZCL_CLUSTER_ID_SE_PRE_PAYMENT = new DoubleByte(0x0706); 
	
	// General Clusters
	public static final String ZCL_CLUSTER_ID_STR_GEN_BASIC                       	  	="0000";
	public static final String ZCL_CLUSTER_ID_STR_GEN_POWER_CFG                         ="0001";
	public static final String ZCL_CLUSTER_ID_STR_GEN_DEVICE_TEMP_CONFIG                ="0002";
	public static final String ZCL_CLUSTER_ID_STR_GEN_IDENTIFY                          ="0003";
	public static final String ZCL_CLUSTER_ID_STR_GEN_GROUPS                            ="0004";
	public static final String ZCL_CLUSTER_ID_STR_GEN_SCENES                            ="0005";
	public static final String ZCL_CLUSTER_ID_STR_GEN_ON_OFF                            ="0006";
	public static final String ZCL_CLUSTER_ID_STR_GEN_ON_OFF_SWITCH_CONFIG              ="0007";
	public static final String ZCL_CLUSTER_ID_STR_GEN_LEVEL_CONTROL                     ="0008";
	public static final String ZCL_CLUSTER_ID_STR_GEN_ALARMS                            ="0009";
	public static final String ZCL_CLUSTER_ID_STR_GEN_TIME                              ="000A";
	public static final String ZCL_CLUSTER_ID_STR_GEN_LOCATION                          ="000B";
	public static final String ZCL_CLUSTER_ID_STR_GEN_ANALOG_INPUT_BASIC                ="000C";
	public static final String ZCL_CLUSTER_ID_STR_GEN_ANALOG_OUTPUT_BASIC               ="000D";
	public static final String ZCL_CLUSTER_ID_STR_GEN_ANALOG_VALUE_BASIC                ="000E";
	public static final String ZCL_CLUSTER_ID_STR_GEN_BINARY_INPUT_BASIC                ="000F";
	public static final String ZCL_CLUSTER_ID_STR_GEN_BINARY_OUTPUT_BASIC               ="0010";
	public static final String ZCL_CLUSTER_ID_STR_GEN_BINARY_VALUE_BASIC                ="0011";
	public static final String ZCL_CLUSTER_ID_STR_GEN_MULTISTATE_INPUT_BASIC            ="0012";
	public static final String ZCL_CLUSTER_ID_STR_GEN_MULTISTATE_OUTPUT_BASIC           ="0013";
	public static final String ZCL_CLUSTER_ID_STR_GEN_MULTISTATE_VALUE_BASIC            ="0014";
	public static final String ZCL_CLUSTER_ID_STR_GREEN_POWER_PROXY                     ="001A";
	public static final String ZCL_CLUSTER_ID_STR_OTA                                   ="0019";
	public static final String ZCL_CLUSTER_ID_STR_GEN_KEY_ESTABLISHMENT                 ="0800";

	// Closures Clusters
	public static final String ZCL_CLUSTER_ID_STR_CLOSURES_SHADE_CONFIG                 ="0100";

	// HVAC Clusters
	public static final String ZCL_CLUSTER_ID_STR_HVAC_PUMP_CONFIG_CONTROL              ="0200";
	public static final String ZCL_CLUSTER_ID_STR_HAVC_THERMOSTAT                       ="0201";
	public static final String ZCL_CLUSTER_ID_STR_HAVC_FAN_CONTROL                      ="0202";
	public static final String ZCL_CLUSTER_ID_STR_HAVC_DIHUMIDIFICATION_CONTROL         ="0203";
	public static final String ZCL_CLUSTER_ID_STR_HAVC_USER_INTERFACE_CONFIG   	        ="0204";

	// Lighting Clusters
	public static final String ZCL_CLUSTER_ID_STR_LIGHTING_COLOR_CONTROL                ="0300";
	public static final String ZCL_CLUSTER_ID_STR_LIGHTING_BALLAST_CONFIG               ="0301";
	  
	// Measurement and Sensing Clusters
	public static final String ZCL_CLUSTER_ID_STR_MS_ILLUMINANCE_MEASUREMENT            ="0400";
	public static final String ZCL_CLUSTER_ID_STR_MS_ILLUMINANCE_LEVEL_SENSING_CONFIG   ="0401";
	public static final String ZCL_CLUSTER_ID_STR_MS_TEMPERATURE_MEASUREMENT            ="0402";
	public static final String ZCL_CLUSTER_ID_STR_MS_PRESSURE_MEASUREMENT               ="0403";
	public static final String ZCL_CLUSTER_ID_STR_MS_FLOW_MEASUREMENT                   ="0404";
	public static final String ZCL_CLUSTER_ID_STR_MS_RELATIVE_HUMIDITY                  ="0405";
	public static final String ZCL_CLUSTER_ID_STR_MS_OCCUPANCY_SENSING                  ="0406";

	// Security and Safety (SS) Clusters
	public static final String ZCL_CLUSTER_ID_STR_SS_IAS_ZONE                           ="0500";
	public static final String ZCL_CLUSTER_ID_STR_SS_IAS_ACE                            ="0501";
	public static final String ZCL_CLUSTER_ID_STR_SS_IAS_WD                             ="0502";
	    
	// Protocol Interfaces
	public static final String ZCL_CLUSTER_ID_STR_PI_GENERIC_TUNNEL                     ="0600";
	public static final String ZCL_CLUSTER_ID_STR_PI_BACNET_PROTOCOL_TUNNEL             ="0601";
	public static final String ZCL_CLUSTER_ID_STR_PI_ANALOG_INPUT_BACNET_REG            ="0602";
	public static final String ZCL_CLUSTER_ID_STR_PI_ANALOG_INPUT_BACNET_EXT            ="0603";
	public static final String ZCL_CLUSTER_ID_STR_PI_ANALOG_OUTPUT_BACNET_REG           ="0604";
	public static final String ZCL_CLUSTER_ID_STR_PI_ANALOG_OUTPUT_BACNET_EXT           ="0605";
	public static final String ZCL_CLUSTER_ID_STR_PI_ANALOG_VALUE_BACNET_REG            ="0606";
	public static final String ZCL_CLUSTER_ID_STR_PI_ANALOG_VALUE_BACNET_EXT            ="0607";
	public static final String ZCL_CLUSTER_ID_STR_PI_BINARY_INPUT_BACNET_REG            ="0608";
	public static final String ZCL_CLUSTER_ID_STR_PI_BINARY_INPUT_BACNET_EXT            ="0609";
	public static final String ZCL_CLUSTER_ID_STR_PI_BINARY_OUTPUT_BACNET_REG           ="060A";
	public static final String ZCL_CLUSTER_ID_STR_PI_BINARY_OUTPUT_BACNET_EXT           ="060B";
	public static final String ZCL_CLUSTER_ID_STR_PI_BINARY_VALUE_BACNET_REG            ="060C";
	public static final String ZCL_CLUSTER_ID_STR_PI_BINARY_VALUE_BACNET_EXT            ="060D";
	public static final String ZCL_CLUSTER_ID_STR_PI_MULTISTATE_INPUT_BACNET_REG        ="060E";
	public static final String ZCL_CLUSTER_ID_STR_PI_MULTISTATE_INPUT_BACNET_EXT        ="060F";
	public static final String ZCL_CLUSTER_ID_STR_PI_MULTISTATE_OUTPUT_BACNET_REG       ="0610";
	public static final String ZCL_CLUSTER_ID_STR_PI_MULTISTATE_OUTPUT_BACNET_EXT       ="0611";
	public static final String ZCL_CLUSTER_ID_STR_PI_MULTISTATE_VALUE_BACNET_REG        ="0612";
	public static final String ZCL_CLUSTER_ID_STR_PI_MULTISTATE_VALUE_BACNET_EXT        ="0613";
	public static final String ZCL_CLUSTER_ID_STR_PI_11073_PROTOCOL_TUNNEL              ="0614";

	// Advanced Metering Initiative (SE) Clusters 
	public static final String ZCL_CLUSTER_ID_STR_SE_PRICING                            ="0700";
	public static final String ZCL_CLUSTER_ID_STR_SE_LOAD_CONTROL                       ="0701";
	public static final String ZCL_CLUSTER_ID_STR_SE_SIMPLE_METERING                    ="0702";
	public static final String ZCL_CLUSTER_ID_STR_SE_MESSAGE                            ="0703";
	public static final String ZCL_CLUSTER_ID_STR_SE_REGISTRATION                       ="0704";
	public static final String ZCL_CLUSTER_ID_STR_SE_SE_TUNNELING                       ="0705";
	public static final String ZCL_CLUSTER_ID_STR_SE_PRE_PAYMENT                        ="0706"; 	
		
	/*** Frame Control bit mask ***/
	public static final int ZCL_FRAME_CONTROL_TYPE                          =0x03;
	public static final int ZCL_FRAME_CONTROL_MANU_SPECIFIC                 =0x04;
	public static final int ZCL_FRAME_CONTROL_DIRECTION                     =0x08;
	public static final int ZCL_FRAME_CONTROL_DISABLE_DEFAULT_RSP           =0x10;

	/*** Frame Types ***/
	public static final int ZCL_FRAME_TYPE_PROFILE_CMD                      =0x00;
	public static final int ZCL_FRAME_TYPE_SPECIFIC_CMD                     =0x01;

	/*** Frame Client/Server Directions ***/
	public static final int ZCL_FRAME_CLIENT_SERVER_DIR                     =0x00;
	public static final int ZCL_FRAME_SERVER_CLIENT_DIR                     =0x01;

	/*** Chipcon Manufacturer Code ***/ 
	public static final int CC_MANUFACTURER_CODE                            =0x1001;

	/*** Foundation Command IDs ***/
	public static final byte ZCL_CMD_READ                                    =(byte)0x00;
	public static final byte ZCL_CMD_READ_RSP                                =(byte)0x01;
	public static final byte ZCL_CMD_WRITE                                   =(byte)0x02;
	public static final byte ZCL_CMD_WRITE_UNDIVIDED                         =(byte)0x03;
	public static final byte ZCL_CMD_WRITE_RSP                               =(byte)0x04;
	public static final byte ZCL_CMD_WRITE_NO_RSP                            =(byte)0x05;
	public static final byte ZCL_CMD_CONFIG_REPORT                           =(byte)0x06;
	public static final byte ZCL_CMD_CONFIG_REPORT_RSP                       =(byte)0x07;
	public static final byte ZCL_CMD_READ_REPORT_CFG                         =(byte)0x08;
	public static final byte ZCL_CMD_READ_REPORT_CFG_RSP                     =(byte)0x09;
	public static final byte ZCL_CMD_REPORT                                  =(byte)0x0a;
	public static final int ZCL_CMD_DEFAULT_RSP                             =	0x0b;
	public static final byte ZCL_CMD_DISCOVER                                =(byte)0x0c;
	public static final byte ZCL_CMD_DISCOVER_RSP                            =(byte)0x0d;

	public static final int ZCL_CMD_MAX                                     =ZCL_CMD_DISCOVER_RSP;

	/*** Data Types ***/
	public static final byte ZCL_DATATYPE_NO_DATA                            =(byte)0x00;
	public static final byte ZCL_DATATYPE_DATA8                              =(byte)0x08;
	public static final byte ZCL_DATATYPE_DATA16                             =(byte)0x09;
	public static final byte ZCL_DATATYPE_DATA24                             =(byte)0x0a;
	public static final byte ZCL_DATATYPE_DATA32                             =(byte)0x0b;
	public static final byte ZCL_DATATYPE_DATA40                             =(byte)0x0c;
	public static final byte ZCL_DATATYPE_DATA48                             =(byte)0x0d;
	public static final byte ZCL_DATATYPE_DATA56                             =(byte)0x0e;
	public static final byte ZCL_DATATYPE_DATA64                             =(byte)0x0f;
	public static final byte ZCL_DATATYPE_BOOLEAN                            =(byte)0x10;
	public static final byte ZCL_DATATYPE_BITMAP8                            =(byte)0x18;
	public static final byte ZCL_DATATYPE_BITMAP16                           =(byte)0x19;
	public static final byte ZCL_DATATYPE_BITMAP24                           =(byte)0x1a;
	public static final byte ZCL_DATATYPE_BITMAP32                           =(byte)0x1b;
	public static final byte ZCL_DATATYPE_BITMAP40                           =(byte)0x1c;
	public static final byte ZCL_DATATYPE_BITMAP48                           =(byte)0x1d;
	public static final byte ZCL_DATATYPE_BITMAP56                           =(byte)0x1e;
	public static final byte ZCL_DATATYPE_BITMAP64                           =(byte)0x1f;
	public static final byte ZCL_DATATYPE_UINT8                              =(byte)0x20;
	public static final byte ZCL_DATATYPE_UINT16                             =(byte)0x21;
	public static final byte ZCL_DATATYPE_UINT24                             =(byte)0x22;
	public static final byte ZCL_DATATYPE_UINT32                             =(byte)0x23;
	public static final byte ZCL_DATATYPE_UINT40                             =(byte)0x24;
	public static final byte ZCL_DATATYPE_UINT48                             =(byte)0x25;
	public static final byte ZCL_DATATYPE_UINT56                             =(byte)0x26;
	public static final byte ZCL_DATATYPE_UINT64                             =(byte)0x27;
	public static final byte ZCL_DATATYPE_INT8                               =(byte)0x28;
	public static final byte ZCL_DATATYPE_INT16                              =(byte)0x29;
	public static final byte ZCL_DATATYPE_INT24                              =(byte)0x2a;
	public static final byte ZCL_DATATYPE_INT32                              =(byte)0x2b;
	public static final byte ZCL_DATATYPE_INT40                              =(byte)0x2c;
	public static final byte ZCL_DATATYPE_INT48                              =(byte)0x2d;
	public static final byte ZCL_DATATYPE_INT56                              =(byte)0x2e;
	public static final byte ZCL_DATATYPE_INT64                              =(byte)0x2f;
	public static final byte ZCL_DATATYPE_ENUM8                              =(byte)0x30;
	public static final byte ZCL_DATATYPE_ENUM16                             =(byte)0x31;
	public static final byte ZCL_DATATYPE_SEMI_PREC                          =(byte)0x38;
	public static final byte ZCL_DATATYPE_SINGLE_PREC                        =(byte)0x39;
	public static final byte ZCL_DATATYPE_DOUBLE_PREC                        =(byte)0x3a;
	public static final byte ZCL_DATATYPE_OCTET_STR                          =(byte)0x41;
	public static final byte ZCL_DATATYPE_CHAR_STR                           =(byte)0x42;
	public static final byte ZCL_DATATYPE_LONG_OCTET_STR                     =(byte)0x43;
	public static final byte ZCL_DATATYPE_LONG_CHAR_STR                      =(byte)0x44;
	public static final byte ZCL_DATATYPE_ARRAY                              =(byte)0x48;
	public static final byte ZCL_DATATYPE_STRUCT                             =(byte)0x4c;
	public static final byte ZCL_DATATYPE_SET                                =(byte)0x50;
	public static final byte ZCL_DATATYPE_BAG                                =(byte)0x51;
	public static final byte ZCL_DATATYPE_TOD                                =(byte)0xe0;
	public static final byte ZCL_DATATYPE_DATE                               =(byte)0xe1;
	public static final byte ZCL_DATATYPE_UTC                                =(byte)0xe2;
	public static final byte ZCL_DATATYPE_CLUSTER_ID                         =(byte)0xe8;
	public static final byte ZCL_DATATYPE_ATTR_ID                            =(byte)0xe9;
	public static final byte ZCL_DATATYPE_BAC_OID                            =(byte)0xea;
	public static final byte ZCL_DATATYPE_IEEE_ADDR                          =(byte)0xf0;
	public static final byte ZCL_DATATYPE_128_BIT_SEC_KEY                    =(byte)0xf1;
	public static final byte ZCL_DATATYPE_UNKNOWN                            =(byte)0xff;

	/*** Error Status Codes ***/
	public static final byte ZCL_STATUS_SUCCESS                              =(byte)0x00;
	public static final byte ZCL_STATUS_FAILURE                              =(byte)0x01;
	// =0x02-=0x7D are reserved.
	public static final byte ZCL_STATUS_NOT_AUTHORIZED                       =(byte)0x7E;
	public static final byte ZCL_STATUS_MALFORMED_COMMAND                    =(byte)0x80;
	public static final byte ZCL_STATUS_UNSUP_CLUSTER_COMMAND                =(byte)0x81;
	public static final byte ZCL_STATUS_UNSUP_GENERAL_COMMAND                =(byte)0x82;
	public static final byte ZCL_STATUS_UNSUP_MANU_CLUSTER_COMMAND           =(byte)0x83;
	public static final byte ZCL_STATUS_UNSUP_MANU_GENERAL_COMMAND           =(byte)0x84;
	public static final byte ZCL_STATUS_INVALID_FIELD                        =(byte)0x85;
	public static final byte ZCL_STATUS_UNSUPPORTED_ATTRIBUTE                =(byte)0x86;
	public static final byte ZCL_STATUS_INVALID_VALUE                        =(byte)0x87;
	public static final byte ZCL_STATUS_READ_ONLY                            =(byte)0x88;
	public static final byte ZCL_STATUS_INSUFFICIENT_SPACE                   =(byte)0x89;
	public static final byte ZCL_STATUS_DUPLICATE_EXISTS                     =(byte)0x8a;
	public static final byte ZCL_STATUS_NOT_FOUND                            =(byte)0x8b;
	public static final byte ZCL_STATUS_UNREPORTABLE_ATTRIBUTE               =(byte)0x8c;
	public static final byte ZCL_STATUS_INVALID_DATA_TYPE                    =(byte)0x8d;
	public static final byte ZCL_STATUS_INVALID_SELECTOR                     =(byte)0x8e;
	public static final byte ZCL_STATUS_WRITE_ONLY                           =(byte)0x8f;
	public static final byte ZCL_STATUS_INCONSISTENT_STARTUP_STATE           =(byte)0x90;
	public static final byte ZCL_STATUS_DEFINED_OUT_OF_BAND                  =(byte)0x91;
	public static final byte ZCL_STATUS_INCONSISTENT                         =(byte)0x92;
	public static final byte ZCL_STATUS_ACTION_DENIED                        =(byte)0x93;
	public static final byte ZCL_STATUS_TIMEOUT                              =(byte)0x94;
	public static final byte ZCL_STATUS_ABORT                                =(byte)0x95;
	public static final byte ZCL_STATUS_INVALID_IMAGE                        =(byte)0x96;
	public static final byte ZCL_STATUS_WAIT_FOR_DATA                        =(byte)0x97;
	public static final byte ZCL_STATUS_NO_IMAGE_AVAILABLE                   =(byte)0x98;
	public static final byte ZCL_STATUS_REQUIRE_MORE_IMAGE                   =(byte)0x99;
	public static final byte ZCL_STATUS_OTA_SUCCES						   =(byte)0x00;
	public static final byte ZCL_STATUS_OTA_ABORT							   =(byte)0x95;
	public static final byte ZCL_STATUS_OTA_NOT_AUTHORIZED				   =(byte)0x7E;
	public static final int  ZCL_STATUS_OTA_INVALID_IMAGE					   =0x96;
	public static final byte ZCL_STATUS_OTA_WAIT_FOR_DATA					   =(byte)0x97;
	public static final byte ZCL_STATUS_OTA_NO_IMAGE_AVAILABLE			   =(byte)0x98;
	public static final byte ZCL_STATUS_OTA_MALFORMED_COMMAND				   =(byte)0x80;
	public static final byte ZCL_STATUS_OTA_UNSUP_CLUSTER_COMMAND			   =(byte)0x81;
	public static final byte ZCL_STATUS_OTA_REQUIRE_MORE_IMAGE			   =(byte)0x99;
	
	

	// =0xbd-bf are reserved.
	public static final byte ZCL_STATUS_HARDWARE_FAILURE                     =(byte)0xc0;
	public static final byte ZCL_STATUS_SOFTWARE_FAILURE                     =(byte)0xc1;
	public static final byte ZCL_STATUS_CALIBRATION_ERROR                    =(byte)0xc2;
	// =0xc3-=0xff are reserved.
	public static final byte ZCL_STATUS_CMD_HAS_RSP                          =(byte)0xFF; // Non-standard status (used for Default Rsp)

	/*** Attribute Access Control - bit masks ***/
	public static final byte ACCESS_CONTROL_READ                             =(byte)0x01;
	public static final byte ACCESS_CONTROL_WRITE                            =(byte)0x02;
	public static final byte ACCESS_CONTROL_COMMAND                          =(byte)0x04;

	public static final int ZCL_INVALID_CLUSTER_ID                         =0xFFFF;
	public static final int ZCL_ATTR_ID_MAX                                =0xFFFF;

	// Used by Configure Reporting Command
	public static final byte ZCL_SEND_ATTR_REPORTS                           =(byte)0x00;
	public static final byte ZCL_EXPECT_ATTR_REPORTS                         =(byte)0x01;
	  
	// Predefined Maximum String Length
	public static final int MAX_UTF8_STRING_LEN                              =50;

	// Used by zclReadWriteCB_t callback function
	public static final byte ZCL_OPER_LEN                                    =(byte)0x00; // Get length of attribute value to be read
	public static final byte ZCL_OPER_READ                                   =(byte)0x01; // Read attribute value
	public static final byte ZCL_OPER_WRITE                                  =(byte)0x02; // Write new attribute value
	
	/** OTA constants **/
	public static final byte	ZCL_OTA_IMAGE_NOTIFY							=(byte)0x00;
	public static final byte	ZCL_OTA_QUERY_NEXT_IMAGE_REQ					=(byte)0x01;
	public static final byte	ZCL_OTA_QUERY_NEXT_IMAGE_RSP					=(byte)0x02;
	public static final byte	ZCL_OTA_IMAGE_BLOCK_REQ							=(byte)0x03;
	public static final byte	ZCL_OTA_IMAGE_PAGE_REQ							=(byte)0x04;
	public static final byte	ZCL_OTA_IMAGE_BLOCK_RSP							=(byte)0x05;
	public static final byte	ZCL_OTA_UPGRADE_END_REQ							=(byte)0x06;
	public static final byte	ZCL_OTA_UPGRADE_END_RSP							=(byte)0x07;
	public static final byte	ZCL_OTA_QUERY_DEVICE_SPECIFIC_FILE_REQ			=(byte)0x08;
	public static final byte	ZCL_OTA_QUERY_DEVICE_SPECIFIC_FILE_RSP			=(byte)0x09;
	
	/** Atributos usados para decir si dispositivo KO o OK **/
	public static final DoubleByte ZCL_ATT_ONOFF = new DoubleByte(0x0000); //del cluster on_off
	public static final DoubleByte ZCL_ATT_CURRENT_LEVEL=new DoubleByte("0x0000"); //del cluster level control
	public static final DoubleByte ZCL_ATT_LOCAL_TEMPERATURE=new DoubleByte("0x0000"); //del cluster thermostat
	public static final DoubleByte ZCL_ATT_SYSTEM_MODE=new DoubleByte("0x001c"); //del cluster thermostat
	public static final DoubleByte ZCL_ATT_CURRENT_HUE= new DoubleByte("0x0000"); //del cluster color control
	public static final DoubleByte ZCL_ATT_CURRENT_SAT=new DoubleByte("0x0001"); //del cluster color control
	public static final DoubleByte ZCL_ATT_COLOR_TEMP=new DoubleByte("0x0007"); //del cluster color control
	public static final DoubleByte ZCL_ATT_IAS_STATUS=new DoubleByte("0x0001"); //del cluster ias zone
	public static final DoubleByte ZCL_ATT_INSTANTANEOUS_DEMAND=new DoubleByte("0x0400"); //cluster simple metering
	public static final DoubleByte ZCL_ATT_MEAZON_ACTIVE_POWERL1=new DoubleByte("0x2001"); //del cluster simple metering en meazon
	public static final DoubleByte ZCL_ATT_MEAZON_ACTIVE_POWERL2=new DoubleByte("0x2002");//cluster simple metering meazon
	public static final DoubleByte ZCL_ATT_MEAZON_ACTIVE_POWERL3=new DoubleByte("0x2003");//cluster simple metering meazon
	
	//Poll Control
	public static final DoubleByte ZCL_ATT_CHECK_IN=new DoubleByte(0x0000);
	public static final DoubleByte ZCL_ATT_LONG_POLL_INTERVAL= new DoubleByte(0x0001);
	public static final DoubleByte ZCL_ATT_SHORT_POLL_INTERVAL= new DoubleByte(0x0002);
	public static final DoubleByte ZCL_ATT_FAST_POLL_TIMEOUT= new DoubleByte(0x0003);
	
	
	

	private ZClusterLibrary() {}
}
