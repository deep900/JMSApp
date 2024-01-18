/**
 * 
 */
package com.oradnata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import weblogic.jms.client.JMSConnectionFactory;

/**
 * 
 */
@Configuration
public class TestConfiguration {
	
	@Bean(value = "connectionFactory")
	public JMSConnectionFactory getConnectionFactory() {
		return new JMSConnectionFactory();
	}
	
	@Bean(value ="jmsListenerContainerFactory")
	public DefaultJmsListenerContainerFactory getjmsListenerContainerFactory() {
		return new DefaultJmsListenerContainerFactory();
	}
}
