/**
 * 
 */
package com.oradnata.event;

import java.io.File;

import javax.annotation.PostConstruct;

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
public class FlightInformationProcessorJob extends AbstractFIProcessorJob
		implements ApplicationContextAware, Retryable {

	private static final Logger log = LogManager.getLogger(FlightInformationProcessorJob.class);

	public int retryCount = 0;

	public FlightInformationProcessorJob() {
		super.setJobId(getJobNumber());
	}

	@Autowired
	private FolderCleaner folderCleaner;

	private ApplicationContext context;

	private String retryPosition = "";

	private File fileObj = null;

	private IATA_AIDX_FlightLegNotifRQ extractedMetaData = null;

	private String fileName = "";
	
	private int maxRetryCount = 5;

	@Autowired
	private RetryableQueueProcessor retryableQueueProcessor;

	@Override
	public void run() {
		log.info("Processing the JMS message: Job:" + getJobId());
		extractedMetaData = getExtractMetaData();
		if (null == extractedMetaData) {
			log.error("Unable to process this metadata " + getSource().toString());
			getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_PARSE_FILE);
			return;
		}
		String attribute2 = getAttribute2(extractedMetaData);
		fileName = getFileName(extractedMetaData,attribute2);
		fileObj = getFileContentCreator().createFileContent(getLocalTmpPath() + fileName + ".xml",
				getSource().toString());
		if (null == fileObj) {
			log.info("Unable to create the file:" + fileName);
			getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_CREATE_FILE);
			handleDuplicateMessage(getSource(), extractedMetaData);
			return;
		}
		log.info("Printing the file name: " + fileName);
		boolean isTransferred = uploadFileInSftpServer(fileObj);
		if (isTransferred) {
			Object entity = updateInDatabase(extractedMetaData.getSequenceNmbr(), fileObj,
					attribute2);
			if (null != entity) {
				log.info("Persisted the entity :" + entity.toString());
				getJmsCounter().incrementParam(JMSCounter.PROCESSED_SUCCESSFULLY);
			} else {
				log.error("Unable to persist the entity with file name:" + fileName);
				getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_UPDATE_IN_DB);
				setRetryPosition(RetryPositionEnum.DB_UPDATE.toString());
				retryableQueueProcessor.addJob(this);
			}
			log.info("File transferred successfully");
			postProcess();
		} else {
			log.error("File transfer failed");
			getJmsCounter().incrementParam(JMSCounter.UNABLE_TO_UPLOAD_FILE);
			setRetryPosition(RetryPositionEnum.FILE_UPLOAD.toString());
			retryableQueueProcessor.addJob(this);
		}
		log.info("Completed - " + getJobId());
	}

	private boolean uploadFileInSftpServer(File fileObj) {
		if(fileObj == null) {
			log.error("File object cannot be null. SFTP upload failed.");
			return false;
		}
		return sftpFile(fileObj.getAbsolutePath(), getRemoteFilePath() + fileObj.getName());
	}

	private Object updateInDatabase(int sequenceNumber, File fileObj, String attribute2) {
		if(fileObj == null ) {
			log.info("File object cannot be null, DB update failed.");
			return null;
		}
		Object entity = handleMetaData(sequenceNumber, fileObj, attribute2);
		return entity;
	}

	private IATA_AIDX_FlightLegNotifRQ getExtractMetaData() {
		return (IATA_AIDX_FlightLegNotifRQ) getDnataMetaDataExtractor().extractMetadata(getSource());
	}

	private String getFileName(IATA_AIDX_FlightLegNotifRQ extractedMetaData,String attribute) {
		int seqNumber = extractedMetaData.getSequenceNmbr();
		String timeStamp = getCurrentTimeStamp();
		return attribute + "_" + seqNumber + "_" + timeStamp;
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

	@Override
	public String getRetryPosition() {
		return this.retryPosition;
	}

	@Override
	public boolean retryFromLastPostition(String position) {
		try {
			if (position.equalsIgnoreCase(RetryPositionEnum.FILE_UPLOAD.toString())) {				
				boolean isTransferred = uploadFileInSftpServer(fileObj);
				if (isTransferred) {
					Object entity = updateInDatabase(extractedMetaData.getSequenceNmbr(), fileObj,
							getAttribute2(extractedMetaData));
					if (null != entity) {
						log.info("Persisted the entity :" + entity.toString());
						return true;
					} else {
						log.error("Unable to persist the entity with file name:" + fileObj);
						return false;
					}
				}
			} else if (position.equalsIgnoreCase(RetryPositionEnum.DB_UPDATE.toString())) {
				Object entity = updateInDatabase(extractedMetaData.getSequenceNmbr(), fileObj,
						getAttribute2(extractedMetaData));
				if (null != entity) {
					log.info("Persisted the entity :" + entity.toString());
					return true;
				} else {
					log.error("Unable to persist the entity with file name:" + fileObj);
					return false;
				}
			}
			return false;
		} catch (Exception err) {
			log.error("Error while retrying the job", err);
			return false;
		}
	}

	@Override
	public void incrementRetryCount() {
		this.retryCount = this.retryCount + 1;
	}

	@Override
	public int getMaxRetyCount() {
		return this.maxRetryCount;
	}

	@Override
	public String getJobDetails() {
		String fileName = "";
		if (fileObj != null) {
			fileName = fileObj.getAbsolutePath();
		}
		return getJobId() + "-" + fileName;
	}

	@Override
	public void postProcess() {		
		folderCleaner.addFileForCleanup(fileObj.getAbsolutePath());
	}

	@Override
	public int compareTo(Object o) {		
		return 0;
	}
	
	@PostConstruct
	public void init() {
		String maxRetryCount = getConnector().getAppProperties().getProperty("max.job.retry.on.failure.count");
		this.setMaxRetryCount(Integer.parseInt(maxRetryCount));
	}

}
