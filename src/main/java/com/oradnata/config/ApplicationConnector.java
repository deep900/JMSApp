/**
 * 
 */
package com.oradnata.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationConnector {

	private static final Logger log = LogManager.getLogger(ApplicationConnector.class);
	
	private Properties properties = null;

	private void loadAppProperties() {
		if (null == properties) {
			File appPropFile = new File("/home/oracle/tmp/application.properties");
			try {
				FileInputStream fis = new FileInputStream(appPropFile);
				properties = new Properties();
				properties.load(fis);
			} catch (FileNotFoundException err) {
				log.error("Error while loading the properties", err);
			} catch (IOException err) {		
				log.error("Error while loading the properties", err);
			}
		}
	}

	public Properties getAppProperties() {
		loadAppProperties();
		return properties;
	}
}
