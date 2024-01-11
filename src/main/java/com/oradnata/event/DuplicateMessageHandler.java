/**
 * 
 */
package com.oradnata.event;

import java.util.Properties;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oradnata.config.AppDataSource;
import com.oradnata.config.ApplicationConnector;

import lombok.Data;

@Data
public class DuplicateMessageHandler extends FileContentCreator implements Runnable {

	private static final Logger log = LogManager.getLogger(AppDataSource.class);

	private Object source;

	private ApplicationConnector appConnector;

	private String seqNumber;

	private Random random = new Random();
	
	private String jobId;

	private String getFileName() {
		return "Duplicate-" + seqNumber + "-" + getRandomNumber() + ".xml";
	}

	@Override
	public void run() {
		log.info("Handling the duplicate file created by job" + jobId);
		Properties properties = appConnector.getAppProperties();
		String duplicateFilePath = properties.getProperty("sftp.local-temp-dup-file-path");
		String filePath = duplicateFilePath + getFileName();
		log.info("Writing the file content" + filePath);
		if(null == source) {
			source = "No Content";
		}
		createFileContent(filePath,getSource().toString());
	}

	private String getRandomNumber() {
		return random.nextInt(1000) + "-" + random.nextInt(1000);
	}

}
