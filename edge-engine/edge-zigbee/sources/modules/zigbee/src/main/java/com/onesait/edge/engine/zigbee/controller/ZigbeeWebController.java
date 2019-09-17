package com.onesait.edge.engine.zigbee.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import com.onesait.edge.engine.zigbee.jsoncontroller.ZbDeviceJson;
import com.onesait.edge.engine.zigbee.service.DeviceManager;
import com.onesait.edge.engine.zigbee.service.SerialZigbeeConnector;
import com.onesait.edge.engine.zigbee.service.ZigbeeService;
import com.onesait.edge.engine.zigbee.util.GlobalConstants;


//@Controller
public class ZigbeeWebController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ZigbeeWebController.class);
		
	private DeviceManager deviceManager;
	
	private SerialZigbeeConnector connector; 
	
	private ZigbeeService zigbeeService;
	
	private static String attributeDevices="devices";
	
    public ZigbeeWebController(DeviceManager deviceManager, SerialZigbeeConnector connector,ZigbeeService zigbeeService){
        this.setDeviceManager(deviceManager);
        this.setConnector(connector);
        this.zigbeeService=zigbeeService;
    }
	
//    @GetMapping(value = { GlobalConstans.Contexts.ROOT, GlobalConstans.Contexts.INDEX })
    public String index(Model model) {
        return GlobalConstants.WebPages.INDEX;
	}
    
//    @GetMapping(value = { GlobalConstans.Contexts.DEVICES })
    public String devices(Model model) {
    	List<ZbDeviceJson> devices =zigbeeService.buildZbdevicesJson();
		model.addAttribute(attributeDevices, devices);
        return GlobalConstants.WebPages.DEVICES;
	}

	public DeviceManager getDeviceManager() {
		return deviceManager;
	}

	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	public SerialZigbeeConnector getConnector() {
		return connector;
	}

	public void setConnector(SerialZigbeeConnector connector) {
		this.connector = connector;
	} 
    
//    @ModelAttribute("registerTypeValues")
//    public ComboboxValue[] getRegisterTypeValues() {
//        return new ComboboxValue[] {
//            new ComboboxValue(RegisterType.HR.toString(), RegisterType.descValue(RegisterType.HR)), 
//            new ComboboxValue(RegisterType.IR.toString(), RegisterType.descValue(RegisterType.IR)),
//            new ComboboxValue(RegisterType.IS.toString(), RegisterType.descValue(RegisterType.IS)),
//            new ComboboxValue(RegisterType.CS.toString(), RegisterType.descValue(RegisterType.CS))
//        };
//        
//    }

}



	

	





//}