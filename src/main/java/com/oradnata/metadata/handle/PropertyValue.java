package com.oradnata.metadata.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyValue {

	@JacksonXmlProperty(isAttribute = true)
	public String propertyName;

	@JacksonXmlText(value = true)
	public String value;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "PropertyValue [propertyName=" + propertyName + ", value=" + value + "]";
	}
	
	

}
