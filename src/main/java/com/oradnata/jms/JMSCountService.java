package com.oradnata.jms;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class JMSCountService {

	private HashMap<String,Object> jmsInformation = new HashMap<String,Object>();
}
