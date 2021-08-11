/**
 * #%L This file is part of "Apromore Core". %% Copyright (C) 2018 - 2021 Apromore Pty Ltd. %% This
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

import org.apromore.portal.servlet.filter.SameSiteFilter;
import org.apromore.security.impl.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "false")
public class PortalSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

    auth.authenticationProvider(usernamePasswordAuthenticationProvider);

  }


  @Override
  public void configure(WebSecurity web) throws Exception {

    web.ignoring().antMatchers("/**/css/*").antMatchers("/**/font/**").antMatchers("/**/img/**")
        .antMatchers("/**/images/**").antMatchers("/**/themes/**").antMatchers("/**/libs/**")
        .antMatchers("/**/js/*").antMatchers("/robots.txt");
  }


  @Override
  protected void configure(final HttpSecurity http) throws Exception {


    http.addFilterAfter(new SameSiteFilter(), BasicAuthenticationFilter.class);
    http.headers().frameOptions().sameOrigin().addHeaderWriter(new StaticHeadersWriter(
        "X-Content-Security-Policy",
        "default-src 'self'; font-src 'self' data: fonts.googleapis.com fonts.gstatic.com; form-action 'self';"
            + " frame-ancestors 'self'; img-src 'self' data:; script-src 'self' 'unsafe-eval' 'unsafe-inline';"
            + " style-src 'self' 'unsafe-inline' fonts.googleapis.com;"))
        .httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(63072000);

    http.csrf().ignoringAntMatchers("/zkau", "/rest/*", "/rest/**/*", "/zkau/*", "/login", "/bpmneditor/editor/*").and()
        .authorizeRequests().antMatchers("/zkau/web/login.zul").permitAll()
        .antMatchers("/zkau/web/denied.zul").permitAll().antMatchers("/zkau").permitAll()
        .antMatchers("/rest/**/*").permitAll().antMatchers("/rest/*").permitAll()
        .antMatchers("/zkau/*").permitAll().antMatchers("/login").permitAll().antMatchers("/logout")
        .permitAll().antMatchers("/zkau/upload").permitAll().anyRequest().authenticated().and()
        .formLogin().loginPage("/zkau/web/login.zul").loginProcessingUrl("/login")
        .defaultSuccessUrl("/zkau/web/index.zul").failureForwardUrl("/zkau/web/denied.zul").and()
        .logout().logoutUrl("/sso/logout").deleteCookies("JSESSIONID", "portalContext")
        .deleteCookies("Apromore", "pluginSessionId").logoutSuccessUrl("/zkau/web/login.zul");

    http.exceptionHandling().accessDeniedPage("/zkau/web/401.zul");

  }

  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

}
