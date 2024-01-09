/**
 * 
 */
package com.oradnata.event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.oradnata.config.AppDataSource;
import com.oradnata.config.ApplicationConnector;
import com.oradnata.data.entity.MetadataEntity;
import com.oradnata.metadata.handle.DnataMetadataExtractor;
import com.oradnata.metadata.handle.MetadataExtractor;
import com.oradnata.sftp.upload.SFTPFileTransfer;

import lombok.Data;

/**
 * Process the flight infomration and persists the data in a file. Store the
 * metadata information in the DB.
 * 
 * Step 1. Parse the XML content. Step 2. Create a file with the XML content.
 * Step 3. Rename the file with format. Step 4. Write the metadata in the DB.
 */

@Component(value = "flightInformationProcessorJob")
@Data
public class FlightInformationProcessorJob implements Runnable {
	
	private static final Logger log = LogManager.getLogger(FlightInformationProcessorJob.class);

	@Autowired
	@Qualifier("dnataMetaDataExtractor")
	private MetadataExtractor dnataMetaDataExtractor;

	private Object source;

	private final String prefix = "FlightNotificationRequest";

	private String local_tmp_path = null;

	private String remote_file_path = null;

	@Autowired
	private SFTPFileTransfer sftpFileTransfer;

	private final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";

	private SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);	
	
	private List deleteFileList = new ArrayList();
	
	@Autowired
	private AppDataSource appDataSource;
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolExecutor;
	
	private ApplicationConnector connector = new ApplicationConnector();

	@Override
	public void run() {
		log.info("Processing the JMS message.");
		Map extractedMetaData = getExtractMetaData();
		if (null == extractedMetaData) {
			log.error("Unable to process this metadata " + source.toString());
			return;
		}
		String fileName = getFileName(extractedMetaData).replace(".", "").replace(" ", "-");
		File fileObj = createFileContent(local_tmp_path + fileName + ".xml", source.toString());
		if (null == fileObj) {
			log.info("Unable to create the file:" + fileName);
			return;
		}
		log.info("Printing the file name: " + fileName);
		
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
			deleteFileList.add(localFileAbsPath);
			if(deleteFileList.size() >= 10) {
				Runnable runnable = new FolderCleaner(new ArrayList(deleteFileList));
				threadPoolExecutor.execute(runnable);
				deleteFileList.clear();
			}
		} else {
			log.info("---- File transfered failed -----");
		}
		return isTransferred;
	}

	private Object handleMetaData(String seqId, File fileObj) {
		try {
			if (null != fileObj) {
				MetadataEntity entity = getEntity(remote_file_path, fileObj.getName(), seqId);
				appDataSource.saveMetadataEntity(entity);
				log.info("Saved the entity");
				return entity;
			}
		} catch (Exception err) {
			log.error("Error in handle metadata", err);
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
		entity.setFlag("NEW");
		return entity;
	}
	
	@PostConstruct
	private void loadProperties() {
		Properties prop = connector.getAppProperties();
		this.local_tmp_path = prop.getProperty("sftp.local-temp-file-path");
		this.remote_file_path = prop.getProperty("sftp.remote-file-path");
		log.info("Printing the local temp path and the remote path:" + local_tmp_path + "," + remote_file_path);
	}
}
