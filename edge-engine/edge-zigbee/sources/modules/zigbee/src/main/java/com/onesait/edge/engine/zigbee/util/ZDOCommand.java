package com.onesait.edge.engine.zigbee.util;


public class ZDOCommand {
	
	public static final DoubleByte NetworkAddressRequest 					= new DoubleByte(0x0000);
	public static final DoubleByte NetworkAddressResponse 					= new DoubleByte(0x8000);
	public static final DoubleByte IEEEAddressRequest 						= new DoubleByte(0x0001);
	public static final DoubleByte IEEEAddressResponse 						= new DoubleByte(0x8001);
	public static final DoubleByte NodeDescriptorRequest 					= new DoubleByte(0x0002);
	public static final DoubleByte NodeDescriptorResponse 					= new DoubleByte(0x8002);
	public static final DoubleByte SimpleDescriptorRequest 					= new DoubleByte(0x0004);
	public static final DoubleByte SimpleDescriptorResponse 				= new DoubleByte(0x8004);
	public static final DoubleByte ActiveEndpointsRequest 					= new DoubleByte(0x0005);
	public static final DoubleByte ActiveEndpointsResponse 					= new DoubleByte(0x8005);
	public static final DoubleByte MatchDescriptorRequest 					= new DoubleByte(0x0006);
	public static final DoubleByte MatchDescriptorResponse 					= new DoubleByte(0x8006);
	public static final DoubleByte ComplexDescriptorRequest 				= new DoubleByte(0x0010);
	public static final DoubleByte ComplexDescriptorResponse 				= new DoubleByte(0x8010);
	public static final DoubleByte UserDescriptorRequest 					= new DoubleByte(0x0011);
	public static final DoubleByte UserDescriptorResponse 					= new DoubleByte(0x8011);
	public static final DoubleByte UserDescriptorSet 						= new DoubleByte(0x0014);
	public static final DoubleByte BindRequest								= new DoubleByte(0x0021);
	public static final DoubleByte BindResponse								= new DoubleByte(0x8021);
	public static final DoubleByte UnbindRequest							= new DoubleByte(0x0022);
	public static final DoubleByte UnbindResponse							= new DoubleByte(0x8022);
	public static final DoubleByte ManagementNetworkDiscoveryRequest 		= new DoubleByte(0x0030);
	public static final DoubleByte ManagementNetworkDiscoveryResponse 		= new DoubleByte(0x8030);
	public static final DoubleByte ManagementLQIRequest 					= new DoubleByte(0x0031);
	public static final DoubleByte ManagementLQIResponse 					= new DoubleByte(0x8031);
	public static final DoubleByte ManagementRtgRequest 					= new DoubleByte(0x0032);
	public static final DoubleByte ManagementRtgResponse 					= new DoubleByte(0x8032);
	public static final DoubleByte ManagementBindingRequest					= new DoubleByte(0x0033);
	public static final DoubleByte ManagementBindingResponse				= new DoubleByte(0x8033);
	public static final DoubleByte ManagementLeaveRequest 					= new DoubleByte(0x0034);
	public static final DoubleByte ManagementLeaveResponse 					= new DoubleByte(0x8034);
	public static final DoubleByte ManagementPermitJoinRequest 				= new DoubleByte(0x0036);
	public static final DoubleByte ManagementPermitJoinResponse 			= new DoubleByte(0x8036);
	public static final DoubleByte ManagementNetworkUpdateRequest 			= new DoubleByte(0x0038);
	public static final DoubleByte ManagementNetworkUpdateNotify 			= new DoubleByte(0x8038);
}
