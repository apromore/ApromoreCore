/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.integration.config;

import javax.sql.DataSource;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import liquibase.integration.spring.SpringLiquibase;


@TestPropertySource(value = {"classpath:application.properties",
    "classpath:integration-test/cache-config.properties",
    "classpath:integration-test/mail-config.properties"})
@ComponentScan(basePackages = "org.apromore")
public class TestConfig {

  @Bean
  public static PropertyPlaceholderConfigurer properties() {
    PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
    Resource[] resources = new ClassPathResource[] {new ClassPathResource("application.properties"),
        new ClassPathResource("integration-test/cache-config.properties"),
        new ClassPathResource("integration-test/mail-config.properties")};
    ppc.setLocations(resources);
    ppc.setIgnoreUnresolvablePlaceholders(true);
    return ppc;
  }

  // @Bean
  // @Qualifier("pluginProvider")
  // public PluginProvider PluginProvider() {
  // PluginProvider pluginProvider = new PluginProvider() {
  //
  // @Override
  // public Set<Plugin> listAll() {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // public Set<Plugin> listByType(String type) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // public Set<Plugin> listByName(String name) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // public Plugin findByName(String name) throws PluginNotFoundException {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // public Plugin findByNameAndVersion(String name, String version) throws PluginNotFoundException
  // {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // };
  // Set<Plugin> pluginSet = new HashSet<Plugin>();
  // pluginSet.add(testPlugin());
  // return pluginProvider;
  //
  // }

  @Bean
  public Plugin testPlugin() {
    return new DefaultPlugin() {

    };
  }

  @Autowired
  DataSource dataSource;

  @Bean
  public SpringLiquibase liquibase() throws ClassNotFoundException {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog("classpath:db/migration/changeLog.yaml");
    liquibase.setContexts("H2");
    liquibase.setDropFirst(true);
    return liquibase;
  }

}
