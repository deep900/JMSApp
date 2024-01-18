/**
 * 
 */
package com.oradnata.metadata.handle;

public interface MetadataExtractor <T>{
	//public Map<String,Object> extractMetadata(Object fileObj);
	
	public T extractMetadata(Object fileObj);
}
