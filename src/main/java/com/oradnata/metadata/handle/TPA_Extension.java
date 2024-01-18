/**
 * 
 */
package com.oradnata.metadata.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement
public class TPA_Extension {
	
	@JacksonXmlElementWrapper(localName="FlightExtensions" ,useWrapping = false)
	public FlightExtensions FlightExtensions;

	public FlightExtensions getFlightExtensions() {
		return FlightExtensions;
	}

	public void setFlightExtensions(FlightExtensions flightExtensions) {
		FlightExtensions = flightExtensions;
	}

	@Override
	public String toString() {
		return "TPA_Extension [FlightExtensions=" + FlightExtensions + "]";
	}
}
