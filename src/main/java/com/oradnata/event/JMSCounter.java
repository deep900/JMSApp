/**
 * 
 */
package com.oradnata.event;

import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Component;

/**
 * 
 */
@Component
public class JMSCounter {

	private static HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

	public static final String MESSAGE_CNT = "Messages Count";

	public static final String UNABLE_TO_CREATE_FILE = "Unable to create file";

	public static final String UNABLE_TO_UPLOAD_FILE = "Unable to upload file";

	public static final String UNABLE_TO_UPDATE_IN_DB = "Unable to update in DB";
	
	public static final String UNABLE_TO_PARSE_FILE = "Unable to parse file";

	public static final String PROCESSED_SUCCESSFULLY = "Processed successfully";
	
	public static final String RETRY_PROCESSED_SUCCESSFULLY = "Processed successfully with retry";
	
	public static final String NO_OF_JOBS_FOR_RETRY = "Number of items in retry queue";
	
	public static final String RETRY_PROCESSED_FAILED = "Number of items failed in retry";	

	public JMSCounter() {
		resetMap();
	}

	public HashMap getJMSData() {
		return counterMap;
	}

	public void resetMap() {
		counterMap.put(MESSAGE_CNT, 0);
		counterMap.put(UNABLE_TO_CREATE_FILE, 0);
		counterMap.put(UNABLE_TO_UPLOAD_FILE, 0);
		counterMap.put(UNABLE_TO_UPDATE_IN_DB, 0);
		counterMap.put(UNABLE_TO_PARSE_FILE, 0);
		counterMap.put(PROCESSED_SUCCESSFULLY, 0);
		counterMap.put(RETRY_PROCESSED_SUCCESSFULLY, 0);
		counterMap.put(NO_OF_JOBS_FOR_RETRY, 0);
		counterMap.put(RETRY_PROCESSED_FAILED, 0);		
	}
	
	public void incrementParam(String param) {
		if(!counterMap.containsKey(param)) {
			return;
		}
		int val = counterMap.get(param);
		counterMap.put(param, val + 1);
	}
	
	public void decrementParam(String param) {
		if(!counterMap.containsKey(param)) {
			return;
		}
		int val = counterMap.get(param);
		if(val > 0) {
		counterMap.put(param, val - 1);
		}
	}

}
