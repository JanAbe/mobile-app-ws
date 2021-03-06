package com.appsdeveloperblog.app.ws.mobileappws.service;

import java.util.List;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;

import org.springframework.security.core.userdetails.UserDetailsService;

// why is this an interface?
// will there be more than 1 implementations?
public interface UserService extends UserDetailsService {
	public UserDto createUser(UserDto user);
	public UserDto getUser(String email);
	public UserDto getUserByUserId(String userId);
	public UserDto updateUser(String userId, UserDto user);
	public void deleteUser(String userId);
	public List<UserDto> getUsers(int page, int limit);
	public boolean verifyEmailToken(String token);
	public boolean requestPasswordReset(String email);
	public boolean resetPassword(String token, String password);
}