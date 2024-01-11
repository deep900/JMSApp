package com.oradnata.config;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.oradnata.event.JMSCounter;
import com.oradnata.sftp.upload.SCPConnector;
import com.oradnata.sftp.upload.SFTPFileTransfer;

@RestController
public class HealthController {

	private static final Logger log = LogManager.getLogger(HealthController.class);

	@Autowired
	private AppDataSource appDataSource;

	@Autowired
	private SCPConnector sftpConnector;

	@Autowired
	private SFTPFileTransfer sftpTransfer;

	@Autowired
	private JMSCounter jmsCounter;

	@GetMapping("health")
	public String getTest() {
		log.info("Testing the health API");
		return "Up and running";
	}

	@GetMapping("db-status")
	public String getDBStatus() {
		try {
			String jndiName = appDataSource.getJNDIName();
			log.info("trying to make a lookup;" + jndiName);
			DataSource dataSource = appDataSource.getDataSource();
			if (null != dataSource) {
				log.info(dataSource.toString());
				return "Datasource found:" + dataSource.toString();
			} else {
				log.info("Unable to find the data source" + jndiName);
				return "Couldnt find the data source";
			}
		} catch (Exception err) {
			log.error("Error while DB lookup in API", err);
			return "Error found";
		}
	}

	@GetMapping("sftp-status")
	public String getSFTPConnectionStatus() {
		try {
			log.info("Trying to make a SFTP connection.");
			ChannelSftp channelSftp = sftpConnector.prepareSFTPConnection();
			if (null == channelSftp) {
				return "Unable to make SFTP Connection";
			} else {
				log.info(channelSftp.toString());
				return "Bulk requests " + channelSftp.getBulkRequests();
			}
		} catch (JSchException err) {
			log.info("Error while creating the SFTP Connection", err);
			return err.getMessage();
		}
	}

	@GetMapping("getJMSStatus")
	public String getJMSStatus() {
		try {
			log.info("get JMS Status");
			return jmsCounter.getJMSData().toString();
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
}
