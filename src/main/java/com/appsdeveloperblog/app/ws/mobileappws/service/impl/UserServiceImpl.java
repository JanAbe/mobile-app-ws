package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import java.util.ArrayList;

import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Utils;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.ErrorMessages;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service // what does this do??
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		// if email already used -> throw error
		if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new RuntimeException("Record already exists");
		}

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity); // field names in user and userEntity must match

		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		UserEntity storedUserEntity = userRepository.save(userEntity);

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(storedUserEntity, userDto);

		return userDto;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}

		UserDto user = new UserDto();
		BeanUtils.copyProperties(userEntity, user);
		return user;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto userDto = new UserDto();
		UserEntity userEntity = this.userRepository.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " for user with id: " + userId);
		}

		BeanUtils.copyProperties(userEntity, userDto);

		return userDto;
	}

	@Override // userId = id of user that wants to get updated. user = class that contains newly updated data
	public UserDto updateUser(String userId, UserDto user) {
		UserDto updatedUserDto = new UserDto();
		UserEntity userEntity = this.userRepository.findByUserId(userId);
		if (userEntity == null) {
			// but now userServiceImpl has a dependency to the ErrorMessages class, which is located in the presentation layer, while this class is located in the service/ business layer
			// which shouldn't be allowed right?
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}

		// add business logic here if necessary, e.g -> check if provided names aren't empty
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());

		UserEntity updatedUser = this.userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedUser, updatedUserDto);

		return updatedUserDto;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = this.userRepository.findByUserId(userId);
		if (userEntity == null) {
			// but now userServiceImpl has a dependency to the ErrorMessages class, which is located in the presentation layer, while this class is located in the service/ business layer
			// which shouldn't be allowed right?
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}

		this.userRepository.delete(userEntity);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}
}