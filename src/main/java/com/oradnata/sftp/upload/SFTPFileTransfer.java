/**
 * 
 */
package com.oradnata.sftp.upload;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Component
@Slf4j
public class SFTPFileTransfer {

	@Autowired
	private SCPConnector connector = new SCPConnector();

	private ChannelSftp channelSftp;
	
	private Map fileStatusMap = new HashMap();

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
			fileStatusMap.put(remoteFile, "File transferred successfully");
			return true;
		} catch (Exception err) {
			log.error("Error while sending the file to the remote server", err);
			fileStatusMap.put(remoteFile, "File transferred failed");
			return false;
		}
	}
	
	public Map getStatusMap() {
		return fileStatusMap;
	}

}
