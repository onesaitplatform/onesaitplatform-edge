package com.onesait.edge.engine.zigbee.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.clients.IASZoneClient;
import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.model.IasZoneDev;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclDatatype;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.model.ZigbeeClusterLibrary;
import com.onesait.edge.engine.zigbee.types.DeviceType;
import com.onesait.edge.engine.zigbee.types.ZigbeeDescriptions;


public class BuildMqttMsg {
	private BuildMqttMsg() {}
	private static final Logger LOG = LoggerFactory.getLogger(BuildMqttMsg.class);
	
	private static void setTime(MqttMsgDetail details) {
		long timestampms = System.currentTimeMillis();
		setTime(details, timestampms);
	}
	
	private static void setTime(MqttMsgDetail details, long timestampms) {		
		details.setTimeStamp(timestampms);	
		details.setTimeStampInNanos(timestampms*1_000_000);
	} 
	
	
	public static synchronized MqttMsgDetail buildIASZoneOnOff(IasZoneDev iasdev,ZclDevice dev) {
		MqttMsgDetail details;
		details=buildIASZoneMqttMsg(iasdev,dev,IASZoneClient.IAS_ZONE_ONOFF,false);
		// descripcion (descripcion)
		details.setDescription(ZigbeeDescriptions.CHANGESTATUS.toString());
		// valor
		details.setValue(iasdev.getBooleanStatus());
		return details;
	}
	
	public static synchronized MqttMsgDetail buildLeavingDeviceMqttMsg(ZclDevice dev){
		MqttMsgDetail details = new MqttMsgDetail();
		details.setProfile(ZigbeeDescriptions.ZIGBEE.toString());
		details.setDeviceId(dev.getIeeeAddress().toString());
		details.setDescription(ZigbeeDescriptions.DEVICELEAVING.toString());
		setTime(details);
		return details;		
	}
	
	public static synchronized MqttMsgDetail buildIASZoneBatteryMqttMsg(IasZoneDev iasdev,ZclDevice dev) {
		MqttMsgDetail details;
		details=buildIASZoneMqttMsg(iasdev,dev,IASZoneClient.IAS_ZONE_BATT,true);
		//description
		details.setDescription(ZigbeeDescriptions.CHANGEBATTERYSTATUS.toString());
		// valor
		details.setValue(iasdev.getBattery());
		return details;
	}
	
	private static MqttMsgDetail buildIASZoneMqttMsg(IasZoneDev iasdev, ZclDevice dev, String signalId,boolean batteryEvent){
		MqttMsgDetail details = new MqttMsgDetail();
		// aqui el protocolo
		details.setProfile(ZigbeeDescriptions.ZIGBEE.toString());
		// aqui la mac
		details.setDeviceId(dev.getIeeeAddress().toString());
		// tipo de dispositivo
		details.setDeviceType(iasdev.getSensorType());
		// Signal Id (nombre del cluster)
		details.setSignalId(signalId);
		// signal (atributo ID)
		details.setSignal(signalId);
		details.setNumber(iasdev.getValueStatus(batteryEvent));
		// timestam en nanos y ms
		setTime(details);
		return details;
	
	}
	
	public static synchronized MqttMsgDetail buildSumMqttMsg(ZclDevice device,String alias,ZclAttribute at,double sum) {
		MqttMsgDetail details = new MqttMsgDetail();
		// aqui el protocolo
		details.setProfile(ZigbeeDescriptions.ZIGBEE.toString());
		// aqui la mac
		details.setDeviceId(device.getIeeeAddress().toString());
		// tipo de dispositivo
		
		DeviceType deviceType=device.getGeneralDeviceType();	
		
		//y asi se hace ahora, con GeneralDeviceType
		details.setDeviceType(deviceType.toString());
		
		
		// Signal Id (nombre del atributo)
		details.setSignalId(alias);
		// signal (nombre del atributo)
		details.setSignal(alias);
		// descripcion (tipo de mensaje)
		details.setDescription(ZigbeeDescriptions.REPORTSUM.toString());
		// timestam en nanos y ms
		setTime(details, at.getLastTimeUpdated().getTime());
		// valor
		details.setValue(String.format("%.2f",sum));
		//DecimalFormat df = new DecimalFormat("#.####");
		//DecimalFormat df = new DecimalFormat("#.#####");
		//String result = df.format(29222330.91231112285);
		details.setNumber(Math.round(sum*1e4)/1e4);
		return details;		
		
	}

