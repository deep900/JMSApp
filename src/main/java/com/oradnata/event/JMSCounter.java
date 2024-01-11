/**
 * 
 */
package com.oradnata.event;

import java.util.HashMap;

import org.springframework.stereotype.Component;

/**
 * 
 */
@Component
public class JMSCounter {

	private static HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

	public static final String MESSAGE_CNT = "MESSAGES_COUNT";

	public static final String UNABLE_TO_CREATE_FILE = "UNABLE_TO_CREATE_FILE";

	public static final String UNABLE_TO_UPLOAD_FILE = "UNABLE_TO_UPLOAD_FILE";

	public static final String UNABLE_TO_UPDATE_IN_DB = "UNABLE_TO_UPDATE_IN_DB";
	
	public static final String UNABLE_TO_PARSE_FILE = "UNABLE_TO_PARSE_FILE";

	public static final String PROCESSED_SUCCESSFULLY = "PROCESSED_SUCCESSFULLY";

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
	}
	
	public void incrementParam(String param) {
		if(!counterMap.containsKey(param)) {
			return;
		}
		int val = counterMap.get(param);
		counterMap.put(param, val + 1);
	}

}
