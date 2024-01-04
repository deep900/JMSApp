/**
 * 
 */
package com.oradnata.jms;

import java.util.Properties;

import com.oradnata.config.ApplicationConnector;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class QueueName {

	//@Value("${spring.jms.listener.queueName}")
	private String queueName;
	
	private Properties properties = null;
	
	ApplicationConnector connector = new ApplicationConnector();	

	public String getQueue() {
		properties = connector.getAppProperties();
		queueName = properties.getProperty("spring.jms.listener.queueName");
		log.info("Printing the queue name:" + queueName);
		return queueName;
	}
}
