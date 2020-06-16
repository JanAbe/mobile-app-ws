package com.appsdeveloperblog.app.ws.mobileappws.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.appsdeveloperblog.app.ws.mobileappws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.UserRest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // will be able to send and receive http requests
@RequestMapping("users") // base resource path. All methods in this class can be called if an HTTP request is send to '/users'
public class UserController {

	@Autowired // when usercontroller is created, spring creates and injects an instance of userService 
	UserService userService;
	
	// if no accept header is present in the GET request, the resource is returned in XML format because it comes before the JSON mediatype, in the producees section of the @getMapping annotation
	// (the order matters)
	@GetMapping(path="/{id}", produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) // maps an incoming GET request to this method
	public UserRest getUser(@PathVariable String id) {
		UserRest userRest = new UserRest();

		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, userRest);

		return userRest;
	}

	@PostMapping(
		consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
		produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) // maps an incoming POST request to this method
	// @RequestBody is necessary if you want the method to be able to read the body of the http request
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest userRest = new UserRest();

		if (userDetails.getFirstName().isEmpty()) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto); // populate userDto with content from userDetails using .copyProperties()

		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, userRest);

		return userRest;
	}

	@PutMapping(
		path="/{id}",
		consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
		produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
	) // maps an incoming PUT request to this method
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest userRest = new UserRest();

		if (userDetails.getFirstName().isEmpty()) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto); // populate userDto with content from userDetails using .copyProperties()

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, userRest);

		return userRest;
	}

	@DeleteMapping(
		path={"/{id}"}, 
		produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
	) // maps an incoming DELETE request to this method
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel status = new OperationStatusModel();
		status.setOperationName(RequestOperationName.DELETE.name());
		this.userService.deleteUser(id);
		status.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return status;
	}

	@GetMapping(produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(
		@RequestParam(value="page", defaultValue="0") int page,
		@RequestParam(value="limit", defaultValue="25") int limit
	) {
		List<UserRest> users = new ArrayList<>();
		List<UserDto> userDtos = this.userService.getUsers(page, limit);

		for (UserDto userDto : userDtos) {
			UserRest user = new UserRest();
			BeanUtils.copyProperties(userDto, user);
			users.add(user);
		}

		return users;
	}

}