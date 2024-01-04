package com.oradnata.metadata.handle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

@Component(value = "dnataMetaDataExtractor")
@Slf4j
public class DnataMetadataExtractor implements MetadataExtractor {

	private XmlMapper xmlMapper = new XmlMapper();

	public static final String SEQ_NUM = "SequenceNmbr";

	public static final String TIMESTAMP = "TimeStamp";

	@Override
	public Map<String, Object> extractMetadata(Object fileObj) {
		try {
			IATA_AIDX_FlightLegNotifRQ flightInfo = xmlMapper.readValue(fileObj.toString(),
					IATA_AIDX_FlightLegNotifRQ.class);
			System.out.println(flightInfo.toString());
			return getMetadataInMap(flightInfo);
		} catch (IOException err) {
			log.error("Error while deserializing the XML file; " + fileObj.toString(), err);
			return null;
		}
	}

	private Map<String, Object> getMetadataInMap(IATA_AIDX_FlightLegNotifRQ flightInfo) {
		log.info("Reading the metadata from flight info:" + flightInfo.toString());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(SEQ_NUM, flightInfo.getSequenceNmbr());
		map.put(TIMESTAMP, flightInfo.getTimeStamp());
		return map;
	}
}
