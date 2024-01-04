/**
 * 
 */
package com.oradnata.metadata.handle;

import java.util.Map;

public interface MetadataExtractor {
	public Map<String,Object> extractMetadata(Object fileObj);
}
