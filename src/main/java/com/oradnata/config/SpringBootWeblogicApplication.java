package com.oradnata.config;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.oradnata.jms.QueueName;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(/* exclude = { ActiveMQAutoConfiguration.class } */)
@ComponentScan(basePackages = "com.oradnata")
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableJms
@Slf4j
public class SpringBootWeblogicApplication {

	/*
	 * @Value("${spring.ds.jndi-name}") private String jndiName;
	 */

	/*
	 * @Value("${spring.ds.principal}") private String userName;
	 * 
	 * @Value("${spring.ds.enc-cred}") private String dbCred;
	 * 
	 * @Value("${spring.ds.driver-class-name}") private String driverClass;
	 * 
	 * @Value("${spring.ds.url}") private String dbUrl;
	 */

	//private PublicUtility utility = new PublicUtility();

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWeblogicApplication.class, args);
	}

	@Bean(name = "queueName")
	public QueueName getQueueName() {
		return new QueueName();
	}

	@Bean(name = "threadPoolTaskExecutor")
	public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(15);
		executor.setCorePoolSize(6);
		executor.setQueueCapacity(30000);
		executor.setBeanName("Dnata-Flight-Info-Processor");
		executor.setThreadNamePrefix("Dnata-Flight-Info");
		executor.setAwaitTerminationMillis(6000);
		return executor;
	}

	/**
	 * Obtains the JNDI Connection for Database.
	 * 
	 * @return
	 */
	//@Bean(name="dataSource")
	/*
	 * public DataSource getJNDISource() { JndiTemplate jndiTemplate = new
	 * JndiTemplate(getDBEnvironmentProperties()); try {
	 * log.info("Trying to get the JNDI: " + jndiName.trim());
	 * System.out.println("JNDI Lookup" + jndiName); DataSource ds = (DataSource)
	 * jndiTemplate.getContext().lookup(jndiName.trim()); log.info(ds != null ?
	 * "Datasource Connection successful" : "Unable to obtain the Data source.");
	 * return ds; } catch (NamingException err) { StringWriter errors = new
	 * StringWriter(); err.printStackTrace(new PrintWriter(errors));
	 * log.info("[Error]" + errors.toString()); log.debug("Error while lookup",
	 * err); err.printStackTrace(); return null; } }
	 */
	
	/*
	 * private Properties getDBEnvironmentProperties() { Properties prop = new
	 * Properties(); // Load any properties fo JNDI connection in future. return
	 * prop; }
	 */
	
}
