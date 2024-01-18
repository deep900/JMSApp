/**
 * 
 */
package com.oradnata.metadata.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement
public class FlightLeg {

	public LegIdentifier LegIdentifier;
	
	public String scheduledDate;
	
	/* public TPA_Extension TPA_Extension; */

	public String getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public LegIdentifier getLegIdentifier() {
		return LegIdentifier;
	}

	public void setLegIdentifier(LegIdentifier legIdentifier) {
		LegIdentifier = legIdentifier;
	}

	@Override
	public String toString() {
		return "FlightLeg [LegIdentifier=" + LegIdentifier + ", scheduledDate=" + scheduledDate + "]";
	}

	/*
	 * public TPA_Extension getTPA_Extension() { return TPA_Extension; }
	 * 
	 * public void setTPA_Extension(TPA_Extension tPA_Extension) { TPA_Extension =
	 * tPA_Extension; }
	 */	
}
