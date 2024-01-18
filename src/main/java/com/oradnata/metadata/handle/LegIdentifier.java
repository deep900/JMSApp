/**
 * 
 */
package com.oradnata.metadata.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement
public class LegIdentifier {
	public String Airline = "";
	public String FlightNumber = "";
	public String DepartureAirport = "";
	public String ArrivalAirport = "";
	public String OriginDate = "";
	public String OperationalSuffix = "";

	public String getAirline() {
		return Airline;
	}

	public void setAirline(String airline) {
		Airline = airline;
	}

	public String getFlightNumber() {
		return FlightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		FlightNumber = flightNumber;
	}

	public String getDepartureAirport() {
		return DepartureAirport;
	}

	public void setDepartureAirport(String departureAirport) {
		DepartureAirport = departureAirport;
	}

	public String getArrivalAirport() {
		return ArrivalAirport;
	}

	public void setArrivalAirport(String arrivalAirport) {
		ArrivalAirport = arrivalAirport;
	}

	public String getOriginDate() {
		return OriginDate;
	}

	public void setOriginDate(String originDate) {
		OriginDate = originDate;
	}

	public String getOperationalSuffix() {
		return OperationalSuffix;
	}

	public void setOperationalSuffix(String operationalSuffix) {
		OperationalSuffix = operationalSuffix;
	}

	@Override
	public String toString() {
		return "LegIdentifier [Airline=" + Airline + ", FlightNumber=" + FlightNumber + ", DepartureAirport="
				+ DepartureAirport + ", ArrivalAirport=" + ArrivalAirport + ", OriginDate=" + OriginDate
				+ ", OperationalSuffix=" + OperationalSuffix + "]";
	}
}
