package com.oradnata.metadata.handle;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement
public class FlightExtensions {
	
	@JacksonXmlElementWrapper(localName="Value" ,useWrapping = false)
	public PropertyValue[] values;

	public PropertyValue[] getValues() {
		return values;
	}

	public void setValues(PropertyValue[] values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "FlightExtensions [values=" + Arrays.toString(values) + "]";
	}
}
