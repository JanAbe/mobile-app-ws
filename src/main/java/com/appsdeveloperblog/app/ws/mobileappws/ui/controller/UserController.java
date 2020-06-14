package com.appsdeveloperblog.app.ws.mobileappws.ui.controller;

import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.UserRest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // will be able to send and receive http requests
@RequestMapping("users") // base resource path. All methods in this class can be called if an HTTP request is send to '/users'
public class UserController {

	@Autowired // when usercontroller is created, spring creates and injects an instance of userService 
	UserService userService;
	
	@GetMapping // maps an incoming GET request to this method
	public String getUser() {
		return "get user was called";
	}

	@PostMapping // maps an incoming POST request to this method
	// @RequestBody is necessary if you want the method to be able to read the body of the http request
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
		UserRest userRest = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto); // populate userDto with content from userDetails using .copyProperties()

		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, userRest);

		return userRest;
	}

	@PutMapping // maps an incoming PUT request to this method
	public String updateUser() {
		return "update user was called";
	}

	@DeleteMapping // maps an incoming DELETE request to this method
	public String deleteUser() {
		return "delete user was called";
	}

}