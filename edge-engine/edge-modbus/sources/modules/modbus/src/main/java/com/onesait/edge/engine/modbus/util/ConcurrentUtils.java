package com.onesait.edge.engine.modbus.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentUtils.class);
	
	private ConcurrentUtils() {}
		
	public static void stop(ExecutorService executor) throws InterruptedException {
        
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS); 
        
        if (!executor.isTerminated()) {
              LOGGER.info("killing non-finished tasks...");
              executor.shutdownNow();
        }
    }

    public static void sleep(int seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }
}
