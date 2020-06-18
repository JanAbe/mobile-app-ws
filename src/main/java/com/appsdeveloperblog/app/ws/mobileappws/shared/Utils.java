package com.appsdeveloperblog.app.ws.mobileappws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import com.appsdeveloperblog.app.ws.mobileappws.security.SecurityConstants;
import com.appsdeveloperblog.app.ws.mobileappws.security.AppProperties;
import com.appsdeveloperblog.app.ws.mobileappws.SpringApplicationContext;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class Utils {
	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String generateUserId(int length) {
		return generateRandomString(length);
	}
	
	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);

		for (int i=0; i<length; i++) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return sb.toString();
	}

	public static boolean hasTokenExpired(String token) {
		Claims claims = Jwts.parser()
							.setSigningKey(SecurityConstants.getTokenSecret())
							.parseClaimsJws(token)
							.getBody();

		Date tokenExpirationDate = claims.getExpiration();
		Date today = new Date();

		return tokenExpirationDate.before(today);
	}

	public static String generateEmailVerificationToken(String userId) {
		String token = Jwts.builder()
						.setSubject(userId)
						.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
						.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
						.compact();
		return token;
	}
	
	public static String getEmail() {
		AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
		return appProperties.getEmail();
	}
}