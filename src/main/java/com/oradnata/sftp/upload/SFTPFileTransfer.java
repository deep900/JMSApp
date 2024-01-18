/**
 * 
 */
package com.oradnata.sftp.upload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;

/**
 *  This class provides the functionality to transfer the file into SFTP Server.
 */
@Component
public class SFTPFileTransfer {
	
	private static final Logger log = LogManager.getLogger(SFTPFileTransfer.class);

	@Autowired
	private SCPConnector connector;

	private ChannelSftp channelSftp;
	
	public synchronized boolean transferFile(String localFile, String remoteFile) {
		try {
			if (connector.getJschSession() == null || channelSftp == null) {
				log.info("Opening a SSH connection to transfer the file.");
				try {
					channelSftp = connector.prepareSFTPConnection();
					channelSftp.connect();
				} catch (JSchException err) {
					log.error("Error while connecting to the SFTP Server", err);
					return false;
				}
			} else {
				if (channelSftp.isClosed()) {
					channelSftp.connect();
				}
			}
			log.info("Trying to transfer the file:" + localFile);
			channelSftp.put(localFile, remoteFile);
			return true;
		} catch (Exception err) {
			log.error("Error while sending the file to the remote server", err);			
			return false;
		}
	}	
}
