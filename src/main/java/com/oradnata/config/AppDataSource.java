/**
 * 
 */
package com.oradnata.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Component;

import com.oradnata.data.entity.MetadataEntity;
import com.oradnata.util.PublicUtility;

/**
 * 
 */
@Component(value = "appDataSource")
public class AppDataSource {

	private static final Logger log = LogManager.getLogger(AppDataSource.class);

	private ApplicationConnector appConnector;

	private Properties prop = null;

	private String jndiName = "";

	private PublicUtility utility = new PublicUtility();

	private DataSource ds = null;

	private void loadProperties() {
		appConnector = new ApplicationConnector();
		if (null == prop) {
			prop = appConnector.getAppProperties();
		}
	}

	private void writeErrorLog(Exception err) {		
		log.error("Error occured:", err);	
	}

	public DataSource getDataSource() {
		if (ds == null) {
			JndiTemplate jndiTemplate = new JndiTemplate(getDBEnvironmentProperties());
			try {
				getJNDIName();
				log.info("Trying to get the JNDI; " + jndiName);				
				ds = (DataSource) jndiTemplate.getContext().lookup(jndiName);
				return ds;
			} catch (NamingException err) {
				writeErrorLog(err);
				return null;
			}
		} else {
			return ds;
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

	public Object saveMetadataEntity(MetadataEntity obj) {
		log.info("About to save the entity;" + obj.toString());
		Connection connection = null;
		String SQL = "INSERT INTO GFF_NOTIFICATIONMSG_TRACK (SEQ_ID,FILE_PATH,FILE_NAME,CREATED_DATE,UPDATED_DATE,FLAG) VALUES (?,?,?,?,?,?)";
		DataSource ds = getDataSource();
		try {
			connection = ds.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(SQL);
			pstmt.setString(1, obj.getSeqId());
			pstmt.setString(2, obj.getFilePath());
			pstmt.setString(3, obj.getFileName());
			pstmt.setString(4, obj.getCreatedDate());
			pstmt.setString(5, obj.getUpdatedDate());
			pstmt.setString(6, obj.getFlag());
			pstmt.executeUpdate();
			pstmt.close();
			log.info("Saved the entity in table.");
		} catch (SQLException err) {
			writeErrorLog(err);
		} finally {
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					writeErrorLog(e);
				}
			}
		}
		return obj;
	}
}
