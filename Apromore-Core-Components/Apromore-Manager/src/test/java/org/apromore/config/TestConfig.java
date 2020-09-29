/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.config;

import javax.sql.DataSource;

import org.apromore.apmlog.impl.APMLogServiceImpl;
import org.apromore.common.ConfigBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.jolbox.bonecp.BoneCPDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@ImportResource({ "classpath:META-INF/spring/managerContext-jpa.xml","classpath:database/test-jpa.xml" })
//@EnableJpaRepositories(repositoryImplementationPostfix = "CustomImpl", basePackages = "org.apromore.dao", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
public class TestConfig {

	@Value("${jdbc.username}")
	private String dbUser;

	@Value("${jdbc.password}")
	private String password;

	@Value("${jdbc.url}")
	private String url;

	@Value("${jdbc.driver}")
	private String driver;

	@Value("${jdbc.context}")
	private String context;

	@Bean
	public DataSource dataSource() {
		BoneCPDataSource ds = new BoneCPDataSource();
		ds.setJdbcUrl(url);
		ds.setDriverClass(driver);
		ds.setUsername(dbUser);
		ds.setPassword(password);
		return ds;
	}

	@Bean
	public SpringLiquibase liquibase() {

		SpringLiquibase liquibase = new SpringLiquibase();

		liquibase.setDataSource(dataSource());
		liquibase.setChangeLog("classpath:db/migration/changeLog.yaml");
		liquibase.setContexts(context);
		liquibase.setDropFirst(true);
		return liquibase;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public static PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] { new ClassPathResource("database/test-config.properties") };
		ppc.setLocations(resources);
		ppc.setIgnoreUnresolvablePlaceholders(true);
		return ppc;
	}

	@Bean
	public APMLogServiceImpl apmLogService() {
		return new APMLogServiceImpl();
	}

	@Bean
	public ConfigBean config() {
		return new ConfigBean();
	}

}
