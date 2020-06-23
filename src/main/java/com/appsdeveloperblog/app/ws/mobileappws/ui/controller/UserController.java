package com.appsdeveloperblog.app.ws.mobileappws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.appsdeveloperblog.app.ws.mobileappws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.mobileappws.service.AddressService;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Roles;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.AddressRest;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.UserRest;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController // will be able to send and receive http requests
@RequestMapping("/users") // base resource path. All methods in this class can be called if an HTTP request is send to '/users'
@CrossOrigin(origins = {"http://localhost:8084", "http://localhost:8083"}) // all endpoints that are created in this class are allowed to be requested from the specified origins
public class UserController {

	@Autowired // when usercontroller is created, spring creates and injects an instance of userService 
	UserService userService;

	@Autowired
	AddressService addressService;
	
	// if no accept header is present in the GET request, the resource is returned in XML format because it comes before the JSON mediatype, in the producees section of the @getMapping annotation
	// (the order matters)
	@PostAuthorize("hasRole('ROLE_ADMIN') or returnObject.userId == principal.id") // principal is the currently logged in user
	@ApiOperation(value = "The GET User Details webservice endpoint", notes = "${userController.GetUser.ApiOperation.Notes}")
	@GetMapping(path="/{id}", produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) // maps an incoming GET request to this method
	public UserRest getUser(@PathVariable String id) {
		UserRest userRest = new UserRest();

		UserDto userDto = userService.getUserByUserId(id);
		// BeanUtils.copyProperties(userDto, userRest);
		ModelMapper modelMapper = new ModelMapper();
		userRest = modelMapper.map(userDto, UserRest.class);

		return userRest;
	}

	@PostMapping(
		consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
		produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) // maps an incoming POST request to this method
	// @RequestBody is necessary if you want the method to be able to read the body of the http request
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest userRest = new UserRest();

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto); // populate userDto with content from userDetails using .copyProperties()
		ModelMapper modelMapper = new ModelMapper(); // to also map nested classes correctly, something beanutils doesn't do
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

		UserDto createdUser = userService.createUser(userDto);
		userRest = modelMapper.map(createdUser, UserRest.class);

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
		userDto = new ModelMapper().map(userDetails, UserDto.class);
		// BeanUtils.copyProperties(userDetails, userDto); // populate userDto with content from userDetails using .copyProperties()

		UserDto updatedUser = userService.updateUser(id, userDto);
		userRest = new ModelMapper().map(updatedUser, UserRest.class);
		// BeanUtils.copyProperties(updatedUser, userRest);

		return userRest;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.id")
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

	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
	})
	@GetMapping(produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(
		@RequestParam(value="page", defaultValue="0") int page,
		@RequestParam(value="limit", defaultValue="25") int limit
	) {
		List<UserRest> users = new ArrayList<>();
		List<UserDto> userDtos = this.userService.getUsers(page, limit);

		Type listType = new TypeToken<List<UserRest>>() {
		}.getType();
		users = new ModelMapper().map(userDtos, listType);

		// for (UserDto userDto : userDtos) {
		// 	UserRest user = new UserRest();
		// 	BeanUtils.copyProperties(userDto, user);
		// 	users.add(user);
		// }

		return users;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public CollectionModel<AddressRest> getAddresses(@PathVariable String id) {
		List<AddressRest> addresses = new ArrayList<>();
		List<AddressDto> addressDtos = this.addressService.getAddresses(id);

		if (addressDtos != null && !addressDtos.isEmpty()) {
			ModelMapper modelMapper = new ModelMapper();
			Type listType = new TypeToken<List<AddressRest>>() {}.getType(); // todo: figure out what happens here
			addresses = modelMapper.map(addressDtos, listType);

			for (AddressRest address : addresses) {
				Link selfLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, address.getAddressId()))
					.withSelfRel();
				
				address.add(selfLink);
			}
		}
		
		Link userLink = WebMvcLinkBuilder
			.linkTo(UserController.class)
			.slash(id)
			.withRel("user");
		
		Link selfLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddresses(id))
			.withSelfRel();

		return CollectionModel.of(addresses, userLink, selfLink);
	}
	
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		AddressRest address = new AddressRest();
		AddressDto addressDto = this.addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		address = modelMapper.map(addressDto, AddressRest.class);

		// Create HATEOS links to other resources
		Link userLink = WebMvcLinkBuilder
			.linkTo(UserController.class)
			.slash(userId)
			.withRel("user");

		Link userAddressesLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddresses(userId))
			.withRel("addresses");

		Link selfLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
			.withSelfRel();

		return EntityModel.of(address, Arrays.asList(userLink, userAddressesLink, selfLink));
	}

	// http://localhost:8080/mobile-app-ws/users/email-verification?token=xxx
	@GetMapping(path="/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
 	// annotation used to allow requests to be made to this endpoint from different origins than the origin where this application is running
	@CrossOrigin(origins = {"http://localhost:8084", "http://localhost:8083"})
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
		OperationStatusModel status = new OperationStatusModel();
		status.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

		boolean isVerified = userService.verifyEmailToken(token);
		if (isVerified) {
			status.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			status.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		return status;
	}

	@PostMapping(
		path = "/password-reset-request",
		produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	) // this method sends an email containing a link to a webpage where the user can change his/her password
	public OperationStatusModel requestPasswordReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel status = new OperationStatusModel();
		boolean succeeded = this.userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		status.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());

		if (succeeded) {
			status.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			status.setOperationResult(RequestOperationStatus.ERROR.name());
		}

		return status;
	}

	@PostMapping(
		path = "/password-reset",
		produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	) // this method resets the password of the user with the newly prodivded password
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel status = new OperationStatusModel();
		boolean succeeded = this.userService.resetPassword(passwordResetModel.getToken(), passwordResetModel.getPassword());

		status.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		if (succeeded) {
			status.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			status.setOperationResult(RequestOperationStatus.ERROR.name());
		}

		return status;
	}
}