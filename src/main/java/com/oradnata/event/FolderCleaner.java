/**
 * 
 */
package com.oradnata.event;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * This thread is used to clean the files in the temp directory.
 */
@Component
public class FolderCleaner implements Runnable ,InitializingBean{

	private static final Logger log = LogManager.getLogger(FolderCleaner.class);

	private List<String> filesToClean = new ArrayList<String>();
	
	@Autowired
	private ThreadPoolTaskScheduler scheduler;	

	@Override
	public void run() {
		try {
			List<String> cleanFilesList = new ArrayList();
			cleanFilesList.addAll(filesToClean);
			filesToClean.clear();
			if (!cleanFilesList.isEmpty()) {
				Thread.currentThread().sleep(6000);
				cleanFilesList.forEach(file -> {
					File obj = new File(file);
					if (obj.exists()) {
						obj.delete();
					}
				});
				log.info(cleanFilesList.size() + " files cleaned successfully");
			} else {
				log.info("No files to clean");
			}
		} catch (Exception err) {
			log.error("Error while cleaning up the folder", err);
		}
	}
	
	public void  addFileForCleanup(String filePath) {
		filesToClean.add(filePath);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduler.scheduleAtFixedRate(this, Duration.ofHours(12));		
	}	
}
