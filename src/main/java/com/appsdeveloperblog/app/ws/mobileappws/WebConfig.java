package com.appsdeveloperblog.app.ws.mobileappws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// class used to configure CORS on a (more) global level
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
		.addMapping("/**")
		.allowedMethods("*")
		.allowedOrigins("*");
	}
}


/*
(can use one of the following ways:)
Different ways to set up cors configuration:

can use cors annotation on a specific method in a controller to allow requests to be made from certain origins
can use cors annotation on a controller class to allow requests to be made from  certain origins
can use WebConfig class to set up the cors configuration on a (more) global level
if spring security is used ->
	can use WebSecurity class to set up cors configuration. Then you have to create a corsConfigurationSource() method,
	which will be called by the springframework. And add .cors() to the configure() method
*/