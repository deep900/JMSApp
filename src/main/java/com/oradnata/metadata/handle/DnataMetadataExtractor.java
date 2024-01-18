package com.oradnata.metadata.handle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Component(value = "dnataMetaDataExtractor")
public class DnataMetadataExtractor implements MetadataExtractor<IATA_AIDX_FlightLegNotifRQ> {

	private static final Logger log = LogManager.getLogger(DnataMetadataExtractor.class);

	private XmlMapper xmlMapper = new XmlMapper();

	private final String SRC_DATA = "<ns1:Value propertyName=\"scheduledDate\">";

	private final String flight_extensions = "<ns1:FlightExtensions>";

	@Override
	public IATA_AIDX_FlightLegNotifRQ extractMetadata(Object fileObj) {
		try {
			log.info("-- About to parse input file -- ");
			IATA_AIDX_FlightLegNotifRQ flightInfo = xmlMapper.readValue(fileObj.toString(),
					IATA_AIDX_FlightLegNotifRQ.class);
			loadScheduledDate(flightInfo, fileObj.toString());
			log.info("Parsed metadata info :" + flightInfo.toString());
			return flightInfo;
		} catch (IOException err) {
			log.error("Error while deserializing the XML file; " + fileObj.toString(), err);
			return null;
		}
	}

	private void loadScheduledDate(IATA_AIDX_FlightLegNotifRQ flightInfo, String source) {
		String scheduledDate = "";
		if (null != flightInfo) {
			FlightLeg flightLeg = flightInfo.getFlightLeg();
			if (null != flightLeg) {
				if (source.contains(SRC_DATA) && source.contains(flight_extensions)) {
					int startIndex = source.indexOf(SRC_DATA, source.indexOf(flight_extensions));
					startIndex = startIndex + SRC_DATA.length();
					scheduledDate = source.substring(startIndex, startIndex + 10);
					if (scheduledDate.contains("ns:Value")) {
						scheduledDate = "";
					}
					log.info("Printing the scheduled Date:" + scheduledDate);
				}
				flightLeg.setScheduledDate(scheduledDate);
			}
		}
	}

	@PostConstruct
	public void init() {
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/*
	 * @Override public Map<String, Object> extractMetadata(Object fileObj) { try {
	 * IATA_AIDX_FlightLegNotifRQ flightInfo =
	 * xmlMapper.readValue(fileObj.toString(), IATA_AIDX_FlightLegNotifRQ.class);
	 * log.info(flightInfo.toString()); return getMetadataInMap(flightInfo); } catch
	 * (IOException err) { log.error("Error while deserializing the XML file; " +
	 * fileObj.toString(), err); return null; } }
	 * 
	 * private Map<String, Object> getMetadataInMap(IATA_AIDX_FlightLegNotifRQ
	 * flightInfo) { log.info("Reading the metadata from flight info:" +
	 * flightInfo.toString()); HashMap<String, Object> map = new HashMap<String,
	 * Object>(); map.put(SEQ_NUM, flightInfo.getSequenceNmbr()); map.put(TIMESTAMP,
	 * flightInfo.getTimeStamp()); return map; }
	 */

	public static void main(String args[]) {

		/*
		 * String fileName = "C:\\Workspace\\SampleData\\Duplicate-208187-485-156.xml";
		 * try { FileReader reader = new FileReader(new File(fileName)); StringBuilder
		 * resultStringBuilder = new StringBuilder(); try (BufferedReader br = new
		 * BufferedReader(reader)) { String line; while ((line = br.readLine()) != null)
		 * { resultStringBuilder.append(line).append("\n"); } } catch (Exception err) {
		 * err.printStackTrace(); } System.out.println(resultStringBuilder.toString());
		 * String source = resultStringBuilder.toString();
		 * 
		 * 
		 * DnataMetadataExtractor extractor = new DnataMetadataExtractor();
		 * IATA_AIDX_FlightLegNotifRQ extractMetadata =
		 * extractor.extractMetadata(resultStringBuilder.toString());
		 * System.out.println(extractMetadata.toString());
		 * 
		 * 
		 * String SRC_DATA = "<ns1:Value propertyName=\"scheduledDate\">"; String
		 * flight_extensions = "<ns1:FlightExtensions>"; if (source.contains(SRC_DATA)
		 * && source.contains(flight_extensions)) { int startIndex =
		 * source.indexOf(SRC_DATA, source.indexOf(flight_extensions));
		 * System.out.println(startIndex); startIndex = startIndex + SRC_DATA.length();
		 * String scheduledDate = source.substring(startIndex, startIndex + 10);
		 * if(scheduledDate.contains("ns:Value")) { scheduledDate = ""; }
		 * log.info("Printing the scheduled Date:" + scheduledDate); } } catch
		 * (FileNotFoundException e) { e.printStackTrace(); }
		 */
	}

}
