/**
 * 
 */
package com.oradnata.event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.oradnata.data.entity.MetadataEntity;
import com.oradnata.data.entity.MetadataEntityRepository;
import com.oradnata.jms.JMSCountService;
import com.oradnata.metadata.handle.DnataMetadataExtractor;
import com.oradnata.metadata.handle.MetadataExtractor;
import com.oradnata.sftp.upload.SFTPFileTransfer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Process the flight infomration and persists the data in a file. Store the
 * metadata information in the DB.
 * 
 * Step 1. Parse the XML content. Step 2. Create a file with the XML content.
 * Step 3. Rename the file with format. Step 4. Write the metadata in the DB.
 */
@Slf4j
@Component(value = "flightInformationProcessorJob")
@Data
public class FlightInformationProcessorJob implements Runnable {

	@Autowired
	@Qualifier("dnataMetaDataExtractor")
	private MetadataExtractor dnataMetaDataExtractor;

	private Object source;

	private final String prefix = "FlightNotificationRequest";

	private final String local_tmp_path = "/home/oracle/tmp/flight_info/";

	private final String remote_file_path = "/OIC/GFF/Request/";

	@Autowired
	private SFTPFileTransfer sftpFileTransfer;

	private final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";

	private SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);

	// @Autowired
	private MetadataEntityRepository metadataEntityRepository;

	@Autowired
	private JMSCountService jmsCountService;

	@Override
	public void run() {
		log.info("Processing the JMS message.");
		Map extractedMetaData = getExtractMetaData();
		if (null == extractedMetaData) {
			log.error("Unable to process this metadata " + source.toString());
			return;
		}
		String fileName = getFileName(extractedMetaData).replace(".", "").replace(" ", "-");
		jmsCountService.getJmsInformation().put(fileName, extractedMetaData);
		File fileObj = createFileContent(local_tmp_path + fileName + ".xml", source.toString());
		if (null == fileObj) {
			log.info("Unable to create the file:" + fileName);
			jmsCountService.getJmsInformation().put(fileName + "-FileCreation", "Unable to create file");
			return;
		}
		log.info("Printing the file name: " + fileName);
		jmsCountService.getJmsInformation().put(fileName + "-FileCreation", "File Created Successfully");

		boolean isTransferred = sftpFile(fileObj.getAbsolutePath(), remote_file_path + fileObj.getName());
		if (isTransferred) {
			String seqId = extractedMetaData.get(DnataMetadataExtractor.SEQ_NUM).toString();
			Object entity = handleMetaData(seqId, fileObj);
			if (null != entity) {
				log.info("Persisted the entity :" + entity.toString());
			} else {
				log.error("Unable to persist the entity with file name:" + fileName);
			}
			log.info("File transferred successfully");
		}

	}

	private Map getExtractMetaData() {
		return dnataMetaDataExtractor.extractMetadata(source);
	}

	private String getFileName(Map extractedMetaData) {
		String seqNumber = extractedMetaData.get(DnataMetadataExtractor.SEQ_NUM).toString();
		String timeStamp = extractedMetaData.get(DnataMetadataExtractor.TIMESTAMP).toString();
		return prefix + "_" + seqNumber + "_" + timeStamp;
	}

	private File createFileContent(String fileName, String content) {
		File fileObj = new File(fileName);
		boolean flag;
		try {
			log.info("Printing the file name :" + fileName);
			flag = fileObj.createNewFile();
			FileWriter writter = new FileWriter(fileObj);
			writter.write(content);
			writter.close();
			if (flag) {
				log.info("File created successfully:" + fileName);
				return fileObj;
			} else {
				log.info("Unable to create the file;" + fileName);
				return null;
			}
		} catch (IOException err) {
			log.error("Error while creating the file:", err);
			return null;
		}
	}

	private boolean sftpFile(String localFileAbsPath, String remotePath) {
		log.info("Transfering the file: " + localFileAbsPath + " to " + remotePath);
		boolean isTransferred = sftpFileTransfer.transferFile(localFileAbsPath, remotePath);
		if (isTransferred) {
			log.info("File is transferred successfully");
		} else {
			log.info("File transfered failed");
		}
		return isTransferred;
	}

	private Object handleMetaData(String seqId, File fileObj) {
		if (null != fileObj) {
			MetadataEntity entity = getEntity(fileObj.getAbsolutePath(), fileObj.getName(), seqId);
			this.metadataEntityRepository.save(entity);
			return entity;
		}
		return null;
	}

	private String getCurrentTimeStamp() {
		Date now = new Date();
		return sdf.format(now);
	}

	private MetadataEntity getEntity(String filePath, String fileName, String seqId) {
		MetadataEntity entity = new MetadataEntity();
		entity.setCreatedDate(getCurrentTimeStamp());
		entity.setSeqId(seqId);
		entity.setFileName(fileName);
		entity.setFilePath(filePath);
		entity.setFlag("UNPROCESSED");
		return entity;
	}

}
