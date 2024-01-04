/**
 * 
 */
package com.oradnata.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

public class ApplicationConnector {

	private Properties properties = null;

	private void loadAppProperties() {
		if (null == properties) {
			File appPropFile = new File("/home/oracle/tmp/application.properties");
			try {
				FileInputStream fis = new FileInputStream(appPropFile);
				properties = new Properties();
				properties.load(fis);
			} catch (FileNotFoundException err) {
				StringWriter errors = new StringWriter();
				err.printStackTrace(new PrintWriter(errors));
				System.out.println("Error while loading the properties:" + errors.toString());
			} catch (IOException err) {
				StringWriter errors = new StringWriter();
				err.printStackTrace(new PrintWriter(errors));
				System.out.println("Error while loading the properties:" + errors.toString());
				err.printStackTrace();
			}
		}
	}

	public Properties getAppProperties() {
		loadAppProperties();
		return properties;
	}
}
