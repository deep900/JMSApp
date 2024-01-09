/**
 * 
 */
package com.oradnata.jms;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the JMS Messages.
 */
@Component
public class DMessageListener implements ApplicationEventPublisherAware{
	
	private static final Logger log = LogManager.getLogger(DMessageListener.class);
	
	public DMessageListener() {
		log.info("Loading the JMS Listener.");
	}

	private ApplicationEventPublisher eventPublisher;		

	@Autowired
	private JMSConnectionDetails queueName;

	@JmsListener(destination = "#{@jmsConnectionDetails.getQueue}")
	public void onMessage(Object message) {
		log.info("Inside on message: " + message.toString() + "Class:" + message.getClass().getName());		
		TextMessage textMessage = (TextMessage) message;
		try {
			this.eventPublisher.publishEvent(getApplicationEvent(textMessage.getBody(String.class).toString()));		
		} catch (JMSException err) {
			log.error("Error on message",err);			
		}
	}

	private FlightInformationEvent getApplicationEvent(Object source) {
		return new FlightInformationEvent(source);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
	
	@PostConstruct
	public void printQueue() {
		log.info("Printing the queuename from post const" + queueName.getQueue());
	}
}
