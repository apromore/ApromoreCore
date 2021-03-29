package org.apromore.calendar.integration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableTransactionManagement
public class TestConfig {
	
	 @Autowired
	  DataSource dataSource;

	  @Bean
	  public SpringLiquibase liquibase() throws ClassNotFoundException {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog("classpath:db/migration/changeLog.yaml");
		liquibase.setContexts("H2");
//		liquibase.setDropFirst(true);
		return liquibase;
	  }

}
