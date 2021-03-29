/**
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
package org.apromore.portal.config;

import org.apromore.security.impl.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.DelegatingFilterProxy;

@Configuration
@EnableWebSecurity
public class PortalSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		
		auth.authenticationProvider(usernamePasswordAuthenticationProvider);

	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable().authorizeRequests()
//        .antMatchers("/**").hasRole("USER")
				.antMatchers("*/css/**").permitAll()
				.antMatchers("*/font/**").permitAll()
				.antMatchers("*/img/**").permitAll()
				.antMatchers("*/themes/**").permitAll()
				.antMatchers("*/libs/**").permitAll()
				.antMatchers("*/js/**").permitAll()
				.antMatchers("/robots.txt").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/zkau/web/login.zul").permitAll()
				.antMatchers("/zkau/web/denied.zul").permitAll()
				.antMatchers("/zkau/web/index.zul*").authenticated()
				.antMatchers("/zkau/web/index.zul*").authenticated()
				.antMatchers("/zkau/**").permitAll()
				.antMatchers("/zkau").permitAll()
				.and()
				.headers().frameOptions().disable().and()
				.formLogin()
				.loginPage("/zkau/web/login.zul").loginProcessingUrl("/login").defaultSuccessUrl("/zkau/web/index.zul")
				.failureForwardUrl("/zkau/web/denied.zul")
				.and()
				.logout().deleteCookies("JSESSIONID","portalContext").deleteCookies("Apromore","pluginSessionId")
				.logoutSuccessUrl("/zkau/web/login.zul");

	}

	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
