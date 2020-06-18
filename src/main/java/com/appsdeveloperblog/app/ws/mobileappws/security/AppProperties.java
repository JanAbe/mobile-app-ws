package com.appsdeveloperblog.app.ws.mobileappws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component // necessary so we can autowire certain classes
public class AppProperties {
	@Autowired
	private Environment env;

	public String getTokenSecret() {
		return env.getProperty("tokenSecret");
	}

	public String getEmail() {
		return env.getProperty("email");
	}
}