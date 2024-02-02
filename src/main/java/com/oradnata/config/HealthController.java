package com.oradnata.config;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oradnata.event.JMSCounter;

@RestController
public class HealthController {

	private static final Logger log = LogManager.getLogger(HealthController.class);

	/*
	 * @Autowired private AppDataSource appDataSource;
	 * 
	 * @Autowired private SCPConnector sftpConnector;
	 * 
	 * @Autowired private SFTPFileTransfer sftpTransfer;
	 */

	@Autowired
	private JMSCounter jmsCounter;
	
	private static Date startTime = new Date();

	@GetMapping("health")
	public String getTest() {
		log.info("Testing the health API");
		return "Up and running";
	}

	/*
	 * @GetMapping("db-status") public String getDBStatus() { try { String jndiName
	 * = appDataSource.getJNDIName(); log.info("trying to make a lookup;" +
	 * jndiName); DataSource dataSource = appDataSource.getDataSource(); if (null !=
	 * dataSource) { log.info(dataSource.toString()); return "Datasource found:" +
	 * dataSource.toString(); } else { log.info("Unable to find the data source" +
	 * jndiName); return "Couldnt find the data source"; } } catch (Exception err) {
	 * log.error("Error while DB lookup in API", err); return "Error found"; } }
	 */	

	/*
	 * @GetMapping("sftp-status") public String getSFTPConnectionStatus() { try {
	 * log.info("Trying to make a SFTP connection."); ChannelSftp channelSftp =
	 * sftpConnector.prepareSFTPConnection(); if (null == channelSftp) { return
	 * "Unable to make SFTP Connection"; } else { log.info(channelSftp.toString());
	 * return "Bulk requests " + channelSftp.getBulkRequests(); } } catch
	 * (JSchException err) { log.info("Error while creating the SFTP Connection",
	 * err); return err.getMessage(); } }
	 */
	
	/*
	 * @GetMapping("setSFTPServer") public String disconnectSFTP(@RequestParam
	 * String hostName) { try { log.info("Set the SFTP Server");
	 * sftpConnector.getConnector().getAppProperties().setProperty("sftp.host",
	 * hostName); sftpTransfer.channelSftp = null; return "Success" + hostName; }
	 * catch (Exception err) { log.info("Error while creating the SFTP Connection",
	 * err); return err.getMessage(); } }
	 * 
	 * 
	 * @GetMapping("setJNDIName") public String changeJNDIName(@RequestParam String
	 * JNDIName) { try { log.info("Set the JNSI Namer");
	 * appDataSource.prop.setProperty("spring.ds.jndi-name", JNDIName);
	 * appDataSource.ds = null; return "Success" + JNDIName; } catch (Exception err)
	 * { log.info("Error while creating the SFTP Connection", err); return
	 * err.getMessage(); } }
	 */
	
	@GetMapping("getJMSStatus")
	public String getJMSStatus() {
		try {
			log.info("get JMS Status");
			 ObjectMapper objectMapper = new ObjectMapper();
			 String jmsStatus = objectMapper.writeValueAsString(jmsCounter.getJMSData());
			return jmsStatus;
		} catch (Exception err) {
			log.error("Error JMS status API Call", err);
			return "Error in getting the status";
		}
	}
	
	@GetMapping("resetJMSStatus")
	public String resetJMSStatus() {
		try {
			log.info("Reset JMS Status");
			jmsCounter.resetMap();
			return jmsCounter.getJMSData().toString();
		} catch (Exception err) {
			log.error("Error JMS status API Call", err);
			return "Error in getting the status";
		}
	}	
	
	@GetMapping("serverStartTime")
	public String getServerStartTime() {		
			log.info("Get the server start time");			
			return startTime.toString();		
	}	
}
