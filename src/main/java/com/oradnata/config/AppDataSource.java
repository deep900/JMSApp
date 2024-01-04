/**
 * 
 */
package com.oradnata.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Component;

import com.oradnata.util.PublicUtility;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Component
@Slf4j
public class AppDataSource {

	private ApplicationConnector appConnector;

	private Properties prop = null;

	private String jndiName = "";
	
	private PublicUtility utility = new PublicUtility();

	private void loadProperties() {
		appConnector = new ApplicationConnector();
		if (null == prop) {
			prop = appConnector.getAppProperties();
		}
	}

	public DataSource getDataSource1() {
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
			env.put(Context.PROVIDER_URL, "t3://localhost:7101");			
			InitialContext ctx = new InitialContext(env);
			getJNDIName();
			DataSource ds = (DataSource) ctx.lookup(jndiName);
			return ds;
		} catch (NamingException err) {
			StringWriter errors = new StringWriter();
			err.printStackTrace(new PrintWriter(errors));
			log.info("[Error]" + errors.toString());
			log.debug("Error while lookup", err);
			err.printStackTrace();
			return null;
		}
	}

	public DataSource getDataSource() {
		JndiTemplate jndiTemplate = new JndiTemplate(getDBEnvironmentProperties());
		try {
			getJNDIName();
			log.info("Trying to get the JNDI; " + jndiName);
			System.out.println("JNDI Lookup" + jndiName);
			DataSource ds = (DataSource) jndiTemplate.getContext().lookup(jndiName);
			return ds;
		} catch (NamingException err) {
			StringWriter errors = new StringWriter();
			err.printStackTrace(new PrintWriter(errors));
			log.info("[Error]" + errors.toString());
			log.debug("Error while lookup", err);
			err.printStackTrace();
			return null;
		}
	}

	private Properties getDBEnvironmentProperties() {		
		String namingProvider = prop.getProperty("spring.ds.naming-provider");
		log.info("Printing the naming provider:" + namingProvider);
		if (namingProvider != null && !namingProvider.isBlank()) {
			prop.setProperty("java.naming.provider.url", namingProvider);
		} else {
			log.info("Naming provider is not given");
		}		
		return prop;
	}

	public String getJNDIName() {
		jndiName = prop.getProperty("spring.ds.jndi-name");
		return jndiName;
	}

	@PostConstruct
	public void loadProp() {
		loadProperties();
	}
}