	public static synchronized MqttMsgDetail buildMqttMsg(ZclDevice device, ZclAttribute at, AfIncomingMsg af,
			ZigbeeDescriptions desc, ZigbeeClusterLibrary zcl) {
		MqttMsgDetail details = new MqttMsgDetail();
		// aqui el protocolo
		details.setProfile(ZigbeeDescriptions.ZIGBEE.toString());
		// aqui la mac
		details.setDeviceId(device.getIeeeAddress().toString());
		// tipo de dispositivo
		
		DeviceType deviceType=device.getGeneralDeviceType();
		//antes se hacia por el id del endpoint
		/*ZclEndpoint ep = device.getEndpoints().get((byte) af.getSrcEndpoint());
		ZclDevicetype devId = zcl.getDeviceIds().get(ep.getDeviceId());
		if (devId != null) {
			details.setDeviceType(devId.getName());
		}
		if (device.getShortAddress().equals(new DoubleByte(0x0000))) {
			details.setDeviceType(DeviceType.COORDINATOR.toString());
		}*/		
		
		//y asi se hace ahora, con GeneralDeviceType
		details.setDeviceType(deviceType.toString());
		
		
		// Signal Id (nombre del atributo)
		details.setSignalId(at.getName());
		// signal (nombre del atributo)
		details.setSignal(at.getName());
		// descripcion (tipo de mensaje)
		details.setDescription(desc.toString());
		// timestam en nanos y ms
		setTime(details, at.getLastTimeUpdated().getTime());
		// valor
		if (at.getConvertedValue() != null) {
			details.setValue(at.getConvertedValue().toString());
			if (!isNumeric(at.getConvertedValue().toString())) {
				if ((at.getDatatype().getName().equals(ZclDatatype.ENUM8_STR))
						|| (at.getDatatype().getName().equals(ZclDatatype.ENUM16_STR) || (!at.getEnums().isEmpty()))) {
					boolean found = false;
					double value=-1;
					for (Map.Entry<DoubleByte, String> entry : at.getEnums().entrySet()) {
						if (entry.getValue().equalsIgnoreCase(at.getConvertedValue().toString())) {
							found = true;
							value=entry.getKey().intValue();
							break;
						}
					}
					if (found) {
						details.setNumber(value);
					} else {
						LOG.error("Error obtaining the number value of: {}",at.getConvertedValue());
					}
				} else {
					details.setNumber(0);
				}
			} else {
				try {
					//TODO chequear que esto funciona
					Double conversionFactor= getConversionFactor(device,at);
					double number = round(Double.parseDouble(at.getConvertedValue().toString())*conversionFactor, 2,at,device,false);
//					LOG.info("Device: "+device.getGeneralDeviceType()+" .Atributo: "+at.getName() +" valorFinal: "+number+ " .valorIni: "+Double.parseDouble(at.getConvertedValue().toString())+" .FactorConv: "+conversionFactor);
//					LOG.info("Att: " + at.getName() + " valor: " + at.getConvertedValue().toString() + " value :" + number);
					details.setNumber(number);
				} catch (IllegalArgumentException e) {
					LOG.info("Error parsing: {}. Value: {} setting to 0",at.getName(),at.getConvertedValue());
					details.setNumber(0);
				}
			}
		}
		return details;
	}
		
	

	public static Double getConversionFactor(ZclDevice device, ZclAttribute at) {
		DoubleByte manuCode = device.getManufacturerCode();
		if (!at.getConversionFactor().isEmpty()) {
			if (manuCode != null) {
				HashMap<DoubleByte, Double> conversionFactor = at.getConversionFactor();
				Double conversion = conversionFactor.get(manuCode);
				if (conversion != null) {
					return conversion;
				} else {
					return at.getDefaultConversion();
				}
			}
		} else if(at.getDefaultConversion()!=null) {
			return at.getDefaultConversion();
		}
		return 1.00;
	}

	public static synchronized MqttMsgDetail buildWriteAttRsp(ZclDevice device, ZclAttribute at, AfIncomingMsg af, ZigbeeClusterLibrary zcl, boolean success) {
		MqttMsgDetail details;
		//aqui el protocolo
		details=buildMqttMsg(device,at,af,ZigbeeDescriptions.WRITE_RSP,zcl);
		if(success){
			details.setValue("OK");
		}else{
			details.setValue("ERROR");
		}
		return details;	
	}	
	
	public static double round(double value, int places, ZclAttribute at, ZclDevice device, boolean onlyRound) {
		if ((!onlyRound) && (places <0)) {
//			if (places < 0) {
				if (device != null && at != null) {
					LOG.info("Error parsing att.: {} in device: {}. Settimg to 0", at.getName(),
							device.getIeeeAddress());
				}
				return 0;
//			}
		}
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	public static Double getConversionFactorYRoundNumber(ZclAttribute at, int places, ZclDevice device) {
		try{
			Double conversionFactor=getConversionFactor(device, at);
			double attDoubleVal=(Double.parseDouble(at.getConvertedValue().toString()))*conversionFactor;
			if (places < 0) {
//			    	if(device!=null && at!=null) {
			    		LOG.error("Error parsing att.: {} in device: {}. Setting to 0",at.getName(),device.getIeeeAddress());
//			    	}
			    	return 0.0;
			    }
			    BigDecimal bd = BigDecimal.valueOf(attDoubleVal);
			    bd = bd.setScale(places, RoundingMode.HALF_UP);
			    return bd.doubleValue();
		}catch(Exception e) {
			LOG.error("Error parsing att.: {} in device {}. Returning 0",at.getName(),device.getIeeeAddress());
			LOG.info("Valor string: {}",at.getConvertedValue());
			return 0.0;
		}
	}
	
	public static boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
	
}
