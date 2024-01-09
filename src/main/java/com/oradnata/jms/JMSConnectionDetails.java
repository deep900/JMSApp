package com.oradnata.jms;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oradnata.config.ApplicationConnector;

/**
 * 
 */

public class JMSConnectionDetails {

	private static final Logger log = LogManager.getLogger(JMSConnectionDetails.class);

	private String queueName;

	private String connectionFactoryName;

	private Properties properties = null;

	private ApplicationConnector connector = new ApplicationConnector();

	public String getQueue() {
		loadProperties();
		queueName = properties.getProperty("spring.jms.listener.queueName");
		log.debug("Printing the queue name:" + queueName);
		return queueName;
	}

	public String getConnectionFactoryName() {
		loadProperties();
		connectionFactoryName = properties.getProperty("spring.jms.jndi-name");
		log.debug("Printing the Connection factory name:" +  connectionFactoryName);
		return connectionFactoryName;
	}
	
	private void loadProperties() {
		properties = connector.getAppProperties();
	}
}
