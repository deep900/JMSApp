/**
 * 
 */
package com.oradnata.sftp.upload;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.oradnata.config.ApplicationConnector;
import com.oradnata.util.PublicUtility;

import lombok.Data;

/**
 * This class connects to the SFTP Server. And allows to transfer files to
 * target system.
 */
@Component
@Data
public class SCPConnector {

	private String hostName = null;

	private String userName = null;

	private String password = null;

	private String knownHosts = null;

	private Session jschSession = null;

	private String privateKeyFile = null;

	private String strictHostKeyChecking = null;

	PublicUtility publicUtility = new PublicUtility();

	ApplicationConnector connector = new ApplicationConnector();

	private Properties properties = null;
	
	private static final Logger log = LogManager.getLogger(SCPConnector.class);

	public ChannelSftp prepareSFTPConnection() throws JSchException {
		userName = properties.getProperty("sftp.username");
		password = properties.getProperty("sftp.enc-credentials");
		hostName = properties.getProperty("sftp.host");
		knownHosts = properties.getProperty("sftp.known-hosts-file");
		privateKeyFile = properties.getProperty("sftp.private.keyfile");
		strictHostKeyChecking = properties.getProperty("sftp.strict-host-key-checking");
		log.info("Preparing the SCP Connector: " + hostName + "," + userName + "," + getDecryptedPassword(password)
				+ ",known hosts:" + knownHosts + " Private key file:" + privateKeyFile + "Scrict host key check:"
				+ strictHostKeyChecking);
		try {
			JSch jsch = new JSch();
			if (null != strictHostKeyChecking && !strictHostKeyChecking.isBlank()) {
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", strictHostKeyChecking);
				jsch.setConfig(config);
			} else {
				log.info("Strict host key checking is disabled");
			}
			if (null != privateKeyFile && !privateKeyFile.isBlank()) {
				jsch.addIdentity(userName, getPvtKey(privateKeyFile), null, null);
			} else {
				log.info("No private key file provided for SFTP connection.");
			}
			if (null != knownHosts && !knownHosts.isBlank()) {
				jsch.setKnownHosts(knownHosts);
			} else {
				log.info("Known hosts file not set for SFTP Connection.");
			}
			jschSession = jsch.getSession(userName, hostName);
			if (null != password && !password.isBlank()) {
				jschSession.setPassword(getDecryptedPassword(password));
			} else {
				log.info("Password is not set for SFTP connection.");
			}
			jschSession.connect();
			jschSession.setServerAliveCountMax(1);
			jschSession.setServerAliveInterval(5000);
			return (ChannelSftp) jschSession.openChannel("sftp");
		} catch (Exception err) {
			log.error("Error occured;"+ err.toString());
			return null;
		}
	}

	private byte[] getPvtKey(String privateKeyFile) {
		log.info("Loading the private key file:" + privateKeyFile);
		Resource res = new ClassPathResource(privateKeyFile);
		try {
			InputStream is = res.getInputStream();
			byte[] bytes = new byte[(int) is.available()];
			DataInputStream dis = new DataInputStream(is);
			dis.readFully(bytes);
			return bytes;
		} catch (IOException err) {
			log.error("Error while loading prrivate stop");
			return null;
		}
	}

	@PostConstruct
	public void loadProp() {
		properties = connector.getAppProperties();
	}

	private String getDecryptedPassword(String encPassword) {
		return publicUtility.DecryptText(encPassword);
	}
}
