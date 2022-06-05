/*-
 * #%L This file is part of "Apromore Enterprise Edition". %% Copyright (C) 2019 - 2022 Apromore Pty
 * Ltd. All Rights Reserved. %% NOTICE: All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any. The intellectual and technical concepts
 * contained herein are proprietary to Apromore Pty Ltd and its suppliers and may be covered by U.S.
 * and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Apromore Pty Ltd. #L%
 */
package org.apromore.portal.config;

import org.apromore.manager.client.ManagerService;
import org.apromore.portal.ApromoreKeycloakAuthenticationProvider;
import org.apromore.portal.ApromoreKeycloakAuthenticationSuccessHandler;
import org.apromore.portal.common.Constants;
import org.apromore.portal.servlet.filter.SameSiteFilter;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@KeycloakConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "true")
public class PortalKeyCloakSecurity extends KeycloakWebSecurityConfigurerAdapter {

  @Autowired
  private ManagerService manager;

  @Value("${contentSecurityPolicy}")
  String contentSecurityPolicy;

  /**
   * Registers the KeycloakAuthenticationProvider with the authentication manager.
   */
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(new ApromoreKeycloakAuthenticationProvider(manager));
  }

  /**
   * Defines the session authentication strategy.
   */
  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
  }

  @Bean
  @Override
  protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
    KeycloakAuthenticationProcessingFilter filter = super.keycloakAuthenticationProcessingFilter();
    filter.setAuthenticationSuccessHandler(new ApromoreKeycloakAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler()));
    return filter;
  }

  @Bean
  public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
    return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
  }

  @Bean
  public KeycloakConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    super.configure(web);
    web.ignoring()
        .antMatchers("/**/css/*")
        .antMatchers("/**/font/**")
        .antMatchers("/**/img/**")
        .antMatchers("/**/images/**")
        .antMatchers("/**/themes/**")
        .antMatchers("/**/libs/**")
        .antMatchers("/**/js/*")
        .antMatchers("/favicon.ico")
        .antMatchers("/portalPluginResource/**")
        .antMatchers("/robots.txt");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);

    http.headers()
        .contentSecurityPolicy(contentSecurityPolicy).and()
        .frameOptions().sameOrigin()
        .referrerPolicy(ReferrerPolicy.NO_REFERRER);
    http.addFilterAfter(new SameSiteFilter(), BasicAuthenticationFilter.class);
    http.csrf().ignoringAntMatchers("/zkau", "/rest", "/rest/*", "/rest/**/*", "/zkau/*", "/bpmneditor/editor/*")
            .ignoringAntMatchers(Constants.API_WHITELIST)
            .and()
        .authorizeRequests()
        // .antMatchers("/**").hasRole("USER")
        .antMatchers("/sso/login").permitAll()
        .antMatchers("/zkau").permitAll()
        .antMatchers("/logout").permitAll()
        .antMatchers("/zkau/*").permitAll()
        .antMatchers("/rest").permitAll()
        .antMatchers("/rest/**/*").permitAll()
        .antMatchers("/rest/*").permitAll()
        .antMatchers("/zkau/web/bpmneditor/*").permitAll()
        .anyRequest().authenticated();
  }
}
