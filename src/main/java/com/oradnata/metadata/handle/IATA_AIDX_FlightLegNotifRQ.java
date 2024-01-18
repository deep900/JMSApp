/**
 * 
 */
package com.oradnata.metadata.handle;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement
public class IATA_AIDX_FlightLegNotifRQ { 
	
	public String Target;
	public double Version;
	public int SequenceNmbr;
	public Date TimeStamp;
	public String text;
	public FlightLeg FlightLeg;
	public String getTarget() {
		return Target;
	}
	public void setTarget(String target) {
		Target = target;
	}
	public double getVersion() {
		return Version;
	}
	public void setVersion(double version) {
		Version = version;
	}
	public int getSequenceNmbr() {
		return SequenceNmbr;
	}
	public void setSequenceNmbr(int sequenceNmbr) {
		SequenceNmbr = sequenceNmbr;
	}
	public Date getTimeStamp() {
		return TimeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		TimeStamp = timeStamp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public FlightLeg getFlightLeg() {
		return FlightLeg;
	}
	public void setFlightLeg(FlightLeg flightLeg) {
		FlightLeg = flightLeg;
	}
	@Override
	public String toString() {
		return "IATA_AIDX_FlightLegNotifRQ [Target=" + Target + ", Version=" + Version + ", SequenceNmbr="
				+ SequenceNmbr + ", TimeStamp=" + TimeStamp + ", text=" + text + ", FlightLeg=" + FlightLeg + "]";
	}	
}
