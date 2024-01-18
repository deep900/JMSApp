/**
 * 
 */
package com.oradnata.event;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.oradnata.config.SpringBootWeblogicApplication;
import com.oradnata.jms.FlightInformationEvent;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {SpringBootWeblogicApplication.class, TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class DnataEventHandlerTest {

	@MockBean
	private ApplicationEventPublisher publisher;	

	@MockBean
	private DnataEventHandler eventHandler;

	@Test
	public void testEventHandler() {
		eventHandler.onApplicationEvent(new FlightInformationEvent("Hello World"));
	}
}
