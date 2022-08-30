/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.portal.config;

import java.util.ArrayList;
import java.util.List;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.PortalProcessAttributePlugin;
import org.apromore.portal.MDCHandlerInterceptor;
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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource(value = {"classpath:git.properties"})
@PropertySource(value = "classpath:i18n.properties", encoding = "UTF-8")
@ComponentScan(basePackages = "org.apromore.portal")
public class PortalWebConfig implements WebMvcConfigurer {

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

  // WebMvcConfigurer

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(new MDCHandlerInterceptor());
  }
}
