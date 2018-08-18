package com.eligaapps.companycarpool.config;

import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import com.eligaapps.companycarpool.types.ROLE;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	private static Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
	
	@Autowired
    private AuthenticationProvider authenticationProvider;

	@Autowired
	private AuthenticationSuccessHandler successHandler;
	

	@Autowired
	private AuthenticationFailureHandler failureHandler;


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().
		antMatchers("/admin/**").hasRole(ROLE.admin.toString()).
		antMatchers("/register","/activate*","/forgotPassword*").permitAll().anyRequest().authenticated().
		and().formLogin().defaultSuccessUrl("/companycarpool.html").successHandler(successHandler).
		failureHandler(failureHandler).and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");
	}
	
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		LOG.info("configuring authentication manager {}",authenticationProvider);
        auth.authenticationProvider(authenticationProvider);
    }
}
