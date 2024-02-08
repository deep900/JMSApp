package com.oradnata.jms;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.oradnata.config.ApplicationConnector;

public class JMSConnectionDetails {

	private static final Logger log = LogManager.getLogger(JMSConnectionDetails.class);

	private String queueName;

	private String connectionFactoryName;

	private Properties properties = null;

	@Autowired
	private ApplicationConnector connector;

	public String getQueue() {
		loadProperties();
		queueName = properties.getProperty("spring.jms.listener.queueName");
		log.info("Printing the queue name:" + queueName);
		return queueName;
	}

	public String getConnectionFactoryName() {
		loadProperties();
		connectionFactoryName = properties.getProperty("spring.jms.jndi-name");
		log.info("Printing the Connection factory name:" + connectionFactoryName);
		return connectionFactoryName;
	}

	private void loadProperties() {
		properties = connector.getAppProperties();
	}
}
