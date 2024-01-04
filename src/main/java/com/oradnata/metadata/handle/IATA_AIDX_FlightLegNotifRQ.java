/**
 * 
 */
package com.oradnata.metadata.handle;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IATA_AIDX_FlightLegNotifRQ { 
	public String Target;
	public double Version;
	public int SequenceNmbr;
	public Date TimeStamp;
	public String text;
}
