package org.apromore.portal.config;

import org.apromore.security.impl.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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

}
