/**
 * 
 */
package com.oradnata.jms;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Listens to the JMS Messages.
 */
@Component
@Slf4j
public class DMessageListener implements ApplicationEventPublisherAware {

	public DMessageListener() {
		System.out.println("Loading the JMS Listener.");
	}

	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private JMSCountService jmsCountService;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	@JmsListener(destination = "#{@queueName.getQueue}")
	public void onMessage(Object message) {
		log.info("Inside on message: " + message.toString() + "Class:" + message.getClass().getName());
		TextMessage textMessage = (TextMessage) message;
		try {
			this.eventPublisher.publishEvent(getApplicationEvent(textMessage.getBody(String.class).toString()));
			this.incrementJmsCount();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private FlightInformationEvent getApplicationEvent(Object source) {
		return new FlightInformationEvent(source);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	private String getCurrentDate() {
		Date obj = new Date();
		return sdf.format(obj);
	}

	private void incrementJmsCount() {
		int counter = 0;
		if (jmsCountService.getJmsInformation().containsKey(getCurrentDate())) {
			Object count = jmsCountService.getJmsInformation().get(getCurrentDate());
			counter = (Integer) count;
		}
		jmsCountService.getJmsInformation().put(getCurrentDate(), counter + 1);
	}
}
