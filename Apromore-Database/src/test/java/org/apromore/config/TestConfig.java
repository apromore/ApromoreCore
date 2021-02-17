/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.sql.Driver;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@ImportResource({ "classpath:META-INF/spring/database-jpa.xml" })
public class TestConfig {

    @Value("${jdbc.username}")
    private String dbUser;

    @Value("${jdbc.password}")
    private String password;

    @Value("${liquibase.jdbc.username}")
    private String liquibaseDbUser;

    @Value("${liquibase.jdbc.password}")
    private String liquibasePassword;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.context}")
    private String context;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Bean
    public DataSource dataSource() {
	HikariDataSource ds = new HikariDataSource();
	ds.setJdbcUrl(url);
	ds.setDriverClassName(driver);
	ds.setUsername(dbUser);
	ds.setPassword(password);
	return ds;
    }

    public DataSource unPooledDataSource() throws ClassNotFoundException {
	SimpleDriverDataSource ds = new SimpleDriverDataSource();
	ds.setUrl(url);
	ds.setDriverClass((Class<? extends Driver>) Class.forName(driver));
	ds.setUsername(liquibaseDbUser);
	ds.setPassword(liquibasePassword);
	return ds;
    }

    @Bean
    public SpringLiquibase liquibase() throws ClassNotFoundException {
	SpringLiquibase liquibase = new SpringLiquibase();
	liquibase.setDataSource(unPooledDataSource());
	liquibase.setChangeLog("classpath:db/migration/changeLog.yaml");
	liquibase.setContexts(context);
	liquibase.setDropFirst(true);
	return liquibase;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
	return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public static PropertyPlaceholderConfigurer properties() {
	PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	Resource[] resources = new ClassPathResource[] { new ClassPathResource("database/test-config.properties") };
	ppc.setLocations(resources);
	ppc.setIgnoreUnresolvablePlaceholders(true);
	return ppc;
    }
}
