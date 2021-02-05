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
package org.apromore.integration.config;

import java.util.HashSet;
import java.util.Set;

import org.apromore.apmlog.APMLogService;
import org.apromore.apmlog.impl.APMLogServiceImpl;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.provider.PluginProvider;
import org.apromore.plugin.provider.impl.PluginProviderImpl;
import org.apromore.stub.EventAdminStub;
import org.osgi.service.event.EventAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ImportResource(value = {
	"classpath:META-INF/spring/database-jpa.xml",
	"classpath:META-INF/spring/cache-config.xml", "classpath:META-INF/spring/storage-context.xml",
	"classpath:META-INF/spring/managerContext-services.xml" })
public class TestConfig {

    @Bean
    public static PropertyPlaceholderConfigurer properties() {
	PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	Resource[] resources = new ClassPathResource[] { new ClassPathResource("integration-test/db-config.properties"),
		new ClassPathResource("integration-test/cache-config.properties"),
		new ClassPathResource("integration-test/mail-config.properties") };
	ppc.setLocations(resources);
	ppc.setIgnoreUnresolvablePlaceholders(true);
	return ppc;
    }

    @Bean
    public APMLogService apmLogService() {
	return new APMLogServiceImpl();
    }
   
    @Bean
    public EventAdmin eventAdmin() {
	return new EventAdminStub();
    }
    
    
    @Bean
    @Qualifier("pluginProvider")
    public PluginProvider PluginProvider()
    {
	PluginProviderImpl pluginProvider=new PluginProviderImpl();
	Set<Plugin> pluginSet=new HashSet<Plugin>();
	pluginSet.add(testPlugin());
	pluginProvider.setPluginList(pluginSet);
	return pluginProvider;
	
    }
    @Bean
    public Plugin testPlugin() {
	return new DefaultPlugin() {
	    
	};
    }
    

  
   

}
