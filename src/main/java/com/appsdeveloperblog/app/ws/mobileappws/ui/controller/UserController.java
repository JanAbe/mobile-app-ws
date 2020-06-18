package com.appsdeveloperblog.app.ws.mobileappws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.appsdeveloperblog.app.ws.mobileappws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.mobileappws.service.AddressService;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.AddressRest;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.UserRest;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
@RequestMapping("/users") // base resource path. All methods in this class can be called if an HTTP request is send to '/users'
public class UserController {

	@Autowired // when usercontroller is created, spring creates and injects an instance of userService 
	UserService userService;

	@Autowired
	AddressService addressService;
	
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

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto); // populate userDto with content from userDetails using .copyProperties()
		ModelMapper modelMapper = new ModelMapper(); // to also map nested classes correctly, something beanutils doesn't do
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

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
}