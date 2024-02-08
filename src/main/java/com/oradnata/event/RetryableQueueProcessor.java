/**
 * 
 */
package com.oradnata.event;

import java.time.Duration;
import java.util.concurrent.PriorityBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.oradnata.config.ApplicationConnector;

public class RetryableQueueProcessor implements Runnable {

	private static final Logger log = LogManager.getLogger(RetryableQueueProcessor.class);

	@Autowired
	@Qualifier(value = "retryBlockingQueue")
	private PriorityBlockingQueue<Retryable> priorityQueue;

	@Autowired
	private ApplicationConnector applicationConnector;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Autowired
	private JMSCounter jmsCounter;

	public void addJob(Retryable obj) {
		log.info("Added an event to retryable queue.");
		if (priorityQueue.offer(obj)) {
			jmsCounter.incrementParam(JMSCounter.NO_OF_JOBS_FOR_RETRY);
		} else {
			log.error("unable to add the element in the queue");
		}
		log.info("Printing the queue size: " + priorityQueue.size());
	}

	public void removeJob(Retryable obj) {
		log.info("Removing the job from the queue.");
		if (priorityQueue.remove(obj)) {
			jmsCounter.decrementParam(JMSCounter.NO_OF_JOBS_FOR_RETRY);
		}
		log.info("Printing the queue size: " + priorityQueue.size());
	}

	private Retryable getRetryable() {
		log.info("Polling from the retry queue");
		Retryable obj = priorityQueue.poll();
		if (null != obj) {
			jmsCounter.decrementParam(JMSCounter.NO_OF_JOBS_FOR_RETRY);
			log.info("Found the retry job " + obj.getJobDetails());
		}
		return obj;
	}

	class RetryRunner implements Runnable {
		Retryable retryableObj;

		public RetryRunner(Retryable obj) {
			this.retryableObj = obj;
		}

		public void run() {
			String position = retryableObj.getRetryPosition();
			boolean state = retryableObj.retryFromLastPostition(position);
			if (state) {
				log.info("[" + retryableObj.getRetryCount() + "] Job retry was successful "
						+ retryableObj.getJobDetails());
				jmsCounter.incrementParam(JMSCounter.RETRY_PROCESSED_SUCCESSFULLY);				
				retryableObj.postProcess();
			} else {
				log.error("[" + retryableObj.getRetryCount() + "] Retry of job failed " + retryableObj.getJobDetails());				
				retryableObj.incrementRetryCount();
				addJob(retryableObj);
			}
		}
	}

	@PostConstruct
	public void initiateRetryMonitor() {
		String frequency = getRetryMonitorFrequencyInMinutes();
		log.info("Loading the retry monitor with frequency in minutes:" + frequency);
		scheduler.scheduleAtFixedRate(this, Duration.ofMinutes(Integer.parseInt(frequency)));
	}

	@Override
	public void run() {
		log.info("Running the retry monitor job :" + priorityQueue.size() + " Jobs found");
		Retryable retryable = null;
		while ((retryable = getRetryable()) != null) {
			log.info("Processing the retryable job " + retryable.getJobDetails());
			log.info("Max retry count" + retryable.getMaxRetyCount());
			log.info("Retry count" + retryable.getRetryCount());
			if ( retryable.getRetryCount() >= retryable.getMaxRetyCount()) {
				log.error("Unable to proceed the job : " + retryable.getJobDetails());
				removeJob(retryable);
				jmsCounter.incrementParam(JMSCounter.RETRY_PROCESSED_FAILED);
				continue;
			}
			RetryRunner retryRunner = new RetryRunner(retryable);
			executor.execute(retryRunner);
		}
	}

	private String getRetryMonitorFrequencyInMinutes() {
		return applicationConnector.getAppProperties().get("retry.monitor.frequency.in.minutes").toString();
	}

}
