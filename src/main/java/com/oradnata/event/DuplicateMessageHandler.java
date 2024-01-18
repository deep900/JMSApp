/**
 * 
 */
package com.oradnata.event;

import java.io.File;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oradnata.config.AppDataSource;
import com.oradnata.metadata.handle.IATA_AIDX_FlightLegNotifRQ;

import lombok.Data;

@Data
public class DuplicateMessageHandler extends AbstractFIProcessorJob {

	private static final Logger log = LogManager.getLogger(AppDataSource.class);	

	private IATA_AIDX_FlightLegNotifRQ extractedMetaData;	

	private char[] part1 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private char[] part2 = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

	@Override
	public void run() {
		log.info("Handling the duplicate file created by job -" + getJobId());
		loadProperties();
		String filePath = getLocalTmpPath() + getFileName() + ".xml";
		log.info("Writing the file content : " + filePath);
		if (null == getSource()) {
			setSource("No Content");
		}
		File fileObj = getFileContentCreator().createFileContent(filePath, getSource().toString());
		boolean isTransferred = sftpFile(fileObj.getAbsolutePath(), getRemoteFilePath() + fileObj.getName());
		if (isTransferred) {
			Object entity = handleMetaData(extractedMetaData.getSequenceNmbr(), fileObj,getAttribute2(extractedMetaData));
			if (null != entity) {
				log.info("Persisted the entity :" + entity.toString());
				getJmsCounter().incrementParam(JMSCounter.PROCESSED_SUCCESSFULLY);
			} else {
				log.error("Unable to persist the entity with file name:" + fileObj.getName());
				getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_UPDATE_IN_DB);
			}
			log.info("File transferred successfully");
		} else {
			log.error("File transfer failed");
			getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_UPLOAD_FILE);
		}
		log.info("Completed - " + getJobId());
	}

	private String getRandomNumber() {
		return part1[getRandom().nextInt(part1.length - 1)] + "" + part2[getRandom().nextInt(part2.length - 1)];
	}

	private String getFileName() {
		int seqNumber = extractedMetaData.getSequenceNmbr();
		String timeStamp = getCurrentTimeStamp();
		String fName = FileContentCreator.duplicatePrefix + "_" + seqNumber + "_" + getRandomNumber() + "_" + timeStamp;
		return fName.replace(".", "").replace(" ", "-");
	}
	
	@PostConstruct
	private void loadProperties() {
		Properties properties = getConnector().getAppProperties();
		String duplicateFilePath = properties.getProperty("sftp.local-temp-dup-file-path");
		String remoteFilePath = properties.getProperty("sftp.remote-file-path");
		setRemoteFilePath(remoteFilePath);
		setLocalTmpPath(duplicateFilePath);
	}

}
