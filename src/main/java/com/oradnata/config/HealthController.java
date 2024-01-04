package com.oradnata.config;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.oradnata.jms.JMSCountService;
import com.oradnata.sftp.upload.SCPConnector;
import com.oradnata.sftp.upload.SFTPFileTransfer;

@RestController
public class HealthController {

	Logger log = LoggerFactory.getLogger(HealthController.class);

	@Autowired
	private JMSCountService jmsCountService;

	@Autowired
	private AppDataSource appDataSource;

	@Autowired
	private SCPConnector sftpConnector;

	@Autowired
	private SFTPFileTransfer sftpTransfer;

	@GetMapping("health")
	public String getTest() {
		log.info("Testing the health API");
		return "Up and running";
	}

	@GetMapping("jmsCount")
	public String getJMSCount() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			log.info("Testing the JMS Count API");
			String jsonData = objectMapper.writeValueAsString(jmsCountService.getJmsInformation());
			return jsonData;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "Unable to fetch the JMS count, " + e.getMessage();
		}
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
			StringWriter errors = new StringWriter();
			err.printStackTrace(new PrintWriter(errors));
			log.info("[Error]" + errors.toString());
			log.debug("Error while DB lookup in API", err);
			err.printStackTrace();
			return "Error found";
		}
	}

	@GetMapping("db-status1")
	public String getDBStatus1() {
		try {
			String jndiName = appDataSource.getJNDIName();
			log.info("trying to make a lookup;" + jndiName);
			DataSource dataSource = appDataSource.getDataSource1();
			if (null != dataSource) {
				log.info(dataSource.toString());
				return "Datasource found:" + dataSource.toString();
			} else {
				log.info("Unable to find the data source" + jndiName);
				return "Couldnt find the data source";
			}
		} catch (Exception err) {
			StringWriter errors = new StringWriter();
			err.printStackTrace(new PrintWriter(errors));
			log.info("[Error]" + errors.toString());
			log.debug("Error while DB lookup in API", err);
			err.printStackTrace();
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
			StringWriter errors = new StringWriter();
			err.printStackTrace(new PrintWriter(errors));
			log.info("[Error]" + errors.toString());
			log.debug("Error while creating the SFTP Connection", err);
			return err.getMessage();
		}
	}

	@GetMapping("sftp-trans-status")
	public String getSFTPFileTransStatus() {
		try {
			log.info("Trying to make a SFTP connection.");
			return sftpTransfer.getStatusMap().toString();
		} catch (Exception err) {
			StringWriter errors = new StringWriter();
			err.printStackTrace(new PrintWriter(errors));
			log.info("[Error]" + errors.toString());
			return "Error";
		}
	}
}
