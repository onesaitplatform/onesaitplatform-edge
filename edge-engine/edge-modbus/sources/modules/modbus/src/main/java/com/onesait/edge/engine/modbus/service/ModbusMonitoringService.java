package com.onesait.edge.engine.modbus.service;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onesait.edge.engine.modbus.model.Device;
import com.onesait.edge.engine.modbus.util.ConcurrentUtils;
import com.onesait.edge.engine.modbus.util.ModbusUtils;
import com.onesait.edge.engine.modbus.util.Task;

@Service
public class ModbusMonitoringService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModbusMonitoringService.class);
	
    private ScheduledExecutorService modbusExecutor;  
    private final HashMap<String, ScheduledFuture<?>> futures;
    private int maxCoreThreadPoolSizeByDefault = 5;
    
    @Autowired
    private MqttConnection mqttConnection;

    private ModbusService modbusService;
    
    /**
     * ModbusMonitoringService constructor
     * @param modBusService
     */
    public ModbusMonitoringService() {
    	this.futures = new HashMap<>();
    }
     
    public void start(ModbusService modbusService) {
    	this.modbusExecutor = Executors.newScheduledThreadPool(this.maxCoreThreadPoolSizeByDefault);
    	this.modbusService = modbusService;
    }
	 
	@PreDestroy
	public void destroy() {
		
		try {
			for (ScheduledFuture<?> future:this.futures.values()) {
				future.cancel(true);
			}
			ConcurrentUtils.stop(this.modbusExecutor);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		    // Restore interrupted state...
		    Thread.currentThread().interrupt();
		} finally {
			ModbusUtils.closeAllOpennedConnections();
		}
	}
	
	/**
	 * addMonitor2Device - One thread for each device. 
	 * @param device
	 */
	public void addMonitor2Device(Device device) {
		
		if(device != null) {
			
			Task task = new Task(device, mqttConnection, modbusService);
			
			// runnable, delay, period, units
	    	ScheduledFuture<?> future = modbusExecutor.scheduleAtFixedRate(task, 0, device.getMonitoringTimeMs(), TimeUnit.MILLISECONDS);
	    	this.futures.put(device.getId(),future);
	    	LOGGER.info("Monitor for device {} added", device.getId());
		}
	}
	
	/**
	 * removeDeviceFromMonitoringThread
	 * @param deviceId
	 * @throws InterruptedException 
	 */
	public void removeDeviceFromMonitoringThread(Device device) {
		
		ScheduledFuture<?> future = futures.get(device.getId());
		
		if(future != null) {
			while(!future.isDone() || !future.isCancelled()) {
				future.cancel(false);
			}
			LOGGER.info("Monitor for device {} removed", device.getId());
			futures.remove(device.getId());
		}
	}
	
	/**
	 * update the monitor device with new features of the device
	 * @param device which want to update monitor
	 */
	public void updateMonitorDevice(@NotNull Device device) {
		this.removeDeviceFromMonitoringThread(device);
		this.addMonitor2Device(device);
	}
	
	/**
	 * Set the max number of treads for the pool
	 * @param newMaxCoreThreadPoolSize
	 */
	public void setMaxCoreThreadPoolSize(@Min(1) int newMaxCoreThreadPoolSize) {
		this.maxCoreThreadPoolSizeByDefault = newMaxCoreThreadPoolSize;
	}
}
