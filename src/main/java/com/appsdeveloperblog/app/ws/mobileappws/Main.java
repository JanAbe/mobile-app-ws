package com.appsdeveloperblog.app.ws.mobileappws;

import com.appsdeveloperblog.app.ws.mobileappws.security.AppProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean // need this method, so we can autowire it into the userService class
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}

	@Bean(name="AppProperties")
	public AppProperties getAppProperties() {
		return new AppProperties();
	}
}
