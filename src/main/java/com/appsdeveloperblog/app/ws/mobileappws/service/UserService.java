package com.appsdeveloperblog.app.ws.mobileappws.service;

import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;

import org.springframework.security.core.userdetails.UserDetailsService;

// why is this an interface?
// will there be more than 1 implementations?
public interface UserService extends UserDetailsService {
	public UserDto createUser(UserDto user);
}