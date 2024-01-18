/**
 * 
 */
package com.oradnata.event;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.oradnata.metadata.handle.IATA_AIDX_FlightLegNotifRQ;

import lombok.Data;

/**
 * Process the flight infomration and persists the data in a file. Store the
 * metadata information in the DB.
 * 
 * Step 1. Parse the XML content. Step 2. Create a file with the XML content.
 * Step 3. Rename the file with format. Step 4. Write the metadata in the DB.
 */
@Data
public class FlightInformationProcessorJob extends AbstractFIProcessorJob implements ApplicationContextAware {

	private static final Logger log = LogManager.getLogger(FlightInformationProcessorJob.class);

	public FlightInformationProcessorJob() {
		super.setJobId(getJobNumber());
	}	

	@Autowired
	private FolderCleaner folderCleaner;

	private ApplicationContext context;

	@Override
	public void run() {
		log.info("Processing the JMS message: Job:" + getJobId());
		IATA_AIDX_FlightLegNotifRQ extractedMetaData = getExtractMetaData();
		if (null == extractedMetaData) {
			log.error("Unable to process this metadata " + getSource().toString());
			getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_PARSE_FILE);
			return;
		}
		String fileName = getFileName(extractedMetaData).replace(".", "").replace(" ", "-");
		File fileObj = getFileContentCreator().createFileContent(getLocalTmpPath() + fileName + ".xml",
				getSource().toString());
		if (null == fileObj) {
			log.info("Unable to create the file:" + fileName);
			getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_CREATE_FILE);
			handleDuplicateMessage(getSource(), extractedMetaData);
			return;
		}
		log.info("Printing the file name: " + fileName);
		boolean isTransferred = sftpFile(fileObj.getAbsolutePath(), getRemoteFilePath() + fileObj.getName());
		if (isTransferred) {
			Object entity = handleMetaData(extractedMetaData.getSequenceNmbr(), fileObj,getAttribute2(extractedMetaData));
			if (null != entity) {
				log.info("Persisted the entity :" + entity.toString());
				getJmsCounter().incrementParam(JMSCounter.PROCESSED_SUCCESSFULLY);
			} else {
				log.error("Unable to persist the entity with file name:" + fileName);
				getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_UPDATE_IN_DB);
			}
			log.info("File transferred successfully");
			folderCleaner.addFileForCleanup(fileObj.getAbsolutePath());
		} else {
			log.error("File transfer failed");
			getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_UPLOAD_FILE);
		}
		log.info("Completed - " + getJobId());
	}

	private IATA_AIDX_FlightLegNotifRQ getExtractMetaData() {
		return (IATA_AIDX_FlightLegNotifRQ) getDnataMetaDataExtractor().extractMetadata(getSource());
	}

	private String getFileName(IATA_AIDX_FlightLegNotifRQ extractedMetaData) {
		int seqNumber = extractedMetaData.getSequenceNmbr();
		String timeStamp = getCurrentTimeStamp();
		return FileContentCreator.prefix + "_" + seqNumber + "_" + timeStamp;
	}

	private void handleDuplicateMessage(Object source, IATA_AIDX_FlightLegNotifRQ extractedMetadata) {
		DuplicateMessageHandler duplicateHandler = (DuplicateMessageHandler) this.context
				.getBean("duplicateMessageHandler");
		duplicateHandler.setSource(source);
		duplicateHandler.setExtractedMetaData(extractedMetadata);		
		duplicateHandler.setJobId("Duplicate " + getJobId());
		getThreadPoolExecutor().execute(duplicateHandler);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
