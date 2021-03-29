package org.apromore.apmlog.config;

import org.apromore.apmlog.APMLogService;
import org.apromore.apmlog.impl.APMLogServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class APMLogConfig {

	@Bean
	public APMLogService apmLogService() {
		return new APMLogServiceImpl();
	}

}
