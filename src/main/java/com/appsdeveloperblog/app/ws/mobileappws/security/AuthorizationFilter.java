package com.appsdeveloperblog.app.ws.mobileappws.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

// Is used to check if incoming requests are made by users that are authorized to make that request.
public class AuthorizationFilter extends BasicAuthenticationFilter {
	public AuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		String header = req.getHeader(SecurityConstants.HEADER_STRING);
		if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			// go to next filter in chain
			chain.doFilter(req, res);
			return;
		}

		UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(req);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		chain.doFilter(req, res);
	}

	// helper function to check if a JWT that is present in the Authorization header of the provided request
	// is valid.
	private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest req) {
		String token = req.getHeader(SecurityConstants.HEADER_STRING);

		if (token == null) {
			return null;
		}

		token = token.replace(SecurityConstants.TOKEN_PREFIX, ""); // remove the token prefix?? why not just not add a token prefix in the first place?
		String userEmail = Jwts.parser()
							.setSigningKey(SecurityConstants.getTokenSecret())
							.parseClaimsJws(token)
							.getBody()
							.getSubject();

		if (userEmail == null) {
			return null;
		} 

		return new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
	}
}