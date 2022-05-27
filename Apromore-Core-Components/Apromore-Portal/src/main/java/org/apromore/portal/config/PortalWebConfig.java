/*-
 * #%L This file is part of "Apromore Core". %% Copyright (C) 2018 - 2022 Apromore Pty Ltd. %% This
 * program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>. #L%
 */
package org.apromore.portal.config;

import java.util.ArrayList;
import java.util.List;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.PortalProcessAttributePlugin;
import org.apromore.portal.servlet.DataChannelServlet;
import org.apromore.portal.servlet.PortalPluginResourceServlet;
import org.apromore.portal.util.ExplicitComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource(value = {"classpath:git.properties"})
@PropertySource(value = "classpath:i18n.properties", encoding = "UTF-8")
@EnableWebMvc
@ComponentScan(basePackages = "org.apromore.portal")
public class PortalWebConfig {

  @Autowired
  List<PortalPlugin> portalPlugins;

  @Autowired(required = false)
  List<PortalProcessAttributePlugin> portalProcessAttributePlugin =
      new ArrayList<PortalProcessAttributePlugin>();

  @Autowired(required = false)
  List<FileImporterPlugin> fileImporterPlugins = new ArrayList<FileImporterPlugin>();

  @Autowired(required = false)
  List<EditorPlugin> editorPlugins = new ArrayList<EditorPlugin>();

  @Bean
  public List<EditorPlugin> editorPlugins() {
    return editorPlugins;
  }

  @Bean
  public List<EditorPlugin> bpmnEditorPlugins() {
    return editorPlugins;
  }

  @Bean
  public List<FileImporterPlugin> fileImporterPlugins() {
    return fileImporterPlugins;
  }

  @Bean
  public List<PortalProcessAttributePlugin> portalProcessAttributePlugins() {
    return portalProcessAttributePlugin;
  }

  @Bean
  public List<PortalPlugin> portalPlugins() {
    return portalPlugins;
  }

  @Bean
  public ExplicitComparator portalMenuOrder(@Value("${portal.menuorder}") String menuOrder) {
    return new ExplicitComparator(menuOrder);
  }

  @Bean
  public ExplicitComparator portalFileMenuitemOrder(
      @Value("${portal.menuitemorder.File}") String menuItemFile) {
    return new ExplicitComparator(menuItemFile);
  }

  @Bean
  public ServletRegistrationBean<PortalPluginResourceServlet> exampleServletBean() {
    ServletRegistrationBean<PortalPluginResourceServlet> bean =
        new ServletRegistrationBean<PortalPluginResourceServlet>(new PortalPluginResourceServlet(),
            "/portalPluginResource/*", "/favicon.ico");
    bean.setLoadOnStartup(1);
    return bean;
  }

  @Bean
  public ServletRegistrationBean<DataChannelServlet> dataChannel() {
    ServletRegistrationBean<DataChannelServlet> bean =
        new ServletRegistrationBean<DataChannelServlet>(new DataChannelServlet(), "/dataRequest/*");
    bean.setLoadOnStartup(1);
    return bean;
  }


  // This bean allows spaces which are url encoded
  @Bean
  public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowUrlEncodedPercent(true);
    return firewall;
  }

}
