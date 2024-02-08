package com.oradnata.config;

import java.util.Properties;
import java.util.concurrent.PriorityBlockingQueue;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.oradnata.event.DuplicateMessageHandler;
import com.oradnata.event.FlightInformationProcessorJob;
import com.oradnata.event.Retryable;
import com.oradnata.event.RetryableQueueProcessor;
import com.oradnata.jms.JMSConnectionDetails;

@SpringBootApplication(exclude = { ActiveMQAutoConfiguration.class })
@EnableJms
@ComponentScan(basePackages = { "com.oradnata", "com.oradnata.jms" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		ActiveMQAutoConfiguration.class, R2dbcAutoConfiguration.class, JooqAutoConfiguration.class })
public class SpringBootWeblogicApplication {

	private static final Logger LOGGER = LogManager.getLogger(SpringBootWeblogicApplication.class);

	@Bean(value = "jmsConnectionFactory")
	public ConnectionFactory getJmsConnectionFactory() {
		ConnectionFactory connectionFactory = null;
		try {
			Context context = new InitialContext();
			String connectionFactoryName = this.getJMSConnectionDetails().getConnectionFactoryName();
			LOGGER.info("Connection lookup;" + connectionFactoryName);
			connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryName);
		} catch (NamingException e) {
			LOGGER.error("Error while lookup", e);
		}
		return connectionFactory;
	}

	private ApplicationConnector appConnector;

	private Properties prop = null;

	private void loadProperties() {
		appConnector = new ApplicationConnector();
		if (null == prop) {
			prop = appConnector.getAppProperties();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWeblogicApplication.class, args);
	}

	@Bean(name = "jmsConnectionDetails")
	public JMSConnectionDetails getJMSConnectionDetails() {
		return new JMSConnectionDetails();
	}

	@Bean(name = "threadPoolTaskExecutor")
	public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(25);
		executor.setCorePoolSize(6);
		executor.setQueueCapacity(35000);
		executor.setBeanName("Dnata-Flight-Info-Processor");
		executor.setThreadNamePrefix("Dnata-Flight-Info");
		executor.setAwaitTerminationMillis(6000);
		return executor;
	}

	public String getJNDIName() {
		loadProperties();
		String jndiName = prop.getProperty("spring.ds.jndi-name");
		return jndiName;
	}

	@Bean(value = "flightInformationProcessorJob")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public FlightInformationProcessorJob getFlightInformationProcessorJob() {
		return new FlightInformationProcessorJob();
	}

	@Bean(value = "duplicateMessageHandler")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DuplicateMessageHandler getDuplicateHandler() {
		return new DuplicateMessageHandler();
	}

	@Bean
	public ThreadPoolTaskScheduler getScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(5);
		scheduler.setWaitForTasksToCompleteOnShutdown(false);
		return scheduler;
	}

	@Bean(value = "retryBlockingQueue")
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PriorityBlockingQueue<Retryable> getRetryQueue() {
		return new PriorityBlockingQueue<Retryable>();
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RetryableQueueProcessor getRetryQueueProcessor() {
		return new RetryableQueueProcessor();
	}
	
	@Bean
	public ApplicationConnector getApplicationConnector() {
		return new ApplicationConnector();
	}
}
