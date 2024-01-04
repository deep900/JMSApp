/**
 * 
 */
package com.oradnata.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.oradnata.jms.FlightInformationEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Component
@Slf4j
public class DnataEventHandler implements ApplicationListener<FlightInformationEvent>, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("threadPoolTaskExecutor")
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Override
	public void onApplicationEvent(FlightInformationEvent event) {
		log.info("Received the message: " + event.getClass().getName());
		if (event instanceof FlightInformationEvent) {
			threadPoolTaskExecutor.execute(getProcessorJob(event.getSource()));
			log.info("Received a flight information event.");
		}
	}

	private FlightInformationProcessorJob getProcessorJob(Object source) {
		FlightInformationProcessorJob job = (FlightInformationProcessorJob) this.applicationContext
				.getBean("flightInformationProcessorJob");
		job.setSource(source);
		return job;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}