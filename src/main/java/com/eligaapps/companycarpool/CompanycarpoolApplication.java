package com.eligaapps.companycarpool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackages = {"com"})
@ComponentScan(basePackages = { "com"} )
public class CompanycarpoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanycarpoolApplication.class, args);
	}
}
