/**
 * 
 */
package com.oradnata.event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class FileContentCreator {
	
	public final static String prefix = "FNR";
	
	public final static String duplicatePrefix = "DFNR";
	
	private static final Logger log = LogManager.getLogger(FileContentCreator.class);

	public File createFileContent(String fileName, String content) {
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
}
