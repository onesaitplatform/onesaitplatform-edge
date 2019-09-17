package com.onesait.edge.engine.zigbee.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.clients.IASZoneClient;
import com.onesait.edge.engine.zigbee.exception.UnknownCoordinatorMacException;
import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.frame.ZdoBindReq;
import com.onesait.edge.engine.zigbee.frame.ZdoBindReq.ADDRESS_MODE;
import com.onesait.edge.engine.zigbee.types.ZigbeeDescriptions;
import com.onesait.edge.engine.zigbee.util.AfRequest;
import com.onesait.edge.engine.zigbee.util.BuildMqttMsg;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.MqttMsgDetail;
import com.onesait.edge.engine.zigbee.util.OctaByte;
import com.onesait.edge.engine.zigbee.util.RequestMaps;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

public class ZclCluster {

	private DoubleByte id;
	private String name;
	private Map<DoubleByte, ZclAttribute> attributes = new ConcurrentHashMap<>();
	private Map<Byte, ZclCommand> commands = new HashMap<>();
	private Map<Byte, ZclCommand> serverCommands = new HashMap<>();
	private byte sequenceNumber;
	private ZclDevice device;
	private static final Logger LOG = LoggerFactory.getLogger(ZclCluster.class);
	private static final int MAX_READ_ATTR_RECORDS = 8;
	private boolean manSpec = false;
	private static final String KO_STATUS_STR = "KO";
	private static final String OK_STATUS_STR = "OK";
	private boolean configured=false;
	private ZigbeeClusterLibrary zcl;
	private static final String POWER_FACTOR="PowerFactor";
	
	public ZclCluster(DoubleByte id, String name, ZigbeeClusterLibrary zcl) {
		this.id = id;
		this.name = name;
		this.zcl = zcl;
	}

	public Map<Byte, ZclCommand> getCommands() {
		return commands;
	}

	public void setCommands(HashMap<Byte, ZclCommand> commands) {
		this.commands = commands;
	}
	public ZclAttribute getAttribute(String attname){
		for (Entry<DoubleByte, ZclAttribute> zatt : this.attributes.entrySet()) {
			ZclAttribute at = (ZclAttribute) zatt.getValue();
			if(at.getName().equalsIgnoreCase(attname)){
				return at;
			}
		}
		return null;
	}
	
	public DoubleByte getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void putAttribute(ZclAttribute zba) {
		this.attributes.put(zba.getId(), zba);
	}

	public Map<DoubleByte, ZclAttribute> getAttributes() {
		return this.attributes;
	}

	public ZclAttribute getAttribute(DoubleByte atId) {
		return this.attributes.get(atId);
	}

	public void putCommands(ZclCommand zbc) {
		this.commands.put(zbc.getId(), zbc);
	}

	public void putServerCommands(ZclCommand zbc) {
		this.serverCommands.put(zbc.getId(), zbc);
	}

