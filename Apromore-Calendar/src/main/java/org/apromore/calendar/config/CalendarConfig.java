package org.apromore.calendar.config;

import java.util.List;

import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.mapper.CustomMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.apromore.calendar")
public class CalendarConfig {
	
	@Bean(initMethod = "init")
	public CustomMapper customMapper()
	{
		List<String> mapperList=List.of("mappers/calendar.xml");
		return new CustomMapper(mapperList);
		
	}

}
