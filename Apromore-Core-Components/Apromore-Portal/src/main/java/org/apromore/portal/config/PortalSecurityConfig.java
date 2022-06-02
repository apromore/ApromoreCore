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

import org.apromore.portal.AuthenticationHandler;
import org.apromore.portal.common.Constants;
import org.apromore.portal.servlet.filter.SameSiteFilter;
import org.apromore.security.impl.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "false")
public class PortalSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  AuthenticationHandler authenticationHandler;

  @Autowired
  UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

  @Value("${contentSecurityPolicy}")
  String contentSecurityPolicy;

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

    auth.authenticationProvider(usernamePasswordAuthenticationProvider);

  }


  @Override
  public void configure(WebSecurity web) throws Exception {

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
  protected void configure(final HttpSecurity http) throws Exception {


    http.addFilterAfter(new SameSiteFilter(), BasicAuthenticationFilter.class);
    http.headers()
        .contentSecurityPolicy(contentSecurityPolicy).and()
        .frameOptions().sameOrigin()
        .referrerPolicy(ReferrerPolicy.NO_REFERRER);

    http.csrf()
            .ignoringAntMatchers("/zkau", "/rest", "/rest/*", "/rest/**/*", "/zkau/*", "/login", "/bpmneditor/editor/*")
            .ignoringAntMatchers(Constants.API_WHITELIST)
        .and()
        .authorizeRequests()
            .antMatchers("/zkau/web/login.zul").permitAll()
            .antMatchers("/zkau/web/denied.zul").permitAll()
            .antMatchers("/zkau").permitAll()
            .antMatchers("/rest").permitAll()
            .antMatchers("/rest/**/*").permitAll()
            .antMatchers("/rest/*").permitAll()
            .antMatchers(Constants.API_WHITELIST).permitAll()
            .antMatchers("/zkau/upload").permitAll()
            .antMatchers("/zkau/*").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/logout").permitAll()
            .anyRequest().authenticated()
        .and()
        .formLogin()
            .loginPage("/zkau/web/login.zul")
            .loginProcessingUrl("/login")
            .failureForwardUrl("/zkau/web/denied.zul")
            .successHandler(authenticationHandler)
            .failureHandler(authenticationHandler)
        .and()
        .logout()
            .logoutUrl("/sso/logout")
            .deleteCookies("JSESSIONID", "portalContext")
            .deleteCookies("Apromore", "pluginSessionId")
            .logoutSuccessUrl("/zkau/web/login.zul");

    http.exceptionHandling()
            .accessDeniedPage("/zkau/web/pages/401.zul");
  }

  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

}
