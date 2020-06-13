package com.appsdeveloperblog.app.ws.mobileappws.ui.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // will be able to send and receive http requests
@RequestMapping("users") // base resource path. All methods in this class can be called if an HTTP request is send to '/users'
public class UserController {
	
	@GetMapping // maps an incoming GET request to this method
	public String getUser() {
		return "get user was called";
	}

	@PostMapping // maps an incoming POST request to this method
	public String createUser() {
		return "create user was called";
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