/**
 * 
 */
package com.oradnata.jms;

import org.springframework.context.ApplicationEvent;

/**
 * Flight information event for handling the JMS messages.
 */
public class FlightInformationEvent extends ApplicationEvent {

	public FlightInformationEvent(Object source) {
		super(source);		
	}

}
