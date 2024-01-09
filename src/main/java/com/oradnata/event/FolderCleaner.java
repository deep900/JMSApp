/**
 * 
 */
package com.oradnata.event;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This thread is used to clean the files in the temp directory.
 */
public class FolderCleaner implements Runnable {

	private static final Logger log = LogManager.getLogger(FolderCleaner.class);

	private List<String> filesToClean;

	public FolderCleaner(List<String> filesToClean) {
		this.filesToClean = filesToClean;
	}

	@Override
	public void run() {
		try {
			if (null != filesToClean && !filesToClean.isEmpty()) {
				Thread.currentThread().sleep(3000);
				filesToClean.forEach(file -> {
					File obj = new File(file);
					if (obj.exists()) {
						obj.delete();
					}
				});
				log.info(filesToClean.size() + " files cleaned successfully");
			}
		} catch (Exception err) {
			log.error("Error while cleaning up the folder", err);
		}
	}

}
