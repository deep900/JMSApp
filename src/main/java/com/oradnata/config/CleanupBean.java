/**
 * 
 */
package com.oradnata.config;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 
 */
@Component
public class CleanupBean implements InitializingBean, Runnable {

	private static final Logger log = LogManager.getLogger(CleanupBean.class);

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	public void run() {
		log.info("Cleaning up the system.");
		System.gc();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			scheduler.scheduleAtFixedRate(this, Duration.ofMinutes(10));
		} catch (Exception err) {
			log.error("Error while setting properties", err);
		}
	}
}
