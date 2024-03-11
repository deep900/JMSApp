package com.oradnata.event;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.oradnata.config.AppDataSource;
import com.oradnata.config.ApplicationConnector;
import com.oradnata.data.entity.MetadataEntity;
import com.oradnata.metadata.handle.FlightExtensions;
import com.oradnata.metadata.handle.FlightLeg;
import com.oradnata.metadata.handle.IATA_AIDX_FlightLegNotifRQ;
import com.oradnata.metadata.handle.LegIdentifier;
import com.oradnata.metadata.handle.MetadataExtractor;
import com.oradnata.metadata.handle.TPA_Extension;

import com.oradnata.sftp.upload.SFTPFileTransfer;

import lombok.Data;

@Data
public abstract class AbstractFIProcessorJob implements Runnable {

	private Object source;

	private String localTmpPath = null;

	private String remoteFilePath = null;

	//YYYYMMDDHH24MISSMS.xml.
	private final String TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS";

	private SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);

	@Autowired
	private ApplicationConnector connector;

	private String jobId;

	private Random random = new Random();

	@Autowired
	private AppDataSource appDataSource;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolExecutor;

	@Autowired
	private SFTPFileTransfer sftpFileTransfer;

	@Autowired
	@Qualifier("dnataMetaDataExtractor")
	private MetadataExtractor dnataMetaDataExtractor;

	private static final Logger log = LogManager.getLogger(AbstractFIProcessorJob.class);

	@Autowired
	private JMSCounter jmsCounter;

	@Autowired
	private FileContentCreator fileContentCreator;

	public String getJobNumber() {
		return "Job-" + random.nextInt(10000) + "-" + random.nextInt(10000);
	}

	public boolean sftpFile(String localFileAbsPath, String remotePath) {
		log.info("Transfering the file: " + localFileAbsPath + " to " + remotePath);
		boolean isTransferred = getSftpFileTransfer().transferFile(localFileAbsPath, remotePath);
		if (isTransferred) {
			log.info("File is transferred successfully");
		} else {
			log.info("---- File transfered failed -----");
		}
		return isTransferred;
	}

	public Object handleMetaData(Integer seqId, File fileObj, String attribute2) {
		try {
			if (null != fileObj) {
				log.info("Inside the handle metadata : " + seqId + ",Attribute 2:" + attribute2 + ",File Obj"
						+ fileObj.getName());
				MetadataEntity entity = getEntity(remoteFilePath, fileObj.getName(), seqId, attribute2);
				appDataSource.saveMetadataEntity(entity);
				log.info("Saved the entity");
				return entity;
			}
		} catch (Exception err) {
			log.error("Error in handle metadata", err);
		}
		return null;
	}

	private MetadataEntity getEntity(String filePath, String fileName, Integer seqId, String attribute2) {
		MetadataEntity entity = new MetadataEntity();
		entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
		entity.setSeqId(seqId);
		entity.setFileName(fileName);
		entity.setFilePath(filePath);
		entity.setFlag("NEW");
		entity.setAttribute2(attribute2);
		return entity;
	}

	public String getAttribute2(IATA_AIDX_FlightLegNotifRQ flightInfo) {
		try {
			if (null != flightInfo && null != flightInfo.getFlightLeg()) {
				LegIdentifier legIdentifier = flightInfo.getFlightLeg().getLegIdentifier();
				String operationalSuffix = getOperationalSuffix(legIdentifier);
				String attribute2 = "";
				if (operationalSuffix.equals("")) {
					attribute2 = legIdentifier.getAirline() + "_" + legIdentifier.getFlightNumber() + "_"
							+ getFlightType(legIdentifier) + "_" + flightInfo.getFlightLeg().getScheduledDate();
				} else {
					attribute2 = legIdentifier.getAirline() + "_" + legIdentifier.getFlightNumber() + "_"
							+ operationalSuffix + "_" + getFlightType(legIdentifier) + "_"
							+ flightInfo.getFlightLeg().getScheduledDate();
				}
				log.info("Printing the attribute 2 value:" + attribute2);
				return attribute2;
			} else {
				return "";
			}
		} catch (Exception err) {
			log.error("Error while computing the attribute 2", err);
		}
		return "";
	}

	private String getOperationalSuffix(LegIdentifier legIdentifier) {
		if (null != legIdentifier.getOperationalSuffix()) {
			return legIdentifier.getOperationalSuffix();
		} else {
			return "";
		}
	}

	private String getFlightType(LegIdentifier legIdentifier) {
		if (null != legIdentifier) {
			if (legIdentifier.getDepartureAirport().equalsIgnoreCase("SIN")) {
				return "D";
			} else {
				return "A";
			}
		}
		return "";
	}

	private String getScheduledDate(IATA_AIDX_FlightLegNotifRQ flightInfo) {

		/*
		 * if (null != flightLeg) { TPA_Extension tpaExtension =
		 * flightLeg.getTPA_Extension(); if (null != tpaExtension) { FlightExtensions
		 * flightExtensions = tpaExtension.getFlightExtensions(); if (null !=
		 * flightExtensions) { List<ValueBean> valueList =
		 * List.of(flightExtensions.getValueBean()); if (null != valueList &&
		 * !valueList.isEmpty()) { Optional<ValueBean> obj = valueList.stream()
		 * .filter(value ->
		 * value.getPropertyName().equalsIgnoreCase("scheduledDate")).findFirst(); if
		 * (obj.isPresent()) { log.info("Printing the scheduled date" +
		 * obj.get().getTextContent()); return obj.get().getTextContent(); } else {
		 * log.info("No scheduled date value found in the given list."); } } else {
		 * log.info("No value list found"); } } else {
		 * log.info("No flight extensions found"); } } else {
		 * log.info("No TPA Extensions found"); } } else {
		 * log.error("No flight leg information found"); }
		 */
		return "";
	}

	@PostConstruct
	private void loadProperties() {
		Properties prop = getConnector().getAppProperties();
		String localTmpPath = prop.getProperty("sftp.local-temp-file-path");
		setLocalTmpPath(localTmpPath);
		String remoteFilePath = prop.getProperty("sftp.remote-file-path");
		setRemoteFilePath(remoteFilePath);		
		log.info("Printing the local temp path and the remote path:" + localTmpPath + "," + remoteFilePath);
	}

	public String getCurrentTimeStamp() {
		Date now = new Date();
		return getSdf().format(now);
	}
	
}