	public Boolean isBindable() {
		for (ZclAttribute at : this.attributes.values()) {
			if (at.getReporting()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String str = "ZclCluster ";
		str += "[id=" + id + ", name=" + name;
		if (!this.attributes.isEmpty()) {
			str += "\n";
		}
		for (ZclAttribute attribute : this.attributes.values()) {
			str += "\t" + attribute + "\n";
			ZclDatatype datatype = attribute.getDatatype();
			if (datatype != null) {
				str += "\t\t" + datatype + "\n";
			}
		}
		if (!this.commands.isEmpty() && this.attributes.isEmpty()) {
			str += "\n";
		}
		for (ZclCommand command : this.commands.values()) {
			str += "\t" + command + "\n";
			List<ZclParam> params = command.getParams();
			if (!params.isEmpty()) {
				for (ZclParam param : params) {
					str += "\t\t" + param + "\n";
					ZclDatatype dt = param.getDatatype();
					if (dt != null) {
						str += "\t\t\t" + dt + "\n";
					}
				}
			}
		}
		str += "]";
		return str;
	}

	public Object clone() {
		ZclCluster zcl = new ZclCluster(this.id, this.name, this.zcl);
		zcl.setManSpec(this.isManSpec());
		zcl.setConfigured(this.configured);
		for (ZclAttribute zat : this.attributes.values()) {
			ZclAttribute newAt = (ZclAttribute) zat.clone();
			zcl.putAttribute(newAt);
		}
		for (ZclCommand zcmd : this.commands.values()) {
			ZclCommand newCmd = new ZclCommand(zcmd.getId(), zcmd.getName());
			newCmd.setSend(zcmd.isSend());
			newCmd.setServer(zcmd.isServer());
			cloneParams(newCmd, zcmd);
			zcl.putCommands(newCmd);
		}
		for (ZclCommand zcmd : this.serverCommands.values()) {
			ZclCommand newCmd = new ZclCommand(zcmd.getId(), zcmd.getName());
			newCmd.setSend(zcmd.isSend());
			newCmd.setServer(zcmd.isServer());
			cloneParams(newCmd, zcmd);
			zcl.putServerCommands(newCmd);
		}
		return zcl;
	}

	private void cloneParams(ZclCommand newCmd, ZclCommand zcmd) {
		for (ZclParam zp : zcmd.getParams()) {
			ZclParam newParam = new ZclParam(zp.getName(),
					(ZclDatatype)zp.getDatatype().clone());
			newParam.setAttId(zp.getAttId());
			newParam.setValue(zp.getValue());
			newCmd.putParam(newParam);
		}
	}
	public List<MqttMsgDetail> manageAFIncomingMSG(AfIncomingMsg zf) {
		List<MqttMsgDetail> msgs = new ArrayList<>();
		this.device.setLqi(zf.getLinkQuality());
		if (!zf.isClusterSpecific()) {
			if (zf.getZclCmd() == ZClusterLibrary.ZCL_CMD_CONFIG_REPORT_RSP) {
				manageAFConfigReportRsp(zf);
			} else if (zf.getZclCmd() == ZClusterLibrary.ZCL_CMD_READ_RSP) {
				msgs=manageAFReadRsp(zf);
			} else if (zf.getZclCmd() == ZClusterLibrary.ZCL_CMD_WRITE_RSP) {
				msgs=manageAFWriteRsp(zf);
			} else if (zf.getZclCmd() == ZClusterLibrary.ZCL_CMD_REPORT) {
				msgs=manageAFReport(zf);
			} else if (zf.getZclCmd() == (ZClusterLibrary.ZCL_CMD_DEFAULT_RSP)) {
				manageAFDefaultRsp(zf);
			}	
		}
		return msgs;
	}	
	
	
	/**
	 * Reads a config report response command, attribute by attribute.
	 * If status field is not equal than SUCCESS, marks attribute as unsupported
	 * 
	 * @param af AF_INCOMING_MSG
	 * @return String with the message contents
	 */
	private void manageAFConfigReportRsp(AfIncomingMsg af) {
		int plOffset = af.isManufacturerSpecific() ? 5 : 3;
		for (int i = plOffset; i < af.getLen(); i += 4) {
			// Analizamos cada registro
			String statusStr = (byte) af.getAfIncomingData()[i] == ZClusterLibrary.ZCL_STATUS_SUCCESS ?
					OK_STATUS_STR : KO_STATUS_STR;
			DoubleByte atId = null;
			if (af.getAfIncomingData()[i] != ZClusterLibrary.ZCL_STATUS_SUCCESS) {
				atId = new DoubleByte(af.getAfIncomingData()[i + 3], af.getAfIncomingData()[i + 2]);
			}
			// No podemos saber implicitamente de que atributo es esta respuesta
			if (atId != null && statusStr.equals(KO_STATUS_STR)) {
				ZclAttribute zat = this.attributes.get(atId);
				if(zat!=null){
				synchronized(zat.getAttributeLock()) {
					zat.setUnsupported(true);
					if (manCodesMatch(zat, af)) {
						// Meazon... Cambio de tipo de dato si es un plug antiguo
						if (af.getClusterID().equals(new DoubleByte(0x702)) &&
								(byte)af.getAfIncomingData()[i] == ZClusterLibrary.ZCL_STATUS_INVALID_VALUE &&
										(zat.getId().equals(new DoubleByte(0x3000)) ||
										 zat.getId().equals(new DoubleByte(0x3001)) ||
										 zat.getId().equals(new DoubleByte(0x3002)) ))
						{
							zat.getDatatype().setDescription("Single precision");
							zat.getDatatype().setId((byte)0x39);
							zat.getDatatype().setLength(4);
							zat.getDatatype().setName("float");
							zat.setUnsupported(false);
						}
					}
					markConfiguredAttribute(zat, this.id, this.device,af.getSrcEndpoint());
				}
				}
			}
			RequestMaps.removeAfRequestId(af);
		}
	}

	private boolean manCodesMatch(ZclAttribute zatt, AfIncomingMsg af) {
		return (checkManCode(zatt, af) != null && checkManCode(zatt, af).equals(true));
	}
	
	/**
	 * Reads a read attributes response command frame record by record and sets
	 * the values on the corresponding attributes.
	 * 
	 * Sets attribute value to null if the device doesn't support the attribute.
	 * This makes attribute lasttimeupdated have a not null value, which tells
	 * a response has been received for this attribute.
	 * 
	 * @param af
	 *            AF_INCOMING_MSG
	 * @param mqttConnection 
	 * @return String with the message contents
	 */
	private List<MqttMsgDetail> manageAFReadRsp(AfIncomingMsg af) {
		List<MqttMsgDetail> msgs=new ArrayList<>(); 
		try {
			int plOffset = af.isManufacturerSpecific() ? 5 : 3;
			for (int i = plOffset; i < af.getAfIncomingData().length; i++) {
				// Analizamos cada registro
				DoubleByte atId = new DoubleByte(af.getAfIncomingData()[i + 1], af.getAfIncomingData()[i]);
				Byte status = (byte) af.getAfIncomingData()[i + 2];
				ZclAttribute zattribute = this.attributes.get(atId);
				
				if (!manCodesMatch(zattribute, af)) {
					i += 3 - 1;
					continue;
				}
				synchronized(zattribute.getAttributeLock()) {
					if (status != ZClusterLibrary.ZCL_STATUS_SUCCESS) {
						if (zattribute != null) {
							zattribute.setUnsupported(true);
							markConfiguredAttribute(zattribute, this.id, this.device,af.getSrcEndpoint());
						}
						i += 3 - 1;
						continue;
					}
					
					ZclDatatype ztype = this.zcl.getDatatypes().get((byte) af.getAfIncomingData()[i + 3]);
	
					int[] lengthAndOffset = this.getLengthAndOffset(ztype, af.getAfIncomingData(), i + 4);
					if (lengthAndOffset == null || lengthAndOffset.length != 2) {
						// No hay manera de seguir
						break;
					}
					int atLength = lengthAndOffset[0];
					int stringOffset = lengthAndOffset[1];
	
					if (zattribute == null || atLength == 0) {
						// Pasamos al siguiente
						i += 4 + atLength - 1;
						continue;
					}
					markConfiguredAttribute(zattribute, this.id, this.device,af.getSrcEndpoint());
					
					this.readAndSetAttributeValueFromArray(atLength, af.getAfIncomingData(),
							zattribute, i + 4 + stringOffset, false);
					//ahumanes: revisar la siguiente linea. Puesta para saber si se ha recibido respuesta de un read request
//					LOG.info("Read rsp id: "+RequestMaps.getAfRequestId(af));
					RequestMaps.removeAfRequestId(af);
//					LOG.info("Read 2 rsp id: "+RequestMaps.getAfRequestId(af));
					if(af.getNwkAddr().equals(new DoubleByte(0x0000))){
					LOG.info("Attribute read rsp: {}. Value {} end point {}",zattribute.getName(),zattribute.getConvertedValue(),af.getSrcEndpoint());
					}
//					LOG.info("Attribute read rsp: {}. Value {} end point {}",zattribute.getName(),zattribute.getConvertedValue(),af.getSrcEndpoint());
					//ahumanes: enviar por mqtt
					this.attributes.put(zattribute.getId(), zattribute);
//					msgs.add(BuildMqttMsg.buildMqttMsg(this.device, zattribute, af,ZigbeeDescriptions.READ,this.name,this.zcl));
//					sendThroughMqtt(msgs.mqttConnection);
					i += 4 + stringOffset + atLength - 1;
				}
			}
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			// No hacemos nada porque ha llegado al fin del paquete.
		}
		return msgs;
	}


	private int[] getLengthAndOffset(ZclDatatype ztype, int[] array, int position) {
		int atLength, stringOffset = 0;
		int[] result = new int[0];
		if (ztype != null && ztype.getName().contains("string")) {
			// Si el tipo de dato es un string, la longitud es el(los)
			// primer(os) byte(s)
			if (ztype.getName().startsWith("l")) {
				atLength = new DoubleByte(array[position + 1], array[position]).getVal();
				stringOffset = 2;
			} else {
				atLength = array[position];
				stringOffset = 1;
			}
			result = new int[] {atLength, stringOffset};
		} else if (ztype != null && ztype.getLength() != null) {
			atLength = ztype.getLength().intValue();
			result = new int[] {atLength, stringOffset};
		}
		return result;
	}

	/**
	 * Reads a write attributes response command frame record by record
	 * 
	 * @param af
	 *            AF_INCOMING_MSG
	 * @return PropertyEventZigbee ArrayList
	 */
	
	//ahumanes: revisar/descomentar esto
	private List<MqttMsgDetail> manageAFWriteRsp(AfIncomingMsg af) {
		List<MqttMsgDetail> msgs=new ArrayList<>();
		try {
			int plOffset = af.isManufacturerSpecific() ? 5 : 3;
			for (int i = plOffset; i < af.getAfIncomingData().length; i += 3) {
				// Analizamos cada registro
//				Byte status = (byte) af.getAfIncomingData[i];
				AfRequest reqMade = RequestMaps.getAfRequest(af);
//				String text=RequestMaps.getAfRequestId(af);
				ZclAttribute att = null;
				if (reqMade != null) {
					DoubleByte atId = reqMade.getOptAttId();
					att = this.attributes.get(atId);
//					LOG.info("Peticion cluster: "+text);
					//ahumanes: prueba para borrar las peticiones de escritura hechas
					RequestMaps.removeAfRequestId(af);
					
				}
//				boolean success = status == ZClusterLibrary.ZCL_STATUS_SUCCESS;
				if (att != null) {
					//ahumanes: descomentar
					//msgs.add(BuildMqttMsg.buildWriteAttRsp(this.device,att,af,this.zcl,success,this.name));
					synchronized (att.getAttributeLock()) {
						markConfiguredAttribute(att, this.id, this.device,af.getSrcEndpoint());
					}
				}
			}
		} catch (IndexOutOfBoundsException iobe) {
		}
		return msgs;
	}

	/**
	 * Read a report command frame record by record and saves the attributes
	 * values into the corresponding classes.
	 * 
	 * @param af
	 *            AF_INCOMING_MSG
	 * @return String with the message contents
	 */
	private List<MqttMsgDetail> manageAFReport(AfIncomingMsg af) {
		List<MqttMsgDetail> msgs=new ArrayList<>();
		int plOffset = af.isManufacturerSpecific() ? 5 : 3;
		try {
			for (int i = plOffset; i < af.getAfIncomingData().length; i++) {
				// Analizamos cada registro
				DoubleByte atId = new DoubleByte(af.getAfIncomingData()[i + 1], af.getAfIncomingData()[i]);
				ZclAttribute zattribute = this.attributes.get(atId);
				Byte atDatatypeId = (byte) af.getAfIncomingData()[i + 2];
				ZclDatatype ztype = this.zcl.getDatatypes().get(atDatatypeId);

				int[] lengthAndOffset = this.getLengthAndOffset(ztype, af.getAfIncomingData(), i + 4);
				if (lengthAndOffset == null || lengthAndOffset.length != 2) {
					// No hay manera de seguir
					break;
				}
				int atLength = lengthAndOffset[0];
				int stringOffset = lengthAndOffset[1];

				if (zattribute == null || atLength == 0) {
					i += 3 + atLength - 1;
					continue;
				}

				synchronized (zattribute.getAttributeLock()) {
					if (ztype.getId().byteValue() != zattribute.getDatatype().getId().byteValue()) {
						// No coincide el tipo del reporte con el esperado.. Meazon...
						if (	(zattribute.getId().equals(new DoubleByte(0x3000)) ||
								 zattribute.getId().equals(new DoubleByte(0x3001)) ||
								 zattribute.getId().equals(new DoubleByte(0x3002))	) &&
										 this.getId().equals(new DoubleByte(0x702)) &&
										 ztype.getId().byteValue() == 0x39) {
							zattribute.getDatatype().setDescription("Single precision");
							zattribute.getDatatype().setId((byte)0x39); 
							zattribute.getDatatype().setLength(4);
							zattribute.getDatatype().setName("float");
							zattribute.setUnsupported(false);
						}
					}
					int arrayPos = i + 3 + stringOffset;
					this.readAndSetAttributeValueFromArray(atLength, af.getAfIncomingData(),
							zattribute, arrayPos, true);
//					LOG.info("Reporte: "+af.getNwkAddr()+ "Cluster: "+af.ClusterID.toString()+ " Value: "+zattribute.getConvertedValue().toString());
					markConfiguredAttribute(zattribute, this.id, this.device,af.getSrcEndpoint());
					checkSpecialSumAttributes(this.device,zattribute,msgs);
					msgs.add(BuildMqttMsg.buildMqttMsg(this.device,zattribute, af,ZigbeeDescriptions.REPORT,this.zcl));
//					sendThroughMqtt(msg, mqttConnection);
					i += 3 + stringOffset + atLength - 1;
				}
			}
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			return msgs;
		}
		return msgs;
	}

	private void checkSpecialSumAttributes(ZclDevice device, ZclAttribute zatt, List<MqttMsgDetail> msgs) {
		String alias = zatt.getAlias();
		double suma = 0.00;
		double factorpower = 0.00;
		// caso espeacial para factor de potencia: cambiar esto en el futuro para que
		// sea mas escalable
		if ((alias!=null) && (alias.equals(POWER_FACTOR))) {
			ZclAttribute zPf1 = zatt;
//			LOG.info("PF1: {}",zPf1.getConvertedValue().toString());
			double pf1 = BuildMqttMsg.getConversionFactorYRoundNumber(zPf1, 5, device);
//			LOG.info("PF1 round: {}",pf1);
			ZclAttribute zPf2 = getAttribute(new DoubleByte(0x2011));
//			LOG.info("PF2: {}",zPf2.getConvertedValue().toString());
			double pf2 = BuildMqttMsg.getConversionFactorYRoundNumber(zPf2, 5, device);
//			LOG.info("PF2 round: {}",pf2);
			ZclAttribute zPf3 = getAttribute(new DoubleByte(0x2012));
//			LOG.info("PF3: {}",zPf3.getConvertedValue().toString());
			double pf3 = BuildMqttMsg.getConversionFactorYRoundNumber(zPf3, 5, device);
//			LOG.info("PF3 round: {}",pf3);
			ZclAttribute zCurrentP1 = getAttribute(new DoubleByte(0x2007));
//			LOG.info("CurrentL1: {}",zCurrentP1.getConvertedValue().toString());
			double currentP1 = BuildMqttMsg.getConversionFactorYRoundNumber(zCurrentP1, 5, device);
//			LOG.info("CurrentP1 round: {}",currentP1);
			ZclAttribute zCurrentP2 = getAttribute(new DoubleByte(0x2008));
//			LOG.info("CurrentL2: {}",zCurrentP2.getConvertedValue().toString());
			double currentP2 = BuildMqttMsg.getConversionFactorYRoundNumber(zCurrentP2, 5, device);
//			LOG.info("CurrentP2 round: {}",currentP2);
			ZclAttribute zCurrentP3 = getAttribute(new DoubleByte(0x2009));
//			LOG.info("CurrentL3: {}",zCurrentP3.getConvertedValue().toString());
			double currentP3 = BuildMqttMsg.getConversionFactorYRoundNumber(zCurrentP3, 5, device);
//			LOG.info("CurrentP3 round: {}",currentP3);
			factorpower = (Math.abs(pf1 * currentP1) + Math.abs(pf2 * currentP2) + Math.abs(pf3 * currentP3)) / (currentP1 + currentP2 + currentP3);
//			LOG.info("Power factor: {}", factorpower);
			msgs.add(BuildMqttMsg.buildSumMqttMsg(device, alias, zatt, factorpower));

		}
		//
		else {
			if (alias != null) {
				List<DoubleByte> att2sum = zatt.getAttrs2sum();
				suma=BuildMqttMsg.getConversionFactorYRoundNumber(zatt, 5, device);
//				if (alias.equals("ReactivePower")) {
					//LOG.info("ReacPL1: {}",zatt.getConvertedValue().toString());
					//LOG.info("ReacPL1 round: {}",suma);
//				}
				for (int i = 0; i < att2sum.size(); i++) {
					ZclAttribute zattElement = getAttribute(att2sum.get(i));
					if (zattElement != null) {
//						if (alias.equals("ReactivePower")) {
//							LOG.info("{}: {}",zattElement.getName(),zattElement.getConvertedValue().toString());
//							LOG.info("{} round: {}",zattElement.getName(),BuildMqttMsg.getConversionFactorYRoundNumber(zattElement, 5, device));
//						}
						suma = suma + BuildMqttMsg.getConversionFactorYRoundNumber(zattElement, 5, device);
//						if (alias.equals("ReactivePower")) {
							//LOG.info("Suma: {}",suma);
//						}
					}
				}
				msgs.add(BuildMqttMsg.buildSumMqttMsg(device, alias, zatt, suma));
			}
		}

	}
	
	

	private void readAndSetAttributeValueFromArray(int atLength, int[] array,
			ZclAttribute zattribute, int arrayPos, boolean isReport) {
		byte[] atValue;
			atValue = new byte[atLength];
		for (int j = 0; j < atLength; j++) {
			// Leemos
			atValue[atValue.length - 1 - j] = (byte) array[arrayPos + j];
		}
		this.updateAttributeValue(zattribute, atValue, isReport);
		this.checkSpecialAttributes(zattribute);
	}

	private void checkSpecialAttributes(ZclAttribute zattribute) {
		//ahumanes: revisar esto
		if (zattribute.getId().equals(IASZoneClient.ZONE_TYPE_ATTRIBUTE_ID) &&
				this.getId().equals(ZClusterLibrary.ZCL_CLUSTER_ID_SS_IAS_ZONE)) {
			LOG.info("Zone Type attribute received from device {}. {}"
					,this.device.getIeeeAddress()
					,(String)zattribute.getConvertedValue());
		}
		//ahumanes Para enviar al IO la info del cluster OTA cuando lo lea
		if(this.getId().equals(ZClusterLibrary.ZCL_CLUSTER_ID_OTA) && zattribute.getId().equals(new DoubleByte(0x0008))){
			LOG.info("OTA cluster detected in device. Value {}",zattribute.getConvertedValue());
		}
						
		if (this.id.equals(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC) &&
				zattribute.getId().equals(new DoubleByte(0x0004))) {
			String manName = (String) zattribute.getConvertedValue();
			this.device.setManufacturerName(manName == null ? "" : manName);
		}
	}

	
	//ahumanes: esta funcion de abajo creo que no es necesaria de momento.
	//asi que se deja sin que haga nada practiamente
	private void  manageAFDefaultRsp(AfIncomingMsg af) {
		int plOffset = af.isManufacturerSpecific() ? 5 : 3;
//		if (af.getAfIncomingData()[plOffset - 1] != ZClusterLibrary.ZCL_CMD_DEFAULT_RSP) {
//			return pez;
//		}
		try {
			if (af.getAfIncomingData()[plOffset + 1] == ZClusterLibrary.ZCL_STATUS_SUCCESS) {
				Byte cmdId = (byte) af.getAfIncomingData()[plOffset];
				ZclCommand cmd = this.commands.get(cmdId);
				if (cmd != null) {
					String reqId = RequestMaps.getAfRequestId(af);
					if (reqId != null) {
						RequestMaps.removeAfRequestId(af);
					}
				}
			}
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | NullPointerException e) {
			LOG.error("ERROR: {}",e.getCause());
		}
	}

	public void manageDefaultRspToServer(AfIncomingMsg af) {
		int plOffset = af.isManufacturerSpecific() ? 5 : 3;
		int posCommandRequest=plOffset;
		int status=posCommandRequest+1;
		if(af.getAfIncomingData()[posCommandRequest]== (ZClusterLibrary.ZCL_CMD_CONFIG_REPORT ) || af.getAfIncomingData()[posCommandRequest]==(ZClusterLibrary.ZCL_CMD_READ)){
			if(status!=0x00){ //no ha tenido exito
				int seqnumber=posCommandRequest-2;
				markasUnssoported(af.getAfIncomingData()[seqnumber]);
			}
		
		}
	}
	private void markasUnssoported(int seqnumber) {
		//recorremos la lista de atributos. 
		//buscamos los que tengan el mismo sequence number
		//los marcamos como no soportados
		//les ponemos de nuevo el id a -1		
		for (ZclAttribute at : this.attributes.values()) {
				synchronized (at.getAttributeLock()) {
					//LOG.info("Atributo: "+at.getName()+"con seqn: "+at.getSequenceNumber()+" comparando con: "+seqnumber);
					if(at.getSequenceNumber()!=-1 && at.getSequenceNumber()==seqnumber){
						//LOG.info("Atributo: "+at.getName()+" no supported");
						at.setUnsupported(true);
						at.setRspReceived(true);
						at.setSequenceNumber(-1);
					}	
				}
				
			}	
	}

	/**
	 * Builds an AF_DATA_REQUEST to configure automatic reporting of one
	 * attribute
	 * 
	 * @param atId
	 * @return ZFrame (AF_DATA_REQUEST)
	 */
	public ZFrame buildConfigReport(DoubleByte atId,Integer endpoint) {
		ZclAttribute zatt = this.attributes.get(atId);
		if (zatt == null || !zatt.getReporting())
			return new ZFrame();
		
		ArrayList<Integer> header = buildZclHeader(zatt, ZClusterLibrary.ZCL_CMD_CONFIG_REPORT);
		ArrayList<Integer> payload = buildConfigReportZclPayload(zatt);
		header.addAll(payload);

		Integer[] confReportBytes = new Integer[header.size()];
		header.toArray(confReportBytes);
		//ahumanes
		//this.configured=true;
		AfDataRequest afdr;
		if(endpoint==null){
		afdr = new AfDataRequest(this.device.getShortAddress(),
				this.device.getEndpointByCluster(this.id).getId(), ZigbeeConstants.COORDINATOR_ENDPOINT, this.id, (byte) 0,
				0, 0, confReportBytes);
		}else{
			afdr = new AfDataRequest(this.device.getShortAddress(),
					endpoint, ZigbeeConstants.COORDINATOR_ENDPOINT, this.id, (byte) 0,
					0, 0, confReportBytes);
		}
		
		return afdr;
	}

	private ArrayList<Integer> buildConfigReportZclPayload(ZclAttribute zatt) {
		ArrayList<Integer> payload = new ArrayList<>();
		payload.add((int) (ZClusterLibrary.ZCL_SEND_ATTR_REPORTS & 0xFF));
		payload.add(zatt.getId().getLsb());
		payload.add(zatt.getId().getMsb());
		payload.add(zatt.getDatatype().getId().intValue());
		DoubleByte devId = this.device.getEndpointByCluster(getId()).getDeviceId();
		payload.add(zatt.getMinReportingTime(devId).getLsb());
		payload.add(zatt.getMinReportingTime(devId).getMsb());
		payload.add(zatt.getMaxReportingTime(devId).getLsb());
		payload.add(zatt.getMaxReportingTime(devId).getMsb());
		
		// Los atributos de tipo string nunca son analogicos
		for (int i = 0; zatt.getDatatype().isAnalog() &&
				i < zatt.getDatatype().getLength(); i++) {
			payload.add(0);				
		}
		return payload;		
	}

	private ArrayList<Integer> buildZclHeader(ZclAttribute zatt, int cmdId) {
		ArrayList<Integer> header = new ArrayList<>();
		int frameControl = 0;
		
		if (cmdId == ZClusterLibrary.ZCL_CMD_READ_REPORT_CFG_RSP ||
				cmdId == ZClusterLibrary.ZCL_CMD_READ_RSP ||
				cmdId == ZClusterLibrary.ZCL_CMD_WRITE_RSP ||
				cmdId == ZClusterLibrary.ZCL_CMD_CONFIG_REPORT_RSP) {
			
			frameControl |= ZClusterLibrary.ZCL_FRAME_CONTROL_DIRECTION;
			frameControl |= ZClusterLibrary.ZCL_FRAME_CONTROL_DISABLE_DEFAULT_RSP;
		}
			
		if (zatt.isManufacturerSpecific()) {
			frameControl |= ZClusterLibrary.ZCL_FRAME_CONTROL_MANU_SPECIFIC;
			header.add(frameControl);
			header.add(zatt.getCode().getLsb());
			header.add(zatt.getCode().getMsb());
		} else {
			header.add(frameControl);
		}
		if((cmdId == (ZClusterLibrary.ZCL_CMD_CONFIG_REPORT & 0xFF )) || (cmdId == (ZClusterLibrary.ZCL_CMD_READ & 0xFF))){
			zatt.setSequenceNumber((int) sequenceNumber & 0xFF);
			header.add((int) (sequenceNumber) & 0xFF);
		}else{
			header.add((int) (sequenceNumber) & 0xFF);
		}
		sequenceNumber++;
		
		/*header.add(new Byte(sequenceNumber++).intValue());
		header.add(cmdId);*/
		
		
		
		header.add(cmdId);
		return header;
	}

	public ZFrame[] buildReadAttributes(ArrayList<DoubleByte> atIds) {

		deleteInvalidAttributesFromArrayList(atIds);

		ArrayList<DoubleByte> commonAttributes = selectMsAttributes(atIds, false);
		ArrayList<DoubleByte> msAttributes = selectMsAttributes(atIds, true);

		ZFrame[] commonFrames = buildReadFrames(commonAttributes);
		ZFrame[] msFrames = buildReadFrames(msAttributes);
		
		ZFrame[] returnedFrames = new ZFrame[commonFrames.length + msFrames.length];
		for (int i = 0; i < commonFrames.length; i++) {
			returnedFrames[i] = commonFrames[i];
		}
		for (int i = commonFrames.length; i < msFrames.length; i++) {
			returnedFrames[i - commonFrames.length] = msFrames[i];
		}
		return returnedFrames;
	}
	
	private ZFrame[] buildReadFrames(ArrayList<DoubleByte> attIds) {
		int nMsgs = (int) Math.ceil(((float) attIds.size()) / (float) MAX_READ_ATTR_RECORDS);
		ZFrame[] returnedFrames = new ZFrame[nMsgs];
		for (int msgId = 0; msgId < nMsgs; msgId++) {
			
			int payloadSize = msgId == (nMsgs - 1) ? (attIds.size() % MAX_READ_ATTR_RECORDS) * 2
					: MAX_READ_ATTR_RECORDS * 2;			
			ArrayList<Integer> zclFrame = buildZclHeader(this.attributes.get(attIds.get(0)),
					ZClusterLibrary.ZCL_CMD_READ);

			int headerSize = zclFrame.size();
			int attRecordOffset = headerSize;
			int atIdx = msgId * MAX_READ_ATTR_RECORDS;
			while (atIdx < ((msgId + 1) * MAX_READ_ATTR_RECORDS) &&
					attRecordOffset < headerSize + payloadSize) {
				ZclAttribute zatt = this.attributes.get(attIds.get(atIdx));
				// Payload
				zclFrame.add(zatt.getId().getLsb());
				zclFrame.add(zatt.getId().getMsb());
				attRecordOffset += 2;
				atIdx++;
			}
			Integer[] zclReadFrame = new Integer[zclFrame.size()];
			zclFrame.toArray(zclReadFrame);
			AfDataRequest afdr = new AfDataRequest(this.device.getShortAddress(),
					this.device.getEndpointByCluster(this.id).getId(), ZigbeeConstants.COORDINATOR_ENDPOINT, this.id,
					(byte) 0, 0, 0, zclReadFrame);
			returnedFrames[msgId] = afdr;
		}
		return returnedFrames;
	}

	private ArrayList<DoubleByte> selectMsAttributes(ArrayList<DoubleByte> atIds, boolean manSpec) {
		ArrayList<DoubleByte> atts = new ArrayList<>();
		for (DoubleByte atId : atIds) {
			ZclAttribute att = this.attributes.get(atId);
			if (att.isManufacturerSpecific() == manSpec) {
				atts.add(atId);
			}
		}
		return atts;
	}

	private void deleteInvalidAttributesFromArrayList(ArrayList<DoubleByte> atIds) {
		for (DoubleByte id : atIds) {
			ZclAttribute zatt = this.attributes.get(id);
			if (zatt == null)
				atIds.remove(id);
		}
	}

	public ZFrame buildReadAttributesResponse(DoubleByte atId, int sequenceId, int dstEp) {
		ZclAttribute zatt = this.attributes.get(atId);
		
		ArrayList<Integer> header = buildZclHeader(zatt, ZClusterLibrary.ZCL_CMD_READ_RSP);
//		if (zatt == null || !zatt.getAccess().contains("r")) {
			if (!zatt.getAccess().contains("r")) {

			header.add(atId.getLsb());
			header.add(atId.getMsb());
//			if (zatt == null) {
//				header.add(new Byte(ZClusterLibrary.ZCL_STATUS_UNSUPPORTED_ATTRIBUTE).intValue());
//			} 
//			else {
				header.add((int) ZClusterLibrary.ZCL_STATUS_WRITE_ONLY & 0xFF);
//			}
			Integer[] zclMsg = new Integer[header.size()];
			header.toArray(zclMsg);
			AfDataRequest afdr = new AfDataRequest(this.device.getShortAddress(),
					dstEp, ZigbeeConstants.COORDINATOR_ENDPOINT, this.id,
					(byte) 0, 0, 0, zclMsg);
			return afdr;
		}
		
		ZclDatatype ztype = zatt.getDatatype();
		if (ztype == null) {
			header.add(atId.getLsb());
			header.add(atId.getMsb());
			header.add((int) ZClusterLibrary.ZCL_STATUS_SOFTWARE_FAILURE & 0xFF);
			Integer[] zclMsg = new Integer[header.size()];
			header.toArray(zclMsg);
			AfDataRequest afdr = new AfDataRequest(this.device.getShortAddress(),
					dstEp, ZigbeeConstants.COORDINATOR_ENDPOINT, this.id,
					(byte) 0, 0, 0, zclMsg);
			return afdr;
		}
		
		byte[] attBytes = zatt.getBigEndianValue();
		
		header.add(atId.getLsb());
		header.add(atId.getMsb());
		header.add((int) ZClusterLibrary.ZCL_STATUS_SUCCESS & 0xFF);
		header.add((int) (ztype.getId()).intValue());
		if (ztype.getName().contains("string")) {
			// Si el tipo de dato es un string, la longitud es el(los)
			// primer(os) byte(s)
			header.add(attBytes.length);
			if (ztype.getName().startsWith("l")) {
				header.add(0);
			}
		}
		for (int i = 0; i < attBytes.length; i++) {
			header.add((int) attBytes[i] & 0xFF);
		}
		Integer[] zclMsg = new Integer[header.size()];
		header.toArray(zclMsg);
		AfDataRequest afdr = new AfDataRequest(this.device.getShortAddress(),
				dstEp, ZigbeeConstants.COORDINATOR_ENDPOINT, this.id,
				(byte) 0, 0, 0, zclMsg);
		return afdr;
	}
	
	public ZFrame buildWriteAttributes(DoubleByte atId, String strValue) {
		ZclAttribute zatt = this.attributes.get(atId);
		if (zatt == null || !zatt.getAccess().contains("w"))
			return null;

		byte[] valueArray = ZclDatatype.strValue2byteArray(zatt, strValue);
		ArrayList<Integer> header = buildZclHeader(zatt, ZClusterLibrary.ZCL_CMD_WRITE);
		
		header.add(zatt.getId().getLsb());
		header.add(zatt.getId().getMsb());
		header.add((zatt.getDatatype().getId()).intValue());
		
		for (int i = 0; i < valueArray.length; i++) {
			header.add((int) valueArray[i] & 0xFF);
		}
		Integer[] zclFrame = new Integer[header.size()];
		header.toArray(zclFrame);
		AfDataRequest afdr = new AfDataRequest(this.device.getShortAddress(),
				this.device.getEndpointByCluster(this.id).getId(), ZigbeeConstants.COORDINATOR_ENDPOINT, this.id, (byte) 0,
				0, 0, zclFrame);
		return afdr;
	}

	public ZFrame[] configureAttributes(OctaByte coorIeeeAddr, Integer endpoint)
			throws UnknownCoordinatorMacException {
		ArrayList<ZFrame> frames = new ArrayList<>();
		boolean bindDisabled = true;
		ArrayList<DoubleByte> readAtIds = new ArrayList<>();
		for (ZclAttribute at : this.attributes.values()) {
			if (!this.isInMyManufacturer(at)) {
				synchronized (at.getAttributeLock()) {
					at.setUnsupported(true);
					//LOG.info("Marcado como no soportado: "+at.getName());
					markConfiguredAttribute(at, id, device,endpoint);
				}
				continue;
			}
			// Read attribute
			if (at.getRead() && !at.isRspReceived()) {
				readAtIds.add(at.getId());
			}
			// Configure attribute reporting
			//LOG.info("LLegamos con:" +at.getName());
			if (at.getReporting()) {
				// Bind
				if (bindDisabled && !this.device.getIeeeAddress().equals(ZigbeeConstants.INVALID_IEEE_ADDRESS)) {
					waitMs(2000); //prueba. Añadido para que pueda enviar todos los bind
					if(endpoint!=null){
					LOG.info("Sendind bind to cluster: {}. EP: {}",this.getId(),endpoint);
					}else{
						LOG.info("Sendind bind to cluster: {}",this.getId());	
					}
					ZdoBindReq bind = new ZdoBindReq(this.device.getShortAddress(), this.device.getIeeeAddress(),
							this.device.getEndpointByCluster(this.getId()).getId(), this.getId(),
							ADDRESS_MODE.ADDRESS_64_BIT, coorIeeeAddr, ZigbeeConstants.COORDINATOR_ENDPOINT);
					bindDisabled = false;
					frames.add(bind);
				}
				frames.add(this.buildConfigReport(at.getId(),null));
			}
		}
		if (!readAtIds.isEmpty()) {
			ZFrame[] readFrames = this.buildReadAttributes(readAtIds);
			for (int i = 0; i < readFrames.length; i++)
				frames.add(readFrames[i]);
		}
		ZFrame returnedFrames[] = new ZFrame[frames.size()];
		return frames.toArray(returnedFrames);
	}

	public ZFrame[] configureCommands() {
		ArrayList<ZFrame> frames = new ArrayList<>();
		for (ZclCommand cmd : this.commands.values()) {
			if (cmd.isSend()) {
				ByteBuffer zclFrameBuf = ByteBuffer.allocate(cmd.getZclPayloadLength() + 3);
				// Header
				zclFrameBuf.put((byte) 1);
				zclFrameBuf.put((byte) sequenceNumber++);
				zclFrameBuf.put((byte) 0);
				// Payload
				zclFrameBuf.put(cmd.buildPayload());

				AfDataRequest cmdFrame = new AfDataRequest(device.getShortAddress(),
						device.getEndpointByCluster(this.id).getId(), ZigbeeConstants.COORDINATOR_ENDPOINT, this.id,
						(byte) 0, 0, 0, zclFrameBuf.array());
				frames.add(cmdFrame);
			}
		}
		ZFrame[] returnedFrames = new ZFrame[frames.size()];
		return frames.toArray(returnedFrames);
	}

	public ZFrame[] manageAFClusterSpecific(AfIncomingMsg af) {
		ArrayList<ZFrame> returnedFramesAr = new ArrayList<>();

		this.device.setLqi(af.getLinkQuality());
		if (!af.getClusterID().equals(this.id)) {
			return null;
		}
		Byte cmdId = (byte) af.getAfIncomingData()[af.getZclPayloadOffset() - 1];
		ZclCommand cmd = this.serverCommands.get(cmdId);

		if (cmd != null) {
			parseParameters(cmd, af);
		}
		
		ZFrame[] returnedFrames = new ZFrame[returnedFramesAr.size()];
		return returnedFramesAr.toArray(returnedFrames);
	}

	private void parseParameters(ZclCommand cmd, AfIncomingMsg af) {
		int paramPosition = af.getZclPayloadOffset();
		
		for (ZclParam param : cmd.getParams()) {
			int paramLength = getParameterLength(af, param, paramPosition);
			DoubleByte attId = param.getAttId();
			
			if (attId != null) {
				ZclAttribute att = this.attributes.get(attId);
				if (att != null) {
					int stringOffset = getStringOffset(param);
					int longLength = Long.SIZE / Byte.SIZE;
					byte[] bigEndianBytes8 = new byte[longLength];
					
					for (int i = 0; i < longLength; i++) {
						bigEndianBytes8[bigEndianBytes8.length - 1 - i] = i < paramLength ?
								(byte) af.getAfIncomingData()[paramPosition + stringOffset + i] : (byte) 0;
					}
					this.updateAttributeValue(att, bigEndianBytes8, true);
				}
			}
			if (paramLength > 0) {
				paramPosition += paramLength;
			} else {
				break;
			}
		}
	}

	private int getStringOffset(ZclParam param) {
		int stringOffset = 0;
		if (param.getDatatype().getName().contains("string")) {
			stringOffset++;
			if (param.getDatatype().getName().startsWith("l")) {
				stringOffset++;
			}
		}
		return stringOffset;
	}

	private int getParameterLength(AfIncomingMsg af, ZclParam param, int paramPosition) {
		int paramLength = param.getDatatype().getLength();
		if (param.getDatatype().getName().contains("string")) {
			if (param.getDatatype().getName().startsWith("l")) {
				paramLength = new DoubleByte(af.getAfIncomingData()[paramPosition + 1], af.getAfIncomingData()[paramPosition]).getVal();
			} else {
				paramLength = af.getAfIncomingData()[paramPosition];
			}
		}
		return paramLength;
	}

	public ZclDevice getDevice() {
		return device;
	}

	public void setDevice(ZclDevice device) {
		this.device = device;
	}

	public boolean isManSpec() {
		return manSpec;
	}

	public void setManSpec(boolean manSpec) {
		this.manSpec = manSpec;
	}

	public ZFrame buildCmd(Byte cmdId, long[] parameters) {
		return buildCmd(sequenceNumber++, cmdId, parameters);
	}

	public ZFrame buildCmd(int seqNumber, Byte cmdId, long[] parameters) {
		ZclCommand cmd = this.commands.get(cmdId);
		if (cmd == null) {
			return null;
		}
		byte[] payload;
		if (parameters == null || parameters.length == 0) {
			payload = cmd.buildPayload();
		} else {
			payload = cmd.buildPayload(parameters);
		}
		if (payload == null) {
			return null;
		}
		int[] zclFrame = new int[payload.length + 3];
		zclFrame[0] = 1;
		zclFrame[1] = seqNumber;
		zclFrame[2] = cmdId;
		for (int i = 3; i < zclFrame.length; i++) {
			zclFrame[i] = payload[i - 3];
		}
		return new AfDataRequest(this.device.getShortAddress(),
				this.device.getEndpointByCluster(this.id).getId(), ZigbeeConstants.COORDINATOR_ENDPOINT, this.id, (byte) 0,
				0, 0, zclFrame);
	}
	/**
	 * Devuelve un objeto booleano que indica si no hay diferencias en el codigo del
	 * fabricante en un atributo y un mensaje recibido.
	 * @param at ZclAttribute
	 * @param af AF_INCOMING_MSG
	 * @return null si alguno de los parametros es null;
	 * false si solo el mensaje tiene codigo o si tambien tiene el atributo pero es distinto;
	 * true en el resto de casos.
	 */
	public Boolean checkManCode(ZclAttribute at, AfIncomingMsg af) {
		if (at == null || af == null) return null;
		if (af.isManufacturerSpecific()) {
			DoubleByte frManCode = new DoubleByte(af.getAfIncomingData()[2], af.getAfIncomingData()[1]);
			DoubleByte atManCode = at.getCode();
			return frManCode.equals(atManCode);
		}
		return true;
	}
	
	/**
	 * Checks if the attribute given is supported by the manufacturer of the device
	 * containing this cluster.
	 * @param at ZclAttribute
	 * @return true if standard attribute or manufacturer code matches manufacturer name
	 * of basic cluster. false otherwise
	 */
	public boolean isInMyManufacturer(ZclAttribute at) {
		if (at == null) {
			return false;
		}
		
		String atManufacturerName = at.getManufacturerName();
		if (atManufacturerName.equals("")) {
			return true;
		}
		
		// Manufacturer Specific Attribute
//		if (this.device == null) return false; // this.device no puede ser null. 
		ZclCluster basicCluster = this.device.getZclCluster(ZClusterLibrary.ZCL_CLUSTER_ID_GEN_BASIC);
		if (basicCluster == null) {
			// TODO: Pedir el cluster basic
			return true; // Devolmemos true por si acaso, ya que no lo podemos saber
		}
		ZclAttribute manNameAt = basicCluster.getAttribute(new DoubleByte(0x0004));
		if (!manNameAt.isRspReceived()) {
			return true; // Por si acaso. No lo podemos saber.
		}
		String manNameValue = (String)manNameAt.getConvertedValue();
		if (manNameValue.equals("")) {
			return false;
		}
		if (atManufacturerName.toLowerCase().contains(manNameValue.toLowerCase()) ||
				manNameValue.toLowerCase().contains(atManufacturerName.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}
	
	private void updateAttributeValue(ZclAttribute att, byte[] value, boolean report) {
		if (report) {
			att.updateValueAndIncreaseReports(value);
		} else {
			att.updateValue(value,true,true);
		}
	}
	
	
	public static void markConfiguredAttribute(ZclAttribute zatt, DoubleByte clId, ZclDevice dev,Integer endpoint) {
		boolean prevStatusRspReceived = zatt.isRspReceived();
		zatt.setRspReceived(true);
		if (zatt.isUnsupported()) {
			//ahumanes: descomentar esto
//			DeviceStorage.deleteAttribute(zatt.getId(), dev.getShortAddress(), endpoint.byteValue(), clId);
		} else if (!prevStatusRspReceived) {
			//ahumanes: descomentar esto
			//DeviceStorage.copyAttributeValue(zatt, dev.getShortAddress(), endpoint.byteValue(), clId);
		} else if(!zatt.isUnsupported()){
			zatt.setReportado(true);
		}
	}
	private static void waitMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error("Error sleeping. Cause: " + e);
			Thread.currentThread().interrupt();
		}
	}

	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}



}