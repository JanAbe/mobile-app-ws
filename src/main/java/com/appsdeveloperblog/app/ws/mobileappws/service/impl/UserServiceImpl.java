package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.PasswordResetTokenRepository;
import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.RoleRepository;
import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.security.UserPrincipal;
import com.appsdeveloperblog.app.ws.mobileappws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.PasswordResetTokenEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.AmazonSES;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Utils;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response.ErrorMessages;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	AmazonSES amazonSES;

	@Override
	public UserDto createUser(UserDto user) {
		// if email already used -> throw error
		if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new UserServiceException("Record already exists");
		}

		// create public address id and bind user to the address, for each address
		for (AddressDto address : user.getAddresses()) {
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
		}

		// BeanUtils.copyProperties(user, userEntity); // field names in user and userEntity must match
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);

		// set roles
		Collection<RoleEntity> roleEntities = new HashSet<>();
		for (String role : user.getRoles()) {
			RoleEntity roleEntity = roleRepository.findByName(role);
			if (roleEntity != null) {
				roleEntities.add(roleEntity);
			}
		}

		userEntity.setRoles(roleEntities);

		UserEntity storedUserEntity = userRepository.save(userEntity);

		// BeanUtils.copyProperties(storedUserEntity, userDto);
		UserDto userDto = modelMapper.map(storedUserEntity, UserDto.class);

		// send email to user for verification purposes
		amazonSES.verifyEmail(userDto);

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
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> users = new ArrayList<>();

		// so callers don't have to provided page=0 to get the first page, but they can use page=1
		if (page > 0) {
			page -= 1;
		}
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> userPage = this.userRepository.findAll(pageableRequest);
		List<UserEntity> foundUsers = userPage.getContent();

		for (UserEntity userEntity : foundUsers) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			users.add(userDto);
		}

		return users;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}

		return new UserPrincipal(userEntity);

		// return new User(
		// 	userEntity.getEmail(), 
		// 	userEntity.getEncryptedPassword(), 
		// 	userEntity.getEmailVerificationStatus(), 
		// 	true, 
		// 	true, 
		// 	true, 
		// 	new ArrayList<>()
		// );
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean isVerified = false;

		UserEntity user = this.userRepository.findUserByEmailVerificationToken(token);
		if (user != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if (!hasTokenExpired) {
				user.setEmailVerificationToken(null);
				user.setEmailVerificationStatus(true);
				this.userRepository.save(user);
				isVerified = true;
			}
		}

		return isVerified;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		boolean emailIsSent = false;
		UserEntity user = this.userRepository.findByEmail(email);

		if (user == null) {
			return emailIsSent;
		}
		
		String token = utils.generatePasswordResetToken(user.getUserId());
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(user);
		this.passwordResetTokenRepository.save(passwordResetTokenEntity);

		emailIsSent = new AmazonSES().sendPasswordResetRequest(
			user.getFirstName(),
			user.getEmail(),
			token
		);

		return emailIsSent;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		boolean passwordReset = false;

		if (Utils.hasTokenExpired(token)) {
			return passwordReset;
		}

		PasswordResetTokenEntity passwordResetTokenEntity = this.passwordResetTokenRepository.findByToken(token);
		if (passwordResetTokenEntity == null) {
			return passwordReset;
		}

		String encodedPassword = this.bCryptPasswordEncoder.encode(password);

		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPassword);
		UserEntity updatedUserEntity = this.userRepository.save(userEntity);

		if (updatedUserEntity != null && updatedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
			passwordReset = true;
		}

		// remove token from the db, so it can't be reused
		this.passwordResetTokenRepository.delete(passwordResetTokenEntity);

		return passwordReset;
	}
}