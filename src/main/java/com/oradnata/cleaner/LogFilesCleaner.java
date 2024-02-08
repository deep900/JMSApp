/**
 * 
 */
package com.oradnata.cleaner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.oradnata.config.ApplicationConnector;

/**
 * This job is to clean the files in a folder for the given period of time.
 */
@Component
public class LogFilesCleaner implements Runnable {

	private static final Logger log = LogManager.getLogger(LogFilesCleaner.class);

	@Autowired
	private ApplicationConnector connector;

	private Properties prop;

	private int logRetentionDays = 30;

	private String folderPathToClean = "";

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@PostConstruct
	private void init() {
		log.info("Setup the log file cleaner");
		prop = connector.getAppProperties();
		String logFilesRetentionDays = prop.getProperty("application.logs.retention.in.days");
		folderPathToClean = prop.getProperty("application.logs.folder.path");
		logRetentionDays = Integer.parseInt(logFilesRetentionDays);
		log.info("Number of days to retain:" + logRetentionDays);
		log.info("Folder path to clean:" + folderPathToClean);
		taskScheduler.scheduleAtFixedRate(this, Duration.ofHours(24));
	}

	@Override
	public void run() {
		log.info("Cleaning the old files in the directory:" + folderPathToClean + ", days of retention:" + logRetentionDays);
		try {
			int cnt = deleteOldFiles(Path.of(folderPathToClean), logRetentionDays);
			log.info("Cleaned " + cnt + " log files");
		} catch (IOException err) {
			log.error("Error while cleaning the log files", err);
		}
	}

	private int deleteOldFiles(final Path destination, final Integer daysToKeep) throws IOException {
		final Instant retentionFilePeriod = ZonedDateTime.now().minusDays(daysToKeep).toInstant();
		final AtomicInteger countDeletedFiles = new AtomicInteger();
		Files.find(destination, 1,
				(path, basicFileAttrs) -> basicFileAttrs.lastModifiedTime().toInstant().isBefore(retentionFilePeriod))
				.forEach(fileToDelete -> {
					try {
						if (!Files.isDirectory(fileToDelete)) {
							Files.delete(fileToDelete);
							countDeletedFiles.incrementAndGet();
						}
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
		return countDeletedFiles.get();
	}
}
